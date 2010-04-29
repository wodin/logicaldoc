package com.logicaldoc.webservice.auth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.handler.MessageContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.transport.http.AbstractHTTPDestination;

import com.logicaldoc.core.document.dao.FolderDAO;
import com.logicaldoc.core.security.Group;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.MenuGroup;
import com.logicaldoc.core.security.SessionManager;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.authentication.AuthenticationChain;
import com.logicaldoc.core.security.dao.GroupDAO;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.webservice.AbstractService;

/**
 * Auth Web Service Implementation
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 5.2
 */
@WebService(endpointInterface = "com.logicaldoc.webservice.auth.AuthService", serviceName = "AuthService")
public class AuthServiceImpl extends AbstractService implements AuthService {

	protected static Log log = LogFactory.getLog(AuthServiceImpl.class);

	@Override
	public String login(String username, String password) throws Exception {
		AuthenticationChain authenticationChain = (AuthenticationChain) Context.getInstance().getBean(
				AuthenticationChain.class);
		MessageContext ctx = context.getMessageContext();
		HttpServletRequest request = (HttpServletRequest) ctx.get(AbstractHTTPDestination.HTTP_REQUEST);

		if (authenticationChain.authenticate(username, password, request.getRemoteAddr()))
			return AuthenticationChain.getSessionId();
		else
			throw new Exception("Unable to create a new session");

	}

	@Override
	public void logout(String sid) {
		SessionManager.getInstance().kill(sid);
	}

	@Override
	public long[] getUsers(String sid) throws Exception {
		validateSession(sid);

		UserDAO userDao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
		long[] userIdsArray = new long[0];
		try {
			List<Long> userIdsList = userDao.findAllIds();
			userIdsArray = new long[userIdsList.size()];
			for (int i = 0; i < userIdsList.size(); i++) {
				userIdsArray[i] = userIdsList.get(i).longValue();
			}
		} catch (Exception e) {
			log.error("Some errors occurred", e);
			throw new Exception("error", e);
		}

		return userIdsArray;
	}

	@Override
	public long[] getGroups(String sid) throws Exception {
		validateSession(sid);

		GroupDAO groupDao = (GroupDAO) Context.getInstance().getBean(GroupDAO.class);
		long[] groupIdsArray = new long[0];
		try {
			List<Long> groupIdsList = groupDao.findAllIds();
			groupIdsArray = new long[groupIdsList.size()];
			for (int i = 0; i < groupIdsList.size(); i++) {
				groupIdsArray[i] = groupIdsList.get(i).longValue();
			}
		} catch (Exception e) {
			log.error("Some errors occurred", e);
			throw new Exception("error", e);
		}

		return groupIdsArray;
	}

	@Override
	public void grantUser(String sid, long folderId, long userId, int permissions, boolean recursive) throws Exception {
		UserDAO userDao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
		User user = userDao.findById(userId);
		grantGroup(sid, folderId, user.getUserGroup().getId(), permissions, recursive);
	}

	@Override
	public void grantGroup(String sid, long folderId, long groupId, int permissions, boolean recursive)
			throws Exception {
		validateSession(sid);

		FolderDAO folderDao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		try {
			Menu folder = folderDao.findById(folderId);
			folderDao.initialize(folder);
			MenuGroup mg = new MenuGroup();
			mg.setGroupId(groupId);
			mg.setPermissions(permissions);
			folder.addMenuGroup(mg);
			folderDao.store(folder);

			if (recursive) {
				// recursively apply permissions to all submenus
				Collection<Menu> submenus = folderDao.findByParentId(folderId);
				for (Menu submenu : submenus) {
					folderDao.initialize(submenu);
					submenu.addMenuGroup(mg);
					folderDao.store(submenu);
				}
			}
		} catch (Exception e) {
			log.error("Some errors occurred", e);
			throw new Exception("error", e);
		}
	}

	@Override
	public Right[] getGrantedUsers(String sid, long folderId) throws Exception {
		return getGranted(sid, folderId, true);
	}

	private Right[] getGranted(String sid, long folderId, boolean users) throws Exception {
		validateSession(sid);

		List<Right> rightsList = new ArrayList<Right>();
		FolderDAO folderDao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		GroupDAO groupDao = (GroupDAO) Context.getInstance().getBean(GroupDAO.class);
		try {
			Menu folder = folderDao.findById(folderId);
			folderDao.initialize(folder);
			for (MenuGroup mg : folder.getMenuGroups()) {
				Group group = groupDao.findById(mg.getGroupId());
				if (group.getName().startsWith("_user_") && users) {
					rightsList.add(new Right(Long.parseLong(group.getName().substring(
							group.getName().lastIndexOf('_') + 1)), mg.getPermissions()));
				} else if (!group.getName().startsWith("_user_") && !users)
					rightsList.add(new Right(group.getId(), mg.getPermissions()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Some errors occurred", e);
			throw new Exception("error", e);
		}

		return (Right[]) rightsList.toArray(new Right[rightsList.size()]);
	}

	@Override
	public Right[] getGrantedGroups(String sid, long folderId) throws Exception {
		return getGranted(sid, folderId, false);
	}
}