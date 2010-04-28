package com.logicaldoc.webservice;

import javax.annotation.Resource;
import javax.xml.ws.WebServiceContext;

import com.logicaldoc.core.security.SessionManager;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.util.Context;

/**
 * Basepoint for creating webservices implementations
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 5.2
 */
public class AbstractService {

	private boolean validateSession = true;

	public void setValidateSession(boolean validateSession) {
		this.validateSession = validateSession;
	}

	@Resource
	protected WebServiceContext context;

	/**
	 * Utility method that validates the session and retrieve the associated
	 * user
	 * 
	 * @param sid The session identifier
	 * @return
	 * @throws Exception
	 */
	protected User validateSession(String sid) throws Exception {
		if (!validateSession)
			return null;

		if (!SessionManager.getInstance().isValid(sid)) {
			throw new Exception("Invalid session");
		} else {
			SessionManager.getInstance().renew(sid);
		}
		String username = SessionManager.getInstance().get(sid).getUserName();
		UserDAO userDao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
		User user = userDao.findByUserName(username);
		if (user == null)
			throw new Exception("User " + username + "not found");
		return user;
	}
}