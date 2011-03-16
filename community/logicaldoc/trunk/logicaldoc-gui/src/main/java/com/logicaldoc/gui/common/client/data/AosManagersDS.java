package com.logicaldoc.gui.common.client.data;

import com.logicaldoc.gui.common.client.Session;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceImageField;
import com.smartgwt.client.data.fields.DataSourceTextField;

/**
 * Datasource to retrieve all Aos user managers. It is based on Xml parsing.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.1
 */
public class AosManagersDS extends DataSource {
	public AosManagersDS() {
		setTitleField("label");
		setRecordXPath("/list/user");

		DataSourceTextField id = new DataSourceTextField("id");
		id.setPrimaryKey(true);

		DataSourceTextField username = new DataSourceTextField("username");
		DataSourceTextField label = new DataSourceTextField("label");
		DataSourceImageField enabled = new DataSourceImageField("eenabled");
		DataSourceTextField name = new DataSourceTextField("name");
		DataSourceTextField firstName = new DataSourceTextField("firstName");
		DataSourceTextField email = new DataSourceTextField("email");

		setFields(id, username, label, enabled, name, firstName, email);
		setDataURL("data/aosmanagers.xml?sid=" + Session.get().getSid());
		setClientOnly(true);
	}
}
