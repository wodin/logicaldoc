package com.logicaldoc.gui.frontend.client.document.grid;

import java.util.ArrayList;
import java.util.Date;

import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.grid.ListGridRecord;

/**
 * Utility methods for documents grids
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 7.0
 */
public class GridUtil {

	static public long[] getIds(Record[] records) {
		long[] ids = new long[records.length];
		for (int i = 0; i < records.length; i++)
			ids[i] = Long.parseLong(records[i].getAttributeAsString("id"));
		return ids;
	}

	static public GUIDocument[] toDocuments(Record[] records) {
		ArrayList<GUIDocument> docs = new ArrayList<GUIDocument>();
		if (records != null)
			for (Record record : records)
				docs.add(GridUtil.toDocument(record));
		return docs.toArray(new GUIDocument[0]);
	}

	static public GUIDocument toDocument(Record record) {
		GUIDocument document = null;
		if (record != null) {
			document = new GUIDocument();
			document.setId(Long.parseLong(record.getAttribute("id")));
			if (record.getAttribute("docref") != null) {
				document.setDocRef(Long.parseLong(record.getAttribute("docref")));
				document.setDocRefType(record.getAttribute("docrefType"));
			}
			document.setExtResId(record.getAttributeAsString("extResId"));
			document.setCustomId(record.getAttributeAsString("customId"));
			document.setTitle(record.getAttribute("title"));
			document.setType(record.getAttribute("type"));
			document.setFileName(record.getAttribute("filename"));
			document.setTemplate(record.getAttribute("template"));
			document.setVersion(record.getAttribute("version"));
			document.setFileVersion(record.getAttribute("fileVersion"));

			document.setPublisher(record.getAttributeAsString("publisher"));

			if (record.getAttributeAsFloat("size") != null)
				document.setFileSize(record.getAttributeAsFloat("size"));

			document.setIndexed(record.getAttributeAsInt("indexed"));
			document.setStatus(record.getAttributeAsInt("status"));
			document.setImmutable(record.getAttributeAsInt("immutable"));
			document.setPasswordProtected(record.getAttributeAsBoolean("password"));
			document.setSigned(record.getAttributeAsInt("signed"));
			document.setStamped(record.getAttributeAsInt("stamped"));

			if (record.getAttribute("lockUserId") != null)
				document.setLockUserId(Long.parseLong(record.getAttribute("lockUserId")));

			if (record.getAttribute("docref") != null) {
				document.setDocRef(Long.parseLong(record.getAttribute("docref")));
				document.setDocRefType(record.getAttribute("docrefType"));
			}

			document.setIcon(record.getAttribute("icon"));
			if (record.getAttributeAsDate("lastModified") != null)
				document.setLastModified(record.getAttributeAsDate("lastModified"));
			if (record.getAttributeAsDate("published") != null)
				document.setDate(record.getAttributeAsDate("published"));
			if (record.getAttributeAsDate("created") != null)
				document.setCreation(record.getAttributeAsDate("created"));

			GUIFolder folder = new GUIFolder();
			if ("folder".equals(document.getType())) {
				folder.setId(Long.parseLong(record.getAttributeAsString("id")));
			} else if (record.getAttributeAsString("folderId") != null)
				folder.setId(Long.parseLong(record.getAttributeAsString("folderId")));
			else
				folder.setId(Session.get().getCurrentFolder().getId());
			folder.setName(record.getAttribute("title"));
			folder.setDescription(record.getAttribute("comment"));

			document.setFolder(folder);
		}
		return document;
	}

	static public ListGridRecord fromDocument(GUIDocument doc) {
		ListGridRecord record = new ListGridRecord();
		record.setAttribute("id", doc.getId());
		record.setAttribute("docref", doc.getDocRef());
		record.setAttribute("docrefType", doc.getDocRefType());
		record.setAttribute("title", doc.getTitle());
		record.setAttribute("size", doc.getFileSize());
		record.setAttribute("icon", doc.getIcon());
		record.setAttribute("version", doc.getVersion());
		record.setAttribute("lastModified", doc.getLastModified());
		record.setAttribute("published", doc.getDate());
		record.setAttribute("publisher", doc.getPublisher());
		record.setAttribute("creator", doc.getCreator());
		record.setAttribute("created", doc.getCreation());
		record.setAttribute("customId", doc.getCustomId());
		record.setAttribute("type", doc.getType());
		record.setAttribute("immutable", doc.getImmutable() == 1 ? "stop" : "blank");
		record.setAttribute("password", doc.isPasswordProtected());
		record.setAttribute("signed", doc.getSigned());
		record.setAttribute("stamped", doc.getStamped());
		record.setAttribute("filename", doc.getFileName());
		record.setAttribute("fileVersion", doc.getFileVersion());
		record.setAttribute("fileVersion", doc.getFileVersion());
		record.setAttribute("comment", doc.getComment());
		record.setAttribute("workflowStatus", doc.getWorkflowStatus());
		record.setAttribute("startPublishing", doc.getStartPublishing());
		record.setAttribute("stopPublishing", doc.getStopPublishing());
		record.setAttribute("publishedStatus", doc.getPublished() == 1 ? "yes" : "no");
		record.setAttribute("score", doc.getScore());
		record.setAttribute("summary", doc.getSummary());
		record.setAttribute("lockUserId", doc.getLockUserId());
		record.setAttribute("folderId", doc.getFolder().getId());
		record.setAttribute("folder", doc.getFolder().getName());
		record.setAttribute("rating", "rating" + doc.getRating());
		record.setAttribute("template", doc.getTemplate());
		record.setAttribute("lockUserId", doc.getLockUserId());
		record.setAttribute("indexed", doc.getIndexed());
		record.setAttribute("status", doc.getStatus());

		String[] extNames = Session.get().getInfo().getConfig("search.extattr").split(",");
		for (String name : extNames) {
			Object value = doc.getValue(name);
			if (value instanceof Date)
				value = I18N.formatDateShort((Date) value);
			record.setAttribute("ext_" + name, value);
		}

		return record;
	}

	static public void updateRecord(GUIDocument document, Record record) {
		if (record == null && document == null)
			return;

		if ("folder".equals(record.getAttribute("type"))) {
			record.setAttribute("title", document.getFolder().getName());
			record.setAttribute("comment", document.getFolder().getDescription());
		} else {
			record.setAttribute("indexed", document.getIndexed());
			record.setAttribute("status", document.getStatus());
			record.setAttribute("immutable", document.getImmutable());
			record.setAttribute("signed", document.getSigned());
			record.setAttribute("password", document.isPasswordProtected());

			if (document.getLockUserId() != null)
				record.setAttribute("lockUserId", Long.toString(document.getLockUserId()));
			else
				record.setAttribute("lockUserId", (String) null);

			if (document.getLockUser() != null)
				record.setAttribute("lockUser", document.getLockUser());
			else
				record.setAttribute("lockUser", (String) null);

			record.setAttribute("title", document.getTitle());
			record.setAttribute("customId", document.getCustomId());
			record.setAttribute("version", document.getVersion());
			record.setAttribute("fileVersion", document.getFileVersion());
			record.setAttribute("size", document.getFileSize());
			record.setAttribute("lastModified", document.getLastModified());
			record.setAttribute("publisher", document.getPublisher());
			record.setAttribute("published", document.getDate());
			record.setAttribute("creator", document.getCreator());
			record.setAttribute("created", document.getCreation());
			record.setAttribute("rating", "rating" + document.getRating());
			record.setAttribute("extResId", document.getExtResId());
			record.setAttribute("template", document.getTemplate());
		}
	}
}
