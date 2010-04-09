package com.logicaldoc.web;

import java.util.Locale;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.util.Constants;

/**
 * 
 * @author Michael Scholz
 * @author Marco Meschieri - Logical Objects
 */
public class SessionManagement {
	public static boolean isValid(HttpSession session) {
		boolean result = true;
		String username = (String) session.getAttribute(Constants.AUTH_USERNAME);

		if ((username == null) || username.equals("")) {
			result = false;
		}

		if (session.isNew()) {
			result = false;
		}

		return result;
	}
	
	public static String getCurrentUserSessionId(){
		FacesContext facesContext = FacesContext.getCurrentInstance();
		Map<String, Object> session = facesContext.getExternalContext().getSessionMap();
		String sid = (String) session.get(Constants.USER_SESSION);
		return sid;
	}

	public static boolean isValid() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(false);
		return isValid(session);
	}

	public static String getUsername() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		Map<String, Object> session = facesContext.getExternalContext().getSessionMap();
		String username = (String) session.get(Constants.AUTH_USERNAME);
		return username;
	}

	public static long getUserId() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		Map<String, Object> session = facesContext.getExternalContext().getSessionMap();
		Long userid = (Long) session.get(Constants.AUTH_USERID);
		if (userid == null)
			userid = new Long(-1);
		return userid.longValue();
	}

	public static User getUser() {
		long userId = getUserId();
		UserDAO dao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
		User user = dao.findById(userId);
		user.getGroupIds();
		return user;
	}

	public static String getLanguage() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		Map<String, Object> session = facesContext.getExternalContext().getSessionMap();
		String language = (String) session.get(Constants.LANGUAGE);
		return language;
	}

	public static Locale getLocale() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		Map<String, Object> session = facesContext.getExternalContext().getSessionMap();
		Locale locale = (Locale) session.get(Constants.LOCALE);
		return locale;
	}

	public static HttpSession getSession(String sessionId) {
		try {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			HttpSession session = (HttpSession) (((HttpServletRequest) facesContext.getExternalContext().getRequest()))
					.getAttribute(Constants.SESSIONS);
			return session;
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return null;
	}
}