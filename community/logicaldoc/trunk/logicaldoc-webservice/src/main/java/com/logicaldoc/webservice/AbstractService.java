package com.logicaldoc.webservice;

import javax.annotation.Resource;
import javax.xml.ws.WebServiceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.document.dao.FolderDAO;
import com.logicaldoc.core.security.Permission;
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

	public static Log log = LogFactory.getLog(AbstractService.class);

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
		if (!validateSession) {
			User user = new User();
			user.setId(1);
			user.setName("admin");
			return user;
		}

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

	protected void checkPermission(Permission permission, User user, long folderId) throws Exception {
		FolderDAO dao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		if (!dao.isPermissionEnabled(permission, folderId, user.getId())) {
			String message = "User " + user.getUserName() + " doesn't have permission " + permission.getName()
					+ " on folder " + folderId;
			log.error(message);
			throw new Exception(message);
		}
	}

	protected void checkWriteEnable(User user, long folderId) throws Exception {
		checkPermission(Permission.WRITE, user, folderId);
	}

	protected void checkReadEnable(User user, long folderId) throws Exception {
		FolderDAO dao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		if (!dao.isReadEnable(folderId, user.getId())) {
			String message = "User " + user.getUserName() + " doesn't have read permission on folder " + folderId;
			log.error(message);
			throw new Exception(message);
		}
	}
}