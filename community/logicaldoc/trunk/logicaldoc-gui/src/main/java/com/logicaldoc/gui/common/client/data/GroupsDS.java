package com.logicaldoc.gui.common.client.data;

import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.frontend.client.Frontend;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceTextField;

/**
 * Datasource to retrieve all templates. It is based on Xml parsing.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class GroupsDS extends DataSource {
	private static GroupsDS instance;

	private GroupsDS() {
		setTitleField("name");
		setRecordXPath("/list/group");
		DataSourceTextField id = new DataSourceTextField("id", Frontend.messages().id());
		id.setPrimaryKey(true);
		id.setHidden(true);

		DataSourceTextField name = new DataSourceTextField("name", Frontend.messages().name());
		DataSourceTextField description = new DataSourceTextField("description");
		DataSourceTextField label = new DataSourceTextField("label");

		setFields(id, name, description, label);
		setDataURL("data/groups.xml?sid=" + Session.get().getSid());

		setClientOnly(true);
	}

	public static GroupsDS get() {
		if (instance == null)
			instance = new GroupsDS();
		return instance;
	}
}