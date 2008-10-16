package com.logicaldoc.core.communication;

import java.util.Date;

import com.logicaldoc.core.PersistentObject;

/**
 * This is the parent class for email and system message.
 * 
 * @author Michael Scholz
 * @author Marco Meschieri
 * @version 1.0
 */
public abstract class Message extends PersistentObject {

	private String messageText = "";

	private String author = "";

	private String subject = "";

	private String sentDate = "";

	private int read = 0; // 0 - false; 1 - true

	public String getMessageText() {
		return messageText;
	}

	public String getAuthor() {
		return author;
	}

	public String getSubject() {
		return subject;
	}

	public String getSentDate() {
		return sentDate;
	}

	public int getRead() {
		return read;
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

	public void setSentDate(String dat) {
		sentDate = dat;
	}

	public void setRead(int rd) {
		read = rd;
	}

	public Date getSentDateAsDate() {
		Date date = new Date();
		try {
			date.setTime(Long.parseLong(getSentDate()));
		} catch (Exception e) {
		}
		return date;

	}
}
