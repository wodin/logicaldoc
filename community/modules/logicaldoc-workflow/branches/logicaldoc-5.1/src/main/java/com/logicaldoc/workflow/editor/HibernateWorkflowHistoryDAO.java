package com.logicaldoc.workflow.editor;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.HibernatePersistentObjectDAO;
import com.logicaldoc.core.security.User;
import com.logicaldoc.util.sql.SqlUtil;
import com.logicaldoc.workflow.WorkflowHistory;

public class HibernateWorkflowHistoryDAO extends HibernatePersistentObjectDAO<WorkflowHistory> implements
		WorkflowHistoryDAO {

	private HibernateWorkflowHistoryDAO() {
		super(WorkflowHistory.class);
		super.log = LogFactory.getLog(HibernateWorkflowHistoryDAO.class);
	}

	/**
	 * @see com.logicaldoc.workflow.editor.WorkflowHistoryDAO#createWorkflowHistory(com.logicaldoc.core.security.User,
	 *      java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void createWorkflowHistory(User user, String eventType, String comment, String sessionId) {
		WorkflowHistory history = new WorkflowHistory();

		history.setDate(new Date());
		history.setUserId(user.getId());
		history.setUserName(user.getFullName());
		history.setEvent(eventType);
		history.setComment(comment);
		if (sessionId != null)
			history.setSessionId(sessionId);

		store(history);
	}

	/**
	 * @see com.logicaldoc.workflow.editor.WorkflowHistoryDAO#findByTemplateIdAndInstanceId(long,
	 *      java.lang.String)
	 */
	@Override
	public List<WorkflowHistory> findByTemplateIdAndInstanceId(long templateId, String instanceId) {
		String query = "_entity.templateId = " + templateId + " and lower(_entity.instanceId) = '"
				+ SqlUtil.doubleQuotes(instanceId.toLowerCase()) + "'";
		return findByWhere(query, "order by _entity.lastModified asc");
	}

}
