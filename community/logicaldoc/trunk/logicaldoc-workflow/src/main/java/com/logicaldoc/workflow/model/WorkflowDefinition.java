package com.logicaldoc.workflow.model;

public class WorkflowDefinition {
	
	private String definitionId;
	
	private String name; 
	
	private String description;
	
	public void setDefinitionId(String definitionId) {
		this.definitionId = definitionId;
	}
	
	public String getDefinitionId() {
		return definitionId;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getName() {
		return name;
	}
	
}
