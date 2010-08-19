package com.logicaldoc.webservice.document;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.ExtendedAttribute;
import com.logicaldoc.core.document.AbstractDocument;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.DocumentTemplate;
import com.logicaldoc.core.document.dao.DocumentTemplateDAO;
import com.logicaldoc.core.security.Folder;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.FolderDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.LocaleUtil;
import com.logicaldoc.webservice.AbstractService;
import com.logicaldoc.webservice.Attribute;

/**
 * Web Service Document. Useful class to create reporitory Documents.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 5.2
 */
public class WSDocument {

	protected static Log log = LogFactory.getLog(WSDocument.class);

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

	private Long deleteUserId;

	private Attribute[] extendedAttributes;

	private String language;

	// Contains the snippet search text
	private String summary;

	private int dateCategory;

	private int docType;

	private Integer score;

	private String icon;

	private int lengthCategory;

	private String path;

	private long size;

	public static WSDocument fromDocument(AbstractDocument document) {
		WSDocument wsDoc = new WSDocument();

		// Populate extended attributes
		Attribute[] extendedAttributes = new Attribute[0];
		if (document.getAttributes() != null && document.getAttributes().size() > 0) {
			extendedAttributes = new Attribute[document.getAttributeNames().size()];
			int i = 0;
			for (String name : document.getAttributeNames()) {
				extendedAttributes[i++] = new Attribute(name, document.getExtendedAttribute(name));
			}
		}

		String[] tags = new String[0];
		if (document.getTags() != null && document.getTags().size() > 0) {
			tags = new String[document.getTags().size()];
			List<String> docTags = new ArrayList<String>(document.getTags());
			if (docTags != null && docTags.size() > 0) {
				for (int j = 0; j < docTags.size(); j++) {
					tags[j] = docTags.get(j);
				}
			}
		}

		wsDoc.setId(document.getId());
		wsDoc.setTitle(document.getTitle());
		wsDoc.setSource(document.getSource());
		wsDoc.setSourceAuthor(document.getSourceAuthor());
		wsDoc.setSourceType(document.getSourceType());
		wsDoc.setCoverage(document.getCoverage());
		wsDoc.setLanguage(document.getLanguage());
		wsDoc.setTags(tags);
		wsDoc.setSourceId(document.getSourceId());
		wsDoc.setObject(document.getObject());
		wsDoc.setRecipient(document.getRecipient());
		if (document.getTemplate() != null)
			wsDoc.setTemplateId(document.getTemplate().getId());
		wsDoc.setExtendedAttributes(extendedAttributes);
		String sourceDate = null;
		if (document.getSourceDate() != null)
			sourceDate = AbstractService.convertDateToString(document.getSourceDate());
		wsDoc.setSourceDate(sourceDate);
		wsDoc.setImmutable(document.getImmutable());
		if (document.getFolder() != null)
			wsDoc.setFolderId(document.getFolder().getId());
		if (document.getIndexed() != INDEX_INDEXED)
			wsDoc.setIndexed(document.getIndexed());
		wsDoc.setVersion(document.getVersion());
		wsDoc.setFileVersion(document.getFileVersion());
		String date = null;
		if (document.getDate() != null)
			date = AbstractService.convertDateToString(document.getDate());
		wsDoc.setDate(date);
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

		return wsDoc;
	}

	public Document toDocument(User user) throws Exception {
		FolderDAO mdao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		Folder folder = mdao.findById(folderId);
		if (folder == null) {
			log.error("Folder " + folder + " not found");
			throw new Exception("error - folder not found");
		}

		Set<String> setTags = new TreeSet<String>();
		if (getTags() != null) {
			for (int i = 0; i < getTags().length; i++) {
				setTags.add(getTags()[i]);
			}
		}

		DocumentTemplate template = null;
		Map<String, ExtendedAttribute> attributes = null;
		if (templateId != null) {
			DocumentTemplateDAO templDao = (DocumentTemplateDAO) Context.getInstance().getBean(
					DocumentTemplateDAO.class);
			template = templDao.findById(templateId);
			if (template != null) {
				if (extendedAttributes != null && extendedAttributes.length > 0) {
					attributes = new HashMap<String, ExtendedAttribute>();
					for (int i = 0; i < extendedAttributes.length; i++) {
						attributes.put(extendedAttributes[i].getName(), extendedAttributes[i].getAttribute());
					}
				}
			}
		}

		Document doc = new Document();
		doc.setTitle(title);
		doc.setFileName(fileName);
		doc.setFolder(folder);
		doc.setLocale(LocaleUtil.toLocale(language));
		Date sdate = null;
		if (StringUtils.isNotEmpty(sourceDate))
			sdate = AbstractService.convertStringToDate(sourceDate);
		doc.setSourceDate(sdate);
		doc.setSource(source);
		doc.setSourceAuthor(sourceAuthor);
		doc.setSourceType(sourceType);
		doc.setCoverage(coverage);
		doc.setTags(setTags);
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

	public Attribute[] getExtendedAttributes() {
		return extendedAttributes;
	}

	public void setExtendedAttributes(Attribute[] extendedAttributes) {
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

	public int getDateCategory() {
		return dateCategory;
	}

	public void setDateCategory(int dateCategory) {
		this.dateCategory = dateCategory;
	}

	public int getDocType() {
		return docType;
	}

	public void setDocType(int docType) {
		this.docType = docType;
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

	public int getLengthCategory() {
		return lengthCategory;
	}

	public void setLengthCategory(int lengthCategory) {
		this.lengthCategory = lengthCategory;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}
}