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

	private String languages = "en, it, de, fr, es";

	private int entries;

	private boolean locked;

	private String includePatters;

	private String excludePatters = "*.exe,*.bin,*.iso";

	private Integer batch = 200;

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

	public String getIncludePatters() {
		return includePatters;
	}

	public void setIncludePatters(String includePatters) {
		this.includePatters = includePatters;
	}

	public String getExcludePatters() {
		return excludePatters;
	}

	public void setExcludePatters(String excludePatters) {
		this.excludePatters = excludePatters;
	}

	public Integer getBatch() {
		return batch;
	}

	public void setBatch(Integer batch) {
		this.batch = batch;
	}
}