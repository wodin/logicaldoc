package com.logicaldoc.gui.common.client.data;

import com.logicaldoc.gui.common.client.I18N;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceTextField;

/**
 * Datasource to retrieve all templates. It is based on Xml parsing.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class UsersDS extends DataSource {
	private static UsersDS instance;

	private UsersDS() {
		setTitleField("username");
		setRecordXPath("/list/user");
		DataSourceTextField id = new DataSourceTextField("id", I18N.getMessage("id"));
		id.setPrimaryKey(true);
		id.setHidden(true);

		DataSourceTextField username = new DataSourceTextField("username", I18N.getMessage("username"));
		DataSourceTextField groupId = new DataSourceTextField("groupId");
		DataSourceTextField label = new DataSourceTextField("label");

		setFields(id, username, label, groupId);
		setDataURL("data/users.xml");

		setClientOnly(true);
	}

	public static UsersDS getInstance() {
		if (instance == null)
			instance = new UsersDS();
		return instance;
	}
}