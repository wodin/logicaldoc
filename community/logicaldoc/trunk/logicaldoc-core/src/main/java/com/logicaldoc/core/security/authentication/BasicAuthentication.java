package com.logicaldoc.core.security.authentication;

import com.logicaldoc.core.security.dao.UserDAO;

/**
 * 
 * @author Sebastian Wenzky
 * @since 4.5
 */
public class BasicAuthentication implements ForeignSystemComponent{

	private UserDAO userDAO;
	
	
	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}
	
	@Override
	public int getOrderId() {
		return 0;
	}

	@Override
	public boolean validateOnUser(String user) {
		if(user.equals("admin"))
			return true;
		else
			return false;
	}

	@Override
	public boolean authenticate(String username, String password) {
		return userDAO.validateUser(username, password);
	}

}
