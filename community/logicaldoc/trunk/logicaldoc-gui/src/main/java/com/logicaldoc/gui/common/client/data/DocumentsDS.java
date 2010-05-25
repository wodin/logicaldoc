package com.logicaldoc.gui.common.client.data;

import com.logicaldoc.gui.common.client.Session;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceDateTimeField;
import com.smartgwt.client.data.fields.DataSourceFloatField;
import com.smartgwt.client.data.fields.DataSourceImageField;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;

/**
 * Datasource to handle documents grid lists. It is based on Xml parsing
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
	 * @param max The marimum number of records (if not specified MAX_ROWS
	 *        is used)
	 */
	public DocumentsDS(Long folderId, String fileFilter, Integer max, boolean indexable) {
		setTitleField("title");
		setRecordXPath("/list/document");
		DataSourceTextField title = new DataSourceTextField("title");
		DataSourceTextField id = new DataSourceTextField("id");
		id.setPrimaryKey(true);
		id.setHidden(true);
		id.setRequired(true);
		DataSourceImageField icon = new DataSourceImageField("icon");
		DataSourceTextField customId = new DataSourceTextField("customId");
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
		DataSourceImageField indexed = new DataSourceImageField("indexed");
		DataSourceImageField locked = new DataSourceImageField("locked");
		DataSourceTextField lockUserId = new DataSourceTextField("lockUserId");
		DataSourceTextField filename = new DataSourceTextField("filename");
		DataSourceIntegerField status = new DataSourceIntegerField("status");
		lockUserId.setHidden(true);

		setFields(id, title, size, publisher, version, docref, lastModified, published, created, creator, customId,
				icon, immutable, indexed, locked, lockUserId, filename, status);
		setClientOnly(true);
		setDataURL("data/documents.xml?sid=" + Session.get().getSid() + "&folderId="
				+ (folderId != null ? folderId : "") + "&filename=" + (fileFilter != null ? fileFilter : "") + "&max="
				+ (max != null ? max : MAX) + "&index="
				+ (indexable ? "true" : "false"));
	}
}