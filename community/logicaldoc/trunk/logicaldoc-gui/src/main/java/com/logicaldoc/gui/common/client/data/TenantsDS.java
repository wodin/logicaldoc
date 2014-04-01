package com.logicaldoc.gui.common.client.data;

import com.logicaldoc.gui.common.client.Session;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceTextField;

/**
 * Datasource to retrieve all tenants. It is based on Xml parsing.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.9
 */
public class TenantsDS extends DataSource {
	public TenantsDS() {
		setTitleField("label");
		setRecordXPath("/list/tenant");

		DataSourceTextField id = new DataSourceTextField("id");
		id.setPrimaryKey(true);

		DataSourceTextField name = new DataSourceTextField("name");
		DataSourceTextField displayName = new DataSourceTextField("displayName");
		DataSourceTextField email = new DataSourceTextField("email");
		DataSourceTextField city = new DataSourceTextField("city");
		DataSourceTextField country = new DataSourceTextField("country");
		DataSourceTextField telephone = new DataSourceTextField("telephone");
		DataSourceTextField postalCode = new DataSourceTextField("postalCode");
		DataSourceTextField state = new DataSourceTextField("state");
		DataSourceTextField address = new DataSourceTextField("address");

		setFields(id, name, displayName, email, city, country, telephone, postalCode, state, address);
		setDataURL("data/tenants.xml?sid=" + Session.get().getSid());
		setClientOnly(true);
	}
}