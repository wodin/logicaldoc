package com.logicaldoc.gui.common.client.data;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceTextField;

/**
 * Datasource to retrieve all the attributes declared in the attribute sets
 * 
 * @author Marco Meschieri - LogicalDOC
 * @since 7.6
 */
public class AttributesDS extends DataSource {
	public AttributesDS() {
		setTitleField("attributes");
		setRecordXPath("/list/attribute");
		DataSourceTextField name = new DataSourceTextField("name");
		name.setPrimaryKey(true);
		
		DataSourceTextField type = new DataSourceTextField("type");
		type.setHidden(true);
		
		setFields(name, type);
		setTitleField("name");
		setDataURL("data/attributes.xml");
		setClientOnly(true);
	}
}