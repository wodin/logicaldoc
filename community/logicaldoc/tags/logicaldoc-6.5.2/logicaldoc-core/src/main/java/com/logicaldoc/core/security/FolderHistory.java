package com.logicaldoc.core.security;

import com.logicaldoc.core.document.AbstractHistory;

/**
 * History entry due to an event on a folder.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.4
 */
public class FolderHistory extends AbstractHistory {

	// Events on folders
	public final static String EVENT_FOLDER_CREATED = "event.folder.created";

	public final static String EVENT_FOLDER_RENAMED = "event.folder.renamed";

	public final static String EVENT_FOLDER_PERMISSION = "event.folder.permission";

	public final static String EVENT_FOLDER_DELETED = "event.folder.deleted";

	public final static String EVENT_FOLDER_MOVED = "event.folder.moved";

	public final static String EVENT_FOLDER_CHANGED = "event.folder.changed";

	// Events on sub-folders
	public final static String EVENT_FOLDER_SUBFOLDER_CREATED = "event.folder.subfolder.created";

	public final static String EVENT_FOLDER_SUBFOLDER_RENAMED = "event.folder.subfolder.renamed";

	public final static String EVENT_FOLDER_SUBFOLDER_PERMISSION = "event.folder.subfolder.permission";

	public final static String EVENT_FOLDER_SUBFOLDER_DELETED = "event.folder.subfolder.deleted";

	@Override
	public Object clone() throws CloneNotSupportedException {
		FolderHistory history = new FolderHistory();
		history.setDate(getDate());
		history.setDocId(getDocId());
		history.setFolderId(getFolderId());
		history.setUser(getUser());
		history.setEvent(getEvent());
		history.setComment(getComment());
		history.setTitle(getTitle());
		history.setVersion(getVersion());
		history.setPath(getPath());
		history.setNotified(getNotified());
		history.setSessionId(getSessionId());
		history.setNew(getNew());
		history.setFilename(getFilename());
		history.setUserId(getUserId());
		history.setUserName(getUserName());

		return history;
	}
}