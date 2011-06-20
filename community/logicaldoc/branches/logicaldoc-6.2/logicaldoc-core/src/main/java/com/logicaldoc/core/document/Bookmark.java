package com.logicaldoc.core.document;

import com.logicaldoc.core.PersistentObject;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.security.dao.FolderDAO;
import com.logicaldoc.core.util.IconSelector;
import com.logicaldoc.util.Context;

/**
 * A bookmark over a document
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 5.2
 * 
 */
public class Bookmark extends PersistentObject {

	private long userId;

	private long docId;

	private String title = "";

	private String description = "";

	private int position = 0;

	// The document file extension
	private String fileType;

	public Bookmark() {
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getDocId() {
		return docId;
	}

	public void setDocId(long docId) {
		this.docId = docId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	/**
	 * The icon for the document associated to the subscription
	 */
	public String getIcon() {
		String icon = IconSelector.selectIcon("");
		try {
			icon = IconSelector.selectIcon(getFileType());
		} catch (Exception e) {
		}
		return icon;
	}

	/**
	 * The path of the document associated to the bookmark.
	 */
	public String getPath() {
		FolderDAO folderDao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		return folderDao.computePathExtended(docDao.findById(docId).getFolder().getId());
	}
}
