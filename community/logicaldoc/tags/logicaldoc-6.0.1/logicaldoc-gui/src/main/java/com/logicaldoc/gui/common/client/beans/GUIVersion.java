package com.logicaldoc.gui.common.client.beans;

import java.util.Date;

/**
 * Represents a document version
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class GUIVersion extends GUIDocument {

	private static final long serialVersionUID = 1L;

	private long docId;

	private String comment;

	private String username;

	private Date versionDate = new Date();

	public long getDocId() {
		return docId;
	}

	public void setDocId(long docId) {
		this.docId = docId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Date getVersionDate() {
		return versionDate;
	}

	public void setVersionDate(Date versionDate) {
		this.versionDate = versionDate;
	}
}
