package com.logicaldoc.webservice;

import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

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
		UserDAO userDao = (UserDAO) Context.get().getBean(UserDAO.class);
		if (!validateSession) {
			User user = new User();
			user.setId(1L);
			user.setTenantId(1L);
			user.setName("admin");
			Set<Group> groups = new HashSet<Group>();
			GroupDAO grpDao = (GroupDAO) Context.get().getBean(GroupDAO.class);
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
		FolderDAO dao = (FolderDAO) Context.get().getBean(FolderDAO.class);
		if (!dao.isPermissionEnabled(permission, folderId, user.getId())) {
			String message = "User " + user.getUserName() + " doesn't have permission " + permission.getName()
					+ " on folder " + folderId;
			log.error(message);
			throw new Exception(message);
		}
	}

	protected void checkMenu(User user, long menuId) throws Exception {
		MenuDAO dao = (MenuDAO) Context.get().getBean(MenuDAO.class);
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
		FolderDAO dao = (FolderDAO) Context.get().getBean(FolderDAO.class);
		if (!dao.isReadEnabled(folderId, user.getId())) {
			String message = "User " + user.getUserName() + " doesn't have read permission on folder " + folderId;
			log.error(message);
			throw new Exception(message);
		}
	}

	protected void checkDownloadEnable(User user, long folderId) throws Exception {
		FolderDAO dao = (FolderDAO) Context.get().getBean(FolderDAO.class);
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
		if (doc.getStatus() == AbstractDocument.DOC_ARCHIVED)
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

	protected boolean isWebserviceEnabled() {
		return "true".equals(Context.get().getProperties().get("webservice.enabled"));
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

	@javax.ws.rs.core.Context
	public void setMessageContext(MessageContext messageContext) {
		// https://docs.oracle.com/cd/E13222_01/wls/docs92/webserv/annotations.html
		// https://jersey.java.net/documentation/latest/jaxrs-resources.html#d0e2790
		// https://jersey.java.net/apidocs-javax.jax-rs/2.0.1/javax/ws/rs/core/Context.html
		this.messageContext = messageContext;
	}

	/**
	 * Gets the current Session ID following this logic:
	 * <ol>
	 * <li>Request parameter sid</li>
	 * <li>Request attribute sid</li>
	 * <li>Session attribute sid</li>
	 * <li>Request cookie ldoc-sid</li>
	 * <li>SecurityContextHolder</li>
	 * 
	 * @return The current Session ID
	 */
	protected String getCurrentSessionId() {
		HttpServletRequest request = null;
		if (context != null && context.getMessageContext() != null)
			request = (HttpServletRequest) context.getMessageContext().get(AbstractHTTPDestination.HTTP_REQUEST);
		else if (messageContext != null)
			request = (HttpServletRequest) messageContext.get(AbstractHTTPDestination.HTTP_REQUEST);
		if (request == null)
			return null;

		String sid = null;
		if (request.getParameter("sid") != null)
			sid = request.getParameter("sid");
		else if (request.getAttribute("sid") != null)
			sid = (String) request.getAttribute("sid");
		else if (request.getParameter("sid") != null)
			sid = request.getParameter("sid");
		else if (request.getSession(false) != null)
			sid = (String) request.getSession(false).getAttribute("sid");

		if (sid == null) {
			Cookie[] cookies = request.getCookies();
			if (cookies != null)
				for (Cookie cookie : cookies) {
					if ("ldoc-sid".equals(cookie.getName())) {
						sid = cookie.getValue();
						break;
					}
				}
		}

		if (sid == null) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (auth != null)
				sid = auth.toString();
		}

		return sid;
	}

	/**
	 * Same as getCurrentSessionId but throws an Exception in case of bad
	 * session
	 * 
	 * @return The session ID (if valid)
	 * @throws Exception
	 */
	protected String validateSession() throws Exception {
		String sid = getCurrentSessionId();
		if (sid == null)
			throw new Exception("Invalid session");
		return sid;
	}
}