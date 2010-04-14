package com.logicaldoc.workflow.editor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
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
		return findByWhere(query, "order by _entity.date desc");
	}

	@Override
	public List<String> findInstanceIds() {
		List<String> coll = new ArrayList<String>();
		try {
			String query = "select distinct(A.ld_instanceid) from ld_workflowhistory A where A.ld_deleted=0";

			Connection con = null;
			Statement stmt = null;
			ResultSet rs = null;

			try {
				con = getSession().connection();
				stmt = con.createStatement();
				rs = stmt.executeQuery(query.toString());
				while (rs.next()) {
					coll.add(rs.getString(1));
				}
			} finally {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
				if (con != null)
					con.close();
			}
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}
		return coll;
	}

	@Override
	public List<Long> findTemplateIds() {
		List<Long> coll = new ArrayList<Long>();
		try {
			String query = "select distinct(A.ld_templateid) from ld_workflowhistory A where A.ld_deleted=0";

			Connection con = null;
			Statement stmt = null;
			ResultSet rs = null;

			try {
				con = getSession().connection();
				stmt = con.createStatement();
				rs = stmt.executeQuery(query.toString());
				while (rs.next()) {
					coll.add(rs.getLong(1));
				}
			} finally {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
				if (con != null)
					con.close();
			}
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}
		return coll;
	}
}
