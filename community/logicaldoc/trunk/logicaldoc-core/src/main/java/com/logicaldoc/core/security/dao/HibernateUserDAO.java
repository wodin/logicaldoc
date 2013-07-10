package com.logicaldoc.core.security.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.LoggerFactory;

import com.logicaldoc.core.HibernatePersistentObjectDAO;
import com.logicaldoc.core.generic.Generic;
import com.logicaldoc.core.generic.GenericDAO;
import com.logicaldoc.core.security.Group;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.UserHistory;
import com.logicaldoc.core.security.UserListener;
import com.logicaldoc.core.security.UserListenerManager;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.io.CryptUtil;

/**
 * Hibernate implementation of <code>UserDAO</code>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.0
 */
@SuppressWarnings("unchecked")
public class HibernateUserDAO extends HibernatePersistentObjectDAO<User> implements UserDAO {

	private UserDocDAO userDocDAO;

	private GenericDAO genericDAO;

	private UserHistoryDAO userHistoryDAO;

	private UserListenerManager userListenerManager;

	// Password time to live
	private int passwordTtl = 90;

	private HibernateUserDAO() {
		super(User.class);
		super.log = LoggerFactory.getLogger(HibernateUserDAO.class);
	}

	/**
	 * @see com.logicaldoc.core.security.dao.UserDAO#delete(long)
	 */
	public boolean delete(long userId) {
		return delete(userId, null);
	}

	/**
	 * @see com.logicaldoc.core.security.dao.UserDAO#findByName(java.lang.String)
	 */
	public List<User> findByName(String name) {
		return findByWhere("lower(_entity.name) like ?", new Object[] { name.toLowerCase() }, null, null);
	}

	/**
	 * @see com.logicaldoc.core.security.dao.UserDAO#findByUserName(java.lang.String)
	 */
	public User findByUserName(String username) {
		User user = null;
		List<User> coll = findByWhere("_entity.userName = ?", new Object[] { username }, null, null);
		if (coll.size() > 0) {
			user = coll.iterator().next();
		}
		return user;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.UserDAO#findByUserName(java.lang.String)
	 */
	public List<User> findByLikeUserName(String username) {
		return findByWhere("_entity.userName like ?", new Object[] { username }, null, null);
	}

	/**
	 * @see com.logicaldoc.core.security.dao.UserDAO#findByUserNameAndName(java.lang.String,
	 *      java.lang.String)
	 */
	public List<User> findByUserNameAndName(String username, String name) {
		return findByWhere("lower(_entity.name) like ? and _entity.userName like ?", new Object[] { name.toLowerCase(),
				username }, null, null);
	}

	/**
	 * @see com.logicaldoc.core.security.dao.UserDAO#store(com.logicaldoc.core.security.User)
	 */
	public boolean store(User user) {
		return store(user, null);
	}

	@Override
	public boolean store(User user, UserHistory transaction) {
		boolean result = true;
		boolean newUser = user.getId() == 0;

		try {
			Map<String, Object> dictionary = new HashMap<String, Object>();

			log.debug("Invoke listeners before store");
			for (UserListener listener : userListenerManager.getListeners()) {
				listener.beforeStore(user, transaction, dictionary);
			}

			saveOrUpdate(user);

			GroupDAO groupDAO = (GroupDAO) Context.getInstance().getBean(GroupDAO.class);

			String userGroupName = "_user_" + Long.toString(user.getId());
			Group grp = groupDAO.findByName(userGroupName);
			if (grp == null) {
				grp = new Group();
				grp.setName(userGroupName);
				grp.setType(Group.TYPE_USER);
				Set<User> users = new HashSet<User>();
				users.add(user);
				grp.setUsers(users);
				groupDAO.store(grp);

				saveUserHistory(user, transaction);
			}

			if (newUser) {
				// Save default dashlets
				Generic dash = new Generic("usersetting", "dashlet-1", user.getId());
				dash.setInteger1(1L);
				dash.setInteger2(0L);
				dash.setInteger3(0L);
				dash.setString1("0");
				genericDAO.store(dash);
				dash = new Generic("usersetting", "dashlet-3", user.getId());
				dash.setInteger1(3L);
				dash.setInteger2(0L);
				dash.setInteger3(1L);
				dash.setString1("0");
				genericDAO.store(dash);
				dash = new Generic("usersetting", "dashlet-6", user.getId());
				dash.setInteger1(6L);
				dash.setInteger2(1L);
				dash.setInteger3(0L);
				dash.setString1("0");
				genericDAO.store(dash);
			}

			log.debug("Invoke listeners after store");
			for (UserListener listener : userListenerManager.getListeners()) {
				listener.afterStore(user, transaction, dictionary);
			}
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
			result = false;
		}

		return result;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.UserDAO#validateUser(java.lang.String,
	 *      java.lang.String)
	 */
	public boolean validateUser(String username, String password) {
		boolean result = true;
		try {
			User user = findByUserName(username);
			// Check the password match
			if ((user == null) || !user.getPassword().equals(CryptUtil.cryptString(password))
					|| user.getType() != User.TYPE_DEFAULT) {
				result = false;
			}

			// Check if the user is enabled
			if (user != null && user.getEnabled() == 0)
				return false;

			if (isPasswordExpired(username))
				return false;
		} catch (Throwable e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
			result = false;
		}
		return result;
	}

	public int getPasswordTtl() {
		return passwordTtl;
	}

	public void setPasswordTtl(int passwordTtl) {
		this.passwordTtl = passwordTtl;
	}

	@Override
	public boolean isPasswordExpired(String username) {
		try {
			User user = findByUserName(username);
			if (user == null)
				return false;

			if (user.getPasswordExpired() == 1)
				return true;

			if (getPasswordTtl() <= 0)
				return false;

			// Check if the password is expired
			if (user.getPasswordExpires() == 1) {
				Date lastChange = user.getPasswordChanged();
				if (lastChange == null)
					return false;
				Calendar calendar = new GregorianCalendar();
				calendar.setTime(lastChange);
				calendar.set(Calendar.MILLISECOND, 0);
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.HOUR, 0);
				lastChange = calendar.getTime();

				calendar.setTime(new Date());
				calendar.set(Calendar.MILLISECOND, 0);
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.HOUR, 0);

				calendar.add(Calendar.DAY_OF_MONTH, -getPasswordTtl());
				Date date = calendar.getTime();

				return (lastChange.before(date));
			}
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
			return true;
		}
		return false;
	}

	@Override
	public int count() {
		String query = "select count(*) from ld_user where ld_type=0 and not(ld_deleted=1)";
		return queryForInt(query);
	}

	@Override
	public boolean delete(long userId, UserHistory transaction) {
		boolean result = true;

		try {
			User user = (User) findById(userId);
			Group userGroup = user.getUserGroup();
			if (user != null) {
				userDocDAO.deleteByUserId(userId);
				user.setDeleted(1);
				user.setUserName(user.getUserName() + "." + user.getId());
				saveOrUpdate(user);
			}

			// Delete the user's group
			if (userGroup != null) {
				GroupDAO groupDAO = (GroupDAO) Context.getInstance().getBean(GroupDAO.class);
				groupDAO.delete(userGroup.getId());
			}

			saveUserHistory(user, transaction);
		} catch (Throwable e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
			result = false;
		}

		return result;
	}

	private void saveUserHistory(User user, UserHistory transaction) {
		if (transaction == null || !userHistoryDAO.isEnabled())
			return;

		transaction.setUser(user);
		transaction.setNotified(0);

		userHistoryDAO.store(transaction);
	}

	public UserHistoryDAO getUserHistoryDAO() {
		return userHistoryDAO;
	}

	public void setUserHistoryDAO(UserHistoryDAO userHistoryDAO) {
		this.userHistoryDAO = userHistoryDAO;
	}

	public UserDocDAO getUserDocDAO() {
		return userDocDAO;
	}

	public void setUserDocDAO(UserDocDAO userDocDAO) {
		this.userDocDAO = userDocDAO;
	}

	public void setUserListenerManager(UserListenerManager userListenerManager) {
		this.userListenerManager = userListenerManager;
	}

	public UserListenerManager getUserListenerManager() {
		return userListenerManager;
	}

	@Override
	public void initialize(User user) {
		if (user == null)
			return;

		refresh(user);

		for (Group group : user.getGroups()) {
			group.getName();
		}
	}

	@Override
	public Map<String, Generic> findUserSettings(long userId, String namePrefix) {
		List<Generic> generics = genericDAO.findByTypeAndSubtype("usersetting", namePrefix + "%", userId);
		Map<String, Generic> map = new HashMap<String, Generic>();
		for (Generic generic : generics) {
			map.put(generic.getSubtype(), generic);
		}
		return map;
	}

	public void setGenericDAO(GenericDAO genericDAO) {
		this.genericDAO = genericDAO;
	}
}