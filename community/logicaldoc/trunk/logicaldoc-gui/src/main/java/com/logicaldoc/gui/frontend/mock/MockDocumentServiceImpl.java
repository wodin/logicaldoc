package com.logicaldoc.gui.frontend.mock;

import java.util.Date;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.gui.common.client.beans.GUIBookmark;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.beans.GUIEmail;
import com.logicaldoc.gui.common.client.beans.GUIExtendedAttribute;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.common.client.beans.GUIVersion;
import com.logicaldoc.gui.frontend.client.services.DocumentService;

/**
 * Implementation of the SecurityService
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class MockDocumentServiceImpl extends RemoteServiceServlet implements DocumentService {

	private static final long serialVersionUID = 1L;

	@Override
	public GUIDocument getById(String sid, long docId) {
		GUIDocument document = new GUIDocument();
		document.setId(docId);
		document.setTitle("Document " + docId);
		document.setCustomId("" + docId);
		document.setTags(new String[] { "pippo", "pluto", "paperino" });
		document.setType("doc");
		document.setFileName(document.getTitle().toLowerCase() + ".doc");
		document.setVersion("1.0");
		document.setCreation(new Date(1234567));
		document.setCreator("Marco Meschieri");
		document.setDate(new Date(1236567));
		document.setPublisher("Marco Meschieri");
		document.setFileVersion("1.0");
		document.setLanguage("it");
		document.setTemplateId(1L);
		document.setTemplate("template1");
		document.setStatus(0);
		document.setPathExtended("/Folder 1/Folder 2/Folder 3/Folder 4");
		GUIFolder folder = new GUIFolder();
		folder.setName("Folder " + docId);
		folder.setId(docId);
		if (docId % 2 == 0)
			folder.setPermissions(new String[] { "read", "write", "addChild", "manageSecurity", "delete", "rename",
					"bulkImport", "bulkExport", "sign", "archive", "workflow", "manageImmutability" });
		else
			folder.setPermissions(new String[] { "read" });

		document.setFolder(folder);
		return document;
	}

	@Override
	public GUIExtendedAttribute[] getAttributes(String sid, long templateId) {
		if (templateId < 0)
			return new GUIExtendedAttribute[0];

		GUIExtendedAttribute[] attributes = new GUIExtendedAttribute[4];

		GUIExtendedAttribute att = new GUIExtendedAttribute();
		att.setName("Attribute A");
		att.setPosition(0);
		att.setMandatory(true);
		att.setType(GUIExtendedAttribute.TYPE_INT);
		attributes[0] = att;

		att = new GUIExtendedAttribute();
		att.setName("Attribute B");
		att.setPosition(1);
		att.setType(GUIExtendedAttribute.TYPE_DOUBLE);
		attributes[1] = att;

		att = new GUIExtendedAttribute();
		att.setName("Attribute C");
		att.setPosition(2);
		att.setType(GUIExtendedAttribute.TYPE_STRING);
		attributes[2] = att;

		att = new GUIExtendedAttribute();
		att.setName("Attribute D");
		att.setPosition(3);
		att.setType(GUIExtendedAttribute.TYPE_DATE);
		attributes[3] = att;

		return attributes;
	}

	@Override
	public GUIDocument save(String sid, GUIDocument document) {
		return document;
	}

	@Override
	public GUIVersion[] getVersionsById(String sid, long id1, long id2) {
		GUIVersion[] result = new GUIVersion[2];

		GUIVersion version = new GUIVersion();
		version.setUsername("Marco Meschieri");
		version.setComment("comment");
		version.setId(id1);
		version.setTitle("Document " + id1);
		version.setCustomId("" + id1);
		version.setTags(new String[] { "pippo", "pluto", "paperino" });
		version.setType("doc");
		version.setFileName(version.getTitle().toLowerCase() + ".doc");
		version.setVersion("1.0");
		version.setCreation(new Date(1234567 + id1));
		version.setCreator("Marco Meschieri");
		version.setDate(new Date(1236567 + id1));
		version.setPublisher("Marco Meschieri");
		version.setFileVersion("1." + id2);
		version.setLanguage("it");
		version.setTemplateId(1L);
		version.setSize(3457F);
		version.setTemplate("template1");
		version.setValue("attrA", "valA");
		version.setValue("attrB", new Date());
		version.setValue("attrC", 123.6);
		GUIFolder folder = new GUIFolder();
		folder.setName("Folder " + id1);
		folder.setId(id1);
		version.setFolder(folder);
		result[0] = version;

		version = new GUIVersion();
		version.setUsername("Marco Meschieri");
		version.setComment("comment");
		version.setId(id1);
		version.setTitle("Document " + id2);
		version.setCustomId("" + id2);
		version.setTags(new String[] { "pippo", "pluto", "paperino" });
		version.setType("doc");
		version.setFileName(version.getTitle().toLowerCase() + ".doc");
		version.setVersion("1." + id2);
		version.setCreation(new Date(1234567 + id2));
		version.setCreator("Marco Meschieri");
		version.setDate(new Date(1236567 + id2));
		version.setPublisher("Marco Meschieri");
		version.setFileVersion("1.0");
		version.setLanguage("it");
		version.setTemplateId(1L);
		version.setSize(3457F);
		version.setTemplate("template1");
		version.setValue("attrA", "valA");
		version.setValue("attrB", new Date());
		folder = new GUIFolder();
		folder.setName("Folder " + id2);
		folder.setId(id2);
		version.setFolder(folder);
		result[1] = version;

		return result;
	}

	@Override
	public String sendAsEmail(String sid, GUIEmail email) {
		return "ok";
	}

	@Override
	public void updateLink(String sid, long id, String type) {
		return;
	}

	@Override
	public void deleteLinks(String sid, long[] ids) {
		return;
	}

	@Override
	public void delete(String sid, long[] ids) {
		return;
	}

	@Override
	public void deleteDiscussions(String sid, long[] ids) {
		return;
	}

	@Override
	public long startDiscussion(String sid, long docId, String title, String message) {
		return 10;
	}

	@Override
	public int replyPost(String sid, long discussionId, int replyTo, String title, String message) {
		return 0;
	}

	@Override
	public void deletePosts(String sid, long discussionId, int[] ids) {
		return;
	}

	@Override
	public void makeImmutable(String sid, long[] ids, String comment) {
		return;
	}

	@Override
	public void lock(String sid, long[] ids, String comment) {
		return;
	}

	@Override
	public void unlock(String sid, long[] ids) {
		return;
	}

	@Override
	public void addDocuments(String sid, String language, boolean importZip) {
		System.out.println("** addDocuments");
		System.out.println("** servlet session:" + getThreadLocalRequest().getSession().getId());
	}

	@Override
	public void checkout(String sid, long id) {
		return;
	}

	@Override
	public void checkin(String sid, long docId, boolean major) {
		System.out.println("** checkin");
		System.out.println("** servlet session:" + getThreadLocalRequest().getSession().getId());
	}

	@Override
	public void linkDocuments(String sid, long[] inDocIds, long[] outDocIds) {
		return;
	}

	@Override
	public void restore(String sid, long docId) {
		return;
	}

	@Override
	public void addBookmarks(String sid, long[] docIds) {

	}

	@Override
	public void deleteBookmarks(String sid, long[] bookmarkIds) {

	}

	@Override
	public void updateBookmark(String sid, GUIBookmark bookmark) {

	}

	@Override
	public void markHistoryAsRead(String sid, String event) {

	}

	@Override
	public void markIndexable(String sid, long[] docIds) {
		return;
	}

	@Override
	public void markUnindexable(String sid, long[] docIds) {
		// TODO if the document is indexed, it must be removed from the index.
		return;
	}
}