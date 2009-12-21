package com.logicaldoc.core.document;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import com.logicaldoc.core.ExtensibleObject;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.util.IconSelector;
import com.logicaldoc.util.LocaleUtil;

/**
 * The Document is the central entity of LogicalDOC. A Document is a persistent
 * business object and represents metadata over a single file stored into the
 * DMS.
 * <p>
 * Each document has one or more Versions. The most recent version is the one
 * used as default when we refer to a Document, but all previous versions are
 * accessible from the history even if the are not indexed.
 * <p>
 * Each Version carries out two main informations, the version code itself that
 * is called simply 'version', and the file version, called 'fileVersion'. The
 * first identified the Version itself while the second refers to the file
 * content. In general not all updates to a document involves the upload of a
 * new file.
 * 
 * A Document is written in a single language, this language defines the
 * full-text index in which the document's content will be stored.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.5
 */
public abstract class AbstractDocument extends ExtensibleObject {

	public static final int DOC_UNLOCKED = 0;

	public static final int DOC_CHECKED_OUT = 1;
	
	public static final int DOC_LOCKED = 2;

	public static final int EXPORT_UNLOCKED = 0;
	
	public static final int EXPORT_LOCKED = 1;
	
	private long fileSize = 0;

	/**
	 * Whether document is checked out,locked or unlocked
	 * 
	 * @see Document#DOC_UNLOCKED
	 * @see Document#DOC_CHECKED_OUT
	 * @see Document#DOC_LOCKED
	 */
	private int status = DOC_UNLOCKED;

	private int exportStatus = EXPORT_UNLOCKED;
	
	private String title;

	private String version;

	private String exportVersion;
	
	private String fileVersion;

	private Date date;

	private String publisher;

	private long publisherId;

	private String creator;

	private long creatorId;

	private String type;

	private Long lockUserId;

	private String source;

	private String sourceAuthor;

	private Date sourceDate;

	private String sourceId;

	private String object;

	private Date creation = new Date();

	private String sourceType;

	private String coverage;

	private String language;

	private String fileName;

	private int indexed = 0;

	private int signed = 0;

	private Set<String> tags = new HashSet<String>();

	private Menu folder;

	private DocumentTemplate template;

	private String customId;

	private int immutable = 0;

	private String digest;

	private String recipient;

	private String exportName;
	
	private Long exportId = null;
	
	public AbstractDocument() {
		super();
	}

	/**
	 * The document status
	 * 
	 * @see Document#DOC_UNLOCKED
	 * @see Document#DOC_CHECKED_OUT
	 * @see Document#DOC_LOCKED
	 */
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * In general a document has a title. This is not the file name, but
	 * sometimes the filename is used as title.
	 */
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * The working version (the most recent version)
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Iterates over the versions searching for the specified id
	 * 
	 * @param id The version id
	 * @return The found version
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * The document's last publication date. This date is altered by checkin
	 * operations.
	 */
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * The user id of the user that published this document
	 */
	public long getPublisherId() {
		return publisherId;
	}

	public void setPublisherId(long publisherId) {
		this.publisherId = publisherId;
	}

	/**
	 * The username that published this document
	 */
	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	/**
	 * The document type
	 */
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	/**
	 * The id of the user that locked this document and that currently locks it
	 */
	public Long getLockUserId() {
		return lockUserId;
	}

	public void setLockUserId(Long lockUserId) {
		this.lockUserId = lockUserId;
	}

	/**
	 * Indication of the document's source
	 */
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * The author of the document (form document's metadata)
	 */
	public String getSourceAuthor() {
		return sourceAuthor;
	}

	public void setSourceAuthor(String sourceAuthor) {
		this.sourceAuthor = sourceAuthor;
	}

	/**
	 * The date of the document (form document's metadata)
	 */
	public Date getSourceDate() {
		return sourceDate;
	}

	public void setSourceDate(Date sourceDate) {
		this.sourceDate = sourceDate;
	}

	/**
	 * The type of the document (form document's metadata)
	 */
	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	/**
	 * The coverage of the document (form document's metadata)
	 */
	public String getCoverage() {
		return coverage;
	}

	public void setCoverage(String coverage) {
		this.coverage = coverage;
	}

	/**
	 * The document's language. This attribute is very important because of it
	 * is used to select the right full-text index.
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * @see Document#setLanguage(java.lang.String)
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * The set of tags for this document.
	 */
	public Set<String> getTags() {
		return tags;
	}

	public void setTags(Set<String> tags) {
		this.tags = tags;
	}

	public int getIndexed() {
		return indexed;
	}

	public void setIndexed(int indexed) {
		this.indexed = indexed;
	}

	public String getTagsString() {
		StringBuffer sb = new StringBuffer();
		Iterator<String> iter = tags.iterator();
		boolean start = true;

		while (iter.hasNext()) {
			String words = iter.next();

			if (!start) {
				sb.append(", ");
			} else {
				start = false;
			}

			sb.append(words);
		}

		return sb.toString();
	}

	/**
	 * The original file name
	 */
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * The icon for this document, it may be kept from file name extension
	 */
	public String getIcon() {
		String icon = IconSelector.selectIcon("");
		try {
			String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
			icon = IconSelector.selectIcon(extension);
		} catch (Exception e) {
		}
		return icon;
	}

	/**
	 * The document's file size expressed in bytes
	 */
	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	/**
	 * Retrieve the folder owning this document
	 */
	public Menu getFolder() {
		return folder;
	}

	public void setFolder(Menu folder) {
		this.folder = folder;
	}

	public void addTag(String word) {
		tags.add(word);
	}

	public void clearTags() {
		tags.clear();
		tags = new HashSet<String>();
	}

	public String getPath() {
		return (getFolder().getPath() + (getFolder().getPath().endsWith("/") ? "" : "/") + getFolder().getId())
				.replaceAll("//", "/");
	}

	public String getFileExtension() {
		return getFileName().substring(getFileName().lastIndexOf(".") + 1).toLowerCase();
	}

	public DocumentTemplate getTemplate() {
		return template;
	}

	public void setTemplate(DocumentTemplate template) {
		this.template = template;
	}

	/**
	 * The document's creation date
	 */
	public Date getCreation() {
		return creation;
	}

	public void setCreation(Date creation) {
		this.creation = creation;
	}

	/**
	 * Each document can be identified with a custom identifier
	 */
	public String getCustomId() {
		return customId;
	}

	public void setCustomId(String customId) {
		this.customId = customId;
	}

	/**
	 * Defines if the document is immutable
	 */
	public int getImmutable() {
		return immutable;
	}

	/**
	 * <b>NOTE:</b> Once this flag is set to 1, it cannot be reverted to 0
	 */
	public void setImmutable(int immutable) {
		if (this.immutable == 0)
			this.immutable = immutable;
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

	/**
	 * The document's digest
	 */
	public String getDigest() {
		return digest;
	}

	public void setDigest(String digest) {
		this.digest = digest;
	}

	/**
	 * The document's recipient
	 */
	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	/**
	 * Return 1 if the document was signed
	 */
	public int getSigned() {
		return signed;
	}

	public void setSigned(int signed) {
		this.signed = signed;
	}

	/**
	 * The working file version. Sometimes the version of the document may
	 * differ from the file versions. In fact if a new version differs from
	 * metadata only, we it have to reference the old file.
	 */
	public String getFileVersion() {
		return fileVersion;
	}

	public void setFileVersion(String fileVersion) {
		this.fileVersion = fileVersion;
	}

	public long getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(long creatorId) {
		this.creatorId = creatorId;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}
	
	public Locale getLocale(){
		return LocaleUtil.toLocale(getLanguage());
	}
	
	public void setLocale(Locale locale){
		setLanguage(locale.toString());
	}

	/**
	 * The document export status
	 * 
	 * @see Document#EXPORT_UNLOCKED
	 * @see Document#EXPORT_LOCKED
	 */
	public int getExportStatus() {
		return exportStatus;
	}

	public void setExportStatus(int exportStatus) {
		this.exportStatus = exportStatus;
	}

	/**
	 * The last exported version
	 */
	public String getExportVersion() {
		return exportVersion;
	}

	public void setExportVersion(String exportVersion) {
		this.exportVersion = exportVersion;
	}

	/**
	 * The last archive name in which the document was exported 
	 */
	public String getExportName() {
		return exportName;
	}

	public void setExportName(String exportName) {
		this.exportName = exportName;
	}

	/**
	 * The last archive in which the document was exported 
	 */
	public Long getExportId() {
		return exportId;
	}

	public void setExportId(Long exportId) {
		this.exportId = exportId;
	}
}