package com.logicaldoc.workflow.editor;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
		history.setUser(user);
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
		return findByWhere(query, "order by _entity.date desc", null);
	}

	@Override
	public List<String> findInstanceIds() {
		
		String query = "select distinct(ld_instanceid) from ld_workflowhistory where ld_deleted=0";
		List<String> coll = (List<String>) queryForList(query, String.class);
		
		return coll;
	}

	@Override
	public List<Long> findTemplateIds() {
		
		String query = "select distinct(ld_templateid) from ld_workflowhistory where ld_deleted=0";
		List<Long> coll = (List<Long>) queryForList(query, Long.class);
		
		return coll;
	}

	@Override
	public void cleanOldHistories(int ttl) {
		if (ttl > 0) {
			Date date = new Date();
			GregorianCalendar cal = new GregorianCalendar();
			cal.setTime(date);
			cal.add(Calendar.DAY_OF_MONTH, -ttl);
			date = cal.getTime();
			
			// Retrieve all old user histories
			String query = "select ld_id from ld_workflowhistory where ld_deleted = 0 and ld_date < ?";
			
			List<Long> histories = (List<Long>) queryForList(query, new Object[]{new Timestamp(date.getTime())}, Long.class);
			for (Long historyId : histories) {
				super.bulkUpdate("set ld_deleted = 1 where ld_id = " + historyId, null);
			}
		}
	}
}
