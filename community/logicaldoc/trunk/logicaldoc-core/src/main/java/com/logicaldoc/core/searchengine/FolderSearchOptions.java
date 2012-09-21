package com.logicaldoc.core.searchengine;

import java.util.Date;

/**
 * Search options specialization for the folder search.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.4
 */
public class FolderSearchOptions extends SearchOptions {

	private static final long serialVersionUID = 1L;

	private String folderName = null;

	private String folderDescription = null;

	private Date creationFrom = null;

	private Date creationTo = null;

	public FolderSearchOptions() {
		super(SearchOptions.TYPE_FOLDERS);
	}

	public Date getCreationTo() {
		return creationTo;
	}

	public void setCreationTo(Date creationTo) {
		this.creationTo = creationTo;
	}

	public Date getCreationFrom() {
		return creationFrom;
	}

	public void setCreationFrom(Date creationFrom) {
		this.creationFrom = creationFrom;
	}

	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

	public String getFolderDescription() {
		return folderDescription;
	}

	public void setFolderDescription(String folderDescription) {
		this.folderDescription = folderDescription;
	}
}
