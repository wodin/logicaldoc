package com.logicaldoc.workflow.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.logicaldoc.workflow.DocumentRecord;
import com.logicaldoc.workflow.WorkflowConstants;

public class WorkflowInstance implements FetchModel {

	private String id;

	private Long processDefinitionId;

	private Date startDate;

	private Date endDate;

	private String name;

	private Map<String, Object> properties = new HashMap<String, Object>();

	// indicates if the instance is selected
	private boolean selected = false;

	public Date getEndDate() {
		return endDate;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Long getProcessDefinitionId() {
		return processDefinitionId;
	}

	public String getId() {
		return id;
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	public String getName() {
		return name;
	}

	@Override
	public boolean isUpdateable() {
		return true;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setProcessDefinitionId(Long processDefinitionId) {
		this.processDefinitionId = processDefinitionId;
	}

	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public WorkflowInstance() {

	}

	public WorkflowInstance(WorkflowInstance workflowInstance) {
		this.endDate = workflowInstance.endDate;
		this.id = workflowInstance.id;
		this.processDefinitionId = workflowInstance.processDefinitionId;
		this.startDate = workflowInstance.startDate;

		if (workflowInstance.properties != null) {
			for (Map.Entry<String, Object> entry : workflowInstance.properties.entrySet()) {
				this.properties.put((String) entry.getKey(), entry.getValue());
			}
		}
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getDocuments() {
		Set<DocumentRecord> documents = (Set<DocumentRecord>) getProperties().get(WorkflowConstants.VAR_DOCUMENTS);
		String txt_doclist = "";

		for (DocumentRecord documentRecord : documents) {
			txt_doclist += documentRecord.getTitle();
			txt_doclist += ", ";
		}

		if (txt_doclist.trim().endsWith(","))
			return StringUtils.abbreviate(txt_doclist.trim().substring(0, txt_doclist.lastIndexOf(",")), 90);
		else
			return StringUtils.abbreviate(txt_doclist.trim(), 90);
	}
}
