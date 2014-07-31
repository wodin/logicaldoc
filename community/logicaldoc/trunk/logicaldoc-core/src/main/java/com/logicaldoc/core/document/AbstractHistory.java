package com.logicaldoc.core.document;

import java.util.Date;

import com.logicaldoc.core.PersistentObject;
import com.logicaldoc.core.security.User;

/**
 * Superclass for history entries
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 5.0
 */
public class AbstractHistory extends PersistentObject {
	protected Long docId;

	private long folderId;

	private long userId;

	private Date date = new Date();

	private String userName = "";

	private String event = "";

	private String comment = "";

	private String title = null;

	private String titleOld = null;

	private String version = null;

	private String path = null;

	private String pathOld = null;

	private int notified = 0;

	private String sessionId = "";

	private int _new = 1;

	private String filename = null;

	private String filenameOld = null;

	/**
	 * Used when storing a document
	 */
	private String file = null;

	/**
	 * Used as convenience to store the name of the tenant
	 */
	private String tenant = null;

	// Not persistent
	private User user;

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

	public AbstractHistory() {
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	/**
	 * @return Returns the date.
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param date The date to set.
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @return Returns the docId.
	 */
	public Long getDocId() {
		return docId;
	}

	/**
	 * @param docId The docId to set.
	 */
	public void setDocId(Long docId) {
		this.docId = docId;
	}

	/**
	 * @return Returns the event.
	 */
	public String getEvent() {
		return event;
	}

	/**
	 * @param event The event to set.
	 */
	public void setEvent(String event) {
		this.event = event;
	}

	/**
	 * @return Returns the userName.
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName The userName to set.
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public long getFolderId() {
		return folderId;
	}

	public void setFolderId(long folderId) {
		this.folderId = folderId;
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

	public User getUser() {
		return user;
	}

	/**
	 * This setter also sets the userId and userName
	 */
	public void setUser(User user) {
		this.user = user;
		if (user != null) {
			setUserId(user.getId());
			setUserName(user.getFullName());
			setTenantId(user.getTenantId());
		}
	}

	public int getNew() {
		return _new;
	}

	public void setNew(int _new) {
		this._new = _new;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getTitleOld() {
		return titleOld;
	}

	public void setTitleOld(String titleOld) {
		this.titleOld = titleOld;
	}

	public String getFilenameOld() {
		return filenameOld;
	}

	public void setFilenameOld(String filenameOld) {
		this.filenameOld = filenameOld;
	}

	public String getPathOld() {
		return pathOld;
	}

	public void setPathOld(String pathOld) {
		this.pathOld = pathOld;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getTenant() {
		return tenant;
	}

	public void setTenant(String tenant) {
		this.tenant = tenant;
	}
}