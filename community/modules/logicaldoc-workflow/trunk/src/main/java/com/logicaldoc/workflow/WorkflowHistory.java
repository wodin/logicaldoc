package com.logicaldoc.workflow;

import com.logicaldoc.core.document.AbstractHistory;

/**
 * History entry due to an event on a workflow.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 5.1
 */
public class WorkflowHistory extends AbstractHistory {

	// Events on workflows
	public final static String EVENT_WORKFLOW_START = "event.workflow.start";

	public final static String EVENT_WORKFLOW_END = "event.workflow.end";

	public final static String EVENT_WORKFLOW_TASK_START = "event.workflow.task.start";

	public final static String EVENT_WORKFLOW_TASK_END = "event.workflow.task.end";

	public final static String EVENT_WORKFLOW_TASK_SUSPENDED = "event.workflow.task.suspended";

	public final static String EVENT_WORKFLOW_TASK_REASSIGNED = "event.workflow.task.reassigned";

	public final static String EVENT_WORKFLOW_TASK_DOCAPPENDED = "event.workflow.task.docappended";

	private long templateId;

	private String instanceId;

	public long getTemplateId() {
		return templateId;
	}

	public void setTemplateId(long templateId) {
		this.templateId = templateId;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}
}