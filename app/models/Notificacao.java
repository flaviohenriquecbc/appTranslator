package models;

import static utils.OfyService.ofy;

import java.util.Date;
import java.util.List;

import play.mvc.Controller;
import utils.SendMail;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Parent;

import enums.TipoNotificacao;

@Entity
@Cache
public class Notificacao extends Controller{
	
	@Id private Long id;
	@Parent private Key<Usuario> keyUsuario;
	private Long idRegistro;
	private TipoNotificacao tipo;
	@Index private Date dataCriacao;
	
	public Notificacao(Key<Usuario> keyUsuario, TipoNotificacao tipo, Long idRegistro){
		this.keyUsuario = keyUsuario;
		this.tipo = tipo;
		this.dataCriacao = new Date();
		this.idRegistro = idRegistro;	
		
//		SendMail sendMail = new SendMail();
//		try {
//			System.out.println("Trying to send mail...");
//			sendMail.traducaoFinalizada(idRegistro);
//		} catch (Exception e) {
//			System.out.println("Erro ao enviar email!!!!");
//			e.printStackTrace();
//		}
		
	}
	
	public Key<Notificacao> save() {
		return ofy().save().entity(this).now();
	}
	
	static public List<Notificacao> findByUser(Long idUsuario){
		return ofy().load().type(Notificacao.class).ancestor(Key.create(Usuario.class, idUsuario)).list();
	}
	
	public String getMessage(){
		if(tipo == TipoNotificacao.traducaoConcluida)
			return "Translation ready!";
		
		return "";
	} 
	
	public Long getIdRegistro() {
		return idRegistro;
	}

	public void setIdRegistro(Long idRegistro) {
		this.idRegistro = idRegistro;
	}

	public Date getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public static Notificacao getLast() {
		return ofy().load().type(Notificacao.class).order("dataCriacao").first().get();
	}

}
