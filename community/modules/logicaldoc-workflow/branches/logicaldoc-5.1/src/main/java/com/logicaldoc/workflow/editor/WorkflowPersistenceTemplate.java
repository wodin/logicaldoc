package com.logicaldoc.workflow.editor;

import java.io.Serializable;

import com.logicaldoc.core.ExtensibleObject;

public class WorkflowPersistenceTemplate extends ExtensibleObject {

	public static int NOT_DEPLOYED = 0;

	public static int DEPLOYED = 1;

	private Serializable xmldata;

	private String name;

	private String description;

	private int deployed = NOT_DEPLOYED;

	private String startState;

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

	public void setDeployed(int deployed) {
		this.deployed = deployed;
	}

	public int getDeployed() {
		return this.deployed;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStartState() {
		return startState;
	}

	public void setStartState(String startState) {
		this.startState = startState;
	}
}