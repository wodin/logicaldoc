package com.logicaldoc.gui.common.client.beans;

import java.io.Serializable;

public class GUIParameter implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;

	private String value;

	public GUIParameter() {
	}

	public GUIParameter(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
