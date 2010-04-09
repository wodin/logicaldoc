package com.logicaldoc.workflow.model;

/**
 * Simple message sent to workflow actors.
 * 
 * @author Sebastian Wenzky
 * @since 5.0
 */
public class WorkflowMessage {

	private String subject;

	private String body;

	public WorkflowMessage(String subject, String body) {
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