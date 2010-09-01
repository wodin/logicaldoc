package com.logicaldoc.gui.common.client.data;

import com.logicaldoc.gui.common.client.Session;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceDateTimeField;
import com.smartgwt.client.data.fields.DataSourceTextField;

public class WorkflowHistoriesDS extends DataSource {
	public WorkflowHistoriesDS(Long workflowId, Long workflowTemplateId) {
		setTitleField("name");
		setRecordXPath("/list/workflowhistory");
		DataSourceTextField id = new DataSourceTextField("id");
		id.setPrimaryKey(true);
		id.setRequired(true);
		DataSourceDateTimeField startDate = new DataSourceDateTimeField("startdate");
		DataSourceDateTimeField endDate = new DataSourceDateTimeField("enddate");
		DataSourceTextField documents = new DataSourceTextField("documents");

		DataSourceTextField event = new DataSourceTextField("event");
		DataSourceDateTimeField date = new DataSourceDateTimeField("date");
		DataSourceTextField user = new DataSourceTextField("user");
		DataSourceTextField document = new DataSourceTextField("document");
		DataSourceTextField sessionId = new DataSourceTextField("sessionid");
		setFields(id, startDate, endDate, documents, event, date, user, document, sessionId);
		setDataURL("data/workflowhistories.xml?sid=" + Session.get().getSid()
				+ (workflowId != null ? "&workflowId=" + workflowId : "")
				+ (workflowTemplateId != null ? "&workflowTemplateId=" + workflowTemplateId : ""));
		setClientOnly(true);
	}
}
