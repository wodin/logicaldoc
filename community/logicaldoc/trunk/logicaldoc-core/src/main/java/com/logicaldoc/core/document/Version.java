package com.logicaldoc.core.document;

import java.util.Date;
import java.util.HashMap;

import org.apache.commons.beanutils.BeanUtils;

/**
 * This class represents versions.
 * 
 * @author Marco Meschieri - Logical Objects
 * @version 1.0
 */
public class Version extends AbstractDocument implements Comparable<Version> {
	/**
	 * specifies different version types
	 */
	public enum VERSION_TYPE {
		NEW_RELEASE, NEW_SUBVERSION, OLD_VERSION;
	}

	private String username;

	private Date versionDate = new Date();

	private String comment;

	private long userId;

	private long folderId;

	private String folderName;

	private Long templateId;

	private String templateName;

	private String kwds;

	private Document document;

	public Version() {
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	/**
	 * @see Version#getUsername()
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @see Version#getComment()
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @see Version#setUsername(java.lang.String)
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @see Version#setComment(java.lang.String)
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * @see Version#getNewVersionName(java.lang.String,
	 *      VersionImpl.VERSION_TYPE)
	 */
	public String getNewVersionName(String oldVersionName, VERSION_TYPE versionType) {
		String release = oldVersionName.substring(0, oldVersionName.indexOf("."));
		String version = oldVersionName.substring(oldVersionName.lastIndexOf(".") + 1);

		int number;
		switch (versionType) {
		case NEW_RELEASE:
			number = Integer.parseInt(release);
			release = String.valueOf(number + 1);
			version = "0";
			break;
		case NEW_SUBVERSION:
			number = Integer.parseInt(version);
			version = String.valueOf(number + 1);
			break;
		case OLD_VERSION:
			return oldVersionName;
		}

		return release + "." + version;
	}

	/** for sorting a list of Version objects by the version number */
	public int compareTo(Version other) {
		return this.getVersion().toLowerCase().compareTo(other.getVersion().toLowerCase());
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Version))
			return false;
		Version other = (Version) obj;
		return this.getVersion().equals(other.getVersion());
	}

	@Override
	public int hashCode() {
		return getVersion().hashCode();
	}

	@Override
	public String toString() {
		return getVersion().toString() + "-" + comment;
	}

	public Date getVersionDate() {
		return versionDate;
	}

	public void setVersionDate(Date versionDate) {
		this.versionDate = versionDate;
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

	public Long getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	/**
	 * Factory method that creates a version and replicate all document
	 * properties
	 * 
	 * @param document The original document
	 * @return The newly created instance
	 */
	public static Version createVersion(Document document) {
		Version version = new Version();
		try {
			BeanUtils.copyProperties(version, document);
		} catch (Exception e) {
			e.printStackTrace();
		}

		version.setId(0);
		version.setDeleted(0);
		version.setLastModified(null);

		if (document.getTemplate() != null) {
			version.setTemplateId(document.getTemplate().getId());
			version.setTemplateName(document.getTemplate().getName());
		}

		version.setAttributes(new HashMap<String, String>());
		if (document.getAttributes() != null) {
			for (String name : document.getAttributeNames()) {
				version.getAttributes().put(name, document.getAttributes().get(name));
			}
		}

		version.setFolderId(document.getFolder().getId());
		version.setTemplateName(document.getFolder().getText());
		version.setKwds(document.getKeywordsString());
		version.setDocument(document);

		return version;
	}

	public String getKwds() {
		return kwds;
	}

	public void setKwds(String kwds) {
		this.kwds = kwds;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}
}