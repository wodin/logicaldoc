package com.logicaldoc.web;

import java.util.Locale;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.core.security.SessionManager;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.UserSession;

/**
 * Base class for services implementation
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public abstract class AbstractService extends RemoteServiceServlet {

	public static final String LOCALE = "locale";

	public static final String USER = "user";

	private static final long serialVersionUID = 1L;

	/**
	 * Throws a runtime exception id the given session is invalid
	 */
	protected UserSession validateSession(String sid) {
		UserSession session = SessionManager.getInstance().get(sid);
		if (session == null)
			throw new RuntimeException("Invalid or Expired Session");
		return session;
	}

	protected Locale currentLocale(String sid) {
		UserSession session = validateSession(sid);
		return (Locale) session.getDictionary().get(LOCALE);
	}

	protected User getSessionUser(String sid) {
		UserSession session = validateSession(sid);
		return (User) session.getDictionary().get(USER);
	}
}