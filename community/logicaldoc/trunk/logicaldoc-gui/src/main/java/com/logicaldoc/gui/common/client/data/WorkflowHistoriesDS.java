package com.logicaldoc.gui.common.client.data;

import com.logicaldoc.gui.common.client.i18n.I18N;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceDateTimeField;
import com.smartgwt.client.data.fields.DataSourceTextField;

public class WorkflowHistoriesDS extends DataSource {
	public WorkflowHistoriesDS(Long instanceId, Long workflowTemplateId, String eventFilter) {
		setRecordXPath("/list/workflowhistory");
		DataSourceTextField id = new DataSourceTextField("id");
		id.setPrimaryKey(true);
		id.setRequired(true);
		
		DataSourceTextField name = new DataSourceTextField("name");
		DataSourceTextField taskId = new DataSourceTextField("taskId");
		DataSourceDateTimeField startDate = new DataSourceDateTimeField("startdate");
		DataSourceDateTimeField endDate = new DataSourceDateTimeField("enddate");
		DataSourceTextField documents = new DataSourceTextField("documents");

		DataSourceTextField event = new DataSourceTextField("event");
		DataSourceDateTimeField date = new DataSourceDateTimeField("date");
		DataSourceTextField user = new DataSourceTextField("user");
		DataSourceTextField comment = new DataSourceTextField("comment");
		DataSourceTextField filename = new DataSourceTextField("filename");
		DataSourceTextField sessionId = new DataSourceTextField("sessionid");
		setFields(id, taskId, name, startDate, endDate, documents, event, date, user, comment, filename, sessionId);
		setDataURL("data/workflowhistories.xml?locale=" + I18N.getLocale()
				+ (instanceId != null ? "&instanceId=" + instanceId : "")
				+ (workflowTemplateId != null ? "&workflowTemplateId=" + workflowTemplateId : "")
				+ (eventFilter != null ? "&event=" + eventFilter : ""));
		setClientOnly(true);
	}
}
