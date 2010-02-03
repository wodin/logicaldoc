package com.logicaldoc.webservice;

/**
 * Document's metadata
 * 
 * @author Marco Meschieri - Logical Object
 * @since 3.0
 */
public class DocumentInfo {
	private long id;

	private String title;

	private long folderId;

	private String folderName;

	private String publisher;

	private String creator;

	private String uploadDate;

	private String author;

	private String language;

	private String type;

	private String source;

	private String sourceDate;

	private String coverage;

	private String filename;

	private String templateName;

	private Long templateId;

	private Attribute[] extendedAttribute;

	private String[] tags;

	private VersionInfo[] version = new VersionInfo[] {};

	private String customId;

	private String sourceId;

	private String object;

	private String recipient;

	private Long docRef;

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

	public long getFolderId() {
		return folderId;
	}

	public void setFolderId(long folderId) {
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

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public Long getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}

	public Attribute[] getExtendedAttribute() {
		return extendedAttribute;
	}

	public void setExtendedAttribute(Attribute[] extendedAttribute) {
		this.extendedAttribute = extendedAttribute;
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

	public String getCustomId() {
		return customId;
	}

	public void setCustomId(String customId) {
		this.customId = customId;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String[] getTags() {
		return tags;
	}

	public void setTags(String[] tags) {
		this.tags = tags;
	}

	public Long getDocRef() {
		return docRef;
	}

	public void setDocRef(Long docRef) {
		this.docRef = docRef;
	}
}