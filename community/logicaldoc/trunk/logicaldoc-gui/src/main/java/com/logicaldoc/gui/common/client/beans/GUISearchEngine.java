package com.logicaldoc.gui.common.client.beans;

import java.io.Serializable;

/**
 * Representation of a search engine handled by the GUI
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class GUISearchEngine implements Serializable {

	private static final long serialVersionUID = 1L;

	private String languages = "english, italian, german, french, spanish";

	private int entries;

	private boolean locked;

	public String getLanguages() {
		return languages;
	}

	public void setLanguages(String languages) {
		this.languages = languages;
	}

	public int getEntries() {
		return entries;
	}

	public void setEntries(int entries) {
		this.entries = entries;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}
}