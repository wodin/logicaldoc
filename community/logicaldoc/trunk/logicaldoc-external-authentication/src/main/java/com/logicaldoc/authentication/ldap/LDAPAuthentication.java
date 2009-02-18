package com.logicaldoc.authentication.ldap;

import java.util.LinkedList;
import java.util.List;

import javax.naming.AuthenticationException;
import javax.naming.CommunicationException;

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

	private List<String> notValidatedUsers;

	private LDAPUserGroupContext ldapUserGroupContext;

	private UserDAO userDAO;

	private UserGroupMappingImpl userGroupDao;

	private ExpImpSynchronisationJob synchronisationJob;

	public void setUserDao(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	public void setSynchronisationJob(ExpImpSynchronisationJob synchronisationJob) {
		this.synchronisationJob = synchronisationJob;
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
			BasicLDAPContextSource ldapContextSource = obtainNewLdapContextSource();

			ldapContextSource.setUserName(name);
			ldapContextSource.setCurrentDN(userBase);
			ldapContextSource.setPassword(password);
			LdapTemplate ldapTemplate = new LdapTemplate(ldapContextSource);
			try {
				// TODO: MUST BE IMPROVED!!!
				ldapTemplate.list("");
				foundOnDirectory = true;
				break;
			} catch (Exception e) {
				if (e.getCause() instanceof AuthenticationException)
					System.out.println("User named '" + name + "' not found in directory");
				else if (e.getCause() instanceof CommunicationException) {
					System.out.println("Directory Server is currently not available");
					return false;
				} else {
					System.out.println(e);
					return false;
				}
			}
		}

		if (foundOnDirectory == false)
			return false;

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

		synchronisationJob.doImport(oneUserList, userGroupDao.getAllGroups());

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
