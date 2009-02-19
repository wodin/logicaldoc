package com.logicaldoc.authentication.ldap;

import java.util.LinkedList;
import java.util.List;

import javax.naming.AuthenticationException;
import javax.naming.CommunicationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.ldap.LdapTemplate;

import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.authentication.AuthenticationProvider;
import com.logicaldoc.core.security.dao.UserDAO;

/**
 * Abstract component used as a basis for LDAP implementations
 * 
 * @author Sebastian Wenzky
 * @since 4.5
 */
public abstract class LDAPAuthentication implements AuthenticationProvider {
	protected static Log log = LogFactory.getLog(LDAPAuthentication.class);

	private List<String> notValidatedUsers;

	private LDAPUserGroupContext ldapUserGroupContext;

	private UserDAO userDAO;

	private UserGroupMappingImpl userGroupDao;

	private LDAPSynchroniser synchroniser;

	public void setUserDao(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	public void setSynchroniser(LDAPSynchroniser synchroniser) {
		this.synchroniser = synchroniser;
	}

	public void setUserGroupDao(UserGroupMappingImpl userGroupDao) {
		this.userGroupDao = userGroupDao;
	}

	@Override
	public boolean validateOnUser(String user) {
		if (this.notValidatedUsers == null)
			return false;

		return (this.notValidatedUsers.contains(user));
	}

	@Override
	public boolean authenticate(String name, String password) {
		boolean foundOnDirectory = false;

		for (String userBase : this.ldapUserGroupContext.getUserBase()) {
			// This instance is created by Spring on the basis of a prototype
			BasicLDAPContextSource ldapContextSource = obtainNewLdapContextSource();

			// Change account informations to try a login against the LDAP
			// server
			ldapContextSource.setUserName(name);
			ldapContextSource.setPassword(password);
			ldapContextSource.setCurrentDN(userBase);
			LdapTemplate ldapTemplate = new LdapTemplate(ldapContextSource);

			try {
				// Execute a simple search just to check if we are connected
				ldapTemplate.list("");
				foundOnDirectory = true;
				break;
			} catch (Throwable e) {
				if (e.getCause() instanceof AuthenticationException)
					log.warn("User named '" + name + "' not found under '" + userBase + "'", e);
				else if (e.getCause() instanceof CommunicationException) {
					log.error("Directory Server is currently not available");
					return false;
				} else {
					log.error(e.getMessage(), e);
					return false;
				}
			}
		}

		if (foundOnDirectory == false) {
			log.warn("User named '" + name + "' not found in directory");
			return false;
		}

		// all is okay, user already exist
		User existingUser = userDAO.findByUserName(name);
		if (existingUser != null)
			return true;

		LdapUser ldapUser = userGroupDao.getUserByUniqueAttributeIdentiferValue(name);
		// check whether in the meantime between two checks the user account has
		// been deleted
		if (ldapUser == null)
			return false;

		List<LdapUser> oneUserList = new LinkedList<LdapUser>();
		oneUserList.add(ldapUser);

		synchroniser.doImport(oneUserList, userGroupDao.getAllGroups());

		return true;
	}

	public void setNotValidatedUsers(List<String> notValidatedUsers) {
		this.notValidatedUsers = notValidatedUsers;
	}

	public void setLdapUserGroupContext(LDAPUserGroupContext ldapUserGroupContext) {
		this.ldapUserGroupContext = ldapUserGroupContext;
	}

	public abstract BasicLDAPContextSource obtainNewLdapContextSource();

	public abstract void obtainNewLdapTemplate();
}