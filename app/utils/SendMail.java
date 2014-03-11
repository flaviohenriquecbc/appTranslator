package utils;

import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import java.util.Properties;

public class SendMail {

    private static final String SMTP_HOST_NAME = "smtp.sendgrid.net";
    private static final String SMTP_AUTH_USER = "flaviohenriquecbc";
    private static final String SMTP_AUTH_PWD  = "flaviosenha";

    public void traducaoFinalizada(Long idRegistro) throws Exception{
    	
    	System.out.println("Sending email...");
    	
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", SMTP_HOST_NAME);
        props.put("mail.smtp.port", 587);
        props.put("mail.smtp.auth", "true");

        Authenticator auth = new SMTPAuthenticator();
        Session mailSession = Session.getInstance(props, auth);
        // uncomment for debugging infos to stdout
        // mailSession.setDebug(true);
        Transport transport = mailSession.getTransport();

        MimeMessage message = new MimeMessage(mailSession);

        Multipart multipart = new MimeMultipart("alternative");

        BodyPart part1 = new MimeBodyPart();
       part1.setText("This is multipart mail and u read part1......");

        //BodyPart part2 = new MimeBodyPart();
        //part2.setContent(emailFormatted("Pedro", "Your translation is ready and waiting for you!"), "text/html");

        multipart.addBodyPart(part1);
        //multipart.addBodyPart(part2);

        message.setContent(multipart);
        message.setFrom(new InternetAddress("flaviohenriquecbc@gmail.com"));
        message.setSubject("Your Translation is ready!");
        message.addRecipient(Message.RecipientType.TO,
        		new InternetAddress("nakedmonkeygames@gmail.com"));
        //new InternetAddress("flaviohenriquecbc@gmail.com"));

        transport.connect();
        transport.sendMessage(message,
            message.getRecipients(Message.RecipientType.TO));
        transport.close();
    }

    private class SMTPAuthenticator extends javax.mail.Authenticator {
        public PasswordAuthentication getPasswordAuthentication() {
           String username = SMTP_AUTH_USER;
           String password = SMTP_AUTH_PWD;
           return new PasswordAuthentication(username, password);
        }
    }
    
    private static String emailFormatted(String nome, String conteudo){
		return "<div bgcolor=\"#e0e0e0\" style=\"margin:0;\">" + 
				"<div style=\"background-color:#e0e0e0; width:100%; margin:0; text-align:center; padding-top: 30px;\">" + 				
				"<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"display:inline-block; width:100%; max-width:600px; min-width: 300px;\">" + 
				"<tbody>" + 
				"<tr>" + 
				"<td style=\"border-collapse:collapse\" align=\"center\" valign=\"top\">" + 
				"<table style=\"border-bottom:0; max-width: 600px; width: 100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" >" + 
				"<tbody>" + 
				"<tr>" + 
				"<td style=\"background-color: white;border-collapse:collapse;color:#202020;font-family:Arial;font-size:34px;font-weight:bold;line-height:100%;padding:0;text-align:center;vertical-align:middle\"><div style=\"background-image: url('http://www.protestai.com/public/images/email/logoTop.png'); width:100%; background-repeat:no-repeat; background-position:center; display: inline-block; max-width:600px; height: 190px;\"></div> </td>" + 
				"</tr>" + 
				"</tbody>" + 
				"</table>" + 
				"</td>" + 
				"</tr>" + 
				"<tr>" + 
				"<td style=\"border-collapse:collapse\" align=\"center\" valign=\"top\">" + 
				"<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"background-color: white; width:100%; max-width: 600px;\">" + 
				"<tbody>" + 
				"<tr>" + 
				"<td style=\"border-collapse:collapse\" valign=\"top\">" + 
				"<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"20\">" + 
				"<tbody>" + 
				"<tr>" + 
				"<td style=\"border-collapse:collapse\" valign=\"top\">" + 
				"<div style=\"color:#505050;font-family:Arial;font-size:14px;line-height:150%;text-align:left\">" + 
				"<h1 style=\"color:#202020;display:block;font-family:Arial;font-size:34px;font-weight:bold;line-height:100%;margin-top:0;margin-right:0;margin-bottom:10px;margin-left:0;text-align:left\"><span style=\"font-size:30px;line-height:30px\">"+nome+"</span></h1>" + 
				"<p style=\"line-height:1.15;margin-top:0pt;margin-bottom:0pt\" dir=\"ltr\">&nbsp;</p>" + 
				"<style>" + 
				".linkVermelho {" + 
				"color: #DC5C05 !important;" + 
				"}" + 
				".atividadeTexto {" + 
				"color: #AAA !important;" + 
				"}" + 
				"</style>" + 
				"<p style=\"line-height:1.15;margin-top:0pt;margin-bottom:0pt; padding: 10px; padding-top:0; background-color: #e9e9e9\" dir=\"ltr\"><span><span style=\"font-size:14px;color:#000000;vertical-align:baseline;white-space:pre-wrap; margin-top: 10px;display: inline-block;\">" + 
				conteudo.replaceAll("(\r\n|\n)", "<br />") + 
				"</span></p>" + 
				"<p style=\"line-height:1.15;margin-top:0pt;margin-bottom:0pt\" dir=\"ltr\">&nbsp;</p>" + 
				"<p style=\"line-height:1.25;margin-top:0pt;margin-bottom:0pt\" dir=\"ltr\"><span><span style=\"font-size:14px;color:#000000;vertical-align:baseline;white-space:pre-wrap\">Um grande abraço,</span></span></p>" + 
				"<p style=\"line-height:1.15;margin-top:0pt;margin-bottom:0pt\" dir=\"ltr\"><span><span style=\"font-size:14px;color:#000000;vertical-align:baseline;white-space:pre-wrap\">A equipe Protestaí</span></span></p>" + 
				"</div>" + 
				"</td>" + 
				"</tr>" + 
				"</tbody>" + 
				"</table>" + 
				"</td>" + 
				"</tr>" + 
				"</tbody>" + 
				"</table>" + 
				"</td>" + 
				"</tr>" + 
				"<tr>" + 
				"<td style=\"border-collapse:collapse\" align=\"center\" valign=\"top\">" + 
				"<table style=\"background-color:#ffd34f;border-top:0; max-width:600px; width:100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"10\">" + 
				"<tbody>" + 
				"<tr>" + 
				"<td style=\"border-collapse:collapse\" valign=\"top\">" + 
				"<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"10\">" + 
				"<tbody>" + 
				"<tr>" + 
				"<td style=\"border-collapse:collapse;background-color:#ffd34f;border:0\" colspan=\"2\" valign=\"middle\">" + 
				"<div style=\"color:#707070;font-family:Arial;font-size:12px;line-height:125%;text-align:center\"><span style=\"font-size:12px\"><span style=\"color:#333333;font-family:'lucida grande',tahoma,verdana,arial,sans-serif;line-height:14px;text-align:left;white-space:pre-wrap\"><strong><span style=\"background-color:#ffd34f\">Protestaí</span></strong><span style=\"background-color:#ffd34f\">, a sua plataforma social para protestar sobre tudo o que pode melhorar</span></span></span></div>" + 
				"</td>" + 
				"</tr>" + 
				"</tbody>" + 
				"</table>" + 
				"</td>" + 
				"</tr>" + 
				"</tbody>" + 
				"</table>" + 
				"</td>" + 
				"</tr>" + 
				"<tr>" + 
				"<td align=\"center\" colspan=\"2\"  style=\"width:100%; max-width:600px;\">" + 
				"    <img src=\"http://www.protestai.com/public/images/email/sombra.jpg\"  style=\"width:100%; max-width:600px; height: 20px; \">" +     
				"    <p  style=\"width:100%; max-width:500px\">" + 
				"        <font face=\"helvetica, arial\" size=\"2\" color=\"#808080\">Para configurar quais notificações deseja receber do Protestaí, <a href=\"http://www.protestai.com/usuario/edit\" target=\"_blank\"><font color=\"#808080\">clique aqui</font></a>.</font>" +         
				"    </p>" + 	
				"   </td>" + 
				"  </tr>" + 
				"</tbody>" + 
				"</table>" + 
				"<br><br>" + 
				"</div>" + 
				"</div>";
	}
    
}