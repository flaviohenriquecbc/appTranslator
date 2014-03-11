package models;

import static utils.OfyService.ofy;

import java.util.Date;
import java.util.List;

import com.google.appengine.api.datastore.Cursor;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Parent;

import controllers.UsuarioController;

@Entity
@Cache
public class Frase {
	@Parent Key<File> keyFile;
	@Id private Long id;
	private String original;
	private String traduzida;
	@Index private Long idTradutor; 
	@Index public Long idRequisitante; //trocar para private 
	@Index private boolean isTranslated;
	@Index private String idiomaTo;
	@Index private String idiomaFrom;
	@Index Date dataCriacao;
	
	public Frase(Key<File> keyFile, String textoCompleto, String idiomaTo, String idiomaFrom) {
		this.original = textoCompleto;
		this.idRequisitante = UsuarioController.getConnectedUserId();
		this.isTranslated = false;
		this.idiomaTo = idiomaTo;
		this.idiomaFrom = idiomaFrom;
		this.keyFile = keyFile;
		this.dataCriacao = new Date();	
	}
	
	public String getOriginal() {
		return original;
	}
	public void setOriginal(String original) {
		this.original = original;
	}
	public String getTraduzida() {
		return traduzida;
	}
	public void setTraduzida(String traduzida, Long idTradutor) {
		this.traduzida = traduzida;
		this.idTradutor = idTradutor;
		this.isTranslated = true;	
		this.dataCriacao = new Date();	
		
		File file = File.find(this.keyFile.getId());
		file.addFraseTraduzida();
		file.save();
	}
	public Key<Frase> save() {
		return ofy().save().entity(this).now();
	}
	public static Frase find(Long idFile, Long idFrase) {
		return ofy().load().key(Key.create(Key.create(File.class, idFile), Frase.class, idFrase)).get();		
	}
	public Long getIdRequisitante() {
		return idRequisitante;
	}
	public void setIdRequisitante(Long idRequisitante) {
		this.idRequisitante = idRequisitante;
	}
	public Long getIdTradutor() {
		return idTradutor;
	}
	public void setIdTradutor(Long idTradutor) {
		this.idTradutor = idTradutor;
	}
	public static Frase getFirst(List<String> idiomaTo, List<String> idiomaFrom) {		
		return ofy().load().type(Frase.class).order("-dataCriacao").filter("isTranslated", false).filter("idiomaTo in", idiomaTo).filter("idiomaFrom in", idiomaFrom).first().get();
	}

	public static void saveList(List<Frase> frases, Key<File> keyFile) {	
		for (Frase frase : frases) {
			frase.setKeyFile(keyFile);
		}		
		ofy().save().entities(frases).now();
	}

	private void setKeyFile(Key<File> keyFile) {
		this.keyFile = keyFile;
	}

	public static List<Frase> all() {
		return ofy().load().type(Frase.class).list();
	}

	public static List<Frase> getFromUserTranslated(Long idUsuario, int limit) {
		if(limit > 0 )
			return ofy().load().type(Frase.class).order("-dataCriacao").filter("idTradutor", idUsuario).limit(limit).list();
		else
			return ofy().load().type(Frase.class).order("-dataCriacao").filter("idTradutor", idUsuario).list();
	}

	public static List<Frase> getFromUserAsked(Long idUsuario, int limit) {
		if(limit > 0)
			return ofy().load().type(Frase.class).order("-dataCriacao").filter("idRequisitante", idUsuario).limit(limit).list();
		else
			return ofy().load().type(Frase.class).order("-dataCriacao").filter("idRequisitante", idUsuario).list();
	}
	public Long getId() {
		return id;
	}
	
	public static List<Frase> findByParent(Key<File> keyFile){
		return ofy().load().type(Frase.class).ancestor(keyFile).list();
	}

	public Key<File> getKeyFile() {
		return keyFile;
	}
	
	private String getIdioma(String idioma){
		if(idioma != null && idioma.equals("english"))
			return "English";
		else if(idioma != null && idioma.equals("portugues"))
			return "Portuguese";
		else if(idioma != null && idioma.equals("chinese"))
			return "Chinese";
		else if(idioma != null && idioma.equals("espanhol"))
			return "Spanish";
		else if(idioma != null && idioma.equals("japanese"))
			return "Japanish";
		else if(idioma != null && idioma.equals("deutsch"))
			return "German";
		else if(idioma != null && idioma.equals("italian"))
			return "Italian";
		else
			return "";
	}
	
	public String getIdiomaTo(){
		return getIdioma(this.idiomaTo);
	}
	
	public String getIdiomaFrom(){
		return getIdioma(this.idiomaFrom);
	}
	
}
