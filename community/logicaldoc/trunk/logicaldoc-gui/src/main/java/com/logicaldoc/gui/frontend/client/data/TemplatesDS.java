package com.logicaldoc.gui.frontend.client.data;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceTextField;

/**
 * Datasource to retrieve all templates. It is based on Xml parsing.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class TemplatesDS extends DataSource {
	private static TemplatesDS instance;

	private TemplatesDS() {
		setID("TemplatesDS");
		setTitleField("template");
		setRecordXPath("/list/template");
		DataSourceTextField idField = new DataSourceTextField("id");
		idField.setPrimaryKey(true);
		DataSourceTextField nameField = new DataSourceTextField("name");
		setFields(idField, nameField);
		setDataURL("data/templates.xml");
		setClientOnly(true);
	}

	public static TemplatesDS getInstance() {
		if (instance == null)
			instance = new TemplatesDS();
		return instance;
	}
}