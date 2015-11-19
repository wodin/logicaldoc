package com.logicaldoc.gui.common.client.data;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceTextField;

public class WorkflowTasksDS extends DataSource {
	public WorkflowTasksDS(Integer type, String taskId) {
		setTitleField("name");
		setRecordXPath("/list/workflowtask");
		DataSourceTextField id = new DataSourceTextField("id");
		id.setPrimaryKey(true);
		id.setRequired(true);
		DataSourceTextField processId = new DataSourceTextField("processId");
		DataSourceTextField name = new DataSourceTextField("name");
		DataSourceTextField workflow = new DataSourceTextField("workflow");
		DataSourceTextField pooledassignees = new DataSourceTextField("pooledassignees");
		setFields(id, processId, name, workflow, pooledassignees);
		setDataURL("data/workflowtasks.xml?1=1" + (type != null ? "&type=" + type : "")
				+ (taskId != null ? "&taskId=" + taskId : ""));
		setClientOnly(true);
	}
}
