package com.logicaldoc.workflow.persistence;

import java.io.Serializable;

public class WorkflowPersistenceTemplate {
	
	private Long id;
	
	private Serializable xmldata;
	
	private String name;
	
	private boolean deployed;
		
	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public void setXmldata(Serializable xmldata) {
		this.xmldata = xmldata;
	}
	
	public Serializable getXmldata() {
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
	
}
