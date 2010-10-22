package com.logicaldoc.gui.common.client.data;

import com.logicaldoc.gui.common.client.Session;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceTextField;

public class WorkflowsDS extends DataSource {
	public WorkflowsDS(Long folderId, boolean retrieveDefinitions, boolean checkUser) {
		setTitleField("name");
		setRecordXPath("/list/workflow");
		DataSourceTextField id = new DataSourceTextField("id");
		id.setPrimaryKey(true);
		id.setRequired(true);
		DataSourceTextField name = new DataSourceTextField("name");
		DataSourceTextField description = new DataSourceTextField("description");
		setFields(id, name, description);
		setDataURL("data/workflows.xml?sid=" + Session.get().getSid()
				+ (folderId != null ? "&folderId=" + folderId : "")
				+ (retrieveDefinitions ? "&retrievedefinitions=true" : "") + (checkUser ? "&checkUser=true" : ""));
		setClientOnly(true);
	}
}