package com.logicaldoc.core.communication;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MessageTest {
	public static void main(String[] args) throws MessagingException {
		Properties props = new Properties();
//		props.put("mail.smtp.auth", "true");
//		props.put("mail.smtp.starttls.enable", "true");
//		props.put("mail.transport.protocol", "smtps");
//		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//		props.put("mail.smtp.socketFactory.fallback", "false");
//		props.put("mail.smtp.auth", "true");
//		props.put("mail.smtp.debug", "true");
//		// props.put("mail.smtp.host", "smtp.office365.com");

//		props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//		props.setProperty("mail.smtp.socketFactory.fallback", "false");
//		props.setProperty("mail.smtp.port", "587");
//		//props.setProperty("mail.smtp.socketFactory.port", "587");
//		props.put("mail.smtp.host", "smtp.office365.com");
//		props.put("mail.smtp.starttls.enable", "true");
//		props.put("mail.smtp.auth", "true");

		
		 props.put("mail.transport.protocol", "smtp");
		 props.put("mail.smtp.starttls.enable", "true");
		 props.put("mail.smtp.port", 587);
		 props.put("mail.smtp.host", "m.outlook.com");
		 props.put("mail.smtp.auth", "true");
		
		Session sess = Session.getDefaultInstance(props, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("ConfigManagAdmin@TECH-MAH.COM", "MEMTecMKn0x2014");
			}
		});

		MimeMessage message = new MimeMessage(sess);
		message.setSubject("test");
		message.setFrom("ConfigManagAdmin@TECH-MAH.COM");
		message.setRecipient(RecipientType.TO, new InternetAddress("support@logicaldoc.com"));
		message.setContent("test", "text/html");
		
		//Session sess = Session.getDefaultInstance(props);
		Transport trans = sess.getTransport("smtp");
		trans.connect("m.outlook.com", 587, "ConfigManagAdmin@TECH-MAH.COM", "MEMTecMKn0x2014");
		//Transport.send(message);

		System.out.println("message sent");
	}
}
