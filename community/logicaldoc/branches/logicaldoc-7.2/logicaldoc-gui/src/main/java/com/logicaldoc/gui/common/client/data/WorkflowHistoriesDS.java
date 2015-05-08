package com.logicaldoc.gui.common.client.data;

import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceDateTimeField;
import com.smartgwt.client.data.fields.DataSourceTextField;

public class WorkflowHistoriesDS extends DataSource {
	public WorkflowHistoriesDS(Long instanceId, Long workflowTemplateId) {
		setTitleField("name");
		setRecordXPath("/list/workflowhistory");
		DataSourceTextField id = new DataSourceTextField("id");
		id.setPrimaryKey(true);
		id.setRequired(true);
		DataSourceTextField name = new DataSourceTextField("name");
		DataSourceDateTimeField startDate = new DataSourceDateTimeField("startdate");
		DataSourceDateTimeField endDate = new DataSourceDateTimeField("enddate");
		DataSourceTextField documents = new DataSourceTextField("documents");

		DataSourceTextField event = new DataSourceTextField("event");
		DataSourceDateTimeField date = new DataSourceDateTimeField("date");
		DataSourceTextField user = new DataSourceTextField("user");
		DataSourceTextField comment = new DataSourceTextField("comment");
		DataSourceTextField document = new DataSourceTextField("document");
		DataSourceTextField sessionId = new DataSourceTextField("sessionid");
		setFields(id, name, startDate, endDate, documents, event, date, user, comment, document, sessionId);
		setDataURL("data/workflowhistories.xml?sid=" + Session.get().getSid()
				+ (instanceId != null ? "&instanceId=" + instanceId : "")
				+ (workflowTemplateId != null ? "&workflowTemplateId=" + workflowTemplateId : "") + "&locale="
				+ I18N.getLocale());
		setClientOnly(true);
	}
}
