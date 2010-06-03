package com.logicaldoc.web.document;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.ExtendedAttribute;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.DocumentTemplate;
import com.logicaldoc.core.document.Version;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.document.dao.DocumentTemplateDAO;
import com.logicaldoc.core.document.dao.VersionDAO;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.gui.common.client.beans.GUIBookmark;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.beans.GUIEmail;
import com.logicaldoc.gui.common.client.beans.GUIExtendedAttribute;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.common.client.beans.GUIVersion;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.TagUtil;
import com.logicaldoc.web.AbstractService;

/**
 * Implementation of the DocumentService
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class DocumentServiceImpl extends AbstractService implements DocumentService {

	private static final long serialVersionUID = 1L;

	private static Log log = LogFactory.getLog(DocumentServiceImpl.class);

	@Override
	public void addBookmarks(String sid, long[] docIds) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addDocuments(String sid, String language, boolean importZip) {
		// TODO Auto-generated method stub

	}

	@Override
	public void checkin(String sid, long docId, boolean major) {
		// TODO Auto-generated method stub

	}

	@Override
	public void checkout(String sid, long docId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(String sid, long[] ids) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteBookmarks(String sid, long[] bookmarkIds) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteDiscussions(String sid, long[] ids) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteLinks(String sid, long[] ids) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deletePosts(String sid, long discussionId, int[] postIds) {
		// TODO Auto-generated method stub

	}

	@Override
	public GUIExtendedAttribute[] getAttributes(String sid, long templateId) {
		validateSession(sid);

		DocumentTemplateDAO templateDao = (DocumentTemplateDAO) Context.getInstance()
				.getBean(DocumentTemplateDAO.class);
		DocumentTemplate template = templateDao.findById(templateId);
		GUIExtendedAttribute[] attributes = new GUIExtendedAttribute[template.getAttributeNames().size()];
		int i = 0;
		for (String attrName : template.getAttributeNames()) {
			ExtendedAttribute extAttr = template.getAttributes().get(attrName);
			GUIExtendedAttribute att = new GUIExtendedAttribute();
			att.setName(attrName);
			att.setPosition(extAttr.getPosition());
			att.setMandatory(extAttr.getMandatory() == 1 ? true : false);
			att.setType(extAttr.getType());
			attributes[i] = att;
			i++;
		}

		return attributes;
	}

	@Override
	public GUIDocument getById(String sid, long docId) {
		validateSession(sid);

		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Document doc = docDao.findById(docId);

		if (doc != null) {
			GUIDocument document = new GUIDocument();
			document.setId(docId);
			document.setTitle(doc.getTitle());
			document.setCustomId(doc.getCustomId());
			document.setTags(doc.getTags().toArray(new String[doc.getTags().size()]));
			document.setType(doc.getType());
			document.setFileName(doc.getFileName());
			document.setVersion(doc.getVersion());
			document.setCreation(doc.getCreation());
			document.setCreator(doc.getCreator());
			document.setDate(doc.getDate());
			document.setPublisher(doc.getPublisher());
			document.setFileVersion(doc.getFileVersion());
			document.setLanguage(doc.getLanguage());
			document.setTemplateId(doc.getTemplateId());
			document.setTemplate(doc.getTemplate().getName());
			document.setStatus(doc.getStatus());
			MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
			document.setPathExtended(mdao.computePathExtended(doc.getFolder().getId()));
			GUIFolder folder = new GUIFolder();
			Menu docFolder = doc.getFolder();
			folder.setName(docFolder.getText());
			folder.setId(docFolder.getId());

			// TODO Fixed after complete implementation
			if (docId % 2 == 0)
				folder.setPermissions(new String[] { "read", "write", "addChild", "manageSecurity", "delete", "rename",
						"bulkImport", "bulkExport", "sign", "archive", "workflow", "manageImmutability" });
			else
				folder.setPermissions(new String[] { "read" });

			document.setFolder(folder);

			return document;
		}

		return null;
	}

	@Override
	public GUIVersion[] getVersionsById(String sid, long id1, long id2) {
		GUIVersion[] versions = new GUIVersion[2];

		VersionDAO versDao = (VersionDAO) Context.getInstance().getBean(VersionDAO.class);
		Version docVersion = versDao.findById(id1);

		GUIVersion version = new GUIVersion();
		version.setUsername(docVersion.getUsername());
		version.setComment(docVersion.getComment());
		version.setId(id1);
		version.setTitle(docVersion.getTitle());
		version.setCustomId(docVersion.getCustomId());
		version.setTags(docVersion.getTags().toArray(new String[docVersion.getTags().size()]));
		version.setType(docVersion.getType());
		version.setFileName(docVersion.getFileName());
		version.setVersion(docVersion.getVersion());
		version.setCreation(docVersion.getCreation());
		version.setCreator(docVersion.getCreator());
		version.setDate(docVersion.getDate());
		version.setPublisher(docVersion.getPublisher());
		version.setFileVersion(docVersion.getFileVersion());
		version.setLanguage(docVersion.getLanguage());
		version.setTemplateId(docVersion.getTemplateId());
		version.setFileSize(new Float(docVersion.getFileSize()));
		version.setTemplate(docVersion.getTemplateName());
		for (String attrName : docVersion.getAttributeNames()) {
			ExtendedAttribute extAttr = docVersion.getAttributes().get(attrName);
			version.setValue(attrName, extAttr);
		}
		GUIFolder folder = new GUIFolder();
		folder.setName(docVersion.getFolderName());
		folder.setId(docVersion.getFolderId());
		version.setFolder(folder);
		versions[0] = version;

		docVersion = versDao.findById(id2);

		version = new GUIVersion();
		version.setUsername(docVersion.getUsername());
		version.setComment(docVersion.getComment());
		version.setId(id1);
		version.setTitle(docVersion.getTitle());
		version.setCustomId(docVersion.getCustomId());
		version.setTags(docVersion.getTags().toArray(new String[docVersion.getTags().size()]));
		version.setType(docVersion.getType());
		version.setFileName(docVersion.getFileName());
		version.setVersion(docVersion.getVersion());
		version.setCreation(docVersion.getCreation());
		version.setCreator(docVersion.getCreator());
		version.setDate(docVersion.getDate());
		version.setPublisher(docVersion.getPublisher());
		version.setFileVersion(docVersion.getFileVersion());
		version.setLanguage(docVersion.getLanguage());
		version.setTemplateId(docVersion.getTemplateId());
		version.setFileSize(new Float(docVersion.getFileSize()));
		version.setTemplate(docVersion.getTemplateName());
		for (String attrName : docVersion.getAttributeNames()) {
			ExtendedAttribute extAttr = docVersion.getAttributes().get(attrName);
			version.setValue(attrName, extAttr);
		}
		folder = new GUIFolder();
		folder.setName(docVersion.getFolderName());
		folder.setId(docVersion.getFolderId());
		version.setFolder(folder);
		versions[1] = version;

		return versions;
	}

	@Override
	public void linkDocuments(String sid, long[] inDocIds, long[] outDocIds) {
		// TODO Auto-generated method stub

	}

	@Override
	public void lock(String sid, long[] docIds, String comment) {
		// TODO Auto-generated method stub

	}

	@Override
	public void makeImmutable(String sid, long[] docIds, String comment) {
		// TODO Auto-generated method stub

	}

	@Override
	public void markHistoryAsRead(String sid, String event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void markIndexable(String sid, long[] docIds) {
		// TODO Auto-generated method stub

	}

	@Override
	public void markUnindexable(String sid, long[] docIds) {
		// TODO Auto-generated method stub

	}

	@Override
	public int replyPost(String sid, long discussionId, int replyTo, String title, String message) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void restore(String sid, long docId) {
		// TODO Auto-generated method stub

	}

	@Override
	public GUIDocument save(String sid, GUIDocument document) {
		validateSession(sid);

		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Document doc;
		if (document.getId() != 0) {
			doc = docDao.findById(document.getId());
			docDao.initialize(doc);
		} else
			doc = new Document();

		doc.setTitle(document.getTitle());
		doc.setCustomId(document.getCustomId());
		// TODO It works???
		doc.setTags(TagUtil.extractTags(document.getTags().toString()));
		doc.setType(document.getType());
		doc.setFileName(document.getFileName());
		doc.setVersion(document.getVersion());
		doc.setCreation(document.getCreation());
		doc.setCreator(document.getCreator());
		doc.setDate(document.getDate());
		doc.setPublisher(document.getPublisher());
		doc.setFileVersion(document.getFileVersion());
		doc.setLanguage(document.getLanguage());
		DocumentTemplateDAO templateDao = (DocumentTemplateDAO) Context.getInstance()
				.getBean(DocumentTemplateDAO.class);
		DocumentTemplate template = templateDao.findById(document.getTemplateId());
		doc.setTemplateId(template.getId());
		doc.setTemplate(template);
		doc.setStatus(document.getStatus());
		MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		doc.setFolder(mdao.findById(document.getFolder().getId()));

		docDao.store(doc);
		doc.setId(document.getId());

		return document;
	}

	@Override
	public String sendAsEmail(String sid, GUIEmail email) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long startDiscussion(String sid, long docId, String title, String message) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void unlock(String sid, long[] docIds) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateBookmark(String sid, GUIBookmark bookmark) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateLink(String sid, long id, String type) {
		// TODO Auto-generated method stub

	}

}
