package com.logicaldoc.authentication.ldap;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.LogFactory;
import org.springframework.ldap.LdapTemplate;

import com.logicaldoc.core.security.Group;
import com.logicaldoc.core.security.SecurityManager;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.GroupDAO;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.core.task.Task;
import com.logicaldoc.util.Context;

/**
 * This class is responsible for the synchronisation of users and groups in the
 * LogicalDOC store from the counterparts in the LDAP directory.
 * 
 * @author Sebastian Wenzky
 * @since 4.5
 */
public class LDAPSynchroniser extends Task {

	public static final String NAME = "LDAPSynchroniser";

	private GroupDAO groupDao;

	private UserDAO userDao;

	private UserGroupDAO userGroupDao;

	private LDAPSynchronisationContext synchronisationContext;

	private LdapTemplate ldapTemplate;

	private long imported = 0;

	private long updated = 0;

	private long errors = 0;

	public LDAPSynchroniser() {
		super(NAME);
		log = LogFactory.getLog(LDAPSynchroniser.class);
	}

	public void setUserDao(UserDAO userDao) {
		this.userDao = userDao;
	}

	public void setGroupDao(GroupDAO groupDao) {
		this.groupDao = groupDao;
	}

	public long getImported() {
		return imported;
	}

	public long getUpdated() {
		return updated;
	}

	public long getErrors() {
		return errors;
	}

	public void setSynchronisationContext(LDAPSynchronisationContext synchronisationContext) {
		this.synchronisationContext = synchronisationContext;
	}

	public LDAPSynchronisationContext getSynchronisationContext() {
		return synchronisationContext;
	}

	public LdapTemplate getLdapTemplate() {
		return ldapTemplate;
	}

	public void setLdapTemplate(LdapTemplate ldapTemplate) {
		this.ldapTemplate = ldapTemplate;
	}

	public void setUserGroupDao(UserGroupDAO userGroupDao) {
		this.userGroupDao = userGroupDao;
	}

	public void doImport(List<LdapUser> users, List<LdapGroup> groups) {
		Map<String, Group> ldocGroups = new HashMap<String, Group>();
		Map<String, User> userMap = new HashMap<String, User>();
		Map<String, LdapGroup> ldapGroupMap = new HashMap<String, LdapGroup>();

		for (LdapUser ldapUser : users)
			userMap.put(ldapUser.dn.toString(), ldapUser.user);

		for (LdapGroup ldapGroup : groups)
			ldapGroupMap.put(ldapGroup.dn.toString(), ldapGroup);

		// we need a pseudoId as we need a preceeding numbering
		// of groups within users (must be unequal to equals method)
		int pseudoId = 1;
		// iterating on every group
		for (LdapGroup ldapGroup : groups) {
			// obtain every user in a group and add this group to the user
			for (String ldapUser : ldapGroup.users) {
				Group group = new Group();
				group.setName(ldapGroup.name);
				User user = (User) userMap.get(ldapUser.toString().toLowerCase());

				if (user == null)
					continue;

				Group _group = ldocGroups.get(ldapGroup.name);

				if (_group == null) {
					_group = new Group();
					_group.setName(ldapGroup.name);
					_group.setId(pseudoId);
					ldocGroups.put(ldapGroup.name, _group);
				}

				user.getGroups().add(_group);
			}

			pseudoId++;
		}
		Collection<Group> _groups = ldocGroups.values();
		createOrUpdateGroups(_groups);

		Collection<User> _users = userMap.values();
		createOrUpdateUsers(_users);
		assignGroupsToUsers(_users);
	}

	private void createOrUpdateGroups(Collection<Group> groups) {
		for (Group group : groups) {
			try {
				Group _group = groupDao.findByName(group.getName());
				if (_group != null) {
					// BeanUtils.copyProperties(group, _group, new String[] {
					// "id" });
					groupDao.initialize(_group);
					group.setId(_group.getId());
					group.setLastModified(group.getLastModified());
					group.setAttributes(_group.getAttributes());
					group.setType(_group.getType());
					if (!groupDao.store(_group))
						throw new Exception("Unable to store group " + group.getName());
					updated++;
				} else {
					if (!groupDao.insert(group, 0))
						throw new Exception("Unable to store group " + group.getName());
					imported++;
				}
			} catch (Throwable e) {
				log.error(e.getMessage(), e);
				errors++;
			} finally {
				next();
			}
		}
	}

	private void createOrUpdateUsers(Collection<User> users) {
		for (User user : users) {
			try {
				User _user = userDao.findByUserName(user.getUserName());
				// if the user exists, no changes will be made
				if (_user != null) {
					// BeanUtils.copyProperties(user, _user, new
					// String[]{"id"});
					userDao.initialize(_user);
					user.setId(_user.getId());
					user.setGroups(_user.getGroups());
					user.setLastModified(_user.getLastModified());
					updated++;
				} else {
					imported++;
				}

				// Do we want to remove all groups associations?
				// Set<Group> grps = user.getGroups();
				// _user.getGroups().removeAll(grps);
				if (!userDao.store(user))
					throw new Exception("Unable to store user " + user.getUserName());
				

			} catch (Throwable e) {
				log.error(e.getMessage(), e);
				errors++;
			} finally {
				next();
			}
		}
	}

	private void assignGroupsToUsers(Collection<User> users) {
		SecurityManager manager = (SecurityManager) Context.getInstance().getBean(SecurityManager.class);
		for (User user : users) {
			manager.assignUserToGroups(user, user.getGroupIds());
		}
	}

	@Override
	public boolean isIndeterminate() {
		return false;
	}

	@Override
	protected void runTask() throws Exception {
		log.info("Start synchronisation from Directory");
		imported = 0;
		updated = 0;
		errors = 0;
		try {
			List<LdapGroup> groups = userGroupDao.getAllGroups();
			List<LdapUser> users = userGroupDao.getAllUsers();

			// First of all compute the task size
			size = groups.size() + users.size();
			log.info("Found a total of " + size + " directory entries");
			doImport(users, groups);
		} finally {
			log.info("Synchronisation finished");
			log.info("Elements imported: " + imported);
			log.info("Elements updated: " + updated);
			log.info("Errors: " + errors);
		}
	}
}