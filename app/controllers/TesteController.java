package controllers;

import com.googlecode.objectify.Key;

import models.File;
import models.Frase;
import models.Usuario;
import play.mvc.Controller;

public class TesteController extends Controller{

	public static void carregaFrase(){
		
		Usuario usuario = new Usuario();
		usuario.setNome("Kayatt");
		usuario.save();
		
		File file = new File(1, usuario.getId(), "Bla1");
		Key<File> keyFile = file.save();
		
		Frase frase = new Frase(keyFile, "Apply here for this vacancy!", "portugues", "english");
		frase.idRequisitante = UsuarioController.getConnectedUserId();		
		frase.save();	
	}
	
	public static void frasesAll(){
		renderJSON(Frase.all());		
	}
	
	public static void userAll(){
		renderJSON(Usuario.all());
	}
	
}
