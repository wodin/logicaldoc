package com.logicaldoc.workflow.editor.model;

import java.io.Serializable;
import java.util.UUID;

public class Assignee implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4093828529136586174L;

	private String id;
	
	private String value;
	
	public Assignee(){
		this.id = UUID.randomUUID().toString();
	}
	
	public Assignee(String id, String value){
		this.id = id;
		this.value = value;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public String getId() {
		return id;
	}
	
	public String getValue() {
		return value;
	}
}
