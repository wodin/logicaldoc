package com.logicaldoc.gui.common.client.data;

import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceBooleanField;
import com.smartgwt.client.data.fields.DataSourceDateTimeField;
import com.smartgwt.client.data.fields.DataSourceImageField;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;

/**
 * Datasource to retrieve the bookmarks of the current user.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class TasksDS extends DataSource {

	private static TasksDS instance;

	public static TasksDS get() {
		if (instance == null)
			instance = new TasksDS();
		return instance;
	}

	private TasksDS() {
		setTitleField("name");
		setRecordXPath("/list/task");
		DataSourceTextField name = new DataSourceTextField("name");
		name.setPrimaryKey(true);
		name.setRequired(true);

		DataSourceTextField label = new DataSourceTextField("label");
		DataSourceImageField enabledIcon = new DataSourceImageField("enabledIcon");
		DataSourceBooleanField enabled = new DataSourceBooleanField("eenabled");
		DataSourceIntegerField status = new DataSourceIntegerField("status");
		DataSourceTextField scheduling = new DataSourceTextField("scheduling");
		DataSourceIntegerField progress = new DataSourceIntegerField("progress");
		DataSourceDateTimeField lastStart = new DataSourceDateTimeField("lastStart");
		DataSourceDateTimeField nextStart = new DataSourceDateTimeField("nextStart");
		DataSourceBooleanField indeterminate = new DataSourceBooleanField("indeterminate");

		setFields(name, label, enabledIcon, enabled, status, scheduling, progress, lastStart, nextStart, indeterminate);
		setClientOnly(true);
		setDataURL("data/tasks.xml?sid=" + Session.get().getSid() + "&locale=" + I18N.getLocale());
	}
}