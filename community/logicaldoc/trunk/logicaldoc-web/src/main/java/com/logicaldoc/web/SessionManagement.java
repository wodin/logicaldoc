package com.logicaldoc.web;

import java.util.Map;

import javax.faces.context.FacesContext;
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
		return userid.longValue();
	}

	public static User getUser() {
		long userId = getUserId();
		UserDAO dao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
		User user = dao.findByPrimaryKey(userId);
		user.getGroupIds();
		return user;
	}

	public static String getLanguage() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		Map<String, Object> session = facesContext.getExternalContext().getSessionMap();
		String language = (String) session.get(Constants.LANGUAGE);
		return language;
	}
}
