package com.logicaldoc.gui.common.client.data;

import com.logicaldoc.gui.common.client.i18n.I18N;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceTextField;

/**
 * Datasource to retrieve all the attributes available for the documents
 * declared in the attribute sets
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

		DataSourceTextField label = new DataSourceTextField("label");

		DataSourceTextField type = new DataSourceTextField("type");
		type.setHidden(true);

		setFields(name, label, type);
		setTitleField("name");
		setDataURL("data/attributes.xml?locale=" + I18N.getLocale());
		setClientOnly(true);
	}
}