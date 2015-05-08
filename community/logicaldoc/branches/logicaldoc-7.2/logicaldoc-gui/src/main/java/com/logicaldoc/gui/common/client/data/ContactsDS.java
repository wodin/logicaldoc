package com.logicaldoc.gui.common.client.data;

import com.logicaldoc.gui.common.client.Session;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceTextField;

/**
 * Data Source to handle user's contacts grid lists. It is based on Xml parsing.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class ContactsDS extends DataSource {

	public ContactsDS() {
		setTitleField("email");
		setRecordXPath("/list/contact");
		DataSourceTextField id = new DataSourceTextField("id");
		id.setPrimaryKey(true);
		id.setHidden(true);
		id.setRequired(true);
		DataSourceTextField email = new DataSourceTextField("email");
		DataSourceTextField firstName = new DataSourceTextField("firstName");
		DataSourceTextField lastName = new DataSourceTextField("lastName");
		DataSourceTextField company = new DataSourceTextField("company");

		setFields(id, email, firstName, lastName, company);
		setDataURL("data/contacts.xml?sid=" + Session.get().getSid() + "&userId=" + Session.get().getUser().getId());
		setClientOnly(true);
	}
}