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
public class GroupsDS extends DataSource {
	private static GroupsDS instance;

	private GroupsDS() {
		setTitleField("name");
		setRecordXPath("/list/group");
		DataSourceTextField id = new DataSourceTextField("id", I18N.getMessage("id"));
		id.setPrimaryKey(true);
		id.setHidden(true);

		DataSourceTextField name = new DataSourceTextField("name", I18N.getMessage("name"));
		DataSourceTextField description = new DataSourceTextField("description");
		DataSourceTextField label = new DataSourceTextField("label");

		setFields(id, name, description, label);
		setDataURL("data/groups.xml");

		setClientOnly(true);
	}

	public static GroupsDS getInstance() {
		if (instance == null)
			instance = new GroupsDS();
		return instance;
	}
}