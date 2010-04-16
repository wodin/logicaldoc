package com.logicaldoc.webapp.security;

import java.util.Date;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.UserHistory;
import com.logicaldoc.core.security.authentication.AuthenticationChain;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.gui.common.client.beans.GUIRight;
import com.logicaldoc.gui.common.client.beans.GUIUser;
import com.logicaldoc.gui.frontend.client.services.SecurityService;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.io.CryptUtil;
import com.logicaldoc.web.SessionManagement;

/**
 * Implementation of the SecurityService
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class SecurityServiceImpl extends RemoteServiceServlet implements SecurityService {

	private static final long serialVersionUID = 1L;

	private static Log log = LogFactory.getLog(SecurityServiceImpl.class);

	@Override
	public GUIRight[] getSecurityEntities(String sid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GUIUser login(String username, String password) {
		UserDAO userDao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
		AuthenticationChain authenticationChain = (AuthenticationChain) Context.getInstance().getBean(
				AuthenticationChain.class);

		FacesContext facesContext = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();

		GUIUser guiUser = new GUIUser();
		if (authenticationChain.authenticate(username, password, request.getRemoteAddr())) {
			User user = userDao.findByUserName(username);
			guiUser.setFirstName(user.getFirstName());
			guiUser.setId(user.getId());
			guiUser.setName(user.getName());
			guiUser.setGroups(user.getGroupNames());
			guiUser.setUserName(username);
			guiUser.setSid(AuthenticationChain.getSessionId());
			guiUser.setExpired(false);
		} else if (userDao.isPasswordExpired(username)) {
			User user = userDao.findByUserName(username);
			guiUser.setId(user.getId());
			guiUser.setExpired(true);
			log.info("User " + username + " password expired");
		} else {
			guiUser = null;
			log.warn("User " + username + " is not valid");
		}

		return guiUser;
	}

	@Override
	public void logout(String sid) {
		// TODO Auto-generated method stub
	}

	@Override
	public int changePassword(long userId, String oldPassword, String newPassword) {
		try {
			UserDAO userDao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
			User user = userDao.findById(userId);
			if (user == null)
				throw new Exception("User " + userId + " not found");

			if (!CryptUtil.cryptString(oldPassword).equals(user.getPassword())) {
				return 1;
			}

			UserHistory history = null;
			// The password was changed
			user.setDecodedPassword(newPassword);
			user.setPasswordChanged(new Date());
			// Add a user history entry
			history = new UserHistory();
			history.setUserId(user.getId());
			history.setUserName(user.getFullName());
			history.setEvent(UserHistory.EVENT_USER_PASSWORDCHANGED);
			history.setComment("");
			history.setSessionId(SessionManagement.getCurrentUserSessionId());
			user.setRepass("");

			UserDAO dao = (UserDAO) Context.getInstance().getBean(UserDAO.class);

			boolean stored = dao.store(user, history);

			if (!stored)
				throw new Exception("User not stored");
			return 0;
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			return 1;
		}
	}
}