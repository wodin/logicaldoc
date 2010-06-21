package com.logicaldoc.web.service;

import java.util.Date;
import java.util.UUID;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.core.task.Task;
import com.logicaldoc.core.task.TaskManager;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.common.client.beans.GUIHistory;
import com.logicaldoc.gui.common.client.beans.GUIInfo;
import com.logicaldoc.gui.common.client.beans.GUIParameter;
import com.logicaldoc.gui.common.client.beans.GUIScheduling;
import com.logicaldoc.gui.common.client.beans.GUITask;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.frontend.client.services.SystemService;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.quartz.DoubleTrigger;
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

	@Override
	public boolean disableTask(String sid, String taskName) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		try {
			GUITask task = getTaskByName(sid, taskName);
			task.getScheduling().setEnabled(false);
			saveTask(sid, task);
			return true;
		} catch (Throwable e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean enableTask(String sid, String taskName) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		try {
			GUITask task = getTaskByName(sid, taskName);
			task.getScheduling().setEnabled(true);
			saveTask(sid, task);
			return true;
		} catch (Throwable e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public GUIInfo getInfo() {
		GUIInfo info = new GUIInfo();
		return info;
	}

	@Override
	public GUIParameter[][] getStatistics(String sid) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		GUIParameter[][] parameters = new GUIParameter[3][8];

		// This is the correct mode to retrieve the doc dir path, but, for
		// now,
		// we use directly the doc dir path
		// String docDirPath = Util.getContext().get("conf_docdir");

		// Repository statistics

		GUIParameter docDirSize = new GUIParameter();
		docDirSize.setName("documents");
		// In hosted mode we cannot read from a folder on the hard disk.
		// File docDir = new
		// File("/C:/Users/Matteo/logicaldoc1005/data/docs/");
		// docDirSize.setValue(Long.toString(FileUtils.sizeOfDirectory(docDir)));

		docDirSize.setValue(Long.toString(28642667));
		parameters[0][0] = docDirSize;

		GUIParameter userDirSize = new GUIParameter();
		userDirSize.setName("users");
		// In hosted mode we cannot read from a folder on the hard disk.
		// File docDir = new
		// File("/C:/Users/Matteo/logicaldoc1005/data/docs/");
		// File userDir = new
		// File("/C:/Users/Matteo/logicaldoc1005/data/users/");
		// if (userDir.exists())
		// userDirSize.setValue(Long.toString(FileUtils.sizeOfDirectory(userDir)));
		// else
		// userDirSize.setValue("0");

		userDirSize.setValue(Long.toString(486420));
		parameters[0][1] = userDirSize;

		GUIParameter indexDirSize = new GUIParameter();
		indexDirSize.setName("fulltextindex");
		// In hosted mode we cannot read from a folder on the hard disk.
		// File indexDir = new
		// File("/C:/Users/Matteo/logicaldoc1005/data/index/");
		// if (indexDir.exists())
		// indexDirSize.setValue(Long.toString(FileUtils.sizeOfDirectory(indexDir)));
		// else
		// indexDirSize.setValue("0");

		indexDirSize.setValue(Long.toString(10344480));
		parameters[0][2] = indexDirSize;

		GUIParameter importDirSize = new GUIParameter();
		importDirSize.setName("iimport");
		// In hosted mode we cannot read from a folder on the hard disk.
		// File importDir = new
		// File("/C:/Users/Matteo/logicaldoc1005/impex/in/");
		// if (importDir.exists())
		// importDirSize.setValue(Long.toString(FileUtils.sizeOfDirectory(importDir)));
		// else
		// importDirSize.setValue("0");

		importDirSize.setValue(Long.toString(21434368));
		parameters[0][3] = importDirSize;

		GUIParameter exportDirSize = new GUIParameter();
		exportDirSize.setName("eexport");
		// In hosted mode we cannot read from a folder on the hard disk.
		// File exportDir = new
		// File("/C:/Users/Matteo/logicaldoc1005/impex/out/");
		// if (exportDir.exists())
		// exportDirSize.setValue(Long.toString(FileUtils.sizeOfDirectory(exportDir)));
		// else
		// exportDirSize.setValue("0");

		exportDirSize.setValue(Long.toString(1613824));
		parameters[0][4] = exportDirSize;

		GUIParameter pluginsDirSize = new GUIParameter();
		pluginsDirSize.setName("plugins");
		// In hosted mode we cannot read from a folder on the hard disk.
		// File pluginsDir = new
		// File("/C:/Users/Matteo/logicaldoc1005/data/plugins/");
		// if (pluginsDir.exists())
		// pluginsDirSize.setValue(Long.toString(FileUtils.sizeOfDirectory(pluginsDir)));
		// else
		// pluginsDirSize.setValue("0");

		pluginsDirSize.setValue(Long.toString(942080));
		parameters[0][5] = pluginsDirSize;

		GUIParameter dbDirSize = new GUIParameter();
		dbDirSize.setName("database");
		// In hosted mode we cannot read from a folder on the hard disk.
		// File dbDir = new File("/C:/Users/Matteo/logicaldoc1005/db/");
		// if (dbDir.exists())
		// dbDirSize.setValue(Long.toString(FileUtils.sizeOfDirectory(dbDir)));
		// else
		// dbDirSize.setValue("0");

		dbDirSize.setValue(Long.toString(11361233));
		parameters[0][6] = dbDirSize;

		GUIParameter logsDirSize = new GUIParameter();
		logsDirSize.setName("logs");
		logsDirSize.setValue(Long.toString(1042081));
		parameters[0][7] = logsDirSize;

		// Documents statistics
		GUIParameter notIndexed = new GUIParameter();
		notIndexed.setName("notindexed");
		// In hosted mode we cannot read from a folder on the hard disk.
		// File docDir = new
		// File("/C:/Users/Matteo/logicaldoc1005/data/docs/");
		// docDirSize.setValue(Long.toString(FileUtils.sizeOfDirectory(docDir)));

		notIndexed.setValue(Long.toString(5));
		parameters[1][0] = notIndexed;

		GUIParameter indexed = new GUIParameter();
		indexed.setName("indexed");
		// In hosted mode we cannot read from a folder on the hard disk.
		// File docDir = new
		// File("/C:/Users/Matteo/logicaldoc1005/data/docs/");
		// File userDir = new
		// File("/C:/Users/Matteo/logicaldoc1005/data/users/");
		// if (userDir.exists())
		// userDirSize.setValue(Long.toString(FileUtils.sizeOfDirectory(userDir)));
		// else
		// userDirSize.setValue("0");

		indexed.setValue(Long.toString(20));
		parameters[1][1] = indexed;

		GUIParameter deletedDocs = new GUIParameter();
		deletedDocs.setName("docstrash");
		deletedDocs.setLabel("trash");
		// In hosted mode we cannot read from a folder on the hard disk.
		// File indexDir = new
		// File("/C:/Users/Matteo/logicaldoc1005/data/index/");
		// if (indexDir.exists())
		// indexDirSize.setValue(Long.toString(FileUtils.sizeOfDirectory(indexDir)));
		// else
		// indexDirSize.setValue("0");

		deletedDocs.setValue(Long.toString(10));
		parameters[1][2] = deletedDocs;

		// Folders statistics

		GUIParameter notEmpty = new GUIParameter();
		notEmpty.setName("withdocs");
		// In hosted mode we cannot read from a folder on the hard disk.
		// File docDir = new
		// File("/C:/Users/Matteo/logicaldoc1005/data/docs/");
		// docDirSize.setValue(Long.toString(FileUtils.sizeOfDirectory(docDir)));

		notEmpty.setValue(Long.toString(13));
		parameters[2][0] = notEmpty;

		GUIParameter empty = new GUIParameter();
		empty.setName("empty");
		// In hosted mode we cannot read from a folder on the hard disk.
		// File docDir = new
		// File("/C:/Users/Matteo/logicaldoc1005/data/docs/");
		// File userDir = new
		// File("/C:/Users/Matteo/logicaldoc1005/data/users/");
		// if (userDir.exists())
		// userDirSize.setValue(Long.toString(FileUtils.sizeOfDirectory(userDir)));
		// else
		// userDirSize.setValue("0");

		empty.setValue(Long.toString(46));
		parameters[2][1] = empty;

		GUIParameter deletedFolders = new GUIParameter();
		deletedFolders.setName("folderstrash");
		deletedFolders.setLabel("trash");
		// In hosted mode we cannot read from a folder on the hard disk.
		// File indexDir = new
		// File("/C:/Users/Matteo/logicaldoc1005/data/index/");
		// if (indexDir.exists())
		// indexDirSize.setValue(Long.toString(FileUtils.sizeOfDirectory(indexDir)));
		// else
		// indexDirSize.setValue("0");

		deletedFolders.setValue(Long.toString(15));
		parameters[2][2] = deletedFolders;

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
				task.setSchedulingLabel(I18N.message("each") + " " + tsk.getScheduling().getInterval() + " "
						+ I18N.message("seconds").toLowerCase());
				task.setIndeterminate(tsk.isIndeterminate());

				GUIScheduling scheduling = new GUIScheduling(tsk.getName());
				scheduling.setEnabled(tsk.getScheduling().isEnabled());
				scheduling.setDelay(tsk.getScheduling().getDelay());
				task.setScheduling(scheduling);
				
				return task;
			}
		} catch (Throwable e) {
			e.printStackTrace();
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
				task.setScheduling(new GUIScheduling(t.getName()));
				task.setSchedulingLabel(I18N.message("each") + " " + t.getScheduling().getInterval() + " "
						+ I18N.message("seconds").toLowerCase());
				task.getScheduling().setEnabled(t.getScheduling().isEnabled());
				task.setIndeterminate(t.isIndeterminate());

				tasks[i] = task;
				i++;
			}

			return tasks;
		} catch (Throwable e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public void saveFolders(String sid, GUIParameter[] folders) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		// TODO Auto-generated method stub
	}

	@Override
	public GUITask saveTask(String sid, GUITask task) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		System.out.println("**** save task!!!");
		TaskManager manager = (TaskManager) Context.getInstance().getBean(TaskManager.class);

		try {
			Task tsk = null;
			for (Task t : manager.getTasks()) {
				if (t.getName().equals(task.getName())) {
					tsk = t;
					break;
				}
			}

			System.out.println("**** tsk: " + tsk.getName());

			if (tsk != null) {
				tsk.getScheduling().setEnabled(task.getScheduling().isEnabled());
				if (task.getScheduling().isSimple()) {
					tsk.getScheduling().setMode(DoubleTrigger.MODE_SIMPLE);
					tsk.getScheduling().setDelay(task.getScheduling().getDelay());
					tsk.getScheduling().setInterval(task.getScheduling().getInterval());
				} else {
					tsk.getScheduling().setMode(DoubleTrigger.MODE_CRON);
					tsk.getScheduling().setSeconds(task.getScheduling().getSeconds());
					tsk.getScheduling().setMinutes(task.getScheduling().getMinutes());
					tsk.getScheduling().setHours(task.getScheduling().getHours());
					tsk.getScheduling().setMonth(task.getScheduling().getMonth());
					tsk.getScheduling().setDayOfMonth(task.getScheduling().getDayOfMonth());
					tsk.getScheduling().setDayOfWeek((task.getScheduling().getDayOfWeek()));
				}
				tsk.getScheduling().setMaxLength(task.getScheduling().getMaxLength());
				tsk.getScheduling().setMinCpuIdle((task.getScheduling().getMinCpuIdle()));

				tsk.getScheduling().save();
			}

			return task;
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public GUIHistory[] search(String sid, String userName, Date from, Date till, int maxResult, String historySid,
			String[] event) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		GUIHistory[] histories = new GUIHistory[maxResult];

		for (int i = 0; i < maxResult; i++) {
			GUIHistory history = new GUIHistory();
			if (i % 2 == 0)
				history.setEvent("event.stored");
			else
				history.setEvent("event.folder.created");
			history.setDate(new Date());
			history.setUserName("Mario Rossi");
			if (i % 2 == 0)
				history.setTitle("document" + i);
			else
				history.setTitle("folder" + i);
			history.setFolderId(5);
			history.setPath("/5/folder" + i);
			history.setSessionId(UUID.randomUUID().toString());

			histories[i] = history;
		}

		return histories;
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
			e.printStackTrace();
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
			e.printStackTrace();
			return false;
		}
	}
}