package controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.File;
import models.Frase;
import models.Notificacao;
import models.Usuario;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.FacebookApi;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.types.User;

import enums.Sexo;

import play.cache.Cache;
import play.mvc.Controller;
import play.mvc.Http.Header;

public class UsuarioController extends Controller{

	private static final Token EMPTY_TOKEN = null;	
	
	private static Usuario loginFacebookIntern(String token) throws IOException{
		
		FacebookClient facebookClient = new DefaultFacebookClient(token);
	    User facebookUser = facebookClient.fetchObject("me", User.class);
	    
	    Usuario usuario = Usuario.getUserByFacebookAccount(facebookUser.getId(), facebookUser.getEmail());
		    
	    
	    if(usuario == null){
			   	 usuario = new Usuario();
			   	 
			   	 usuario.setNome(facebookUser.getFirstName());
			   	 usuario.setEmail(facebookUser.getEmail());
			   	 usuario.setFacebookId(facebookUser.getId());
			   	 usuario.setTokenFacebook(token);
			   	 
			   	 if(facebookUser.getLocation() != null)
			   		 usuario.setCidade(facebookUser.getLocation().getName());			   	 			 
			   	 
			   	 usuario.setNascimento(facebookUser.getBirthdayAsDate());
			   	 usuario.setSexo(facebookUser.getGender());			   	 
			   		    	 
			   	 String sobrenome = "";
			   	 if(facebookUser.getMiddleName() != null && !facebookUser.getMiddleName().trim().equals(""))
			   		 sobrenome = facebookUser.getMiddleName();
			   	 
			   	 if(facebookUser.getLastName() != null && !facebookUser.getLastName().trim().equals("")){
			   		 
			   		 if(sobrenome.equals(""))
			   			 sobrenome = facebookUser.getLastName();
			   		 else
			   			 sobrenome = sobrenome + " " + facebookUser.getLastName();	 	    		 
			   	 }
			   	 
			   	 usuario.setSobrenome(sobrenome);			   	 
			   	 usuario.setTokenFacebook(token);
			   	 usuario.setFacebookId(facebookUser.getId());
				   	 
			   	 usuario.setFotoUsuarioKey(Usuario.getImagemFacebook(facebookUser.getId()));
			   	 
			   	 usuario.save();
			   	 
			   	 String nomeCompletoUsuario = usuario.getNome();
			   	 if(usuario.getSobrenome() != null && !usuario.getSobrenome().trim().equals(""))
			   		 nomeCompletoUsuario = nomeCompletoUsuario + " " + usuario.getSobrenome();
			   	 	   	
	    }
	    else{	    
	    	
	    	if((usuario.getEmail() == null ||  usuario.getEmail() == "") && facebookUser.getEmail() != null)
	    		 usuario.setEmail(facebookUser.getEmail());
	    		    	
	    	 if((usuario.getCidade() == null || usuario.getCidade() == "") && facebookUser.getLocation() != null)
	    		 usuario.setCidade(facebookUser.getLocation().getName());
	    	 
	    	 if(usuario.getFotoUsuarioKey() == null)
	    		 usuario.setFotoUsuarioKey(Usuario.getImagemFacebook(facebookUser.getId()));
	    		    		    	 
	    	 usuario.setFacebookId(facebookUser.getId());
		   	 usuario.setTokenFacebook(token);
		   	 usuario.save();
	    }
	    
	    logarUsuario(usuario);
	
	    return usuario;
	}
	
	public static void loginFacebook(String token) throws IOException {		
			   
		 Usuario usuario = loginFacebookIntern(token);
			     
    	 if(usuario.getSexo() != null && usuario.getSexo().equals(Sexo.masculino))
    		 flash.put("message", "Welcome " + usuario.getNome());
    	 else if(usuario.getSexo() != null && usuario.getSexo().equals(Sexo.feminino))
    		 flash.put("message", "Welcome " + usuario.getNome());
    	 else
    		 flash.put("message", "Welcome " + usuario.getNome());
    	 
    	 
	     String linkImagem = "";
	     if(usuario.getLinkFotoUsuario() != null && !usuario.getLinkFotoUsuario().equals(""))
	    	 linkImagem = usuario.getLinkFotoUsuario();
	     
	     String[] values = {usuario.getNome(), Long.toString(usuario.getId()), linkImagem};
	     renderJSON(values);	     		     
	}
	
	private static void logarUsuario(Usuario usuario) {
		session.put("usuarioId", usuario.getId());		
		Cache.set(session.getId() + "_usuario", usuario);
	}
	
	public static Long getConnectedUserId(){
		if(session.get("usuarioId") != null)
			return Long.parseLong(session.get("usuarioId"));
		
		return null;
	}
	
	static Usuario getConnectedUser(){
		Usuario usuario = (Usuario) Cache.get(session.getId() + "_usuario");
		
		if(usuario == null){
			Long userId = null;
			if(session.get("usuarioId") != null)
				userId = Long.parseLong(session.get("usuarioId"));
			
			if(userId !=null){
				usuario = Usuario.find(userId);
			}
		}		
		return usuario;
	}
	
		
	public static void disconnectUser(){
		Cache.delete(session.getId() + "_usuario");
		session.clear();
		flash.put("message", play.i18n.Messages.get("usuario.deslogadoSucesso"));
	}
	
	public static void setLanguages(List<String> languagesFrom, List<String> languagesTo){	
		Usuario usuario = UsuarioController.getConnectedUser();		
		usuario.setIdiomasFrom(languagesFrom);
		usuario.setIdiomasTo(languagesTo);		
		usuario.save();	
		show(usuario.getId());
	}
	
	
	public static void show(Long id){
		
		Usuario usuario;
		if(id == null || id == 0)
			usuario = UsuarioController.getConnectedUser();
		else
			 usuario = Usuario.find(id);
		
		Frase frase = Frase.getFirst(usuario.getIdiomasTo(), usuario.getIdiomasFrom());
		
		List<Frase> frasesTraduzidas = Frase.getFromUserTranslated(usuario.getId(), 5);
		List<File> arquivosPedidos = File.findByUser(usuario.getId(), 5);
			
		render(usuario, frase, frasesTraduzidas, arquivosPedidos);		
	}
	
	public static void cadastro(){
		Usuario usuario = UsuarioController.getConnectedUser();
		render(usuario);	
	}
	
	public static void notificacao(){
		
		Usuario usuario = UsuarioController.getConnectedUser();
		
		List<Notificacao> notificacoes = Notificacao.findByUser(usuario.getId());
		
		usuario.setLastNotification(new Date());
		usuario.save();
		
		render(notificacoes, usuario);
	}
	
}
