package com.logicaldoc.gui.frontend.server;

import java.util.Date;
import java.util.UUID;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.common.client.beans.GUIHistory;
import com.logicaldoc.gui.common.client.beans.GUIParameter;
import com.logicaldoc.gui.common.client.beans.GUIScheduling;
import com.logicaldoc.gui.common.client.beans.GUITask;
import com.logicaldoc.gui.common.client.beans.GUIValuePair;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.frontend.client.services.SystemService;

/**
 * Implementation of the SystemService
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class MockSystemServiceImpl extends RemoteServiceServlet implements SystemService {

	private static final long serialVersionUID = 1L;

	private static int progress = 0;

	@Override
	public GUIParameter[][] getStatistics(String sid, String locale) {
		GUIParameter[][] parameters = new GUIParameter[4][8];

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

		GUIParameter lastupdate = new GUIParameter();
		lastupdate.setName("lastupdate");
		lastupdate.setValue("01/01/2011");
		parameters[3][0] = lastupdate;

		return parameters;
	}

	@Override
	public GUIHistory[] search(String sid, String userName, Date from, Date till, int maxResult, String historySid,
			String event[]) {
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
	public GUITask[] loadTasks(String sid, String locale) {
		if (progress >= 100)
			progress = -1;
		progress++;

		GUITask[] tasks = new GUITask[20];

		for (int i = 0; i < tasks.length; i++) {
			GUITask task = new GUITask();

			task.setName("Task" + i);
			task.setStatus(1);
			task.setProgress(progress);
			task.setSize(0);
			task.setScheduling(new GUIScheduling("Task" + i));
			task.setSchedulingLabel(I18N.message("each") + " " + task.getScheduling().getInterval() + " "
					+ I18N.message("seconds").toLowerCase());
			if (i % 2 == 0)
				task.getScheduling().setEnabled(true);
			else
				task.getScheduling().setEnabled(false);

			if (i == 2)
				task.setStatus(0);
			if (i == 4)
				task.setIndeterminate(true);

			tasks[i] = task;
		}

		return tasks;
	}

	@Override
	public boolean startTask(String taskName) {

		return true;
	}

	@Override
	public boolean stopTask(String taskName) {

		return true;
	}

	@Override
	public GUITask getTaskByName(String sid, String taskName, String locale) {
		GUITask task = new GUITask();

		task.setName("Task 0");
		task.setStatus(1);
		task.setProgress(progress);
		task.setSize(0);
		task.setScheduling(new GUIScheduling("Task 0"));
		task.setSchedulingLabel(I18N.message("each") + " " + task.getScheduling().getInterval() + " "
				+ I18N.message("seconds").toLowerCase());

		task.getScheduling().setEnabled(true);

		task.setStatus(0);
		task.setIndeterminate(false);

		return task;
	}

	@Override
	public boolean disableTask(String sid, String taskName) {
		GUITask task = getTaskByName(sid, taskName, "en");
		task.getScheduling().setEnabled(false);
		return true;
	}

	@Override
	public boolean enableTask(String sid, String taskName) {
		GUITask task = getTaskByName(sid, taskName, "en");
		task.getScheduling().setEnabled(true);
		return true;
	}

	@Override
	public GUITask saveTask(String sid, GUITask task, String locale) {
		if (task.getScheduling().isSimple()) {
			task.setSchedulingLabel(I18N.message("each") + " " + task.getScheduling().getInterval() + " "
					+ I18N.message("seconds").toLowerCase());
		} else {
			GUIScheduling s = task.getScheduling();
			task.setSchedulingLabel(s.getSeconds() + " " + s.getMinutes() + " " + s.getHours() + " "
					+ s.getDayOfMonth() + " " + s.getMonth() + " " + s.getDayOfWeek());
		}
		return task;
	}

	@Override
	public void setGUILanguageStatus(String sid, String language, boolean active) throws InvalidSessionException {

	}

	@Override
	public void maskFeedMsgAsRead(String sid, long[] ids) throws InvalidSessionException {
		return;
	}

	@Override
	public void maskFeedMsgAsNotRead(String sid, long[] ids) throws InvalidSessionException {
		return;
	}

	@Override
	public void deleteFeedMessages(String sid, long[] ids) throws InvalidSessionException {
		return;
	}

	@Override
	public GUIValuePair[] getPlugins(String sid) throws InvalidSessionException {
		GUIValuePair[] plugins = new GUIValuePair[20];

		for (int i = 0; i < plugins.length; i++) {
			GUIValuePair task = new GUIValuePair();
			task.setCode("Plugin" + i);
			task.setValue("version" + i);
			plugins[i] = task;
		}

		return plugins;
	}
}