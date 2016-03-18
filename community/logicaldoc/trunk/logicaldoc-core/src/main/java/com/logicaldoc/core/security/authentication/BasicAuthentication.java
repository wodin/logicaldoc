package com.logicaldoc.core.security.authentication;

import com.logicaldoc.core.security.dao.UserDAO;

/**
 * This is the basic authentication mechanism, that searches for the user in the
 * LogicalDOC database.
 * 
 * @author Sebastian Wenzky
 * @since 4.5
 */
public class BasicAuthentication implements AuthenticationProvider {

	protected UserDAO userDAO;

	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	@Override
	public boolean validateOnUser(String user) {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean authenticate(String username, String password, String key, Object userObject) {
		return authenticate(username, password, key);
	}

	@Override
	public boolean authenticate(String username, String password) {
		return this.authenticate(username, password, null);
	}

	@Override
	public boolean authenticate(String username, String password, String key) {
		return userDAO.validateUser(username, password);
	}
}