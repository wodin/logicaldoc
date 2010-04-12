package com.logicaldoc.workflow.exception;

@SuppressWarnings("serial")
public class WorkflowParseException extends RuntimeException{
	
	public WorkflowParseException(String s){
		super(s);
	}
	
	public WorkflowParseException(Exception e){
		super(e);
	}
}
