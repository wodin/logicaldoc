package com.logicaldoc.core.document;

import java.util.HashMap;
import java.util.HashSet;

import com.logicaldoc.core.ExtendedAttribute;

/**
 * Basic concrete implementation of <code>AbstractDocument</code>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 1.0
 */
public class Document extends AbstractDocument {
	// Useful but not persisted
	public Long templateId;

	public Document() {
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		Document cloned = new Document();

		cloned.setImmutable(getImmutable());
		cloned.setTitle(getTitle());
		cloned.setVersion(getVersion());
		cloned.setFileVersion(getFileVersion());
		cloned.setDate(getDate());
		cloned.setPublisher(getPublisher());
		cloned.setPublisherId(getPublisherId());
		cloned.setCreator(getCreator());
		cloned.setCreatorId(getCreatorId());
		cloned.setStatus(getStatus());
		cloned.setType(getType());
		cloned.setLockUserId(getLockUserId());
		cloned.setSource(getSource());
		cloned.setSourceAuthor(getSourceAuthor());
		cloned.setSourceDate(getSourceDate());
		cloned.setSourceId(getSourceId());
		cloned.setSourceType(getSourceType());
		cloned.setObject(getObject());
		cloned.setCoverage(getCoverage());
		cloned.setLanguage(getLanguage());
		cloned.setFileName(getFileName());
		cloned.setFileSize(getFileSize());
		if (getIndexed() != INDEX_INDEXED)
			cloned.setIndexed(getIndexed());
		cloned.setBarcoded(getBarcoded());
		cloned.setDigest(getDigest());
		cloned.setRecipient(getRecipient());
		cloned.setDocRef(getDocRef());
		cloned.setFolder(getFolder());
		cloned.setTemplate(getTemplate());

		cloned.setAttributes(new HashMap<String, ExtendedAttribute>());
		for (String name : getAttributes().keySet()) {
			cloned.getAttributes().put(name, getAttributes().get(name));
		}

		cloned.setTags(new HashSet<String>());
		for (String tag : getTags()) {
			cloned.getTags().add(tag);
		}

		return cloned;
	}

	public Long getTemplateId() {
		if (templateId != null)
			return templateId;
		else if (getTemplate() != null)
			return getTemplate().getId();
		else
			return null;
	}

	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}
}