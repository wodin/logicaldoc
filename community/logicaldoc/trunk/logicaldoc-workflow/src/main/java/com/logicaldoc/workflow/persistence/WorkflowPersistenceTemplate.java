package com.logicaldoc.workflow.persistence;

import java.sql.Blob;

public class WorkflowPersistenceTemplate {
	
	private long id;
	
	private Blob xmldata;
	
	private String name;
	
	private String description;
	
	private boolean deployed;
	
	private String startState;
	
	public void setId(long id) {
		this.id = id;
	}
	
	public long getId() {
		return id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setXmldata(Blob xmldata) {
		this.xmldata = xmldata;
	}
	
	public Blob getXmldata() {
		return xmldata;
	}
	
	public void setDeployed(boolean deployed) {
		this.deployed = deployed;
	}
	
	public boolean getDeployed(){
		return this.deployed;
	}
	
	public boolean isDeployed() {
		return deployed;
	}
	
	public String getStartState() {
		return startState;
	}
	
	public void setStartState(String startState) {
		this.startState = startState;
	}
	
}
