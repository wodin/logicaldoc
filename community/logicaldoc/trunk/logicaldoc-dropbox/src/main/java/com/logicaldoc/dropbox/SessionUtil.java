package com.logicaldoc.dropbox;

import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.logicaldoc.core.security.SessionManager;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.Session;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.gui.common.client.ServerException;
import com.logicaldoc.util.Context;

/**
 * Various methods related to the user session
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class SessionUtil {
	public static final String LOCALE = "locale";

	public static final String USER = "user";

	public static Session validateSession(HttpServletRequest request) throws ServletException {
		try {
			String sid = (String) request.getParameter("sid");
			if (sid == null) {
				// Check if the sid is in the cookies
				Cookie[] cookies = request.getCookies();
				if (cookies != null)
					for (Cookie cookie : cookies) {
						if ("ldoc-sid".equals(cookie.getName()))
							sid = cookie.getValue();
					}
			}

			return validateSession(sid);
		} catch (ServerException e) {
			throw new ServletException(e);
		}
	}

	/**
	 * Throws a runtime exception id the given session is invalid
	 * 
	 * @throws SecurityException
	 */
	public static Session validateSession(String sid) throws ServerException {
		Session session = SessionManager.get().get(sid);
		if (session == null)
			throw new ServerException("Invalid Session");
		if (session.getStatus() != Session.STATUS_OPEN)
			throw new ServerException("Invalid or Expired Session");
		session.renew();
		return session;
	}

	public static Locale currentLocale(Session session) throws ServerException {
		return (Locale) session.getDictionary().get(LOCALE);
	}

	public static Locale currentLocale(String sid) throws ServerException {
		Session session = validateSession(sid);
		return currentLocale(session);
	}

	public static User getSessionUser(String sid) throws ServerException {
		Session session = validateSession(sid);
		User user = (User) session.getDictionary().get(USER);
		UserDAO userDao = (UserDAO) Context.get().getBean(UserDAO.class);
		userDao.initialize(user);
		return user;
	}

	public static User getSessionUser(HttpServletRequest request) throws ServletException {
		Session session = validateSession(request);
		User user = (User) session.getDictionary().get(USER);
		UserDAO userDao = (UserDAO) Context.get().getBean(UserDAO.class);
		userDao.initialize(user);
		return user;
	}
}