package com.logicaldoc.webservice.document;

import com.logicaldoc.core.document.AbstractHistory;
import com.logicaldoc.webservice.AbstractService;

/**
 * Web Service History.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.5
 */
public class WSHistory {
	protected Long docId;

	private long folderId;

	private long userId;

	private String date;

	private String userName = "";

	private String event = "";

	private String comment = "";

	private String title = null;

	private String version = null;

	private String path = null;

	private String sessionId = "";

	private String filename = null;

	public Long getDocId() {
		return docId;
	}

	public void setDocId(Long docId) {
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

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public static WSHistory fromHistory(AbstractHistory history) {
		WSHistory wsHist = new WSHistory();

		try {
			wsHist.setDocId(history.getDocId());
			wsHist.setDate(AbstractService.convertDateToString(history.getDate()));
			wsHist.setComment(history.getEvent());
			wsHist.setFilename(history.getFilename());
			wsHist.setEvent(history.getEvent());
			wsHist.setFolderId(history.getFolderId());
			wsHist.setPath(history.getPath());
			wsHist.setSessionId(history.getSessionId());
			wsHist.setTitle(history.getTitle());
			wsHist.setUserId(history.getUserId());
			wsHist.setUserName(history.getUserName());
			wsHist.setVersion(history.getVersion());
		} catch (Throwable e) {

		}
		return wsHist;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
}
