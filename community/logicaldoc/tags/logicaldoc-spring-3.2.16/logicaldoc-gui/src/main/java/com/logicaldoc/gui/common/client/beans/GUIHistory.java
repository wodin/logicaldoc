package com.logicaldoc.gui.common.client.beans;

import java.io.Serializable;
import java.util.Date;

/**
 * Representation of a single history handled by the GUI
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class GUIHistory implements Serializable {
	private static final long serialVersionUID = 1L;

	private long docId;

	private long folderId;

	private long userId;

	private Date date = new Date();

	private String userName = "";

	private String event = "";

	private String comment = "";

	private String title = null;

	private String version = null;

	private String path = null;

	private int notified = 0;

	private String sessionId = "";

	public long getDocId() {
		return docId;
	}

	public void setDocId(long docId) {
		this.docId = docId;
	}

	public long getFolderId() {
		return folderId;
	}

	public void setFolderId(long folderId) {
		this.folderId = folderId;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getNotified() {
		return notified;
	}

	public void setNotified(int notified) {
		this.notified = notified;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
}