package com.logicaldoc.gui.common.client;

public final class Constants {
	// The currenly logged user name
	public static final String AUTH_USERNAME = "authUser";

	// The currenly logged user identifier
	public static final String AUTH_USERID = "authUserId";

	// The currenly logged user password
	public static final String AUTH_PASSWORD = "authPassword";

	// The current user session
	public static final String USER_SESSION = "UserSession";

	// The language of the currently logged user
	public static final String LANGUAGE = "language";

	// Sessions the context key of the sessions map
	public static final String SESSIONS = "sessions";

	public static final long ROOT_FOLDERID = -1;

	public static final long DOCUMENTS_FOLDERID = 5;

	public static final String PERMISSION_DELETE = "delete";

	public static final String PERMISSION_IMMUTABLE = "manageImmutability";

	public static final String PERMISSION_SECURITY = "manageSecurity";

	public static final String PERMISSION_WRITE = "write";

	public static final String PERMISSION_ADD = "addChild";

	public static final String PERMISSION_IMMUTABILITY = "manageImmutability";

	public static final String PERMISSION_RENAME = "rename";

	public static final String PERMISSION_IMPORT = "ld_import";

	public static final String PERMISSION_EXPORT = "bulkExport";

	public static final String PERMISSION_SIGN = "sign";

	public static final String PERMISSION_ARCHIVE = "archive";

	public static final String PERMISSION_WORKFLOW = "workflow";

	public static final int DOC_UNLOCKED = 0;

	public static final int DOC_CHECKED_OUT = 1;

	public static final int DOC_LOCKED = 2;

	public static final String SMTP_SECURITY_NONE = "0";

	public static final String SMTP_SECURITY_TLS_IF_AVAILABLE = "1";

	public static final String SMTP_SECURITY_TLS = "2";

	public static final String SMTP_SECURITY_SSL = "3";

	public static final String KEY_ENTER = "enter";

	public static final String GROUP_ADMIN = "admin";

	public final static String EVENT_LOCKED = "event.locked";

	public final static String EVENT_CHECKEDOUT = "event.checkedout";

	public static final String EVENT_DOWNLOADED = "event.downloaded";
	
	public final static String EVENT_CHANGED = "event.changed";

	public final static String EVENT_CHECKEDIN = "event.checkedin";
	
	public final static int INDEX_TO_INDEX = 0;

	public final static int INDEX_INDEXED = 1;

	// The document is un-indexable
	public final static int INDEX_SKIP = 2;

	public static final String LOCALE = "locale";

	public static final String BLANK_PLACEHOLDER = "___";
}