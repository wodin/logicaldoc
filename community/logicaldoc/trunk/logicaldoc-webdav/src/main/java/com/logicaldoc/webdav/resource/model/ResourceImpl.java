package com.logicaldoc.webdav.resource.model;

import java.io.InputStream;
import java.util.Date;

import com.logicaldoc.webdav.session.DavSession;

/**
 * @see Resource
 * 
 * @author Sebastian Wenzky
 */
public class ResourceImpl implements Resource {

	private String id;

	private String name;

	private Long contentLength;

	private boolean isFolder;

	private boolean isLocked;

	private String path;

	private InputStream is;

	private long personRequest;

	private boolean isCheckedOut;

	private String versionLabel;

	private Date lastModified;

	private Date versionDate;

	private String author;

	private String comment;

	private Date creationDate;

	private boolean writeEnabled;

	private boolean deleteEnabled;

	private boolean renameEnabled;

	private boolean addChildEnabled;

	DavSession session;

	public Long getContentLength() {
		return contentLength;
	}

	public String getName() {
		return name;
	}

	public void setContentLength(Long contentLength) {
		this.contentLength = contentLength;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void isFolder(boolean isFolder) {
		this.isFolder = isFolder;
	}

	public boolean isFolder() {
		return this.isFolder;
	}

	public void isLocked(boolean isLocked) {
		this.isLocked = isLocked;
	}

	public boolean isLocked() {
		return this.isLocked;
	}

	public String getPath() {
		return this.path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getID() {
		return this.id;
	}

	public void setID(String id) {
		this.id = id;
	}

	@Override
	public InputStream getInputStream() {
		return this.is;
	}

	@Override
	public void setInputStream(InputStream is) {
		this.is = is;
	}

	@Override
	public long getRequestedPerson() {
		return this.personRequest;
	}

	@Override
	public void setRequestedPerson(long id) {
		this.personRequest = id;
	}

	@Override
	public boolean getIsCheckedOut() {
		return this.isCheckedOut;
	}

	@Override
	public void setIsCheckedOut(boolean isCheckedOut) {
		this.isCheckedOut = isCheckedOut;
	}

	@Override
	public String getVersionLabel() {
		return this.versionLabel;
	}

	@Override
	public void setVersionLabel(String versionLabel) {
		this.versionLabel = versionLabel;
	}

	public Date getLastModified() {
		return this.lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	@Override
	public Date getVersionDate() {
		return this.versionDate;
	}

	@Override
	public void setVersionDate(Date date) {
		this.versionDate = date;
	}

	@Override
	public void setAuthor(String author) {
		this.author = author;
	}

	@Override
	public String getAuthor() {
		return this.author;
	}

	@Override
	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public String getComment() {
		return this.comment;
	}

	public void setCreationDate(Date creation) {
		this.creationDate = creation;
	}

	public Date getCreationDate() {
		return this.creationDate;
	}

	public boolean isDeleteEnabled() {
		return this.deleteEnabled;
	}

	public boolean isRenameEnabled() {
		return this.renameEnabled;
	}

	public void setDeleteEnabled(boolean deleteEnabled) {
		this.deleteEnabled = deleteEnabled;
	}

	public void setRenameEnabled(boolean renameEnabled) {
		this.renameEnabled = renameEnabled;
	}

	public boolean isWriteEnabled() {
		return writeEnabled;
	}

	public void setWriteEnabled(boolean writeEnabled) {
		this.writeEnabled = writeEnabled;
	}

	public boolean isAddChildEnabled() {
		return this.addChildEnabled;
	}

	public void setAddChildEnabled(boolean addChildEnabled) {
		this.addChildEnabled = addChildEnabled;
	}

	public DavSession getSession() {
		return session;
	}

	public void setSession(DavSession session) {
		this.session = session;
	}

}
