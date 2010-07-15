package com.logicaldoc.workflow.editor.model;

@SuppressWarnings("serial")
public class WorkflowEditorException extends RuntimeException{

	public WorkflowEditorException(String msg){
		super(msg);
	}
	
	public WorkflowEditorException(Exception e){
		super(e);
	}
	
}
