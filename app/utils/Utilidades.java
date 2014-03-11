package utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import play.i18n.Lang;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.appengine.repackaged.org.apache.commons.codec.net.URLCodec;
import com.googlecode.objectify.util.jackson.BlobKeyDeserializer;

import controllers.Application;

public class Utilidades {

	public static String changeLanguage(String idioma){
		
		String lang = "";
		if(idioma.equals("pt") || idioma.equals("pt_BR")){
			Lang.change("pt");
			lang = "pt";
		}else if(idioma.equals("fr")){
			Lang.change("fr");
			lang = "fr";
		}else if(idioma.equals("es")){
			Lang.change("es");
			lang = "es";
		}
		else if(idioma.equals("de")){
			Lang.change("de");
			lang = "de";
		}
		else if(idioma.equals("ro")){
			Lang.change("ro");
			lang = "ro";
		}
		else{
			Lang.change("en");
			lang = "en";
		}
		
		return lang;
	}
		
	
	public static String getFormattedDate(Date date){	
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy kk:mm");
        df.setTimeZone(TimeZone.getTimeZone("GMT-3"));
        return df.format(date); 
	} 
	
	public static String getFormattedDatePerfil(Date date){
		if(date == null)
			return "";
		
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy"); 
        df.setTimeZone(TimeZone.getTimeZone("GMT-3"));
        return df.format(date); 
	}
	
	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
	    int cp;
	    while ((cp = rd.read()) != -1) {
	      sb.append((char) cp);
	    }
	    return sb.toString();
	  }
	
	public static JSONArray readJsonArrayFromUrl(String url) {		
		InputStream is = null;
	    try {
	    	HttpURLConnection connection= (HttpURLConnection) new URL(url).openConnection();
	    	int code = connection.getResponseCode();
	    	
	    	if(code < 200 || code > 206)
	    		return null;
	    	
	    	is = connection.getInputStream();
	    	BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
	    	String jsonText = readAll(rd);
	    	JSONArray json = new JSONArray(jsonText);
	    	is.close();
	      
	    	return json;
	    }catch(Exception e){	    	
	    	System.out.println(e);
	    	return null;
	    }
	    
	}
	
	
	
	public static JSONObject readJsonFromUrl(String url) {
		JSONObject obj = readJsonFromUrlIntern(url);
		
		int i = 0;
		while(i < 30 && obj.toString().indexOf("OVER_QUERY_LIMIT") != -1){			
			try {
				Thread.sleep(150);
				obj = readJsonFromUrlIntern(url);
			} catch (Exception e) {			
				System.out.println(e);
			}
			i++;			
		}
		
		return obj;	
	}
	
	private static String readUrl(String url){
		InputStream is = null;
	    try {
			HttpURLConnection connection= (HttpURLConnection) new URL(url).openConnection();
	    	int code = connection.getResponseCode();
	    	
	    	if(code < 200 || code > 206)
	    		return null;
	    	
	    	is = connection.getInputStream();
	    	BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));    	
			return readAll(rd);
		} catch(Exception e) {
	    	System.out.println(e);
	    	return null;
	    }
	}
	
	private static JSONObject readJsonFromUrlIntern(String url) {
		InputStream is = null;
	    try {
	    	String jsonText = readUrl(url);
	    	JSONObject json = new JSONObject(jsonText.trim());
	    	System.out.println(json);
	    	return json;
	    } catch(Exception e) {
	    	System.out.println(e);
	    	return null;
	    }
	}
	
		public static String getLocalDateStringFromUTCString(String utcLongDateTime) {

			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy kk:mm");
		    String localDateString = null;
	
		    long when = 0;
		    try {
		        when = dateFormat.parse(utcLongDateTime).getTime();
		    } catch (Exception e) {
		    	System.out.println(e);
		    }
		    localDateString = dateFormat.format(new Date(when + TimeZone.getTimeZone("GMT-3").getRawOffset() + (TimeZone.getTimeZone("GMT-3").inDaylightTime(new Date()) ? TimeZone.getTimeZone("GMT-3").getDSTSavings() : 0)));
	
		    return localDateString;
		}
		
		public static BlobKey saveImageFromURL(String link, String nomeImage) {
		    try {
			        FileService fileService = FileServiceFactory.getFileService();
			        AppEngineFile file = fileService.createNewBlobFile("image/png", nomeImage);
			        URL url = new URL(link);
			        InputStream is = url.openStream();
					byte[] imageBytes = IOUtils.toByteArray(is);
					is.close();
			        
			        FileWriteChannel writeChannel = fileService.openWriteChannel(file, true);
			        OutputStream out = Channels.newOutputStream(writeChannel);
			        out.write(imageBytes);
			        out.close();
			        writeChannel.closeFinally();

			        return fileService.getBlobKey(file);				    
		    } catch (IOException ex) {
		        System.out.println(ex);
		        return null;
		    }			
		}
		
		public static BlobKey saveImageFromByte(byte[] image, String nomeImage) throws IOException {
	        FileService fileService = FileServiceFactory.getFileService();
	        AppEngineFile file = fileService.createNewBlobFile("image/png", nomeImage);				
	        
	        FileWriteChannel writeChannel = fileService.openWriteChannel(file, true);
	        OutputStream out = Channels.newOutputStream(writeChannel);
	        out.write(image);
	        out.close();
	        writeChannel.closeFinally();
	        return fileService.getBlobKey(file);
		}

		public static void deleteKeyBlobstore(BlobKey blobKey){
			BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
			blobstoreService.delete(blobKey);
		}

		public static String getServingUrl(BlobKey fotoUsuarioKey) {
			ImagesService imagesService = ImagesServiceFactory.getImagesService();	
			ServingUrlOptions options = ServingUrlOptions.Builder.withBlobKey(fotoUsuarioKey);
			return imagesService.getServingUrl(options);			
		}
		
		public static String encurtarString(String texto, int tamanho){
			if(texto != null && texto.length() > tamanho)
				return texto.substring(0, tamanho - 2) + "...";
			else
				return texto;			
		}
	
		public static String formatImageSize(String link, int size){
			
			if(link == null){
				return "";
			}
			
			if(link.startsWith("../../public/images/none_") || link.startsWith("../../public/images/bairro_") || link.startsWith("../../public/images/cidade_")){
				
				int tamanho = 32;
				if(size <= 32)
					tamanho = 32;
				else if(size > 32 && size <= 44)
					tamanho = 44;
				else if(size > 44 && size <= 64)
					tamanho = 64;
				else if(size > 64 && size < 200)
					tamanho = 88;
				else if(size == 200)
					tamanho = 200;
				
				if(link.startsWith("../../public/images/bairro_"))
					return "../../public/images/bairro_" + tamanho + ".png";
				else if(link.startsWith("../../public/images/cidade_"))
					return "../../public/images/cidade_" + tamanho + ".png";
				else
					return "../../public/images/none_" + tamanho + ".png";
			}else{
				if(link.endsWith("-c"))
					return link.substring(0, link.length() - 4) + size + "-c";
				else
					return link + "=s"+size+"-c";
			}
		}
		
		public static String formatToMobile(String link){			
			
			if(link != null && !link.trim().equals("")){
				String pattern = "../../public/images/";
				String patternMobile = "./images/";
				
				if(link.startsWith(pattern)){				
					link = link.replace(pattern, patternMobile);
				}
			}
			return link;
		}
		
		public static String flattenToAscii(String string) {			
			if(string != null && !string.trim().equals("")){
			    char[] out = new char[string.length()];		    
			    string = Normalizer.normalize(string, Normalizer.Form.NFD);
			    int j = 0;
			    for (int i = 0, n = string.length(); i < n; ++i) {
			        char c = string.charAt(i);
			        if (c <= '\u007F') out[j++] = c;
			    }
			    return new String(out);			    
			}else{
				return "";
			}
		}
}
