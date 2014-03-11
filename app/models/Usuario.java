package models;

import static utils.OfyService.ofy;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import play.data.validation.Required;
import utils.Utilidades;

import com.google.appengine.api.blobstore.BlobKey;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Index;

import enums.Sexo;

@Entity
@Cache
public class Usuario implements Serializable {
	
	@Id private Long id;
	@Required private String nome;
	@Index private String email;
	
	private String sobrenome;
	private BlobKey fotoUsuarioKey;
	private String linkFotoUsuario;	
	
	private String cidade;
	private Sexo sexo;
	private Date nascimento;
	
	@Index private String facebookId;
	private String tokenFacebook;
	
	private int noSorteios;
	private Date dataCadastro;	
	
	private int noTraducoes;
	private int noPedidos;
	private int pontos;
		
	private Date lastNotification;

	@Index private List<String> idiomasTo;
	@Index private List<String> idiomasFrom; 
	
	public Usuario(){
		this.noTraducoes = 0;
		this.noPedidos = 0;
		this.pontos = 2;		
	}
	
	public static Usuario find(Long usuarioId) {		
		return ofy().load().key(Key.create(Usuario.class, usuarioId)).get();		
	}
	
	public static Usuario getUserByFacebookAccount(String facebookId, String email) {
		Usuario usuario = null; 
		
		if(facebookId != null && !facebookId.trim().equals(""))
			usuario = ofy().load().type(Usuario.class).filter("facebookId", facebookId).first().get();
		
		if(usuario == null && email != null && !email.trim().equals(""))
			usuario = ofy().load().type(Usuario.class).filter("email", email).first().get();
		
		return usuario;
	} 
	
	public static Usuario getUserByEmail(String email) {
		return ofy().load().type(Usuario.class).filter("email", email).first().get();
	}
	
	public static BlobKey getImagemFacebook(String idImagem) throws IOException {
		//String linkImagem = "https://graph.facebook.com/"+idImagem+"/picture?type=large";
		//BlobKey bk = Utilidades.saveImageFromURL(linkImagem, "Usuario_FacebookImage");
		//return bk;
		return null;
	}
	
	public static Usuario getUsuarioById(Long idUsuario) {
		return ofy().load().key(Key.create(Usuario.class, idUsuario)).get();
	}
	
	public Key<Usuario> save() {
		return ofy().save().entity(this).now();
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSobrenome() {
		return sobrenome;
	}

	public void setSobrenome(String sobrenome) {
		this.sobrenome = sobrenome;
	}

	public BlobKey getFotoUsuarioKey() {
		return fotoUsuarioKey;
	}

	public void setFotoUsuarioKey(BlobKey fotoUsuarioKey) {
		this.fotoUsuarioKey = fotoUsuarioKey;
	}

	public String getLinkFotoUsuario() {
		return linkFotoUsuario;
	}

	public void setLinkFotoUsuario(String linkFotoUsuario) {
		this.linkFotoUsuario = linkFotoUsuario;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public Sexo getSexo() {
		return sexo;
	}

	public void setSexo(String sexo) {
		if(sexo != null && !sexo.trim().equals("")){
			if(sexo.equals("female"))
				this.sexo = Sexo.feminino;
			else
				this.sexo = Sexo.masculino;
		}
	}

	public Date getNascimento() {
		return nascimento;
	}

	public void setNascimento(Date nascimento) {
		this.nascimento = nascimento;
	}

	public String getFacebookId() {
		return facebookId;
	}

	public void setFacebookId(String facebookId) {
		this.facebookId = facebookId;
	}

	public String getTokenFacebook() {
		return tokenFacebook;
	}

	public void setTokenFacebook(String tokenFacebook) {
		this.tokenFacebook = tokenFacebook;
	}

	public int getNoSorteios() {
		return noSorteios;
	}

	public void setNoSorteios(int noSorteios) {
		this.noSorteios = noSorteios;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public List<String> getIdiomasTo() {
		return idiomasTo;
	}

	public void setIdiomasTo(List<String> idiomasTo) {
		this.idiomasTo = idiomasTo;
	}

	public List<String> getIdiomasFrom() {
		return idiomasFrom;
	}

	public void setIdiomasFrom(List<String> idiomasFrom) {		
		this.idiomasFrom = idiomasFrom;
	}

	public void addTraducao() {
		this.noTraducoes += 1;
		this.pontos += 1;
	}

	public void consomeTraducao(){		
		this.pontos -= 1;
	}

	public void askTraducao() {
		this.noPedidos += 1;	
	}
	public int getNoTraducoes() {
		return noTraducoes;
	}

	public void setNoTraducoes(int noTraducoes) {
		this.noTraducoes = noTraducoes;
	}

	public int getNoPedidos() {
		return noPedidos;
	}

	public void setNoPedidos(int noPedidos) {
		this.noPedidos = noPedidos;
	}

	public int getPontos() {
		return this.pontos;
	}

	public void setPontos(int pontos) {
		this.pontos = pontos;
	}

	public boolean isThereNotification() {
		
		Notificacao notification = Notificacao.getLast();	
		if(notification != null){
			Date ultima = lastNotification;
			
			if(lastNotification == null || (lastNotification != null && lastNotification.after(ultima)))
				return true;
		}
		
		return false;				
	}
	
	public void setLastNotification(Date lastNotification) {
		this.lastNotification = lastNotification;
	}

	public static List<Usuario> all() {
		return ofy().load().type(Usuario.class).list();
	}

	
}
