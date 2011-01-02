package com.logicaldoc.gui.common.client.data;

import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceTextField;

/**
 * Datasource to handle workflow trigger lists.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class WorkflowTriggersDS extends DataSource {
	public WorkflowTriggersDS(String folderId) {
		setTitleField("workflow");
		setRecordXPath("/list/workflowtrigger");
		DataSourceTextField id = new DataSourceTextField("id");
		id.setPrimaryKey(true);
		id.setHidden(true);
		id.setRequired(true);
		DataSourceTextField workflowId = new DataSourceTextField("workflowId");
		workflowId.setHidden(true);
		DataSourceTextField templateId = new DataSourceTextField("templateId");
		templateId.setHidden(true);
		DataSourceTextField workflow = new DataSourceTextField("workflow", I18N.message("workflow"));
		DataSourceTextField template = new DataSourceTextField("template", I18N.message("template"));
		setFields(id, workflowId, templateId, workflow, template);
		setDataURL("data/workflowtriggers.xml?sid=" + Session.get().getSid() + "&folderId=" + folderId);
		setClientOnly(false);
	}
}