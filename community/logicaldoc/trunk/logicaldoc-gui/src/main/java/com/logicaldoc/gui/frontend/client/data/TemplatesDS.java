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

	private static TemplatesDS instanceWithEmpty;

	private TemplatesDS(boolean withEmpty) {
		setTitleField("template");
		setRecordXPath("/list/template");
		DataSourceTextField idField = new DataSourceTextField("id");
		idField.setPrimaryKey(true);
		DataSourceTextField nameField = new DataSourceTextField("name");
		setFields(idField, nameField);
		setDataURL("data/templates.xml" + (withEmpty ? "?withempty=true" : ""));
		setClientOnly(true);
	}

	public static TemplatesDS getInstance() {
		if (instance == null)
			instance = new TemplatesDS(false);
		return instance;
	}

	public static TemplatesDS getInstanceWithEmpty() {
		if (instanceWithEmpty == null)
			instanceWithEmpty = new TemplatesDS(true);
		return instanceWithEmpty;
	}
}