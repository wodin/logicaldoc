package com.logicaldoc.gui.frontend.client.document.grid;

import java.util.ArrayList;

import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.grid.ListGridRecord;

/**
 * Utility methods for documents grids
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 7.0
 */
public class GridUtil {

	static long[] getIds(Record[] records) {
		long[] ids = new long[records.length];
		for (int i = 0; i < records.length; i++)
			ids[i] = Long.parseLong(records[i].getAttributeAsString("id"));
		return ids;
	}

	static GUIDocument[] toDocuments(Record[] records) {
		ArrayList<GUIDocument> docs = new ArrayList<GUIDocument>();
		if (records != null)
			for (Record record : records)
				docs.add(GridUtil.toDocument(record));
		return docs.toArray(new GUIDocument[0]);
	}

	static GUIDocument toDocument(Record record) {
		GUIDocument document = null;
		if (record != null) {
			document = new GUIDocument();
			document.setId(Long.parseLong(record.getAttribute("id")));
			if (record.getAttribute("docref") != null) {
				document.setDocRef(Long.parseLong(record.getAttribute("docref")));
				document.setDocRefType(record.getAttribute("docrefType"));
			}
			document.setExtResId(record.getAttributeAsString("extResId"));
			document.setTitle(record.getAttribute("title"));
			document.setType(record.getAttribute("type"));
			document.setFileName(record.getAttribute("filename"));
			document.setTemplate(record.getAttribute("template"));
			document.setVersion(record.getAttribute("version"));
			document.setFileVersion(record.getAttribute("fileVersion"));
			document.setImmutable("blank".equals(record.getAttributeAsString("immutable")) ? 0 : 1);
			
			if ("indexed".equals(record.getAttributeAsString("indexed")))
				document.setIndexed(Constants.INDEX_INDEXED);
			else if ("blank".equals(record.getAttributeAsString("indexed")))
				document.setIndexed(Constants.INDEX_TO_INDEX);
			else
				document.setIndexed(Constants.INDEX_SKIP);

			document.setSigned("blank".equals(record.getAttributeAsString("signed")) ? 0 : 1);
			document.setStamped("blank".equals(record.getAttributeAsString("stamped")) ? 0 : 1);

			if (record.getAttribute("lockUserId") != null)
				document.setLockUserId(Long.parseLong(record.getAttribute("lockUserId")));

			if (record.getAttribute("docref") != null) {
				document.setDocRef(Long.parseLong(record.getAttribute("docref")));
				document.setDocRefType(record.getAttribute("docrefType"));
			}
			if (record.getAttributeAsString("status") != null)
				document.setStatus(Integer.parseInt(record.getAttributeAsString("status")));
			document.setIcon(record.getAttribute("icon"));
			if (record.getAttributeAsDate("lastModified") != null)
				document.setLastModified(record.getAttributeAsDate("lastModified"));

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

	static ListGridRecord fromDocument(GUIDocument doc) {
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
		record.setAttribute("sourceDate", doc.getSourceDate());
		record.setAttribute("sourceAuthor", doc.getSourceAuthor());
		record.setAttribute("customId", doc.getCustomId());
		record.setAttribute("type", doc.getType());
		record.setAttribute("immutable", doc.getImmutable() == 1 ? "stop" : "blank");
		record.setAttribute("signed", doc.getSigned() == 1 ? "rosette" : "blank");
		record.setAttribute("stamped", doc.getStamped() == 1 ? "stamp" : "blank");
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
		record.setAttribute("source", doc.getSource());
		record.setAttribute("sourceId", doc.getSourceId());
		record.setAttribute("recipient", doc.getRecipient());
		record.setAttribute("object", doc.getObject());
		record.setAttribute("coverage", doc.getCoverage());
		record.setAttribute("rating", "rating" + doc.getRating());
		record.setAttribute("template", doc.getTemplate());
		record.setAttribute("lockUserId", doc.getLockUserId());

		if (doc.getIndexed() == Constants.INDEX_INDEXED)
			record.setAttribute("indexed", "indexed");
		else if (doc.getIndexed() == Constants.INDEX_SKIP)
			record.setAttribute("indexed", "unindexable");
		else
			record.setAttribute("indexed", "blank");

		if (doc.getStatus() == Constants.DOC_LOCKED)
			record.setAttribute("locked", "stop");
		else if (doc.getStatus() == Constants.DOC_CHECKED_OUT)
			record.setAttribute("locked", "page_edit");
		else
			record.setAttribute("locked", "blank");

		String[] extNames = Session.get().getInfo().getConfig("search.extattr").split(",");
		for (String name : extNames) {
			record.setAttribute("ext_" + name, doc.getValue(name));
		}

		return record;
	}

	static void updateRecord(GUIDocument document, Record record) {
		if (record == null && document == null)
			return;

		if ("folder".equals(record.getAttribute("type"))) {
			record.setAttribute("title", document.getFolder().getName());
			record.setAttribute("comment", document.getFolder().getDescription());
		} else {
			if (document.getIndexed() == Constants.INDEX_INDEXED)
				record.setAttribute("indexed", "indexed");
			else if (document.getIndexed() == Constants.INDEX_SKIP)
				record.setAttribute("indexed", "unindexable");
			else
				record.setAttribute("indexed", "blank");

			if (document.getStatus() == 2)
				record.setAttribute("locked", "lock");
			else if (document.getStatus() == 1)
				record.setAttribute("locked", "page_edit");
			else
				record.setAttribute("locked", "blank");
			record.setAttribute("status", document.getStatus());

			if (document.getLockUserId() != null)
				record.setAttribute("lockUserId", Long.toString(document.getLockUserId()));
			else
				record.setAttribute("lockUserId", (String) null);

			if (document.getImmutable() == 1)
				record.setAttribute("immutable", "stop");
			else
				record.setAttribute("immutable", "blank");

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
