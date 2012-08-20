package com.logicaldoc.gui.common.client.data;

import java.util.ArrayList;
import java.util.List;

import com.logicaldoc.gui.common.client.Session;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.fields.DataSourceDateTimeField;
import com.smartgwt.client.data.fields.DataSourceFloatField;
import com.smartgwt.client.data.fields.DataSourceImageField;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;

/**
 * Data Source to handle documents grid lists. It is based on Xml parsing
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class DocumentsDS extends DataSource {

	private static final Integer MAX = 100;

	/**
	 * Constructor.
	 * 
	 * @param folderId The folder to be listed (optional)
	 * @param filename A filter on the file nale (optional)
	 * @param max The maximum number of records (if not specified MAX_ROWS is
	 *        used)
	 * @param indexed The indexed flag
	 * @param barcoded The barcoded flag
	 */
	public DocumentsDS(Long folderId, String fileFilter, Integer max, Integer indexed, Integer barcoded) {
		setTitleField("title");
		setRecordXPath("/list/document");
		DataSourceTextField title = new DataSourceTextField("title");
		DataSourceTextField id = new DataSourceTextField("id");
		id.setPrimaryKey(true);
		id.setHidden(true);
		id.setRequired(true);
		DataSourceImageField icon = new DataSourceImageField("icon");
		DataSourceTextField customId = new DataSourceTextField("customId");
		DataSourceTextField type = new DataSourceTextField("type");
		DataSourceTextField version = new DataSourceTextField("version");
		DataSourceTextField docref = new DataSourceTextField("docref");
		docref.setHidden(true);
		DataSourceTextField publisher = new DataSourceTextField("publisher");
		DataSourceTextField creator = new DataSourceTextField("creator");
		DataSourceFloatField size = new DataSourceFloatField("size");
		DataSourceDateTimeField lastModified = new DataSourceDateTimeField("lastModified");
		DataSourceDateTimeField published = new DataSourceDateTimeField("published");
		DataSourceDateTimeField created = new DataSourceDateTimeField("created");
		DataSourceImageField immutable = new DataSourceImageField("immutable");
		DataSourceImageField iindexed = new DataSourceImageField("indexed");
		DataSourceImageField signed = new DataSourceImageField("signed");
		DataSourceImageField locked = new DataSourceImageField("locked");
		DataSourceTextField lockUserId = new DataSourceTextField("lockUserId");
		DataSourceTextField filename = new DataSourceTextField("filename");
		DataSourceTextField fileVersion = new DataSourceTextField("fileVersion");
		DataSourceIntegerField status = new DataSourceIntegerField("status");
		lockUserId.setHidden(true);
		DataSourceTextField aliasId = new DataSourceTextField("aliasId");
		aliasId.setHidden(true);
		DataSourceDateTimeField sourceDate = new DataSourceDateTimeField("sourceDate");
		sourceDate.setHidden(true);
		DataSourceTextField sourceAuthor = new DataSourceTextField("sourceAuthor");
		sourceAuthor.setHidden(true);
		DataSourceImageField rating = new DataSourceImageField("rating");
		DataSourceTextField comment = new DataSourceTextField("comment");
		DataSourceIntegerField wfStatus = new DataSourceIntegerField("workflowStatus");
		DataSourceTextField publishedStatus = new DataSourceTextField("publishedStatus");
		publishedStatus.setHidden(true);
		DataSourceDateTimeField startPublishing = new DataSourceDateTimeField("startPublishing");
		DataSourceDateTimeField stopPublishing = new DataSourceDateTimeField("stopPublishing");

		List<DataSourceField> fields = new ArrayList<DataSourceField>();
		fields.add(id);
		fields.add(title);
		fields.add(type);
		fields.add(size);
		fields.add(publisher);
		fields.add(version);
		fields.add(docref);
		fields.add(lastModified);
		fields.add(published);
		fields.add(created);
		fields.add(creator);
		fields.add(sourceDate);
		fields.add(sourceAuthor);
		fields.add(customId);
		fields.add(icon);
		fields.add(immutable);
		fields.add(iindexed);
		fields.add(signed);
		fields.add(locked);
		fields.add(lockUserId);
		fields.add(filename);
		fields.add(status);
		fields.add(rating);
		fields.add(fileVersion);
		fields.add(comment);
		fields.add(wfStatus);
		fields.add(publishedStatus);
		fields.add(startPublishing);
		fields.add(stopPublishing);

		String[] extNames = Session.get().getInfo().getConfig("search.extattr").split(",");
		for (String name : extNames) {
			DataSourceTextField ext = new DataSourceTextField("ext_" + name, name);
			ext.setHidden(true);
			ext.setCanFilter(true);
			fields.add(ext);
		}

		setFields(fields.toArray(new DataSourceField[0]));
		setClientOnly(true);

		if (barcoded == null)
			setDataURL("data/documents.xml?sid=" + Session.get().getSid() + "&locale="
					+ Session.get().getUser().getLanguage() + "&folderId=" + (folderId != null ? folderId : "")
					+ "&filename=" + (fileFilter != null ? fileFilter : "") + "&max=" + (max != null ? max : MAX)
					+ "&indexed=" + (indexed != null ? indexed.toString() : ""));
		else
			setDataURL("data/tobarcode.xml?sid=" + Session.get().getSid() + "&max=" + (max != null ? max : MAX));
	}

	public DocumentsDS(String docIds) {
		setTitleField("title");
		setRecordXPath("/list/document");
		DataSourceTextField title = new DataSourceTextField("title");
		DataSourceTextField id = new DataSourceTextField("id");
		DataSourceTextField filename = new DataSourceTextField("filename");
		DataSourceTextField fileVersion = new DataSourceTextField("fileVersion");
		DataSourceTextField version = new DataSourceTextField("version");

		id.setPrimaryKey(true);
		id.setRequired(true);
		DataSourceDateTimeField lastModified = new DataSourceDateTimeField("lastModified");
		DataSourceImageField icon = new DataSourceImageField("icon");
		DataSourceTextField folderId = new DataSourceTextField("folderId");

		setFields(id, icon, title, lastModified, folderId, version, fileVersion, filename);
		setClientOnly(true);
		setDataURL("data/documents.xml?sid=" + Session.get().getSid() + "&docIds=" + docIds);
	}

	public DocumentsDS(int status, int max) {
		setTitleField("title");
		setRecordXPath("/list/document");
		DataSourceTextField title = new DataSourceTextField("title");
		DataSourceTextField id = new DataSourceTextField("id");
		id.setPrimaryKey(true);
		id.setRequired(true);
		DataSourceDateTimeField lastModified = new DataSourceDateTimeField("lastModified");
		DataSourceImageField icon = new DataSourceImageField("icon");
		DataSourceTextField folderId = new DataSourceTextField("folderId");
		DataSourceTextField version = new DataSourceTextField("version");
		DataSourceTextField filename = new DataSourceTextField("filename");
		DataSourceTextField fileVersion = new DataSourceTextField("fileVersion");

		setFields(id, icon, title, lastModified, folderId, version, fileVersion, filename);
		setClientOnly(true);
		setDataURL("data/documents.xml?sid=" + Session.get().getSid() + "&status=" + status + "&max=" + max);
	}
}