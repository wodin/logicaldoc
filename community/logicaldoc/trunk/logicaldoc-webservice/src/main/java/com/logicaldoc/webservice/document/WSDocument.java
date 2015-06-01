package com.logicaldoc.webservice.document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.core.ExtendedAttribute;
import com.logicaldoc.core.document.AbstractDocument;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.DocumentTemplate;
import com.logicaldoc.core.document.dao.DocumentTemplateDAO;
import com.logicaldoc.core.security.Folder;
import com.logicaldoc.core.security.dao.FolderDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.LocaleUtil;
import com.logicaldoc.webservice.AbstractService;
import com.logicaldoc.webservice.WSAttribute;

/**
 * Web Service Document. Useful class to create repository Documents.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 5.2
 */
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

	private String source;

	private String sourceAuthor;

	private String sourceDate;

	private String sourceId;

	private String object;

	private String creation;

	private String sourceType;

	private String coverage;

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

	private String recipient;

	private String exportName;

	private Long exportId = null;

	private Long docRef;

	private String docRefType;

	private Long deleteUserId;

	private WSAttribute[] extendedAttributes = new WSAttribute[0];

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

	public static WSDocument fromDocument(AbstractDocument document) {
		WSDocument wsDoc = new WSDocument();

		try {
			wsDoc.setId(document.getId());
			wsDoc.setCustomId(document.getCustomId());
			wsDoc.setTitle(document.getTitle());
			wsDoc.setSource(document.getSource());
			wsDoc.setSourceAuthor(document.getSourceAuthor());
			wsDoc.setSourceType(document.getSourceType());
			wsDoc.setCoverage(document.getCoverage());
			wsDoc.setLanguage(document.getLanguage());
			wsDoc.setSourceId(document.getSourceId());
			wsDoc.setObject(document.getObject());
			wsDoc.setRecipient(document.getRecipient());
			wsDoc.setComment(document.getComment());
			wsDoc.setWorkflowStatus(document.getWorkflowStatus());
			if (document.getTemplate() != null)
				wsDoc.setTemplateId(document.getTemplate().getId());
			wsDoc.setImmutable(document.getImmutable());
			if (document.getFolder() != null)
				wsDoc.setFolderId(document.getFolder().getId());
			if (document.getIndexed() != INDEX_INDEXED)
				wsDoc.setIndexed(document.getIndexed());
			wsDoc.setVersion(document.getVersion());
			wsDoc.setFileVersion(document.getFileVersion());
			wsDoc.setPublisher(document.getPublisher());
			wsDoc.setPublisherId(document.getPublisherId());
			wsDoc.setCreator(document.getCreator());
			wsDoc.setCreatorId(document.getCreatorId());
			wsDoc.setStatus(document.getStatus());
			wsDoc.setType(document.getType());
			wsDoc.setLockUserId(document.getLockUserId());
			wsDoc.setFileName(document.getFileName());
			wsDoc.setFileSize(document.getFileSize());
			wsDoc.setDigest(document.getDigest());
			wsDoc.setRecipient(document.getRecipient());
			wsDoc.setDocRef(document.getDocRef());
			wsDoc.setDocRefType(document.getDocRefType());
			wsDoc.setLastModified(AbstractService.convertDateToString(document.getLastModified()));
			wsDoc.setRating(document.getRating());
			wsDoc.setPages(document.getPages());
			wsDoc.setSigned(document.getSigned());
			wsDoc.setStamped(document.getStamped());
			wsDoc.setNature(document.getNature());
			wsDoc.setFormId(document.getFormId());
			
			
			String date = null;
			if (document.getSourceDate() != null)
				date = AbstractService.convertDateToString(document.getSourceDate());
			wsDoc.setSourceDate(date);
			date = null;
			if (document.getDate() != null)
				date = AbstractService.convertDateToString(document.getDate());
			wsDoc.setDate(date);
			date = null;
			if (document.getCreation() != null)
				date = AbstractService.convertDateToString(document.getCreation());
			wsDoc.setCreation(date);
			date = null;
			if (document.getStartPublishing() != null)
				date = AbstractService.convertDateToString(document.getStartPublishing());
			wsDoc.setStartPublishing(date);
			date = null;
			if (document.getStopPublishing() != null)
				date = AbstractService.convertDateToString(document.getStopPublishing());
			wsDoc.setStopPublishing(date);

			// Populate extended attributes
			WSAttribute[] attributes = new WSAttribute[0];
			try {
				if (document.getAttributes() != null && document.getAttributes().size() > 0) {
					attributes = new WSAttribute[document.getAttributeNames().size()];
					int i = 0;
					for (String name : document.getAttributeNames()) {
						ExtendedAttribute attr = document.getExtendedAttribute(name);

						WSAttribute attribute = new WSAttribute();
						attribute.setName(name);
						attribute.setMandatory(attr.getMandatory());
						attribute.setPosition(attr.getPosition());
						attribute.setType(attr.getType());
						attribute.setValue(attr.getValue());

						if (attr.getType() == ExtendedAttribute.TYPE_USER) {
							attribute.setIntValue(attr.getIntValue());
							attribute.setStringValue(attr.getStringValue());
						}

						attribute.setType(attr.getType());
						attributes[i++] = attribute;
					}
				}
			} catch (Throwable t) {
			}
			wsDoc.setExtendedAttributes(attributes);

			String[] tags = new String[0];
			if (document.getTags() != null && document.getTags().size() > 0) {
				tags = new String[document.getTags().size()];
				List<String> docTags = new ArrayList<String>(document.getTagsAsWords());
				if (docTags != null && docTags.size() > 0) {
					for (int j = 0; j < docTags.size(); j++) {
						tags[j] = docTags.get(j);
					}
				}
			}
			wsDoc.setTags(tags);
		} catch (Throwable e) {
			e.printStackTrace();
		}

		return wsDoc;
	}

	public Collection<String> listAttributeNames() {
		List<String> names = new ArrayList<String>();
		for (WSAttribute att : getExtendedAttributes()) {
			names.add(att.getName());
		}
		return names;
	}

	public WSAttribute attribute(String name) {
		for (WSAttribute att : getExtendedAttributes()) {
			if (att.getName().equals(name))
				return att;
		}
		return null;
	}

	public Document toDocument() throws Exception {
		FolderDAO fdao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		Folder folder = fdao.findById(folderId);
		if (folder == null) {
			log.error("Folder " + folder + " not found");
			throw new Exception("error - folder not found");
		}
		fdao.initialize(folder);

		Set<String> setTags = new TreeSet<String>();
		if (getTags() != null) {
			for (int i = 0; i < getTags().length; i++) {
				setTags.add(getTags()[i]);
			}
		}

		DocumentTemplate template = null;
		Map<String, ExtendedAttribute> attributes = new HashMap<String, ExtendedAttribute>();
		if (templateId != null) {
			DocumentTemplateDAO templDao = (DocumentTemplateDAO) Context.getInstance().getBean(
					DocumentTemplateDAO.class);
			template = templDao.findById(templateId);
			if (template != null) {
				if (extendedAttributes != null && extendedAttributes.length > 0) {
					for (int i = 0; i < extendedAttributes.length; i++) {
						ExtendedAttribute extAttribute = new ExtendedAttribute();
						extAttribute.setMandatory(extendedAttributes[i].getMandatory());
						extAttribute.setPosition(extendedAttributes[i].getPosition());
						extAttribute.setIntValue(extendedAttributes[i].getIntValue());
						extAttribute.setStringValue(extendedAttributes[i].getStringValue());
						extAttribute.setDoubleValue(extendedAttributes[i].getDoubleValue());
						extAttribute.setDateValue(AbstractService.convertStringToDate(extendedAttributes[i]
								.getDateValue()));
						extAttribute.setType(extendedAttributes[i].getType());

						attributes.put(extendedAttributes[i].getName(), extAttribute);
					}
				}
			}
		}

		Document doc = new Document();
		doc.setTitle(title);
		doc.setFileName(fileName);
		doc.setFolder(folder);
		doc.setComment(comment);
		doc.setWorkflowStatus(workflowStatus);
		doc.setLocale(LocaleUtil.toLocale(language));
		Date sdate = null;
		if (StringUtils.isNotEmpty(sourceDate))
			sdate = AbstractService.convertStringToDate(sourceDate);
		doc.setSourceDate(sdate);
		doc.setSource(source);
		doc.setSourceAuthor(sourceAuthor);
		doc.setSourceType(sourceType);
		doc.setCoverage(coverage);
		doc.setTagsFromWords(setTags);
		doc.setTemplate(template);
		if (template != null)
			doc.setTemplateId(template.getId());
		doc.setAttributes(attributes);
		doc.setSourceId(sourceId);
		doc.setObject(object);
		doc.setCustomId(customId);
		doc.setLanguage(language);
		doc.setRecipient(recipient);
		doc.setImmutable(immutable);
		if (indexed != INDEX_INDEXED)
			doc.setIndexed(indexed);
		doc.setVersion(version);
		doc.setFileVersion(fileVersion);
		Date newdate = null;
		if (StringUtils.isNotEmpty(date))
			newdate = AbstractService.convertStringToDate(date);
		doc.setDate(newdate);
		doc.setPages(pages);
		doc.setNature(nature);
		doc.setFormId(formId);

		Date creationDate = null;
		if (StringUtils.isNotEmpty(creation))
			creationDate = AbstractService.convertStringToDate(creation);
		doc.setCreation(creationDate);

		doc.setPublisher(publisher);
		doc.setPublisherId(publisherId);
		doc.setCreator(creator);
		doc.setCreatorId(creatorId);
		doc.setStatus(status);
		doc.setType(type);
		doc.setLockUserId(lockUserId);
		doc.setFileSize(fileSize);
		doc.setDigest(digest);
		doc.setDocRef(docRef);
		doc.setDocRefType(docRefType);
		if (rating != null)
			doc.setRating(rating);
		doc.setPublished(published);
		if (StringUtils.isNotEmpty(startPublishing))
			doc.setStartPublishing(AbstractService.convertStringToDate(startPublishing));
		if (StringUtils.isNotEmpty(stopPublishing))
			doc.setStopPublishing(AbstractService.convertStringToDate(stopPublishing));

		return doc;
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

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
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

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public String getCoverage() {
		return coverage;
	}

	public void setCoverage(String coverage) {
		this.coverage = coverage;
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

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
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

	public String getSourceDate() {
		return sourceDate;
	}

	public void setSourceDate(String sourceDate) {
		this.sourceDate = sourceDate;
	}

	public String getCreation() {
		return creation;
	}

	public void setCreation(String creation) {
		this.creation = creation;
	}

	public WSAttribute[] getExtendedAttributes() {
		return extendedAttributes;
	}

	public void setExtendedAttributes(WSAttribute[] extendedAttributes) {
		this.extendedAttributes = extendedAttributes;
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

	public void addExtendedAttribute(WSAttribute att) {
		if (extendedAttributes == null)
			extendedAttributes = new WSAttribute[0];
		List<WSAttribute> buf = new ArrayList<WSAttribute>();
		for (WSAttribute tmp : extendedAttributes)
			buf.add(tmp);
		buf.add(att);
		setExtendedAttributes(buf.toArray(new WSAttribute[0]));
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