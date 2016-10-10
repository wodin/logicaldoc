package com.logicaldoc.gui.common.client.data;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;

/**
 * Datasource to retrieve all templates. It is based on Xml parsing.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class AttributeSetsDS extends DataSource {
	public AttributeSetsDS(boolean withEmpty, Integer type) {
		setTitleField("attributeset");
		setRecordXPath("/list/attributeset");
		DataSourceTextField id = new DataSourceTextField("id");
		id.setPrimaryKey(true);
		DataSourceTextField name = new DataSourceTextField("name");
		DataSourceTextField readonly = new DataSourceTextField("readonly");
		DataSourceIntegerField ttype = new DataSourceIntegerField("type");
		setFields(id, name, readonly, ttype);
		setDataURL("data/attributesets.xml?withempty=" + withEmpty + (type != null ? "&type=" + type : ""));
		setClientOnly(true);
	}
}