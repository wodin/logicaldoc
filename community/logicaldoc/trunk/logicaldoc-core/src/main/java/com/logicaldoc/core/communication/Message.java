package com.logicaldoc.core.communication;

import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import com.logicaldoc.core.PersistentObject;

/**
 * This is the parent class for email and system message.
 * 
 * @author Michael Scholz
 * @author Marco Meschieri
 * @version 1.0
 */
public abstract class Message extends PersistentObject {

	public final static int TYPE_SYSTEM = 0;

	public final static int TYPE_NOTIFICATION = 1;

	private String messageText = "";

	private String author = "";

	private String subject = "";

	// The message creation date
	private Date sentDate = new Date();

	private int type = TYPE_SYSTEM;

	protected Set<Recipient> recipients = new HashSet<Recipient>();

	// The locale in which the message is written
	protected Locale locale;

	public String getMessageText() {
		return messageText;
	}

	public String getAuthor() {
		return author;
	}

	public String getSubject() {
		return subject;
	}

	public void setMessageText(String mess) {
		messageText = mess;
	}

	public void setAuthor(String auth) {
		author = auth;
	}

	public void setSubject(String subj) {
		subject = subj;
	}

	public Date getSentDate() {
		return sentDate;
	}

	public void setSentDate(Date sentDate) {
		this.sentDate = sentDate;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Recipient getRecipient(String name) {
		for (Recipient recipient : recipients) {
			if (name.equals(recipient.getName()))
				return recipient;
		}
		return null;
	}

	public boolean wasReadBy(String name) {
		Recipient rec = getRecipient(name);
		if (rec != null)
			return rec.getRead() == 1;
		else
			return false;
	}

	public Set<Recipient> getRecipients() {
		return recipients;
	}

	public void setRecipients(Set<Recipient> recipients) {
		this.recipients = recipients;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}
}
