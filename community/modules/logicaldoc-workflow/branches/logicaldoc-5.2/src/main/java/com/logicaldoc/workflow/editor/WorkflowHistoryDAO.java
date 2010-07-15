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

	/**
	 * Get all instances ids.
	 */
	public List<String> findInstanceIds();
	
	/**
	 * Get all template ids.
	 */
	public List<Long> findTemplateIds();
	
	/**
	 * This method deletes all the workflow history entries oldest than the given days
	 * from now. If <code>ttl</code> is 0 or -1, the cancellation is not made.
	 * 
	 * @param ttl The maximum number of days over which the history is
	 *        considered old
	 */
	public void cleanOldHistories(int ttl);
}
