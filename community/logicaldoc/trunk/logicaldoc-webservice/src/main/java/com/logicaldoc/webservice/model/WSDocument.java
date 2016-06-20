package com.logicaldoc.webservice.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.core.document.AbstractDocument;
import com.logicaldoc.core.document.Document;

/**
 * Web Service Document. Useful class to create repository Documents.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 5.2
 */
@XmlRootElement(name = "document")
public class WSDocument implements Serializable {

	private static final long serialVersionUID = 1L;

	protected static Logger log = LoggerFactory.getLogger(WSDocument.class);

	public static final int DOC_UNLOCKED = 0;

	public static final int DOC_CHECKED_OUT = 1;

	public static final int DOC_LOCKED = 2;

	public static final int EXPORT_UNLOCKED = 0;

	public static final int EXPORT_LOCKED = 1;

	public static final int INDEX_TO_INDEX = 0;

	public static final int INDEX_INDEXED = 1;

	public static final int INDEX_SKIP = 2;

	private long id;

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

	private String date;

	private String publisher;

	private long publisherId;

	private String creator;

	private long creatorId;

	private String type;

	private Long lockUserId;

	private String creation;

	private String fileName;

	private int indexed = INDEX_TO_INDEX;

	private int signed = 0;

	private int stamped = 0;

	private String[] tags = new String[0];

	private Long folderId;

	private Long templateId;

	private String customId;

	private int immutable = 0;

	private String digest;

	private String exportName;

	private Long exportId = null;

	private Long docRef;

	private String docRefType;

	private Long deleteUserId;

	private WSAttribute[] attributes = new WSAttribute[0];

	private String language;

	// Contains the snippet search text
	private String summary;

	private Integer score;

	private String icon;

	private String path;

	private String comment;

	private String lastModified;

	private Integer rating;

	private String workflowStatus;

	private int published = 1;

	private String startPublishing;

	private String stopPublishing;

	private int pages = -1;

	private int nature = AbstractDocument.NATURE_DOC;

	private Long formId = null;

	public Collection<String> listAttributeNames() {
		List<String> names = new ArrayList<String>();
		for (WSAttribute att : getAttributes()) {
			names.add(att.getName());
		}
		return names;
	}

	public WSAttribute attribute(String name) {
		for (WSAttribute att : getAttributes()) {
			if (att.getName().equals(name))
				return att;
		}
		return null;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getExportStatus() {
		return exportStatus;
	}

	public void setExportStatus(int exportStatus) {
		this.exportStatus = exportStatus;
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

	public String getExportVersion() {
		return exportVersion;
	}

	public void setExportVersion(String exportVersion) {
		this.exportVersion = exportVersion;
	}

	public String getFileVersion() {
		return fileVersion;
	}

	public void setFileVersion(String fileVersion) {
		this.fileVersion = fileVersion;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public long getPublisherId() {
		return publisherId;
	}

	public void setPublisherId(long publisherId) {
		this.publisherId = publisherId;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public long getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(long creatorId) {
		this.creatorId = creatorId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getLockUserId() {
		return lockUserId;
	}

	public void setLockUserId(Long lockUserId) {
		this.lockUserId = lockUserId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getIndexed() {
		return indexed;
	}

	public void setIndexed(int indexed) {
		this.indexed = indexed;
	}

	public int getSigned() {
		return signed;
	}

	public void setSigned(int signed) {
		this.signed = signed;
	}

	public String[] getTags() {
		return tags;
	}

	public void setTags(String[] tags) {
		this.tags = tags;
	}

	public Long getFolderId() {
		return folderId;
	}

	public void setFolderId(Long folderId) {
		this.folderId = folderId;
	}

	public Long getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}

	public String getCustomId() {
		return customId;
	}

	public void setCustomId(String customId) {
		this.customId = customId;
	}

	public int getImmutable() {
		return immutable;
	}

	public void setImmutable(int immutable) {
		this.immutable = immutable;
	}

	public String getDigest() {
		return digest;
	}

	public void setDigest(String digest) {
		this.digest = digest;
	}

	public String getExportName() {
		return exportName;
	}

	public void setExportName(String exportName) {
		this.exportName = exportName;
	}

	public Long getExportId() {
		return exportId;
	}

	public void setExportId(Long exportId) {
		this.exportId = exportId;
	}

	public Long getDocRef() {
		return docRef;
	}

	public void setDocRef(Long docRef) {
		this.docRef = docRef;
	}

	public Long getDeleteUserId() {
		return deleteUserId;
	}

	public void setDeleteUserId(Long deleteUserId) {
		this.deleteUserId = deleteUserId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getCreation() {
		return creation;
	}

	public void setCreation(String creation) {
		this.creation = creation;
	}

	public WSAttribute[] getAttributes() {
		return attributes;
	}

	public WSAttribute getAttribute(String name) {
		if (attributes == null)
			return null;
		for (WSAttribute att : attributes) {
			if (att.getName().equals(name))
				return att;
		}
		return null;
	}

	public void setAttributes(WSAttribute[] attributes) {
		this.attributes = attributes;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getLastModified() {
		return lastModified;
	}

	public void setLastModified(String lastModified) {
		this.lastModified = lastModified;
	}

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

	public void addAttribute(WSAttribute att) {
		if (attributes == null)
			attributes = new WSAttribute[0];
		List<WSAttribute> buf = new ArrayList<WSAttribute>();
		for (WSAttribute tmp : attributes)
			buf.add(tmp);
		buf.add(att);
		setAttributes(buf.toArray(new WSAttribute[0]));
	}

	public String getWorkflowStatus() {
		return workflowStatus;
	}

	public void setWorkflowStatus(String workflowStatus) {
		this.workflowStatus = workflowStatus;
	}

	public int getPublished() {
		return published;
	}

	public void setPublished(int published) {
		this.published = published;
	}

	public String getStartPublishing() {
		return startPublishing;
	}

	public void setStartPublishing(String startPublishing) {
		this.startPublishing = startPublishing;
	}

	public String getStopPublishing() {
		return stopPublishing;
	}

	public void setStopPublishing(String stopPublishing) {
		this.stopPublishing = stopPublishing;
	}

	public String getDocRefType() {
		return docRefType;
	}

	public void setDocRefType(String docRefType) {
		this.docRefType = docRefType;
	}

	public int getPages() {
		return pages;
	}

	public void setPages(int pages) {
		this.pages = pages;
	}

	public int getStamped() {
		return stamped;
	}

	public void setStamped(int stamped) {
		this.stamped = stamped;
	}

	public int getNature() {
		return nature;
	}

	public void setNature(int nature) {
		this.nature = nature;
	}

	public Long getFormId() {
		return formId;
	}

	public void setFormId(Long formId) {
		this.formId = formId;
	}
}