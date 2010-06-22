package com.logicaldoc.web.service;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.document.dao.FolderDAO;
import com.logicaldoc.core.document.dao.HistoryDAO;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.task.Task;
import com.logicaldoc.core.task.TaskManager;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.common.client.beans.GUIHistory;
import com.logicaldoc.gui.common.client.beans.GUIInfo;
import com.logicaldoc.gui.common.client.beans.GUIParameter;
import com.logicaldoc.gui.common.client.beans.GUIScheduling;
import com.logicaldoc.gui.common.client.beans.GUITask;
import com.logicaldoc.gui.common.client.beans.GUIValuePair;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.frontend.client.services.SystemService;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.LocaleUtil;
import com.logicaldoc.util.config.PropertiesBean;
import com.logicaldoc.util.quartz.DoubleTrigger;
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

	private static Log log = LogFactory.getLog(SystemServiceImpl.class);

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
			return false;
		}
	}

	@Override
	public GUIInfo getInfo(String locale) {
		GUIInfo info = new GUIInfo();

		try {
			Properties i18n = new Properties();
			try {
				i18n.load(this.getClass().getResourceAsStream("/i18n/i18n.properties"));
			} catch (IOException e) {
				log.error(e.getMessage());
			}

			Locale withLocale = LocaleUtil.toLocale(locale);
			ArrayList<GUIValuePair> supportedLanguages = new ArrayList<GUIValuePair>();
			GUIValuePair l = new GUIValuePair();
			l.setCode("en");
			l.setValue(Locale.ENGLISH.getDisplayName(withLocale));
			supportedLanguages.add(l);

			StringTokenizer st = new StringTokenizer(i18n.getProperty("locales"), ",", false);
			while (st.hasMoreElements()) {
				String code = (String) st.nextElement();
				if (code.equals("en"))
					continue;
				Locale lc = LocaleUtil.toLocale(code);
				l = new GUIValuePair();
				l.setCode(code);
				l.setValue(lc.getDisplayName(withLocale));
				supportedLanguages.add(l);
			}

			info.setSupportedLanguages(supportedLanguages.toArray(new GUIValuePair[0]));
			info.setBundle(getBundle(locale));
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		}

		return info;
	}

	@Override
	public GUIParameter[][] getStatistics(String sid) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		PropertiesBean conf = (PropertiesBean) Context.getInstance().getBean("ContextProperties");

		GUIParameter[][] parameters = new GUIParameter[3][8];

		try {
			// Repository statistics
			GUIParameter docDirSize = new GUIParameter();
			docDirSize.setName("documents");
			File docDir = new File(conf.getPropertyWithSubstitutions("conf.docdir"));
			if (docDir.exists())
				docDirSize.setValue(Long.toString(FileUtils.sizeOfDirectory(docDir)));
			else
				docDirSize.setValue("0");

			parameters[0][0] = docDirSize;

			GUIParameter userDirSize = new GUIParameter();
			userDirSize.setName("users");
			File userDir = new File(conf.getPropertyWithSubstitutions("conf.userdir"));
			if (userDir.exists())
				userDirSize.setValue(Long.toString(FileUtils.sizeOfDirectory(userDir)));
			else
				userDirSize.setValue("0");

			parameters[0][1] = userDirSize;

			GUIParameter indexDirSize = new GUIParameter();
			indexDirSize.setName("fulltextindex");
			File indexDir = new File(conf.getPropertyWithSubstitutions("conf.indexdir"));
			if (indexDir.exists())
				indexDirSize.setValue(Long.toString(FileUtils.sizeOfDirectory(indexDir)));
			else
				indexDirSize.setValue("0");

			parameters[0][2] = indexDirSize;

			GUIParameter importDirSize = new GUIParameter();
			importDirSize.setName("iimport");
			File importDir = new File(conf.getPropertyWithSubstitutions("conf.importdir"));
			if (importDir.exists())
				importDirSize.setValue(Long.toString(FileUtils.sizeOfDirectory(importDir)));
			else
				importDirSize.setValue("0");

			parameters[0][3] = importDirSize;

			GUIParameter exportDirSize = new GUIParameter();
			exportDirSize.setName("eexport");
			File exportDir = new File(conf.getPropertyWithSubstitutions("conf.exportdir"));
			if (exportDir.exists())
				exportDirSize.setValue(Long.toString(FileUtils.sizeOfDirectory(exportDir)));
			else
				exportDirSize.setValue("0");

			parameters[0][4] = exportDirSize;

			GUIParameter pluginsDirSize = new GUIParameter();
			pluginsDirSize.setName("plugins");
			File pluginsDir = new File(conf.getPropertyWithSubstitutions("conf.plugindir"));
			if (pluginsDir.exists())
				pluginsDirSize.setValue(Long.toString(FileUtils.sizeOfDirectory(pluginsDir)));
			else
				pluginsDirSize.setValue("0");

			parameters[0][5] = pluginsDirSize;

			GUIParameter dbDirSize = new GUIParameter();
			dbDirSize.setName("database");
			File dbDir = new File(conf.getPropertyWithSubstitutions("conf.dbdir"));
			if (dbDir.exists())
				dbDirSize.setValue(Long.toString(FileUtils.sizeOfDirectory(dbDir)));
			else
				dbDirSize.setValue("0");

			parameters[0][6] = dbDirSize;

			GUIParameter logsDirSize = new GUIParameter();
			logsDirSize.setName("logs");
			File logsDir = new File(conf.getPropertyWithSubstitutions("conf.logdir"));
			if (logsDir.exists())
				logsDirSize.setValue(Long.toString(FileUtils.sizeOfDirectory(logsDir)));
			else
				logsDirSize.setValue("0");

			parameters[0][7] = logsDirSize;

			// Documents statistics
			DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
			FolderDAO folderDao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
			GUIParameter notIndexed = new GUIParameter();
			notIndexed.setName("notindexed");
			StringBuilder query = new StringBuilder(
					"SELECT COUNT(A.id) FROM Document A where A.indexed = 0 and A.deleted = 0 ");
			List<Object> records = (List<Object>) docDao.findByQuery(query.toString(), null, null);
			Long count = (Long) records.get(0);
			notIndexed.setValue(Long.toString(count));

			parameters[1][0] = notIndexed;

			GUIParameter indexed = new GUIParameter();
			indexed.setName("indexed");
			query = new StringBuilder("SELECT COUNT(A.id) FROM Document A where A.indexed = 1 and A.deleted = 0 ");
			records = (List<Object>) docDao.findByQuery(query.toString(), null, null);
			count = (Long) records.get(0);
			indexed.setValue(Long.toString(count));

			parameters[1][1] = indexed;

			GUIParameter deletedDocs = new GUIParameter();
			deletedDocs.setName("docstrash");
			deletedDocs.setLabel("trash");
			query = new StringBuilder("SELECT COUNT(A.id) FROM Document A where A.deleted = 1 ");
			records = (List<Object>) docDao.findByQuery(query.toString(), null, null);
			count = (Long) records.get(0);
			deletedDocs.setValue(Long.toString(count));

			parameters[1][2] = deletedDocs;

			// Folders statistics
			GUIParameter notEmptyFolders = new GUIParameter();
			notEmptyFolders.setName("withdocs");
			query = new StringBuilder("SELECT COUNT(A.id) FROM Menu A where (A.type = " + Menu.MENUTYPE_DIRECTORY
					+ " or A.id= " + Menu.MENUID_DOCUMENTS
					+ " ) and A.deleted = 0 and A.id in (select B.folder.id FROM Document B where B.deleted = 0) ");
			records = (List<Object>) folderDao.findByQuery(query.toString(), null, null);
			count = (Long) records.get(0);
			notEmptyFolders.setValue(Long.toString(count));

			parameters[2][0] = notEmptyFolders;

			GUIParameter emptyFolders = new GUIParameter();
			emptyFolders.setName("empty");
			query = new StringBuilder("SELECT COUNT(A.id) FROM Menu A where (A.type = " + Menu.MENUTYPE_DIRECTORY
					+ " or A.id= " + Menu.MENUID_DOCUMENTS
					+ " ) and A.deleted = 0 and A.id not in (select B.folder.id FROM Document B where B.deleted = 0) ");
			records = (List<Object>) folderDao.findByQuery(query.toString(), null, null);
			count = (Long) records.get(0);
			emptyFolders.setValue(Long.toString(count));

			parameters[2][1] = emptyFolders;

			GUIParameter deletedFolders = new GUIParameter();
			deletedFolders.setName("folderstrash");
			deletedFolders.setLabel("trash");
			query = new StringBuilder("SELECT COUNT(A.id) FROM Menu A where (A.type = " + Menu.MENUTYPE_DIRECTORY
					+ " or A.id= " + Menu.MENUID_DOCUMENTS + " ) and A.deleted = 1 ");
			records = (List<Object>) folderDao.findByQuery(query.toString(), null, null);
			count = (Long) records.get(0);
			deletedFolders.setValue(Long.toString(count));

			parameters[2][2] = deletedFolders;

		} catch (Throwable e) {
			e.printStackTrace();
		}

		return parameters;
	}

	@Override
	public GUITask getTaskByName(String sid, String taskName) throws InvalidSessionException {
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

				task.setIndeterminate(tsk.isIndeterminate());

				GUIScheduling scheduling = new GUIScheduling(tsk.getName());
				scheduling.setEnabled(tsk.getScheduling().isEnabled());
				scheduling.setMode(tsk.getScheduling().getMode());
				if (tsk.getScheduling().getMode().equals(DoubleTrigger.MODE_SIMPLE)) {
					scheduling.setSimple(true);
					scheduling.setDelay(tsk.getScheduling().getDelay());
					scheduling.setInterval(tsk.getScheduling().getIntervalSeconds());
					task.setSchedulingLabel(I18N.message("each") + " " + tsk.getScheduling().getIntervalSeconds() + " "
							+ I18N.message("seconds").toLowerCase());
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
				scheduling.setMinCpuIdle((tsk.getScheduling().getMinCpuIdle()));

				task.setScheduling(scheduling);

				return task;
			}
		} catch (Throwable e) {
		}

		return null;
	}

	@Override
	public GUITask[] loadTasks(String sid) throws InvalidSessionException {
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

				GUIScheduling scheduling = new GUIScheduling(t.getName());
				scheduling.setEnabled(t.getScheduling().isEnabled());
				scheduling.setMode(t.getScheduling().getMode());
				if (t.getScheduling().getMode().equals(DoubleTrigger.MODE_SIMPLE)) {
					scheduling.setSimple(true);
					scheduling.setDelay(t.getScheduling().getDelay());
					scheduling.setInterval(t.getScheduling().getIntervalSeconds());
					task.setSchedulingLabel(I18N.message("each") + " " + t.getScheduling().getIntervalSeconds() + " "
							+ I18N.message("seconds").toLowerCase());
				} else if (t.getScheduling().getMode().equals(DoubleTrigger.MODE_CRON)) {
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
				scheduling.setMinCpuIdle((t.getScheduling().getMinCpuIdle()));

				task.setScheduling(scheduling);

				tasks[i] = task;
				i++;
			}

			return tasks;
		} catch (Throwable e) {
		}

		return null;
	}

	@Override
	public void saveFolders(String sid, GUIParameter[] folders) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		PropertiesBean conf = (PropertiesBean) Context.getInstance().getBean("ContextProperties");
		try {
			conf.setProperty("conf.docdir", folders[0].getValue());
			conf.setProperty("conf.indexdir", folders[1].getValue());
			conf.setProperty("conf.userdir", folders[2].getValue());
			conf.setProperty("conf.importdir", folders[4].getValue());
			conf.setProperty("conf.exportdir", folders[5].getValue());
			conf.setProperty("conf.plugindir", folders[3].getValue());
			conf.setProperty("conf.dbdir", folders[6].getValue());
			conf.setProperty("conf.logdir", folders[7].getValue());

			conf.write();
		} catch (IOException e) {
		}
	}

	@Override
	public GUITask saveTask(String sid, GUITask task) throws InvalidSessionException {
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
					tsk.getScheduling().setMode(DoubleTrigger.MODE_SIMPLE);
					tsk.getScheduling().setDelay(task.getScheduling().getDelay());
					tsk.getScheduling().setInterval(task.getScheduling().getInterval() * 1000);
					tsk.getScheduling().setIntervalSeconds(task.getScheduling().getInterval());
					task.setSchedulingLabel(I18N.message("each") + " " + tsk.getScheduling().getIntervalSeconds() + " "
							+ I18N.message("seconds").toLowerCase());
				} else {
					tsk.getScheduling().setMode(DoubleTrigger.MODE_CRON);
					tsk.getScheduling().setSeconds(task.getScheduling().getSeconds());
					tsk.getScheduling().setMinutes(task.getScheduling().getMinutes());
					tsk.getScheduling().setHours(task.getScheduling().getHours());
					tsk.getScheduling().setMonth(task.getScheduling().getMonth());
					tsk.getScheduling().setDayOfMonth(task.getScheduling().getDayOfMonth());
					tsk.getScheduling().setDayOfWeek((task.getScheduling().getDayOfWeek()));
					task.setSchedulingLabel(tsk.getScheduling().getCronExpression());
				}
				tsk.getScheduling().setMaxLength(task.getScheduling().getMaxLength());
				tsk.getScheduling().setMinCpuIdle((task.getScheduling().getMinCpuIdle()));

				tsk.getScheduling().save();
			}

			return task;
		} catch (Throwable e) {
		}
		return null;
	}

	@Override
	public GUIHistory[] search(String sid, String userName, Date from, Date till, int maxResult, String historySid,
			String[] event) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		HistoryDAO dao = (HistoryDAO) Context.getInstance().getBean(HistoryDAO.class);
		try {
			StringBuffer query = new StringBuffer(
					"select A.userName, A.event, A.date, A.title, A.folderId, A.path, A.sessionId from History A where 1=1 ");
			if (userName != null && StringUtils.isNotEmpty(userName))
				query.append(" and lower(A.userName) like '%" + SqlUtil.doubleQuotes(userName.toLowerCase()) + "%'");
			if (historySid != null && StringUtils.isNotEmpty(historySid))
				query.append(" and A.sessionId=" + historySid);
			if (from != null) {
				query.append(" and A.date > '" + new Timestamp(from.getTime()) + "'");
			}
			if (till != null) {
				query.append(" and A.date < '" + new Timestamp(till.getTime()) + "'");
			}
			if (event.length > 0) {
				boolean first = true;
				for (String e : event) {
					if (first)
						query.append(" and (");
					else
						query.append(" or ");

					query.append(" A.event = '" + SqlUtil.doubleQuotes(e) + "'");
					first = false;
				}
				query.append(" ) ");
			}
			query.append(" order by A.date asc ");

			List<Object> records = (List<Object>) dao.findByQuery(query.toString(), null, maxResult);

			if (records.size() == 0)
				return null;

			GUIHistory[] histories = new GUIHistory[records.size()];

			int i = 0;
			for (Object record : records) {
				Object[] cols = (Object[]) record;

				GUIHistory history = new GUIHistory();
				history.setUserName((String) cols[0]);
				history.setEvent((String) cols[1]);
				history.setDate((Date) cols[2]);
				if (((String) cols[3]).trim().equals("menu.documents"))
					history.setTitle("/");
				else
					history.setTitle((String) cols[3]);
				history.setFolderId((Long) cols[4]);
				history.setPath((String) cols[5]);
				history.setSessionId((String) cols[6]);

				histories[i] = history;
				i++;
			}

			return histories;
		} catch (Throwable e) {
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
			return false;
		}
	}

	static GUIValuePair[] getBundle(String locale) {
		// In production, use our LocaleUtil to instantiate the locale
		Locale l = LocaleUtil.toLocale(locale);
		ResourceBundle rb = ResourceBundle.getBundle("i18n.messages", l);
		GUIValuePair[] buf = new GUIValuePair[rb.keySet().size()];
		int i = 0;
		for (String key : rb.keySet()) {
			GUIValuePair entry = new GUIValuePair();
			entry.setCode(key);
			entry.setValue(rb.getString(key));
			buf[i++] = entry;
		}
		return buf;
	}
}