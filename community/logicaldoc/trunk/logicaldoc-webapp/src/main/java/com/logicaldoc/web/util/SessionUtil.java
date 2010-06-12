package com.logicaldoc.web.util;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import com.logicaldoc.core.security.SessionManager;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.UserSession;

/**
 * Various methods related to the user session
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class SessionUtil {
	public static final String LOCALE = "locale";

	public static final String USER = "user";

	private static final long serialVersionUID = 1L;

	public static UserSession validateSession(HttpServletRequest request) throws SecurityException {
		return validateSession((String) request.getParameter("sid"));
	}

	/**
	 * Throws a runtime exception id the given session is invalid
	 * 
	 * @throws SecurityException
	 */
	public static UserSession validateSession(String sid) throws SecurityException {
		UserSession session = SessionManager.getInstance().get(sid);
		if (session == null)
			throw new SecurityException("Invalid Session");
		if (session.getStatus() != UserSession.STATUS_OPEN)
			throw new SecurityException("Invalid or Expired Session");
		return session;
	}

	public static Locale currentLocale(String sid) throws SecurityException {
		UserSession session = validateSession(sid);
		return (Locale) session.getDictionary().get(LOCALE);
	}

	public static User getSessionUser(String sid) throws SecurityException {
		UserSession session = validateSession(sid);
		return (User) session.getDictionary().get(USER);
	}

	public static User getSessionUser(HttpServletRequest request) throws SecurityException {
		UserSession session = validateSession(request);
		return (User) session.getDictionary().get(USER);
	}
}