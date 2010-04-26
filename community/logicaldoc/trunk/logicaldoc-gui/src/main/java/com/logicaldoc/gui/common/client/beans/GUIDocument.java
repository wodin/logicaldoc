package com.logicaldoc.gui.common.client.beans;

import java.io.Serializable;
import java.util.Date;

import com.logicaldoc.gui.common.client.Constants;

/**
 * Representation of a single document handled by the GUI
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class GUIDocument implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;

	private String title;

	private String customId;

	private String[] tags = null;

	private String type;

	private String version;

	private String fileVersion;

	private String fileName;

	private Date date;

	private Date creation;

	private Date sourceDate;

	private String creator;

	private String publisher;

	private String source;

	private String language;

	private String sourceAuthor;

	private String sourceId;

	private String sourceType;

	private String object;

	private String recipient;

	private String coverage;

	private String template;

	private Long templateId;

	private Float size;

	private Date lastModified;

	private GUIExtendedAttribute[] attributes = new GUIExtendedAttribute[0];

	private String versionComment;

	private String folder;

	private String icon = "generic";

	private Long lockUserId;

	private int status = 0;

	private String[] permissions = new String[] {};

	public boolean isWrite() {
		for (String permission : permissions) {
			if (Constants.PERMISSION_WRITE.equals(permission))
				return true;
		}
		return false;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCustomId() {
		return customId;
	}

	public void setCustomId(String customId) {
		this.customId = customId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String[] getTags() {
		return tags;
	}

	public void setTags(String[] tags) {
		this.tags = tags;
	}

	public void clearTags() {
		this.tags = new String[] {};
	}

	public void addTag(String tag) {
		String[] tmp = new String[tags.length + 1];
		int i = 0;
		for (String tg : tags) {
			tmp[i++] = tg;
		}
		tmp[i] = tag;
		tags = tmp;
	}

	public void removeTag(String tag) {
		String[] tmp = new String[tags.length - 1];
		int i = 0;
		for (String tg : tags) {
			if (!tg.equals(tag))
				tmp[i++] = tg;
		}
		tags = tmp;
	}

	public String getTagsString() {
		StringBuffer buf = new StringBuffer();
		for (String tag : getTags()) {
			buf.append(tag);
			buf.append(" ");
		}
		return buf.toString();
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public Date getCreation() {
		return creation;
	}

	public void setCreation(Date creation) {
		this.creation = creation;
	}

	public String getFileVersion() {
		return fileVersion;
	}

	public void setFileVersion(String fileVersion) {
		this.fileVersion = fileVersion;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Date getSourceDate() {
		return sourceDate;
	}

	public void setSourceDate(Date sourceDate) {
		this.sourceDate = sourceDate;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getSourceAuthor() {
		return sourceAuthor;
	}

	public void setSourceAuthor(String sourceAuthor) {
		this.sourceAuthor = sourceAuthor;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
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

	public String getCoverage() {
		return coverage;
	}

	public void setCoverage(String coverage) {
		this.coverage = coverage;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public Long getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}

	public GUIExtendedAttribute[] getAttributes() {
		return attributes;
	}

	public void setAttributes(GUIExtendedAttribute[] attributes) {
		this.attributes = attributes;
	}

	public Object getValue(String attributeName) {
		for (GUIExtendedAttribute att : attributes) {
			if (att.getName().equals(attributeName))
				return att.getValue();
		}
		return null;
	}

	public GUIExtendedAttribute getExtendedAttribute(String attributeName) {
		for (GUIExtendedAttribute att : attributes) {
			if (att.getName().equals(attributeName))
				return att;
		}
		return null;
	}

	public Float getSize() {
		return size;
	}

	public void setSize(Float size) {
		this.size = size;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public String getVersionComment() {
		return versionComment;
	}

	public void setVersionComment(String versionComment) {
		this.versionComment = versionComment;
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public GUIExtendedAttribute setValue(String name, Object value) {
		GUIExtendedAttribute[] tmp = new GUIExtendedAttribute[attributes.length + 1];
		int i = 0;
		for (GUIExtendedAttribute a : attributes) {
			tmp[i++] = a;
		}

		GUIExtendedAttribute ext = new GUIExtendedAttribute();
		ext.setName(name);
		ext.setValue(value);
		tmp[i] = ext;
		attributes = tmp;
		return ext;
	}

	@Override
	public boolean equals(Object obj) {
		return id == ((GUIDocument) obj).getId();
	}

	@Override
	public int hashCode() {
		return new Long(getId()).hashCode();
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public Long getLockUserId() {
		return lockUserId;
	}

	public void setLockUserId(Long lockUserId) {
		this.lockUserId = lockUserId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String[] getPermissions() {
		return permissions;
	}

	public void setPermissions(String[] permissions) {
		this.permissions = permissions;
	}
}