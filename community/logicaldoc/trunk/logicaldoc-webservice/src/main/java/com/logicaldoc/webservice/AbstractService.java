package com.logicaldoc.webservice;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;
import javax.xml.ws.WebServiceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.security.Group;
import com.logicaldoc.core.security.Permission;
import com.logicaldoc.core.security.SessionManager;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.FolderDAO;
import com.logicaldoc.core.security.dao.GroupDAO;
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
		UserDAO userDao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
		if (!validateSession) {
			User user = new User();
			user.setId(1);
			user.setName("admin");
			Set<Group> groups = new HashSet<Group>();
			GroupDAO grpDao = (GroupDAO) Context.getInstance().getBean(GroupDAO.class);
			groups.add(grpDao.findById(1));
			user.setGroups(groups);
			return user;
		}

		if (!SessionManager.getInstance().isValid(sid)) {
			throw new Exception("Invalid session");
		} else {
			SessionManager.getInstance().renew(sid);
		}
		String username = SessionManager.getInstance().get(sid).getUserName();
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

	public static String convertDateToString(Date date) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			return df.format(date);
		} catch (Exception e) {
			df = new SimpleDateFormat("yyyy-MM-dd");
			try {
				return df.format(date);
			} catch (Exception e1) {
			}
		}
		return null;
	}

	public static Date convertStringToDate(String date) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			return df.parse(date);
		} catch (ParseException e) {
			df = new SimpleDateFormat("yyyy-MM-dd");
			try {
				return df.parse(date);
			} catch (ParseException e1) {
			}
		}
		return null;
	}
}