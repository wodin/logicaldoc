package com.logicaldoc.gui.frontend.mock;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.gui.common.client.beans.GUIParameter;
import com.logicaldoc.gui.frontend.client.services.SystemService;

/**
 * Implementation of the SystemService
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class MockSystemServiceImpl extends RemoteServiceServlet implements SystemService {

	private static final long serialVersionUID = 1L;

	@Override
	public GUIParameter[][] getStatistics(String sid) {
		GUIParameter[][] parameters = new GUIParameter[3][7];

		// This is the correct mode to retrieve the doc dir path, but, for now,
		// we use directly the doc dir path
		// String docDirPath = Util.getContext().get("conf_docdir");

		// Repository statistics

		GUIParameter docDirSize = new GUIParameter();
		docDirSize.setName("documents");
		// In hosted mode we cannot read from a folder on the hard disk.
		// File docDir = new File("/C:/Users/Matteo/logicaldoc1005/data/docs/");
		// docDirSize.setValue(Long.toString(FileUtils.sizeOfDirectory(docDir)));

		docDirSize.setValue(Long.toString(28642667));
		parameters[0][0] = docDirSize;

		GUIParameter userDirSize = new GUIParameter();
		userDirSize.setName("users");
		// TODO In hosted mode we cannot read from a folder on the hard disk.
		// File docDir = new File("/C:/Users/Matteo/logicaldoc1005/data/docs/");
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
		// TODO In hosted mode we cannot read from a folder on the hard disk.
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
		// TODO In hosted mode we cannot read from a folder on the hard disk.
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
		// TODO In hosted mode we cannot read from a folder on the hard disk.
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
		// TODO In hosted mode we cannot read from a folder on the hard disk.
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
		// TODO In hosted mode we cannot read from a folder on the hard disk.
		// File dbDir = new File("/C:/Users/Matteo/logicaldoc1005/db/");
		// if (dbDir.exists())
		// dbDirSize.setValue(Long.toString(FileUtils.sizeOfDirectory(dbDir)));
		// else
		// dbDirSize.setValue("0");

		dbDirSize.setValue(Long.toString(11361233));
		parameters[0][6] = dbDirSize;

		// Documents statistics

		GUIParameter notIndexed = new GUIParameter();
		notIndexed.setName("notindexed");
		// In hosted mode we cannot read from a folder on the hard disk.
		// File docDir = new File("/C:/Users/Matteo/logicaldoc1005/data/docs/");
		// docDirSize.setValue(Long.toString(FileUtils.sizeOfDirectory(docDir)));

		notIndexed.setValue(Long.toString(5));
		parameters[1][0] = notIndexed;

		GUIParameter indexed = new GUIParameter();
		indexed.setName("indexed");
		// TODO In hosted mode we cannot read from a folder on the hard disk.
		// File docDir = new File("/C:/Users/Matteo/logicaldoc1005/data/docs/");
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
		// TODO In hosted mode we cannot read from a folder on the hard disk.
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
		// File docDir = new File("/C:/Users/Matteo/logicaldoc1005/data/docs/");
		// docDirSize.setValue(Long.toString(FileUtils.sizeOfDirectory(docDir)));

		notEmpty.setValue(Long.toString(13));
		parameters[2][0] = notEmpty;

		GUIParameter empty = new GUIParameter();
		empty.setName("empty");
		// TODO In hosted mode we cannot read from a folder on the hard disk.
		// File docDir = new File("/C:/Users/Matteo/logicaldoc1005/data/docs/");
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
		// TODO In hosted mode we cannot read from a folder on the hard disk.
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

}
