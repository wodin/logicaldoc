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

	public void copyAttributes(Document docVO) {
		setTenantId(docVO.getTenantId());
		setCustomId(docVO.getCustomId());
		setImmutable(docVO.getImmutable());
		setTitle(docVO.getTitle());
		setVersion(docVO.getVersion());
		setFileVersion(docVO.getFileVersion());
		setDate(docVO.getDate());
		setPublisher(docVO.getPublisher());
		setPublisherId(docVO.getPublisherId());
		setCreator(docVO.getCreator());
		setCreatorId(docVO.getCreatorId());
		setStatus(docVO.getStatus());
		setType(docVO.getType());
		setLockUserId(docVO.getLockUserId());
		setSource(docVO.getSource());
		setSourceAuthor(docVO.getSourceAuthor());
		setSourceDate(docVO.getSourceDate());
		setSourceId(docVO.getSourceId());
		setSourceType(docVO.getSourceType());
		setObject(docVO.getObject());
		setCoverage(docVO.getCoverage());
		setLanguage(docVO.getLanguage());
		setFileName(docVO.getFileName());
		setFileSize(docVO.getFileSize());
		setIndexed(docVO.getIndexed());
		setBarcoded(docVO.getBarcoded());
		setDigest(docVO.getDigest());
		setRecipient(docVO.getRecipient());
		setDocRef(docVO.getDocRef());
		setFolder(docVO.getFolder());
		setTemplate(docVO.getTemplate());

		setAttributes(new HashMap<String, ExtendedAttribute>());
		for (String name : docVO.getAttributes().keySet()) {
			getAttributes().put(name, docVO.getAttributes().get(name));
		}

		setTags(new HashSet<Tag>());
		for (Tag tag : docVO.getTags()) {
			getTags().add(tag);
		}
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		Document cloned = new Document();
		cloned.copyAttributes((Document) this);
		if (getIndexed() != INDEX_INDEXED)
			cloned.setIndexed(getIndexed());
		cloned.setCustomId(null);
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