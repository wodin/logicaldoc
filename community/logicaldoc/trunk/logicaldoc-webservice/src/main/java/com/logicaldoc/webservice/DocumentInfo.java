package com.logicaldoc.webservice;

/**
 * Document's metadata
 * 
 * @author Marco Meschieri
 * @version $Id:$
 * @since 3.0
 */
public class DocumentInfo {
	private int id;

	private String name;

	private int writeable = 0;

	private int parentId;

	private String parentName;

	private String uploadUser;

	private String uploadDate;

	private String author;

	private String language;

	private String type;

	private String source;

	private String sourceDate;

	private String coverage;
	
	private String filename;

	private VersionInfo[] version = new VersionInfo[] {};

	public int getId() {
		return id;
	}

	public void setId(int id) {
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

	public String getUploadUser() {
		return uploadUser;
	}

	public void setUploadUser(String uploadUser) {
		this.uploadUser = uploadUser;
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