package controllers;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.objectify.Key;

import models.File;
import models.Frase;
import models.Notificacao;
import models.Usuario;
import play.mvc.Controller;

public class TraducaoController extends Controller {

	public static void askTranslation(String textoFrase, List<String> idiomasTo, String idiomaFrom){	
				
		Usuario usuarioRequisitante = UsuarioController.getConnectedUser();
		
		for (String idiomaTo : idiomasTo) {
			List<Frase> frases = new ArrayList<Frase>();
			
			String[] linhas = textoFrase.split("\r\n");
			
			for (String linha : linhas) {
				if(linha != null && !linha.trim().equals("")){			
					Frase frase = new Frase(null, linha, idiomaTo, idiomaFrom);
					frases.add(frase);
					usuarioRequisitante.askTraducao();				
				}
			}
			
			File file = new File(frases.size(), usuarioRequisitante.getId(), linhas[0]);			
			Key<File> keyFile = file.save();
			Frase.saveList(frases, keyFile);		
		}
		
		usuarioRequisitante.save();
		
		flash.put("message", "Translations added to our database.");
		
		traduzir();
	}

	public static void addTranslation(Long idFile, Long idFrase, String fraseTraducao){	
				
		Usuario usuario = UsuarioController.getConnectedUser();
		
		Frase frase = Frase.find(idFile, idFrase);
		frase.setTraduzida(fraseTraducao, usuario.getId());
		frase.save();
		
		usuario.addTraducao();
		usuario.save();
		
		Usuario usuarioRequisitante = Usuario.find(frase.getIdRequisitante());
		usuarioRequisitante.consomeTraducao();
		usuarioRequisitante.save();
	}
	
	public static void getRandomPhraseJSON(){
		renderJSON(getRandomPhrase());
	}
	
	public static Frase getRandomPhrase(){	
		Usuario usuario = UsuarioController.getConnectedUser();
		return Frase.getFirst(usuario.getIdiomasTo(), usuario.getIdiomasFrom());
	}
	
	public static void traduzir(){
		Usuario usuario = UsuarioController.getConnectedUser();
		render(usuario);	
	}
	
	public static void showFile(Long idFile){
		Usuario usuario = UsuarioController.getConnectedUser();
		
		List<Frase> frases = Frase.findByParent(Key.create(File.class, idFile));
		File file = File.find(idFile);
		
		
		render(frases, file, usuario);
	}
	public static void frasesTraduzidas(){
		Usuario usuario = UsuarioController.getConnectedUser();		
			
		Long idUsuario = UsuarioController.getConnectedUserId();
		
		List<Frase> frasesTraduzidas = Frase.getFromUserTranslated(idUsuario, 0);		
		render(frasesTraduzidas, usuario);	
	}
	
	public static void arquivosPedidos(){
		Usuario usuario = UsuarioController.getConnectedUser();
		
		Long idUsuario = UsuarioController.getConnectedUserId();
		List<File> arquivosPedidos = File.findByUser(idUsuario, 0);
			
				
		
		render(arquivosPedidos, usuario);	
	}
	
}
