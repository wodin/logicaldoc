package com.logicaldoc.gui.common.client.data;

import com.logicaldoc.gui.common.client.Session;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceImageField;
import com.smartgwt.client.data.fields.DataSourceTextField;

/**
 * Datasource to retrieve all templates. It is based on Xml parsing.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class UsersDS extends DataSource {
	public UsersDS(Long grpId) {
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
		DataSourceTextField phone = new DataSourceTextField("phone");
		DataSourceTextField cell = new DataSourceTextField("cell");
		DataSourceTextField usergroup = new DataSourceTextField("usergroup");

		setFields(id, username, label, enabled, name, firstName, email, phone, cell, usergroup);
		setDataURL("data/users.xml?sid=" + Session.get().getSid() + (grpId != null ? "&groupId=" + grpId : ""));
		setClientOnly(true);
	}

	public static UsersDS get(long groupId) {
		return new UsersDS(groupId);
	}
}