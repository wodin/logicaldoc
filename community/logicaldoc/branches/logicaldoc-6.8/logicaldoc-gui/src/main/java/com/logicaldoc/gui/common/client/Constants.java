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

	public static final long DOCUMENTS_FOLDERID = 5;

	public static final long WORKSPACE_DEFAULTID = 4;
	
	public final static String TIME_MINUTE = "minute";

	public final static String TIME_HOUR = "hour";

	public final static String TIME_BUSINESS_HOUR = "businesshour";

	public final static String TIME_DAY = "day";

	public final static String TIME_BUSINESS_DAY = "businessday";

	public final static String TIME_WEEK = "week";

	public final static String TIME_BUSINESS_WEEK = "businessweek";
	
	public static final String PERMISSION_DELETE = "delete";

	public static final String PERMISSION_IMMUTABLE = "immutable";

	public static final String PERMISSION_SECURITY = "security";

	public static final String PERMISSION_WRITE = "write";

	public static final String PERMISSION_ADD = "add";

	public static final String PERMISSION_RENAME = "rename";

	public static final String PERMISSION_IMPORT = "import";

	public static final String PERMISSION_EXPORT = "export";

	public static final String PERMISSION_SIGN = "sign";

	public static final String PERMISSION_ARCHIVE = "archive";

	public static final String PERMISSION_WORKFLOW = "workflow";

	public static final String PERMISSION_DOWNLOAD = "download";

	public static final String PERMISSION_CALENDAR = "calendar";
	
	public static final int DOC_UNLOCKED = 0;

	public static final int DOC_CHECKED_OUT = 1;

	public static final int DOC_LOCKED = 2;

	public static final String SMTP_SECURITY_NONE = "0";

	public static final String SMTP_SECURITY_TLS_IF_AVAILABLE = "1";

	public static final String SMTP_SECURITY_TLS = "2";

	public static final String SMTP_SECURITY_SSL = "3";

	public static final String KEY_ENTER = "enter";

	public static final String GROUP_ADMIN = "admin";

	public static final String GROUP_PUBLISHER = "publisher";
	
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
	
	public static final String ANONYMOUS = "anonymous";

	public static final String BLANK_PLACEHOLDER = "___";
	
	public static final String COOKIE_HITSLIST = "ldoc-hitslist";
	
	public static final String COOKIE_DOCSLIST = "ldoc-docslist";
	
	public static final String COOKIE_DOCSLIST_MAX = "ldoc-docslist-max";
	
	public static final String COOKIE_DOCSMENU_W = "ldoc-docsmenu-w";
	
	public static final String COOKIE_SAVELOGIN = "ldoc-savelogin";
	
	public static final String COOKIE_USER = "ldoc-user";
	
	public static final String COOKIE_PASSWORD = "ldoc-password";
	
	public static final String COOKIE_VERSION = "ldoc-version";
	
	public static final String COOKIE_SID = "ldoc-sid";
	
	public final static int DASHLET_CHECKOUT = 1;

	public final static int DASHLET_CHECKIN = 2;

	public final static int DASHLET_LOCKED = 3;
	
	public final static int DASHLET_DOWNLOADED = 4;

	public final static int DASHLET_CHANGED = 5;
	
	public final static int DASHLET_LAST_NOTES = 6;
	
	public final static int DASHLET_TAGCLOUD = 7;
}