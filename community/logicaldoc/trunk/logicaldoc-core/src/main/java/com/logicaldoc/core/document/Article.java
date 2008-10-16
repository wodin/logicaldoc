package com.logicaldoc.core.document;

import java.util.Date;

import com.logicaldoc.core.PersistentObject;

/**
 * 
 * @author Michael Scholz
 * @author Marco Meschieri
 */
public class Article extends PersistentObject {
	private static final long serialVersionUID = 1L;

	private long docId = 0;

	private String subject = "";

	private String message = "";

	private Date date = new Date();

	private String username = "";

	public Article() {
	}

	public long getDocId() {
		return docId;
	}

	public void setDocId(long docId) {
		this.docId = docId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void reset() {
		setId(0);
		docId = 0;
		subject = "";
		message = "";
		username = "";
		date = new Date();
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}