package com.logicaldoc.web.util;

public final class Constants {
	// ActionForward to success.
	public static final String SUCCESS = "success";

	// ActionForward to failure.
	public static final String FAILURE = "failure";

	// ActionForward to "outcome".
	public static final String OUTCOME = "outcome";

	// ActionForward to "noaccess".
	public static final String NOACCESS = "noaccess";

	// ActionForward to "invalid".
	public static final String INVALID = "invalid";

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

	// The application skin name
	public static final String SKIN = "skin";

	public static final String LOCALE = "locale";

	public static final String TIMEZONE = "timezone";

	// Sessions the context key of the sessions map
	public static final String SESSIONS = "sessions";

	public static final String OSName = System.getProperty("os.name");

	public static final boolean isOSX = OSName.toLowerCase().startsWith("mac os");

	public static final boolean isLinux = OSName.equalsIgnoreCase("Linux");

	public static final boolean isSolaris = OSName.equalsIgnoreCase("SunOS");

	public static final boolean isFreeBSD = OSName.equalsIgnoreCase("FreeBSD");

	public static final boolean isWindowsXP = OSName.equalsIgnoreCase("Windows XP");

	public static final boolean isWindowsVista = OSName.equalsIgnoreCase("Windows Vista");

	public static final boolean isWindows95 = OSName.equalsIgnoreCase("Windows 95");

	public static final boolean isWindows98 = OSName.equalsIgnoreCase("Windows 98");

	public static final boolean isWindows2000 = OSName.equalsIgnoreCase("Windows 2000");

	public static final boolean isWindowsME = OSName.equalsIgnoreCase("Windows ME");

	public static final boolean isWindows9598ME = isWindows95 || isWindows98 || isWindowsME;

	public static boolean isSafeMode = false;

	public static final boolean isWindows = OSName.toLowerCase().startsWith("windows");

	// If it isn't windows or osx, it's most likely an unix flavor
	public static final boolean isUnix = !isWindows && !isOSX;

	public static final String JAVA_VERSION = System.getProperty("java.version");
}
