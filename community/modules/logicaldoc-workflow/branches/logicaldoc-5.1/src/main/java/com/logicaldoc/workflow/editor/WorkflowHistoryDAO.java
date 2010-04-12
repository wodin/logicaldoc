package com.logicaldoc.workflow.editor;

import java.util.List;

import com.logicaldoc.core.PersistentObjectDAO;
import com.logicaldoc.core.security.User;
import com.logicaldoc.workflow.WorkflowHistory;

public interface WorkflowHistoryDAO extends PersistentObjectDAO<WorkflowHistory> {

	/**
	 * This method selects all histories of a template with the given templateId
	 * and of a instance with the given instanceId.
	 * 
	 * @param templateId The Workflow Persistence Template Id
	 * @param instanceId The Workflow Instance Id
	 * @return list of histories ordered by last modified date
	 */
	public List<WorkflowHistory> findByTemplateIdAndInstanceId(long templateId, String instanceId);

	/**
	 * Creates an workflow history entry
	 * 
	 * @param user The user that made the operation
	 * @param eventType The event type
	 * @param comment The comment provided by the user
	 * @param sessionId The user session id
	 */
	public void createWorkflowHistory(User user, String eventType, String comment, String sessionId);

}
