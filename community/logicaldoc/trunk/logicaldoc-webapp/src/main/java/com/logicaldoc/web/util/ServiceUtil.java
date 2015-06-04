package com.logicaldoc.web.util;

import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;

import com.logicaldoc.core.security.SessionManager;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.UserSession;
import com.logicaldoc.core.security.UserSession.Log;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.common.client.ServerException;
import com.logicaldoc.util.Context;

/**
 * Various methods related to the user session
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class ServiceUtil {
	public static final String LOCALE = "locale";

	public static final String USER = "user";

	public static UserSession validateSession(HttpServletRequest request) throws ServletException {
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
	 * @throws InvalidSessionException
	 */
	public static UserSession validateSession(String sid) throws InvalidSessionException {
		UserSession session = SessionManager.getInstance().get(sid);
		if (session == null)
			throw new InvalidSessionException("Invalid Session");
		if (session.getStatus() != UserSession.STATUS_OPEN)
			throw new InvalidSessionException("Invalid or Expired Session");
		session.renew();
		return session;
	}

	public static Locale currentLocale(UserSession session) throws InvalidSessionException {
		return (Locale) session.getDictionary().get(LOCALE);
	}

	public static Locale currentLocale(String sid) throws InvalidSessionException {
		UserSession session = validateSession(sid);
		return currentLocale(session);
	}

	public static User getSessionUser(String sid) throws InvalidSessionException {
		UserSession session = validateSession(sid);
		User user = (User) session.getDictionary().get(USER);
		UserDAO userDao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
		userDao.initialize(user);
		return user;
	}

	public static User getSessionUser(HttpServletRequest request) throws ServletException {
		UserSession session = validateSession(request);
		User user = (User) session.getDictionary().get(USER);
		UserDAO userDao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
		userDao.initialize(user);
		return user;
	}

	public static Object throwServerException(UserSession session, Logger logger, Throwable t) throws ServerException {
		if (logger != null)
			logger.error(t.getMessage(), t);

		Log lastError = session.getLastError();
		String message = "";
		if (lastError != null) {
			message = lastError.getMessage();
			session.getLogs().clear();
		} else {
			message = t.getMessage();
		}

		message = message.replaceAll("com.logicaldoc.", "").replaceAll("java.lang.", "");
		throw new ServerException(message);
	}
}