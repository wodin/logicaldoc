package com.logicaldoc.webservice;

/**
 * Data structure for folder metadata
 * 
 * @author Marco Meschieri - Logical Object
 * @since 3.0
 */
@Deprecated
public class FolderContent {
	private Content[] folder = new Content[] {};

	private Content[] document = new Content[] {};

	private long id;

	private String name = "";

	private int writeable = 0;

	private long parentId;

	private String parentName = "";

	public Content[] getFolder() {
		return folder;
	}

	public void setFolder(Content[] folder) {
		this.folder = folder;
	}

	public Content[] getDocument() {
		return document;
	}

	public void setDocument(Content[] document) {
		this.document = document;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getWriteable() {
		return writeable;
	}

	public void setWriteable(int writeable) {
		this.writeable = writeable;
	}

	public long getParentId() {
		return parentId;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public void addFolder(Content content) {
		Content[] newFolders = new Content[folder.length + 1];
		for (int i = 0; i < folder.length; i++)
			newFolders[i] = folder[i];
		newFolders[folder.length] = content;
		folder = newFolders;
	}

	public void addDocument(Content content) {
		Content[] newDocuments = new Content[document.length + 1];
		for (int i = 0; i < document.length; i++)
			newDocuments[i] = document[i];
		newDocuments[document.length] = content;
		document = newDocuments;
	}
}