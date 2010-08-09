package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUIArchive;

public interface ArchiveServiceAsync {

	void delete(String sid, long archiveId, AsyncCallback<Void> callback);

	void deleteVersions(String sid, long archiveId, long[] versionIds, AsyncCallback<Void> callback);

	void save(String sid, GUIArchive archive, AsyncCallback<GUIArchive> callback);

	void setStatus(String sid, long archiveId, int status, AsyncCallback<Void> callback);

}
