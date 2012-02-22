package com.logicaldoc.core.document;

/**
 * Registers an event on folder or document
 * 
 * @author Michael Scholz
 * @author Alessandro Gasparini - Logical Objects
 * @author Marco Meschieri - Logical Objects
 */
public class History extends AbstractHistory {
	// Events on documents
	public final static String EVENT_STORED = "event.stored";

	public final static String EVENT_CHANGED = "event.changed";

	public final static String EVENT_CHECKEDIN = "event.checkedin";

	public final static String EVENT_CHECKEDOUT = "event.checkedout";

	public static final String EVENT_IMMUTABLE = "event.makeimmutable";

	public static final String EVENT_RENAMED = "event.renamed";

	public static final String EVENT_DOWNLOADED = "event.downloaded";

	public final static String EVENT_MOVED = "event.moved";

	public final static String EVENT_LOCKED = "event.locked";

	public final static String EVENT_UNLOCKED = "event.unlocked";

	public final static String EVENT_ARCHIVED = "event.archived";

	public final static String EVENT_DELETED = "event.deleted";
	
	public final static String EVENT_SENT = "event.sent";
	
	public final static String EVENT_BARCODED = "event.barcoded";
	
	public final static String EVENT_WORKFLOWSTATUS = "event.workflowstatus";

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

	// Events on shortcuts
	public final static String EVENT_SHORTCUT_STORED = "event.shortcut.stored";

	public final static String EVENT_SHORTCUT_MOVED = "event.shortcut.moved";

	public final static String EVENT_SHORTCUT_DELETED = "event.shortcut.deleted";
	
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		History history = new History();
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

		return history;
	}
}
