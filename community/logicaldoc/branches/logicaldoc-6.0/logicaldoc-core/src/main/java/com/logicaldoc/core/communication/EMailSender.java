package com.logicaldoc.core.communication;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.apache.commons.lang.StringUtils;

/**
 * SMTP E-Mail sender service
 * 
 * @author Michael Scholz
 * @author Matteo Caruso - Logical Objects
 */
public class EMailSender {

	public static final int SECURITY_NONE = 0;

	public static final int SECURITY_TLS_IF_AVAILABLE = 1;

	public static final int SECURITY_TLS = 2;

	public static final int SECURITY_SSL = 3;

	private String host = "localhost";

	private String sender = "logicaldoc@acme.com";

	private String username = "";

	private String password = "";

	private int port = 25;

	private boolean authEncripted = false;

	private int connectionSecurity = SECURITY_NONE;

	public EMailSender() {
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * This method sends an email using the smtp-protocol. The email can be a
	 * simple mail or a multipart mail containing up to 5 attachments.
	 * 
	 * @param account E-Mail account of the sender.
	 * @param email E-Mail which should be sent.
	 * @throws Exception
	 */
	public void send(EMail email) throws Exception {
		Properties props = new Properties();
		if (!StringUtils.isEmpty(username))
			props.put("mail.smtp.auth", "true");

		if (authEncripted) {
			// The 'smtps' protocol must be used
			props.put("mail.transport.protocol", "smtps");
			props.put("mail.smtps.host", host);
			if (connectionSecurity == SECURITY_TLS_IF_AVAILABLE)
				props.put("mail.smtps.starttls.enable", "true");
			if (connectionSecurity == SECURITY_TLS)
				props.put("mail.smtps.starttls.required", "true");
			if (connectionSecurity == SECURITY_SSL) {
				// Necessary property to send e-mails with SSL
				props.put("mail.smtps.starttls.enable", "true");
				props.put("mail.smtps.ssl.enable", "true");
			}
		} else {
			props.put("mail.transport.protocol", "smtp");
			props.put("mail.smtp.host", host);
			if (connectionSecurity == SECURITY_TLS_IF_AVAILABLE)
				props.put("mail.smtp.starttls.enable", "true");
			if (connectionSecurity == SECURITY_TLS)
				props.put("mail.smtp.starttls.required", "true");
			if (connectionSecurity == SECURITY_SSL) {
				// Necessary property to send e-mails with SSL
				props.put("mail.smtp.starttls.enable", "true");
				props.put("mail.smtp.ssl.enable", "true");
			}
		}

		Session sess = Session.getDefaultInstance(props);
		MimeMessage message = new MimeMessage(sess);
		String frm = email.getAuthorAddress();
		if (StringUtils.isEmpty(frm))
			frm = sender;
		InternetAddress from = new InternetAddress(frm);
		InternetAddress[] to = email.getAddresses();
		InternetAddress[] cc = email.getAddressesCC();
		InternetAddress[] bcc = email.getAddressesBCC();
		message.setFrom(from);
		message.setRecipients(javax.mail.Message.RecipientType.TO, to);
		if (cc.length > 0)
			message.setRecipients(javax.mail.Message.RecipientType.CC, cc);
		if (bcc.length > 0)
			message.setRecipients(javax.mail.Message.RecipientType.BCC, bcc);
		message.setSubject(email.getSubject(), "UTF-8");

		MimeBodyPart body = new MimeBodyPart();
		body.setContent(email.getMessageText(), "text/plain");
		body.setText(email.getMessageText(), "UTF-8");

		Multipart mpMessage = new MimeMultipart();
		mpMessage.addBodyPart(body);

		EMailAttachment att = email.getAttachment(2);
		if (att != null) {
			DataSource fdSource = new FileDataSource(att.getFile());
			DataHandler fdHandler = new DataHandler(fdSource);
			MimeBodyPart part = new MimeBodyPart();
			part.setDataHandler(fdHandler);
			String fileName = MimeUtility.encodeText(att.getFileName(), "UTF-8", null);
			part.setFileName(fileName);
			mpMessage.addBodyPart(part);
		}
		message.setContent(mpMessage);

		Transport trans = null;
		if (authEncripted)
			trans = sess.getTransport("smtps");
		else
			trans = sess.getTransport("smtp");

		if (StringUtils.isEmpty(username)) {
			trans.connect(host, port, null, null);
		} else {
			trans.connect(host, port, username, password);
		}

		Address[] adr = message.getAllRecipients();
		trans.sendMessage(message, adr);
		trans.close();
	}

	public boolean isAuthEncripted() {
		return authEncripted;
	}

	public void setAuthEncripted(boolean authEncripted) {
		this.authEncripted = authEncripted;
	}

	public int getConnectionSecurity() {
		return connectionSecurity;
	}

	public void setConnectionSecurity(int connectionSecurity) {
		this.connectionSecurity = connectionSecurity;
	}
}