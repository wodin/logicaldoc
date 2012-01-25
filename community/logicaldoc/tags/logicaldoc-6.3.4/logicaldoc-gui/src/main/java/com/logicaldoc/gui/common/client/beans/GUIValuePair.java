package com.logicaldoc.gui.common.client.beans;

import java.io.Serializable;

/**
 * General purpose value bean
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class GUIValuePair implements Serializable {
	private static final long serialVersionUID = 1L;

	private String code;

	private String value;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}