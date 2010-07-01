package com.logicaldoc.gui.common.client.beans;

import java.io.Serializable;

/**
 * Genaeric message to the user
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class GUIMessage implements Serializable {

	private static final long serialVersionUID = 1L;

	long id;
	
	private String message;

	private String url;

	private int priority;

	private String recipient;

	private String subject;

	private boolean confirmation = false;

	private Integer validity;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public boolean isConfirmation() {
		return confirmation;
	}

	public void setConfirmation(boolean confirmation) {
		this.confirmation = confirmation;
	}

	public Integer getValidity() {
		return validity;
	}

	public void setValidity(Integer validity) {
		this.validity = validity;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
}