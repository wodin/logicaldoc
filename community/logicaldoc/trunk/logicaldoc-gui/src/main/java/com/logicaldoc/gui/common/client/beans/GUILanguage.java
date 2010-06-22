package com.logicaldoc.gui.common.client.beans;

import java.io.Serializable;

/**
 * A locale representation
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class GUILanguage implements Serializable {
	private static final long serialVersionUID = 1L;

	private String code;

	private String displayName;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
}