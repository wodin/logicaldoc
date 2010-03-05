package com.logicaldoc.web.admin;

import java.io.Serializable;
import java.util.UUID;

public class NotificationUser implements Serializable {
	private static final long serialVersionUID = -4093828529136586174L;

	private String id;

	private String value;

	public NotificationUser() {
		this.id = UUID.randomUUID().toString();
		this.value = "";
	}

	public NotificationUser(String value) {
		this.id = UUID.randomUUID().toString();
		this.value = value;
	}
	
	public NotificationUser(String id, String value) {
		this.id = id;
		this.value = value;
	}

	public String getId() {
		return id;
	}

	public void setId(String name) {
		this.id = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
