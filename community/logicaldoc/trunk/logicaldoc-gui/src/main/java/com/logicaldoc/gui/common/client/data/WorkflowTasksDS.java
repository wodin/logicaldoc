package com.logicaldoc.gui.common.client.data;

import com.logicaldoc.gui.common.client.Session;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceTextField;

public class WorkflowTasksDS extends DataSource {
	public WorkflowTasksDS(int type) {
		setTitleField("name");
		setRecordXPath("/list/workflowtask");
		DataSourceTextField id = new DataSourceTextField("id");
		id.setPrimaryKey(true);
		id.setRequired(true);
		DataSourceTextField name = new DataSourceTextField("name");
		DataSourceTextField workflow = new DataSourceTextField("workflow");
		DataSourceTextField pooledassignees = new DataSourceTextField("pooledassignees");
		setFields(id, name, workflow, pooledassignees);
		setDataURL("data/workflowtasks.xml?sid=" + Session.get().getSid() + "&type=" + type);
		setClientOnly(true);
	}
}
