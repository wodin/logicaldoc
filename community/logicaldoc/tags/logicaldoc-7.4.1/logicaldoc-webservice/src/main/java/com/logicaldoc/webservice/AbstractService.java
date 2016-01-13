package com.logicaldoc.webservice;

import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;
import javax.xml.ws.WebServiceContext;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.jaxrs.ext.MessageContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.core.document.AbstractDocument;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.security.Group;
import com.logicaldoc.core.security.Permission;
import com.logicaldoc.core.security.SessionManager;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.FolderDAO;
import com.logicaldoc.core.security.dao.GroupDAO;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.ContextProperties;

/**
 * Basepoint for creating webservices implementations
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 5.2
 */
public class AbstractService {

	protected static Logger log = LoggerFactory.getLogger(AbstractService.class);

	private boolean validateSession = true;

	public void setValidateSession(boolean validateSession) {
		this.validateSession = validateSession;
	}

	@Resource
	protected WebServiceContext context;

	@Resource
	protected MessageContext messageContext;

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
			user.setId(1L);
			user.setTenantId(1L);
			user.setName("admin");
			Set<Group> groups = new HashSet<Group>();
			GroupDAO grpDao = (GroupDAO) Context.getInstance().getBean(GroupDAO.class);
			groups.add(grpDao.findById(1));
			user.setGroups(groups);
			return user;
		}

		if (!isWebserviceEnabled())
			throw new Exception("WebServices are disabled");

		if (sid == null || !SessionManager.getInstance().isValid(sid)) {
			throw new Exception("Invalid session " + sid);
		} else {
			SessionManager.getInstance().renew(sid);
		}
		String username = SessionManager.getInstance().get(sid).getUserName();
		User user = userDao.findByUserName(username);
		if (user == null)
			throw new Exception("User " + username + "not found");
		else
			userDao.initialize(user);
		return user;
	}

	/**
	 * Checks if the current user belongs to a group
	 */
	protected void checkGroup(String sid, String group) throws Exception {
		User user = validateSession(sid);
		if (!user.isInGroup(group)) {
			String message = "User " + user.getUserName() + " doesn't belong to group " + group;
			log.error(message);
			throw new Exception(message);
		}
	}

	/**
	 * Checks if the current user is an administrator (group admin).
	 */
	protected void checkAdministrator(String sid) throws Exception {
		checkGroup(sid, "admin");
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

	protected void checkMenu(User user, long menuId) throws Exception {
		MenuDAO dao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		if (!dao.isReadEnable(menuId, user.getId())) {
			String message = "User " + user.getUserName() + " doesn't have read permission on menu " + menuId;
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

	protected void checkDownloadEnable(User user, long folderId) throws Exception {
		FolderDAO dao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		if (!dao.isPermissionEnabled(Permission.DOWNLOAD, folderId, user.getId())) {
			String message = "User " + user.getUserName() + " doesn't have download permission on folder " + folderId;
			log.error(message);
			throw new Exception(message);
		}
	}

	protected void checkPublished(User user, Document doc) throws Exception {
		if (!user.isInGroup("admin") && !user.isInGroup("publisher") && !doc.isPublishing())
			throw new FileNotFoundException("Document not published");
	}

	protected void checkArchived(Document doc) throws Exception {
		if (doc.getStatus()==AbstractDocument.DOC_ARCHIVED)
			throw new FileNotFoundException("Document is archived");
	}
	
	public static String convertDateToString(Date date) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
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
		if (StringUtils.isEmpty(date))
			return null;

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
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

	protected ContextProperties getSettings() {
		return (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
	}

	protected boolean isWebserviceEnabled() {
		return "true".equals(getSettings().get("webservice.enabled"));
	}

	public WebServiceContext getContext() {
		return context;
	}

	public void setContext(WebServiceContext context) {
		this.context = context;
	}

	public MessageContext getMessageContext() {
		return messageContext;
	}

	public void setMessageContext(MessageContext messageContext) {
		this.messageContext = messageContext;
	}
}