package com.logicaldoc.webservice;


/**
 * Data structure for folder metadata
 * 
 * @author Marco Meschieri
 * @version $Id:$
 * @since 3.0
 */
public class FolderContent {
	private Content[] folder = new Content[]{};

	private Content[] document = new Content[]{};

	private int id;

	private String name = "";

	private int writeable = 0;

	private int parentId;

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

	public int getId() {
		return id;
	}

	public void setId(int id) {
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

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}
	
	public void addFolder(Content content){
		Content[] newFolders=new Content[folder.length+1];
		for(int i=0; i<folder.length; i++)
			newFolders[i]=folder[i];
		newFolders[folder.length]=content;
		folder=newFolders;
	}
	
	public void addDocument(Content content){
		Content[] newDocuments=new Content[document.length+1];
		for(int i=0; i<document.length; i++)
			newDocuments[i]=document[i];
		newDocuments[document.length]=content;
		document=newDocuments;
	}
}