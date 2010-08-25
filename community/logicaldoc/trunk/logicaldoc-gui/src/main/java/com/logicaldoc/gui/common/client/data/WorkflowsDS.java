package com.logicaldoc.gui.common.client.data;

import com.logicaldoc.gui.common.client.Session;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceTextField;

public class WorkflowsDS extends DataSource {
	public WorkflowsDS() {
		setTitleField("workflow");
		setRecordXPath("/list/workflow");
		DataSourceTextField id = new DataSourceTextField("id");
		id.setPrimaryKey(true);
		id.setRequired(true);

		DataSourceTextField name = new DataSourceTextField("name");

		setFields(id, name);
		setClientOnly(true);
		setDataURL("data/workflows.xml?sid=" + Session.get().getSid());
	}
}