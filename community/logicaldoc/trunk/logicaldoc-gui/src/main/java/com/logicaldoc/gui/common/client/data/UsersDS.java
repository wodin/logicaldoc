package com.logicaldoc.gui.common.client.data;

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
	private static UsersDS instance;

	private UsersDS(Long grpId) {
		setTitleField("label");
		setRecordXPath("/list/user");

		DataSourceTextField id = new DataSourceTextField("id");
		id.setPrimaryKey(true);

		DataSourceTextField username = new DataSourceTextField("username");
		DataSourceTextField groupId = new DataSourceTextField("groupId");
		DataSourceTextField label = new DataSourceTextField("label");
		DataSourceImageField active = new DataSourceImageField("active");
		DataSourceTextField name = new DataSourceTextField("name");
		DataSourceTextField firstName = new DataSourceTextField("firstName");
		DataSourceTextField email = new DataSourceTextField("email");
		DataSourceTextField phone = new DataSourceTextField("phone");
		DataSourceTextField cell = new DataSourceTextField("cell");

		setFields(id, username, label, groupId, active, name, firstName, email, phone, cell);
		setDataURL("data/users.xml" + (grpId != null ? "?groupId=" + grpId : ""));
		setClientOnly(true);
	}

	public static UsersDS get() {
		if (instance == null)
			instance = new UsersDS(null);
		return instance;
	}

	public static UsersDS get(long groupId) {
		return new UsersDS(groupId);
	}
}