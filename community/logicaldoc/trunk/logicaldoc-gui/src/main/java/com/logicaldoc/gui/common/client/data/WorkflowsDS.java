package com.logicaldoc.gui.common.client.data;

import com.logicaldoc.gui.common.client.Session;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceTextField;

public class WorkflowsDS extends DataSource {
	public WorkflowsDS(Long folderId) {
		setTitleField("name");
		setRecordXPath("/list/workflow");
		DataSourceTextField id = new DataSourceTextField("id");
		id.setPrimaryKey(true);
		id.setRequired(true);
		DataSourceTextField name = new DataSourceTextField("name");
		setFields(id, name);
		setDataURL("data/workflows.xml?sid=" + Session.get().getSid()
				+ (folderId != null ? "&folderId=" + folderId : ""));
		setClientOnly(true);
	}
}