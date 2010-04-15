package com.logicaldoc.gui.frontend.client.data;

import com.logicaldoc.gui.common.client.Session;
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
		DataSourceTextField id = new DataSourceTextField("id");
		id.setPrimaryKey(true);
		id.setHidden(true);
		id.setRequired(true);
		DataSourceTextField folderId = new DataSourceTextField("folderId");

		DataSourceImageField icon = new DataSourceImageField("icon");
		DataSourceTextField direction = new DataSourceTextField("direction");
		DataSourceTextField type = new DataSourceTextField("type");

		setFields(id, folderId, title, icon, direction, type);
		setClientOnly(true);
		setDataURL("data/links.xml?sid=" + Session.getInstance().getSid() + "&docId=" + docId);
	}
}