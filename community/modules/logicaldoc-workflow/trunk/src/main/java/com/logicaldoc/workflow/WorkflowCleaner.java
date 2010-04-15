package com.logicaldoc.workflow;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.impex.cleaner.Cleaner;
import com.logicaldoc.util.config.PropertiesBean;

public class WorkflowCleaner extends Cleaner {

	protected static Log log = LogFactory.getLog(WorkflowCleaner.class);

	public WorkflowCleaner() {
		super();
		setDbScript("/sql/clean-workflow.sql");
	}

	public WorkflowCleaner(Connection con) {
		super(con);
		setDbScript("/sql/clean-workflow.sql");
	}

	@Override
	protected void afterDbUpdate() throws Exception {

	}

	@Override
	protected void beforeDbUpdate() throws Exception {
		// Mark as deleted the obsolete entries
		try {
			PropertiesBean bean = new PropertiesBean();
			int ttl = Integer.parseInt(bean.getProperty("history.workflow.ttl"));
			Date date = new Date();
			GregorianCalendar cal = new GregorianCalendar();
			cal.setTime(date);
			cal.add(Calendar.DAY_OF_MONTH, -ttl);
			date = cal.getTime();
			int count = connection.createStatement().executeUpdate(
					"update ld_workflowhistory set ld_deleted=1 where ld_deleted = 0 and ld_date < '"
							+ new Timestamp(date.getTime()) + "'");

			log.debug("The number of obsolete entries is: " + count);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}