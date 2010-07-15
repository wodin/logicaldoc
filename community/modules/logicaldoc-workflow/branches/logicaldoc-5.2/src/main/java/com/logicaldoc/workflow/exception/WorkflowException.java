package com.logicaldoc.workflow.exception;

public class WorkflowException extends RuntimeException{

	private static final long serialVersionUID = -6288298207059937464L;

	public WorkflowException(String s){
		super("An WorkflowException has been raised: " + s);
	}
	
	public WorkflowException(Exception e){
		super("WorkflowException: " + e);
	}
}
