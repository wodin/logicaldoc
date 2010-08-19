package com.logicaldoc.webservice.folder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.security.Folder;

/**
 * Web Service Folder. Useful class to create reporitory Folders.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 5.2
 */
public class WSFolder {

	protected static Log log = LogFactory.getLog(WSFolder.class);

	private long id = 0;

	private String name = "";

	private long parentId = 0;

	private String description = "";

	public static WSFolder fromFolder(Folder folder) {
		WSFolder wsFolder = new WSFolder();
		wsFolder.setId(folder.getId());
		wsFolder.setName(folder.getName());
		wsFolder.setDescription(folder.getDescription());
		wsFolder.setParentId(folder.getParentId());

		return wsFolder;
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

	public long getParentId() {
		return parentId;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}