package com.logicaldoc.workflow.model;

public class EMailMessage {
	
	private String subject;
	
	private String body; 
	
	public EMailMessage(String subject, String body){
		this.subject = subject;
		this.body = body;
	}
	
	public String getBody() {
		return body;
	}
	
	public String getSubject() {
		return subject;
	}
	
	public void setBody(String body) {
		this.body = body;
	}
	
	public void setSubject(String subject) {
		this.subject = subject;
	}
}
