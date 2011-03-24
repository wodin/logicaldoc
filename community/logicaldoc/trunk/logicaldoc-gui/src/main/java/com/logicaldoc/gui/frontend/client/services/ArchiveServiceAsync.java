package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUIArchive;
import com.logicaldoc.gui.common.client.beans.GUIIncrementalArchive;
import com.logicaldoc.gui.common.client.beans.GUISostConfig;
import com.logicaldoc.gui.common.client.beans.GUIVersion;

public interface ArchiveServiceAsync {

	void delete(String sid, long archiveId, AsyncCallback<Void> callback);

	void deleteVersions(String sid, long archiveId, Long[] versionIds, AsyncCallback<GUIArchive> callback);

	void save(String sid, GUIArchive archive, AsyncCallback<GUIArchive> callback);

	void setStatus(String sid, long archiveId, int status, AsyncCallback<Void> callback);

	void deleteIncremental(String sid, long id, AsyncCallback<Void> callback);

	void loadIncremental(String sid, long id, AsyncCallback<GUIIncrementalArchive> callback);

	void saveIncremental(String sid, GUIIncrementalArchive incremental, AsyncCallback<GUIIncrementalArchive> callback);

	void addDocuments(String sid, long archiveId, long[] documentIds, AsyncCallback<Void> callback);

	void deleteFolder(String sid, String folderName, AsyncCallback<Void> callback);

	void startImport(String sid, String folderName, AsyncCallback<Void> callback);

	void load(String sid, long archiveId, AsyncCallback<GUIArchive> callback);

	void getSostConfigurations(String sid, long archiveId, AsyncCallback<GUISostConfig[]> callback);

	void signArchive(String sid, long userId, long archiveId, AsyncCallback<String> callback);

	void verifyArchive(String sid, long archiveId, AsyncCallback<GUIVersion[]> callback);
}
