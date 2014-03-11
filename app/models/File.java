package models;

import static utils.OfyService.ofy;

import java.util.Date;
import java.util.List;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import controllers.UsuarioController;
import enums.TipoNotificacao;

@Entity
@Cache
public class File {
	@Id private Long id;
	private int noFrases;	
	private int noFrasesTraduzidas;
	@Index public Long idRequisitante;
	private String firstPhrase;
	@Index Date dataCriacao;
	
	public File(int noFrases, Long idUsuarioRequisitante, String firstPhrase){
		this.noFrases = noFrases;
		this.noFrasesTraduzidas = 0;
		this.idRequisitante = idUsuarioRequisitante;
		this.firstPhrase = firstPhrase;
		this.dataCriacao = new Date();
	}
	
	public static File find(long idFile) {
		return ofy().load().type(File.class).id(idFile).get();
	}
	
	public int getNoFrases() {
		return noFrases;
	}
	public void setNoFrases(int noFrases) {
		this.noFrases = noFrases;
	}
	public Long getId() {
		return id;
	}

	public Key<File> save() {
		return ofy().save().entity(this).now();
	}
	
	public Long getIdRequisitante() {
		return idRequisitante;
	}

	public void setIdRequisitante(Long idRequisitante) {
		this.idRequisitante = idRequisitante;
	}

	public void addFraseTraduzida() {
		this.noFrasesTraduzidas += 1;
		
		if(this.noFrasesTraduzidas == this.noFrases){
			Long idUsuario = UsuarioController.getConnectedUserId();
			Key<Usuario> keyUsuario = Key.create(Usuario.class, idUsuario);
			Notificacao notificacao = new Notificacao(keyUsuario, TipoNotificacao.traducaoConcluida, this.id);
			notificacao.save();
		}	
	}

	static public List<File> findByUser(Long idRequisitante, int limit){
		if(limit > 0)
			return ofy().load().type(File.class).order("dataCriacao").filter("idRequisitante", idRequisitante).limit(limit).list();
		else
			return ofy().load().type(File.class).order("dataCriacao").filter("idRequisitante", idRequisitante).list();
	}
	public int getNoFrasesTraduzidas() {
		return noFrasesTraduzidas;
	}
	

	public String getFirstPhrase() {
		return firstPhrase;
	}
	
	public int getDonePercentage(){
		return (this.noFrasesTraduzidas / this.noFrases) * 100;
	}
	
}
