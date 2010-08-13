package com.logicaldoc.gui.common.client.data;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceTextField;

/**
 * Datasource to retrieve all templates. It is based on Xml parsing.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class TemplatesDS extends DataSource {
	public TemplatesDS(boolean withEmpty) {
		setTitleField("template");
		setRecordXPath("/list/template");
		DataSourceTextField id = new DataSourceTextField("id");
		id.setPrimaryKey(true);
		DataSourceTextField name = new DataSourceTextField("name");
		setFields(id, name);
		setDataURL("data/templates.xml" + (withEmpty ? "?withempty=true" : ""));
		setClientOnly(true);
	}
}