package com.logicaldoc.gui.common.client.data;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceImageField;
import com.smartgwt.client.data.fields.DataSourceTextField;

/**
 * Datasource to handle documents grid lists. It is based on Xml parsing
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class LinksDS extends DataSource {
	public LinksDS(long docId) {
		setID("LinksDS");
		
		setTitleField("title");
		setRecordXPath("/list/link");
		DataSourceTextField title = new DataSourceTextField("title");
		DataSourceTextField linkId = new DataSourceTextField("linkId");
		DataSourceTextField folderId = new DataSourceTextField("folderId");
		DataSourceTextField documentId = new DataSourceTextField("documentId");
		documentId.setPrimaryKey(true);
		documentId.setHidden(true);
		documentId.setRequired(true);

		DataSourceImageField icon = new DataSourceImageField("icon");
		DataSourceTextField direction = new DataSourceTextField("direction");
		DataSourceTextField type = new DataSourceTextField("type");

		DataSourceTextField parent = new DataSourceTextField("parent", "Parent ID");
		parent.setRequired(true);
		parent.setForeignKey("LinksDS.documentId");
		parent.setRootValue("/");
		
		setFields(linkId, parent, folderId, documentId, title, icon, direction, type);
		setClientOnly(true);
		
		setDataURL("data/links.xml?docId=" + docId);
		setClientOnly(false);
	}
}