package com.logicaldoc.web.service;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.java.plugin.registry.PluginDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.core.document.dao.HistoryDAO;
import com.logicaldoc.core.generic.Generic;
import com.logicaldoc.core.generic.dao.GenericDAO;
import com.logicaldoc.core.rss.FeedMessage;
import com.logicaldoc.core.rss.dao.FeedMessageDAO;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.core.stats.StatsCollector;
import com.logicaldoc.core.task.Task;
import com.logicaldoc.core.task.TaskManager;
import com.logicaldoc.core.task.TaskTrigger;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.common.client.beans.GUIHistory;
import com.logicaldoc.gui.common.client.beans.GUIParameter;
import com.logicaldoc.gui.common.client.beans.GUIScheduling;
import com.logicaldoc.gui.common.client.beans.GUITask;
import com.logicaldoc.gui.common.client.beans.GUIUser;
import com.logicaldoc.gui.common.client.beans.GUIValuePair;
import com.logicaldoc.gui.frontend.client.services.SystemService;
import com.logicaldoc.i18n.I18N;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.ContextProperties;
import com.logicaldoc.util.plugin.PluginRegistry;
import com.logicaldoc.util.sql.SqlUtil;
import com.logicaldoc.web.util.SessionUtil;

/**
 * Implementation of the SystemService
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class SystemServiceImpl extends RemoteServiceServlet implements SystemService {

	private static final long serialVersionUID = 1L;

	private static int progress = 0;

	private static Logger log = LoggerFactory.getLogger(SystemServiceImpl.class);

	@Override
	public boolean disableTask(String sid, String taskName) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		TaskManager manager = (TaskManager) Context.getInstance().getBean(TaskManager.class);
		try {
			Task task = null;
			for (Task t : manager.getTasks()) {
				if (t.getName().equals(taskName)) {
					task = t;
					break;
				}
			}

			task.getScheduling().setEnabled(false);
			task.getScheduling().save();

			return true;
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			return false;
		}
	}

	@Override
	public boolean enableTask(String sid, String taskName) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		TaskManager manager = (TaskManager) Context.getInstance().getBean(TaskManager.class);
		try {
			Task task = null;
			for (Task t : manager.getTasks()) {
				if (t.getName().equals(taskName)) {
					task = t;
					break;
				}
			}

			task.getScheduling().setEnabled(true);
			task.getScheduling().save();

			return true;
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			return false;
		}
	}

	@Override
	public GUIParameter[][] getStatistics(String sid, String locale) throws InvalidSessionException {
		SessionUtil.validateSession(sid);
		GenericDAO genDao = (GenericDAO) Context.getInstance().getBean(GenericDAO.class);

		GUIParameter[][] parameters = new GUIParameter[4][8];
		try {
			/*
			 * Repository statistics
			 */
			Generic gen = genDao.findByAlternateKey(StatsCollector.STAT, "docdir");
			GUIParameter docDirSize = new GUIParameter();
			docDirSize.setName("documents");
			if (gen != null)
				docDirSize.setValue(gen.getString1());
			else
				docDirSize.setValue("0");
			parameters[0][0] = docDirSize;

			gen = genDao.findByAlternateKey(StatsCollector.STAT, "userdir");
			GUIParameter userDirSize = new GUIParameter();
			userDirSize.setName("users");
			if (gen != null)
				userDirSize.setValue(gen.getString1());
			else
				userDirSize.setValue("0");
			parameters[0][1] = userDirSize;

			gen = genDao.findByAlternateKey(StatsCollector.STAT, "indexdir");
			GUIParameter indexDirSize = new GUIParameter();
			indexDirSize.setName("fulltextindex");
			if (gen != null)
				indexDirSize.setValue(gen.getString1());
			else
				indexDirSize.setValue("0");

			parameters[0][2] = indexDirSize;

			gen = genDao.findByAlternateKey(StatsCollector.STAT, "importdir");
			GUIParameter importDirSize = new GUIParameter();
			importDirSize.setName("iimport");
			if (gen != null)
				importDirSize.setValue(gen.getString1());
			else
				importDirSize.setValue("0");
			parameters[0][3] = importDirSize;

			gen = genDao.findByAlternateKey(StatsCollector.STAT, "exportdir");
			GUIParameter exportDirSize = new GUIParameter();
			exportDirSize.setName("eexport");
			if (gen != null)
				exportDirSize.setValue(gen.getString1());
			else
				exportDirSize.setValue("0");
			parameters[0][4] = exportDirSize;

			gen = genDao.findByAlternateKey(StatsCollector.STAT, "plugindir");
			GUIParameter pluginsDirSize = new GUIParameter();
			pluginsDirSize.setName("plugins");
			if (gen != null)
				pluginsDirSize.setValue(gen.getString1());
			else
				pluginsDirSize.setValue("0");
			parameters[0][5] = pluginsDirSize;

			gen = genDao.findByAlternateKey(StatsCollector.STAT, "dbdir");
			GUIParameter dbDirSize = new GUIParameter();
			dbDirSize.setName("database");
			if (gen != null)
				dbDirSize.setValue(gen.getString1());
			else
				dbDirSize.setValue("0");

			parameters[0][6] = dbDirSize;

			gen = genDao.findByAlternateKey(StatsCollector.STAT, "logdir");
			GUIParameter logsDirSize = new GUIParameter();
			logsDirSize.setName("logs");
			if (gen != null)
				logsDirSize.setValue(gen.getString1());
			else
				logsDirSize.setValue("0");

			parameters[0][7] = logsDirSize;

			/*
			 * Document statistics
			 */
			gen = genDao.findByAlternateKey(StatsCollector.STAT, "notindexeddocs");
			GUIParameter notIndexed = new GUIParameter();
			notIndexed.setName("notindexed");
			notIndexed.setValue(gen != null ? Integer.toString(gen.getInteger1()) : "0");
			parameters[1][0] = notIndexed;

			gen = genDao.findByAlternateKey(StatsCollector.STAT, "indexeddocs");
			GUIParameter indexed = new GUIParameter();
			indexed.setName("indexed");
			indexed.setValue(gen != null ? Integer.toString(gen.getInteger1()) : "0");
			parameters[1][1] = indexed;

			gen = genDao.findByAlternateKey(StatsCollector.STAT, "deleteddocs");
			GUIParameter deletedDocs = new GUIParameter();
			deletedDocs.setName("docstrash");
			deletedDocs.setLabel("trash");
			deletedDocs.setValue(gen != null ? Integer.toString(gen.getInteger1()) : "0");
			parameters[1][2] = deletedDocs;

			/*
			 * Folders statistics
			 */
			gen = genDao.findByAlternateKey(StatsCollector.STAT, "withdocs");
			GUIParameter notEmptyFolders = new GUIParameter();
			notEmptyFolders.setName("withdocs");
			notEmptyFolders.setValue(gen != null ? Integer.toString(gen.getInteger1()) : "0");
			parameters[2][0] = notEmptyFolders;

			gen = genDao.findByAlternateKey(StatsCollector.STAT, "empty");
			GUIParameter emptyFolders = new GUIParameter();
			emptyFolders.setName("empty");
			emptyFolders.setValue(gen != null ? Integer.toString(gen.getInteger1()) : "0");
			parameters[2][1] = emptyFolders;

			gen = genDao.findByAlternateKey(StatsCollector.STAT, "deletedfolders");
			GUIParameter deletedFolders = new GUIParameter();
			deletedFolders.setName("folderstrash");
			deletedFolders.setLabel("trash");
			deletedFolders.setValue(gen != null ? Integer.toString(gen.getInteger1()) : "0");
			parameters[2][2] = deletedFolders;

			/*
			 * Last run
			 */
			gen = genDao.findByAlternateKey(StatsCollector.STAT, "lastrun");
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = gen != null ? df.parse(gen.getString1()) : null;
			GUIParameter lastrun = new GUIParameter();
			lastrun.setName("lastrun");
			if (date != null) {
				DateFormat df2 = new SimpleDateFormat(I18N.message("format_date", locale));
				lastrun.setValue(df2.format(date));
			} else {
				lastrun.setValue("");
			}
			parameters[3][0] = lastrun;
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		}

		return parameters;
	}

	@Override
	public GUITask getTaskByName(String sid, String taskName, String locale) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		TaskManager manager = (TaskManager) Context.getInstance().getBean(TaskManager.class);
		try {
			Task tsk = null;
			for (Task t : manager.getTasks()) {
				if (t.getName().equals(taskName)) {
					tsk = t;
					break;
				}
			}

			if (tsk != null) {
				GUITask task = new GUITask();
				task.setName(tsk.getName());
				task.setStatus(tsk.getStatus());
				task.setProgress((int) tsk.getProgress());
				task.setSize(tsk.getSize());
				task.setCompletionPercentage(tsk.getCompletionPercentage());
				task.setIndeterminate(tsk.isIndeterminate());

				GUIScheduling scheduling = new GUIScheduling(tsk.getName());
				scheduling.setEnabled(tsk.getScheduling().isEnabled());
				scheduling.setMode(tsk.getScheduling().getMode());
				if (tsk.getScheduling().getMode().equals(TaskTrigger.MODE_SIMPLE)) {
					scheduling.setSimple(true);
					scheduling.setDelay(tsk.getScheduling().getDelaySeconds());
					scheduling.setInterval(tsk.getScheduling().getIntervalSeconds());
					task.setSchedulingLabel(I18N.message("each", locale) + " "
							+ tsk.getScheduling().getIntervalSeconds() + " "
							+ I18N.message("seconds", locale).toLowerCase());
				} else {
					scheduling.setSimple(false);
					scheduling.setSeconds(tsk.getScheduling().getSeconds());
					scheduling.setMinutes(tsk.getScheduling().getMinutes());
					scheduling.setHours(tsk.getScheduling().getHours());
					scheduling.setMonth(tsk.getScheduling().getMonth());
					scheduling.setDayOfMonth(tsk.getScheduling().getDayOfMonth());
					scheduling.setDayOfWeek(tsk.getScheduling().getDayOfWeek());
					task.setSchedulingLabel(tsk.getScheduling().getCronExpression());
				}

				scheduling.setMaxLength(tsk.getScheduling().getMaxLength());

				task.setScheduling(scheduling);

				task.setSendActivityReport(tsk.isSendActivityReport());

				/*
				 * Parse the recipients ids and check the users existence
				 */
				UserDAO dao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
				if (tsk.getReportRecipients() != null) {
					StringTokenizer st = new StringTokenizer(tsk.getReportRecipients(), ",", false);
					while (st.hasMoreTokens()) {
						User user = dao.findById(Long.parseLong(st.nextToken()));
						if (user != null) {
							GUIUser u = new GUIUser();
							u.setId(user.getId());
							u.setUserName(user.getUserName());
							u.setName(user.getName());
							u.setFirstName(user.getFirstName());
							task.addReportRecipient(u);
						}
					}
				}

				return task;
			}
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		}

		return null;
	}

	@Override
	public GUITask[] loadTasks(String sid, String locale) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		if (progress >= 100)
			progress = -1;
		progress++;

		TaskManager manager = (TaskManager) Context.getInstance().getBean(TaskManager.class);
		GUITask[] tasks;
		try {
			tasks = new GUITask[manager.getTasks().size()];

			int i = 0;
			for (Task t : manager.getTasks()) {
				GUITask task = new GUITask();
				task.setName(t.getName());
				task.setStatus(t.getStatus());
				task.setProgress((int) t.getProgress());
				task.setSize(t.getSize());
				task.setIndeterminate(t.isIndeterminate());
				task.setCompletionPercentage(t.getCompletionPercentage());

				GUIScheduling scheduling = new GUIScheduling(t.getName());
				scheduling.setEnabled(t.getScheduling().isEnabled());
				scheduling.setMode(t.getScheduling().getMode());
				if (t.getScheduling().getMode().equals(TaskTrigger.MODE_SIMPLE)) {
					scheduling.setSimple(true);
					scheduling.setDelay(t.getScheduling().getDelay());
					scheduling.setInterval(t.getScheduling().getIntervalSeconds());
					task.setSchedulingLabel(I18N.message("each", locale) + " " + t.getScheduling().getIntervalSeconds()
							+ " " + I18N.message("seconds", locale).toLowerCase());
				} else if (t.getScheduling().getMode().equals(TaskTrigger.MODE_CRON)) {
					scheduling.setSimple(false);
					scheduling.setSeconds(t.getScheduling().getSeconds());
					scheduling.setMinutes(t.getScheduling().getMinutes());
					scheduling.setHours(t.getScheduling().getHours());
					scheduling.setMonth(t.getScheduling().getMonth());
					scheduling.setDayOfMonth(t.getScheduling().getDayOfMonth());
					scheduling.setDayOfWeek(t.getScheduling().getDayOfWeek());
					task.setSchedulingLabel(t.getScheduling().getCronExpression());
				}

				scheduling.setMaxLength(t.getScheduling().getMaxLength());
				scheduling.setPreviousFireTime(t.getScheduling().getPreviousFireTime());
				scheduling.setNextFireTime(t.getScheduling().getNextFireTime());

				task.setScheduling(scheduling);

				task.setSendActivityReport(t.isSendActivityReport());

				tasks[i] = task;
				i++;
			}

			return tasks;
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		}

		return null;
	}

	@Override
	public GUITask saveTask(String sid, GUITask task, String locale) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		TaskManager manager = (TaskManager) Context.getInstance().getBean(TaskManager.class);

		try {
			Task tsk = null;
			for (Task t : manager.getTasks()) {
				if (t.getName().equals(task.getName())) {
					tsk = t;
					break;
				}
			}

			if (tsk != null) {
				tsk.getScheduling().setEnabled(task.getScheduling().isEnabled());
				if (task.getScheduling().isSimple()) {
					tsk.getScheduling().setMode(TaskTrigger.MODE_SIMPLE);
					tsk.getScheduling().setDelay(task.getScheduling().getDelay() * 1000);
					tsk.getScheduling().setInterval(task.getScheduling().getInterval() * 1000);
					tsk.getScheduling().setIntervalSeconds(task.getScheduling().getInterval());
					task.setSchedulingLabel(I18N.message("each", locale) + " "
							+ tsk.getScheduling().getIntervalSeconds() + " "
							+ I18N.message("seconds", locale).toLowerCase());
				} else {
					tsk.getScheduling().setMode(TaskTrigger.MODE_CRON);
					tsk.getScheduling().setSeconds(task.getScheduling().getSeconds());
					tsk.getScheduling().setMinutes(task.getScheduling().getMinutes());
					tsk.getScheduling().setHours(task.getScheduling().getHours());
					tsk.getScheduling().setMonth(task.getScheduling().getMonth());
					tsk.getScheduling().setDayOfMonth(task.getScheduling().getDayOfMonth());
					tsk.getScheduling().setDayOfWeek((task.getScheduling().getDayOfWeek()));
					task.setSchedulingLabel(tsk.getScheduling().getCronExpression());
				}
				tsk.getScheduling().setMaxLength(task.getScheduling().getMaxLength());

				tsk.setSendActivityReport(task.isSendActivityReport());
				StringBuffer sb = new StringBuffer();
				for (GUIUser user : task.getReportRecipients()) {
					if (sb.length() > 0)
						sb.append(",");
					sb.append(user.getId());
				}
				tsk.setReportRecipients(sb.toString());

				try {
					tsk.save();
				} catch (IOException e) {
					return null;
				}
			}

			return task;
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public GUIHistory[] search(String sid, String userName, Date from, Date till, int maxResult, String historySid,
			String[] event) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		HistoryDAO dao = (HistoryDAO) Context.getInstance().getBean(HistoryDAO.class);
		List<GUIHistory> histories = new ArrayList<GUIHistory>();
		try {

			// Search in the document/folder history
			StringBuffer query = new StringBuffer(
					"select A.ld_username, A.ld_event, A.ld_date, A.ld_title, A.ld_folderid, A.ld_path, A.ld_sessionid from ld_history A where 1=1 ");
			if (userName != null && StringUtils.isNotEmpty(userName))
				query.append(" and lower(A.ld_username) like '%" + SqlUtil.doubleQuotes(userName.toLowerCase()) + "%'");
			if (historySid != null && StringUtils.isNotEmpty(historySid))
				query.append(" and A.sessionId=" + historySid);
			if (from != null) {
				query.append(" and A.ld_date > '" + new Timestamp(from.getTime()) + "'");
			}
			if (till != null) {
				query.append(" and A.ld_date < '" + new Timestamp(till.getTime()) + "'");
			}
			if (event.length > 0) {
				boolean first = true;
				for (String e : event) {
					if (first)
						query.append(" and (");
					else
						query.append(" or ");

					query.append(" A.ld_event = '" + SqlUtil.doubleQuotes(e) + "'");
					first = false;
				}
				query.append(" ) ");
			}

			// Search in the folder history
			query.append("union select B.ld_username, B.ld_event, B.ld_date, null, null, null, B.ld_sessionid from ld_folder_history B where 1=1 ");
			if (userName != null && StringUtils.isNotEmpty(userName))
				query.append(" and lower(B.ld_username) like '%" + SqlUtil.doubleQuotes(userName.toLowerCase()) + "%'");
			if (historySid != null && StringUtils.isNotEmpty(historySid))
				query.append(" and B.ld_sessionid=" + historySid);
			if (from != null) {
				query.append(" and B.ld_date > '" + new Timestamp(from.getTime()) + "'");
			}
			if (till != null) {
				query.append(" and B.ld_date < '" + new Timestamp(till.getTime()) + "'");
			}
			if (event.length > 0) {
				boolean first = true;
				for (String e : event) {
					if (first)
						query.append(" and (");
					else
						query.append(" or ");

					query.append(" B.ld_event = '" + SqlUtil.doubleQuotes(e) + "'");
					first = false;
				}
				query.append(" ) ");
			}

			// Search in the user history
			query.append("union select C.ld_username, C.ld_event, C.ld_date, null, null, null, C.ld_sessionid from ld_user_history C where 1=1 ");
			if (userName != null && StringUtils.isNotEmpty(userName))
				query.append(" and lower(C.ld_username) like '%" + SqlUtil.doubleQuotes(userName.toLowerCase()) + "%'");
			if (historySid != null && StringUtils.isNotEmpty(historySid))
				query.append(" and C.ld_sessionid=" + historySid);
			if (from != null) {
				query.append(" and C.ld_date > '" + new Timestamp(from.getTime()) + "'");
			}
			if (till != null) {
				query.append(" and C.ld_date < '" + new Timestamp(till.getTime()) + "'");
			}
			if (event.length > 0) {
				boolean first = true;
				for (String e : event) {
					if (first)
						query.append(" and (");
					else
						query.append(" or ");

					query.append(" C.ld_event = '" + SqlUtil.doubleQuotes(e) + "'");
					first = false;
				}
				query.append(" ) ");
			}

			// Search in the workflow history
			query.append("union select D.ld_username, D.ld_event, D.ld_date, null, null, null, D.ld_sessionid from ld_workflowhistory D where 1=1 ");
			if (userName != null && StringUtils.isNotEmpty(userName))
				query.append(" and lower(D.ld_username) like '%" + SqlUtil.doubleQuotes(userName.toLowerCase()) + "%'");
			if (historySid != null && StringUtils.isNotEmpty(historySid))
				query.append(" and D.ld_sessionid=" + historySid);
			if (from != null) {
				query.append(" and D.ld_date > '" + new Timestamp(from.getTime()) + "'");
			}
			if (till != null) {
				query.append(" and D.ld_date < '" + new Timestamp(till.getTime()) + "'");
			}
			if (event.length > 0) {
				boolean first = true;
				for (String e : event) {
					if (first)
						query.append(" and (");
					else
						query.append(" or ");

					query.append(" D.ld_event = '" + SqlUtil.doubleQuotes(e.trim()) + "'");
					first = false;
				}
				query.append(" ) ");
			}

			query.append(" order by 3 desc ");

			histories = (List<GUIHistory>) dao.query(query.toString(), null, new RowMapper<GUIHistory>() {

				@Override
				public GUIHistory mapRow(ResultSet rs, int arg1) throws SQLException {
					GUIHistory history = new GUIHistory();
					history.setUserName(rs.getString(1));
					history.setEvent(rs.getString(2));
					if (rs.getObject(3) instanceof Timestamp)
						history.setDate(rs.getTimestamp(3));
					else
						history.setDate(rs.getDate(3));

					history.setTitle(rs.getString(4));
					history.setFolderId(rs.getLong(5));
					history.setPath(rs.getString(6));
					history.setSessionId(rs.getString(7));
					return history;
				}

			}, maxResult);

			return histories.toArray(new GUIHistory[histories.size()]);
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		}

		return null;
	}

	@Override
	public boolean startTask(String taskName) {
		TaskManager manager = (TaskManager) Context.getInstance().getBean(TaskManager.class);

		try {
			for (Task task : manager.getTasks()) {
				if (task.getName().equals(taskName)) {
					Thread thread = new Thread(task);
					thread.start();
					break;
				}
			}

			return true;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return false;
		}
	}

	@Override
	public boolean stopTask(String taskName) {
		TaskManager manager = (TaskManager) Context.getInstance().getBean(TaskManager.class);

		try {
			for (Task task : manager.getTasks()) {
				if (task.getName().equals(taskName)) {
					task.interrupt();
					break;
				}
			}

			return true;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return false;
		}
	}

	@Override
	public void setGUILanguageStatus(String sid, String language, boolean active) throws InvalidSessionException {
		SessionUtil.validateSession(sid);
		try {
			ContextProperties conf = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
			conf.setProperty("lang." + language + ".gui", active ? "enabled" : "disabled");
			conf.write();
		} catch (IOException t) {
			log.error(t.getMessage(), t);
			throw new RuntimeException(t.getMessage(), t);
		}
	}

	@Override
	public void maskFeedMsgAsRead(String sid, long[] ids) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		Context context = Context.getInstance();
		FeedMessageDAO dao = (FeedMessageDAO) context.getBean(FeedMessageDAO.class);
		for (long id : ids) {
			FeedMessage message = dao.findById(id);
			dao.initialize(message);
			message.setRead(1);
			dao.store(message);
		}
	}

	@Override
	public void maskFeedMsgAsNotRead(String sid, long[] ids) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		Context context = Context.getInstance();
		FeedMessageDAO dao = (FeedMessageDAO) context.getBean(FeedMessageDAO.class);
		for (long id : ids) {
			FeedMessage message = dao.findById(id);
			dao.initialize(message);
			message.setRead(0);
			dao.store(message);
		}
	}

	@Override
	public void deleteFeedMessages(String sid, long[] ids) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		Context context = Context.getInstance();
		FeedMessageDAO dao = (FeedMessageDAO) context.getBean(FeedMessageDAO.class);
		for (long id : ids) {
			dao.delete(id);
		}
	}

	@Override
	public GUIValuePair[] getPlugins(String sid) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		Collection<PluginDescriptor> descriptors = PluginRegistry.getInstance().getPlugins();
		List<GUIValuePair> plugins = new ArrayList<GUIValuePair>();
		for (PluginDescriptor descriptor : descriptors) {
			try {
				GUIValuePair plugin = new GUIValuePair();
				String pluginName = descriptor.getId();
				if (pluginName.startsWith("logicaldoc-"))
					pluginName = pluginName.replaceAll("logicaldoc-", "");
				plugin.setCode(pluginName);
				plugin.setValue(descriptor.getVersion().toString());
				plugins.add(plugin);
			} catch (Exception t) {
				log.error(t.getMessage(), t);
				throw new RuntimeException(t.getMessage(), t);
			}
		}

		if (plugins.size() > 1) {
			// Sort by ascending date and number
			Collections.sort(plugins, new Comparator<GUIValuePair>() {
				public int compare(GUIValuePair c1, GUIValuePair c2) {
					if (c1.getCode() != null && c2.getCode() != null) {
						int compare = c1.getCode().compareTo(c2.getCode());
						if (compare != 0)
							return compare;
					}
					return c1.getValue().compareTo(c2.getValue());
				}
			});
		}

		return plugins.toArray(new GUIValuePair[0]);
	}
}