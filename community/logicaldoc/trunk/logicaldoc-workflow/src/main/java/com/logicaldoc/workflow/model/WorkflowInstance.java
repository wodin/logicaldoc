package com.logicaldoc.workflow.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class WorkflowInstance {
	
	public String id;
	
	public Long processDefinitionId;
	
	public Date startDate;
	
	public Date endDate;
	
	public String name;
	
	public String description;
	
	public Map<String, Object> properties = new HashMap<String, Object>();
	
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
	
	public String getDescription() {
		return description;
	}
}
