package com.logicaldoc.gui.common.client.data;

import com.logicaldoc.gui.common.client.Session;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;

/**
 * Datasource to retrieve all templates. It is based on Xml parsing.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class TemplatesDS extends DataSource {
	public TemplatesDS(boolean withEmpty, Long folderId, Integer templateType, boolean count) {
		setTitleField("template");
		setRecordXPath("/list/template");
		DataSourceTextField id = new DataSourceTextField("id");
		id.setPrimaryKey(true);
		DataSourceTextField name = new DataSourceTextField("name");
		DataSourceTextField readonly = new DataSourceTextField("readonly");
		DataSourceIntegerField documents = new DataSourceIntegerField("documents");
		DataSourceIntegerField type = new DataSourceIntegerField("type");
		DataSourceTextField category = new DataSourceTextField("category");
		DataSourceIntegerField signRequired = new DataSourceIntegerField("signrequired");
		setFields(id, name, documents, readonly, type, category, signRequired);
		setDataURL("data/templates.xml?withempty=" + withEmpty + "&count=" + count
				+ (folderId != null ? "&folderId=" + folderId : "")
				+ (templateType != null ? "&templateType=" + templateType : "")
				+ (Session.get() != null ? "&sid=" + Session.get().getSid() : ""));
		setClientOnly(true);
	}
}