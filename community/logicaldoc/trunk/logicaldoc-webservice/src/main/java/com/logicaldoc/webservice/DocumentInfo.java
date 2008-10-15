package com.logicaldoc.webservice;

/**
 * Document's metadata
 * 
 * @author Marco Meschieri
 * @version $Id:$
 * @since 3.0
 */
public class DocumentInfo {
	private long id;

	private String title;

	private int folderId;

	private String folderName;

	private String publisher;

	private String uploadDate;

	private String author;

	private String language;

	private String type;

	private String source;

	private String sourceDate;

	private String coverage;

	private String filename;

	private VersionInfo[] version = new VersionInfo[] {};

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getSourceDate() {
		return sourceDate;
	}

	public String getCoverage() {
		return coverage;
	}

	public void setCoverage(String coverage) {
		this.coverage = coverage;
	}

	public void setSourceDate(String sourceDate) {
		this.sourceDate = sourceDate;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getFolderId() {
		return folderId;
	}

	public void setFolderId(int folderId) {
		this.folderId = folderId;
	}

	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getUploadDate() {
		return uploadDate;
	}

	public void setUploadDate(String uploadDate) {
		this.uploadDate = uploadDate;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public VersionInfo[] getVersion() {
		return version;
	}

	public void setVersion(VersionInfo[] versionInfo) {
		this.version = versionInfo;
	}

	public void addVersion(VersionInfo versionInfo) {
		VersionInfo[] newVersionInfo = new VersionInfo[version.length + 1];
		for (int i = 0; i < version.length; i++)
			newVersionInfo[i] = version[i];
		newVersionInfo[version.length] = versionInfo;
		version = newVersionInfo;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String fileName) {
		this.filename = fileName;
	}
}