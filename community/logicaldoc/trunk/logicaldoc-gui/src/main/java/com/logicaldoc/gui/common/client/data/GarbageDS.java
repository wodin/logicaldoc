package com.logicaldoc.gui.common.client.data;

import com.logicaldoc.gui.common.client.Session;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceDateTimeField;
import com.smartgwt.client.data.fields.DataSourceImageField;
import com.smartgwt.client.data.fields.DataSourceTextField;

/**
 * Datasource to handle the garbage of the current user.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class GarbageDS extends DataSource {
	public GarbageDS() {
		setTitleField("title");
		setRecordXPath("/list/document");
		DataSourceTextField title = new DataSourceTextField("title");
		DataSourceTextField id = new DataSourceTextField("id");
		id.setPrimaryKey(true);
		id.setHidden(true);
		id.setRequired(true);
		DataSourceImageField icon = new DataSourceImageField("icon");
		DataSourceTextField customId = new DataSourceTextField("customId");
		DataSourceDateTimeField lastModified = new DataSourceDateTimeField("lastModified");
		DataSourceTextField folderId = new DataSourceTextField("folderId");

		setFields(id, title, customId, icon, lastModified, folderId);
		setClientOnly(true);
		setDataURL("data/garbage.xml?sid=" + Session.get().getSid());
	}
}