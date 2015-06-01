package com.logicaldoc.gui.common.client.data;

import com.logicaldoc.gui.common.client.Session;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceTextField;

/**
 * Data source to retrieve all forms. It is based on Xml parsing.
 * 
 * @author Marco Meschieri - LogicalDOC
 * @since 7.3
 */
public class FormsDS extends DataSource {

	public FormsDS() {
		setTitleField("name");
		setRecordXPath("/list/form");
		DataSourceTextField id = new DataSourceTextField("id");
		id.setPrimaryKey(true);
		DataSourceTextField name = new DataSourceTextField("name");

		setFields(id, name);
		setDataURL("data/forms.xml?sid=" + Session.get().getSid());
		setClientOnly(true);
	}
}