package com.logicaldoc.core.document;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.logicaldoc.core.ExtensibleObject;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.util.IconSelector;

/**
 * The Document is the central entity of LogicalDOC. A Document is a persistent
 * business object and represents metadata over a single file stored into the
 * DMS.
 * <p>
 * Each document has one or more Versions. The most recent version is the one
 * used as default when we refer to a Document, but all previous versions are
 * accessible from the history even if the are not indexed.
 * <p>
 * A Document is written in a single language, this language defines the
 * full-text index in which the document's content will be stored.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 1.0
 */
public class Document extends ExtensibleObject {
	public static final int DOC_CHECKED_IN = 0;

	public static final int DOC_CHECKED_OUT = 1;

	private long fileSize = 0;

	/**
	 * Whether document is checked in or out
	 * 
	 * @see Document#DOC_CHECKED_IN
	 * @see Document#DOC_CHECKED_OUT
	 */
	private int status = DOC_CHECKED_IN;

	private String title;

	private String version;

	private Date date;

	private String publisher;

	private String type;

	private String checkoutUser;

	private String source;

	private String sourceAuthor;

	private Date sourceDate;

	private String sourceType;

	private String coverage;

	private String language;

	private String fileName;

	private int indexed = 0;

	private Set<String> keywords = new HashSet<String>();

	private Set<Version> versions = new HashSet<Version>();

	private Menu folder;

	private DocumentTemplate template;

	public Document() {
	}

	/**
	 * The document status
	 * 
	 * @see Document#DOC_CHECKED_IN
	 * @see Document#DOC_CHECKED_OUT
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
	 * The document creation date
	 */
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
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
	 * The username that checked-out this document and that currently locks it
	 */
	public String getCheckoutUser() {
		return checkoutUser;
	}

	public void setCheckoutUser(String checkoutUser) {
		this.checkoutUser = checkoutUser;
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
	 * The set of keywords for this document. Each keyword is a tag on this
	 * document.
	 */
	public Set<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(Set<String> keywords) {
		this.keywords = keywords;
	}

	/**
	 * The set of all Versions
	 */
	public Set<Version> getVersions() {
		return versions;
	}

	public void setVersions(Set<Version> versions) {
		this.versions = versions;
	}

	public int getIndexed() {
		return indexed;
	}

	public void setIndexed(int indexed) {
		this.indexed = indexed;
	}

	public String getKeywordsString() {
		StringBuffer sb = new StringBuffer();
		Iterator<String> iter = keywords.iterator();
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

	public Version getVersion(String id) {
		for (Version version : versions) {
			if (version.getVersion().equals(id))
				return version;
		}
		return null;
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

	public void addKeyword(String word) {
		keywords.add(word);
	}

	public void addVersion(Version vers) {
		versions.add(vers);
	}

	public void clearKeywords() {
		keywords.clear();
		keywords = new HashSet<String>();
	}

	public void clearVersions() {
		versions.clear();
		versions = new HashSet<Version>();
	}

	public String getPath() {
		return (getFolder().getPath() + (getFolder().getPath().endsWith("/") ? "" : "/") + getFolder().getId())
				.replaceAll("//", "/");
	}

	public String getFileExtension() {
		return getFileName().substring(getFileName().lastIndexOf(".") + 1);
	}

	public DocumentTemplate getTemplate() {
		return template;
	}

	public void setTemplate(DocumentTemplate template) {
		this.template = template;
	}
}