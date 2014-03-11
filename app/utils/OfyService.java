package utils;

import models.File;
import models.Frase;
import models.Notificacao;
import models.Usuario;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

public class OfyService {
   
	static {
		factory().register(File.class);
		factory().register(Frase.class);
		factory().register(Notificacao.class);
		factory().register(Usuario.class);
    }

	public static Objectify ofy() {
        return ObjectifyService.ofy();
    }

    public static ObjectifyFactory factory() {
        return ObjectifyService.factory();
    }
}
