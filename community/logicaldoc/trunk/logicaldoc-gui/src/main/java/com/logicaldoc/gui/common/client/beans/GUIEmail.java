package com.logicaldoc.gui.common.client.beans;

import java.io.Serializable;

/**
 * GUI representation of an e-mail to be sent
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class GUIEmail implements Serializable {

	private static final long serialVersionUID = 1L;

	private String recipients;

	private String cc;

	private String object;

	private boolean sendAdTicket = false;

	private String message;

	private GUIUser user;

	private long docId;

	public String getRecipients() {
		return recipients;
	}

	public void setRecipients(String recipients) {
		this.recipients = recipients;
	}

	public String getCc() {
		return cc;
	}

	public void setCc(String cc) {
		this.cc = cc;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

	public boolean isSendAdTicket() {
		return sendAdTicket;
	}

	public void setSendAdTicket(boolean sendAdTicket) {
		this.sendAdTicket = sendAdTicket;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public GUIUser getUser() {
		return user;
	}

	public void setUser(GUIUser user) {
		this.user = user;
	}

	public long getDocId() {
		return docId;
	}

	public void setDocId(long docId) {
		this.docId = docId;
	}
}
