package com.logicaldoc.gui.common.client.data;

import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceTextField;

/**
 * Datasource to retrieve all groups. It is based on Xml parsing.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class GroupsDS extends DataSource {
	private static GroupsDS instance;

	private GroupsDS(Long excludeUserId) {
		setTitleField("name");
		setRecordXPath("/list/group");
		DataSourceTextField id = new DataSourceTextField("id", I18N.message("id"));
		id.setPrimaryKey(true);
		id.setHidden(true);

		DataSourceTextField name = new DataSourceTextField("name", I18N.message("name"));
		DataSourceTextField description = new DataSourceTextField("description");
		DataSourceTextField label = new DataSourceTextField("label");

		setFields(id, name, description, label);
		setDataURL("data/groups.xml?sid=" + Session.get().getSid() + "&locale=" + I18N.getLocale()
				+ (excludeUserId != null ? "&excludeUserId=" + excludeUserId : ""));

		setClientOnly(true);
	}

	public static GroupsDS get() {
		if (instance == null)
			instance = new GroupsDS(null);
		return instance;
	}

	/**
	 * Useful method to retrieve all groups that not contains the user with the
	 * given excludeUserId.
	 * 
	 * @param excludeUserId The user id
	 */
	public static GroupsDS get(long excludeUserId) {
		return new GroupsDS(excludeUserId);
	}
}