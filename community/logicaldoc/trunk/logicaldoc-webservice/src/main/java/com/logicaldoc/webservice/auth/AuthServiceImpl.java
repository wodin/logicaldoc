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

import com.logicaldoc.core.security.Folder;
import com.logicaldoc.core.security.FolderGroup;
import com.logicaldoc.core.security.Group;
import com.logicaldoc.core.security.SessionManager;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.authentication.AuthenticationChain;
import com.logicaldoc.core.security.dao.FolderDAO;
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
			List<User> usersList = userDao.findByWhere("_entity.type = " + Integer.toString(User.TYPE_DEFAULT), null,
					null);
			userIdsArray = new long[usersList.size()];
			for (int i = 0; i < usersList.size(); i++) {
				userIdsArray[i] = usersList.get(i).getId();
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
		try {
			User user = userDao.findById(userId);
			grantGroup(sid, folderId, user.getUserGroup().getId(), permissions, recursive);
		} catch (Exception e) {
			throw new Exception("error", e);
		}
	}

	@Override
	public void grantGroup(String sid, long folderId, long groupId, int permissions, boolean recursive)
			throws Exception {
		validateSession(sid);

		FolderDAO folderDao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		try {
			Folder folder = folderDao.findById(folderId);
			folderDao.initialize(folder);
			FolderGroup mg = new FolderGroup();
			mg.setGroupId(groupId);
			mg.setPermissions(permissions);
			folder.addFolderGroup(mg);
			folderDao.store(folder);

			if (recursive) {
				// recursively apply permissions to all subfolders
				Collection<Folder> subfolders = folderDao.findByParentId(folderId);
				for (Folder subfolder : subfolders) {
					folderDao.initialize(subfolder);
					subfolder.addFolderGroup(mg);
					folderDao.store(subfolder);
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
			Folder folder = folderDao.findById(folderId);
			if (folder.getSecurityRef() != null)
				folder = folderDao.findById(folder.getSecurityRef());
			folderDao.initialize(folder);
			for (FolderGroup mg : folder.getFolderGroups()) {
				Group group = groupDao.findById(mg.getGroupId());
				if (group.getName().startsWith("_user_") && users) {
					rightsList.add(new Right(Long.parseLong(group.getName().substring(
							group.getName().lastIndexOf('_') + 1)), mg.getPermissions()));
				} else if (!group.getName().startsWith("_user_") && !users)
					rightsList.add(new Right(group.getId(), mg.getPermissions()));
			}
		} catch (Exception e) {
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