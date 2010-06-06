package com.logicaldoc.web;

import java.util.Locale;

import com.logicaldoc.core.security.SessionManager;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.UserSession;

/**
 * Various methods related to the user session
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class SessionBean {
	public static final String LOCALE = "locale";

	public static final String USER = "user";

	private static final long serialVersionUID = 1L;

	/**
	 * Throws a runtime exception id the given session is invalid
	 */
	public static UserSession validateSession(String sid) {
		UserSession session = SessionManager.getInstance().get(sid);
		if (session == null)
			throw new RuntimeException("Invalid or Expired Session");
		return session;
	}

	public static Locale currentLocale(String sid) {
		UserSession session = validateSession(sid);
		return (Locale) session.getDictionary().get(LOCALE);
	}

	public static User getSessionUser(String sid) {
		UserSession session = validateSession(sid);
		return (User) session.getDictionary().get(USER);
	}
}