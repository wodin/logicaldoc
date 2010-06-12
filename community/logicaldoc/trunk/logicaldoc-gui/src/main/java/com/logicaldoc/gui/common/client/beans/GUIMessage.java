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

	private String message;

	private String url;

	private int priority;

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
}