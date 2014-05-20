package com.logicaldoc.webservice.security;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.core.security.Group;
import com.logicaldoc.core.security.SecurityManager;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.UserHistory;
import com.logicaldoc.core.security.dao.GroupDAO;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.crypt.CryptUtil;
import com.logicaldoc.webservice.AbstractService;

/**
 * Security Web Service Implementation
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.1
 */
public class SecurityServiceImpl extends AbstractService implements SecurityService {
	protected static Logger log = LoggerFactory.getLogger(SecurityServiceImpl.class);

	@Override
	public WSUser[] listUsers(String sid) throws Exception {
		checkAdministrator(sid);
		User user = validateSession(sid);

		try {
			List<WSUser> users = new ArrayList<WSUser>();
			UserDAO dao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
			for (User usr : dao.findAll(user.getTenantId()))
				if (usr.getType() == User.TYPE_DEFAULT)
					users.add(WSUser.fromUser(usr));
			return users.toArray(new WSUser[0]);
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new Exception(t.getMessage());
		}
	}

	@Override
	public WSGroup[] listGroups(String sid) throws Exception {
		checkAdministrator(sid);
		User user=validateSession(sid);
		
		try {
			List<WSGroup> groups = new ArrayList<WSGroup>();
			GroupDAO dao = (GroupDAO) Context.getInstance().getBean(GroupDAO.class);
			for (Group grp : dao.findAll(user.getTenantId())) {
				if (grp.getType() == Group.TYPE_DEFAULT) {
					dao.initialize(grp);
					groups.add(WSGroup.fromGroup(grp));
				}
			}
			return groups.toArray(new WSGroup[0]);
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new Exception(t.getMessage());
		}
	}

	@Override
	public long storeUser(String sid, WSUser user) throws Exception {
		checkAdministrator(sid);
		User sessionUser=validateSession(sid);
		
		try {
			UserDAO dao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
			User usr = user.toUser();
			usr.setTenantId(sessionUser.getTenantId());
			
			if (user.getId() != 0) {
				usr = dao.findById(user.getId());
				if (usr.getType() != User.TYPE_DEFAULT) {
					throw new Exception("You cannot edit user with id " + usr.getId() + " because it is a system user");
				}
				usr.setCity(user.getCity());
				usr.setCountry(user.getCountry());
				usr.setEmail(user.getEmail());
				usr.setFirstName(user.getFirstName());
				usr.setName(user.getName());
				usr.setLanguage(user.getLanguage());
				usr.setPostalcode(user.getPostalcode());
				usr.setState(user.getState());
				usr.setStreet(user.getStreet());
				usr.setTelephone(user.getTelephone());
				usr.setTelephone2(user.getTelephone2());
				usr.setUserName(user.getUserName());
				usr.setEnabled(user.getEnabled());
				usr.setPasswordExpires(user.getPasswordExpires());
				usr.setQuota(user.getQuota());
				usr.setType(user.getType());
				usr.setSource(user.getSource());
			}

			if (StringUtils.isEmpty(usr.getUserName()))
				throw new Exception("Missing mandatory value 'UserName'");
			else if (StringUtils.isEmpty(usr.getEmail()))
				throw new Exception("Missing mandatory value 'Email'");
			else if (StringUtils.isEmpty(usr.getName()))
				throw new Exception("Missing mandatory value 'Name'");
			else if (StringUtils.isEmpty(usr.getFirstName()))
				throw new Exception("Missing mandatory value 'FirstName'");

			if (!dao.store(usr))
				throw new Exception("Unable to store the user");

			if (user.getGroupIds() != null && user.getGroupIds().length > 0) {

				SecurityManager manager = (SecurityManager) Context.getInstance().getBean(SecurityManager.class);
				manager.removeUserFromAllGroups(usr);
				manager.assignUserToGroups(usr, user.getGroupIds());
			}

			return usr.getId();
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new Exception(t.getMessage());
		}
	}

	@Override
	public long storeGroup(String sid, WSGroup group) throws Exception {
		checkAdministrator(sid);

		try {
			GroupDAO dao = (GroupDAO) Context.getInstance().getBean(GroupDAO.class);
			Group grp = group.toGroup();
			if (group.getId() != 0) {
				grp = dao.findById(group.getId());
				if (grp.getType() != Group.TYPE_DEFAULT) {
					throw new Exception("You cannot edit group with id " + grp.getId()
							+ " because it is a system group");
				}
				grp.setName(group.getName());
				grp.setDescription(group.getDescription());
				grp.setType(group.getType());
			}

			if (StringUtils.isEmpty(grp.getName()))
				throw new Exception("Missing mandatory value 'Name'");

			if (group.getUserIds() != null && group.getUserIds().length > 0) {
				UserDAO userDao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
				SecurityManager manager = (SecurityManager) Context.getInstance().getBean(SecurityManager.class);
				manager.removeAllUsersFromGroup(grp);
				ArrayList<User> users = new ArrayList<User>();
				for (long userId : group.getUserIds()) {
					User user = userDao.findById(userId);
					users.add(user);
				}
				try {
					manager.assignUsersToGroup(users, grp);
				} catch (Exception e) {
					throw new Exception("Unable to assign users to group");
				}
			}

			if (dao.store(grp)) {
				if (group.getInheritGroupId() != null && group.getInheritGroupId().longValue() > 0)
					dao.inheritACLs(group.getId(), group.getInheritGroupId().longValue());
				return grp.getId();
			} else
				throw new Exception("Unable to store the group");
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new Exception(t.getMessage());
		}
	}

	@Override
	public void deleteUser(String sid, long userId) throws Exception {
		checkAdministrator(sid);

		if (userId == 1)
			throw new Exception("You cannot delete the admin user");

		try {
			UserDAO dao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
			User usr = dao.findById(userId);
			if (usr.getType() != User.TYPE_DEFAULT) {
				throw new Exception("You cannot delete user with id " + usr.getId() + " because it is a system user");
			}
			dao.delete(userId);
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new Exception("Unable to delete the user with id " + userId);
		}
	}

	@Override
	public void deleteGroup(String sid, long groupId) throws Exception {
		checkAdministrator(sid);

		if (groupId == 1)
			throw new Exception("You cannot delete the admin group");

		try {
			GroupDAO dao = (GroupDAO) Context.getInstance().getBean(GroupDAO.class);
			Group grp = dao.findById(groupId);
			if (grp.getType() != Group.TYPE_DEFAULT) {
				throw new Exception("You cannot delete group with id " + grp.getId() + " because it is a system group");
			}
			dao.delete(groupId);
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new Exception("Unable to delete the group with id " + groupId);
		}
	}

	@Override
	public int changePassword(String sid, long userId, String oldPassword, String newPassword) throws Exception {
		checkAdministrator(sid);

		try {
			UserDAO userDao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
			User user = userDao.findById(userId);
			if (user == null)
				throw new Exception("User " + userId + " not found");

			if (oldPassword != null && !CryptUtil.cryptString(oldPassword).equals(user.getPassword())) {
				return 1;
			}

			UserHistory history = null;
			// The password was changed
			user.setDecodedPassword(newPassword);
			user.setPasswordChanged(new Date());
			// Add a user history entry
			history = new UserHistory();
			history.setUser(user);
			history.setEvent(UserHistory.EVENT_USER_PASSWORDCHANGED);
			history.setComment("");
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

	@Override
	public WSUser getUser(String sid, long userId) throws Exception {
		checkAdministrator(sid);
		try {
			UserDAO userDao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
			User user = userDao.findById(userId);
			if (user == null)
				return null;

			userDao.initialize(user);
			return WSUser.fromUser(user);
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}

	@Override
	public WSUser getUserByUsername(String sid, String username) throws Exception {
		checkAdministrator(sid);
		try {
			UserDAO userDao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
			User user = userDao.findByUserName(username);
			
			if (user == null)
				return null;

			userDao.initialize(user);
			return WSUser.fromUser(user);
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}

	@Override
	public WSGroup getGroup(String sid, long groupId) throws Exception {
		checkAdministrator(sid);

		GroupDAO groupDao = (GroupDAO) Context.getInstance().getBean(GroupDAO.class);
		Group group = groupDao.findById(groupId);
		if (group == null)
			return null;

		groupDao.initialize(group);
		return WSGroup.fromGroup(group);
	}
}