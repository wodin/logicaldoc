package com.logicaldoc.ldap.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.naming.directory.SearchControls;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.ldap.AttributesMapper;
import org.springframework.ldap.LdapTemplate;

/**
 * A DAO for accessing LDAP informations regarding users and groups
 * 
 * @author Sebastian Wenzky
 * @since 4.5
 */
public class UserGroupDAO {
	protected static Log log = LogFactory.getLog(UserGroupDAO.class);

	private LDAPUserGroupContext ldapUserGroupContext;

	private LdapTemplate ldapTemplate;

	private AttributesMapper userMapper;

	private AttributesMapper groupMapper;

	public void setLdapUserGroupContext(LDAPUserGroupContext ldapUserGroupContext) {
		this.ldapUserGroupContext = ldapUserGroupContext;
	}

	public void setLdapTemplate(LdapTemplate ldapTemplate) {
		this.ldapTemplate = ldapTemplate;
	}

	public void setGroupMapper(AttributesMapper groupMapper) {
		this.groupMapper = groupMapper;
	}

	public void setUserMapper(AttributesMapper userMapper) {
		this.userMapper = userMapper;
	}

	@SuppressWarnings("unchecked")
	public LdapUser getUserByUniqueAttributeIdentiferValue(String identifierValue) {
		SearchControls sc = new SearchControls();
		sc.setSearchScope(SearchControls.SUBTREE_SCOPE);

		ArrayList<String> userBases = this.ldapUserGroupContext.getUserBase();

		for (String groupDN : userBases) {
			List<LdapUser> userList = new ArrayList<LdapUser>();

			try {
				userList = this.ldapTemplate.search(groupDN, "(& (objectClass=" + ldapUserGroupContext.getUserClass()
						+ ") (" + ldapUserGroupContext.getLogonAttribute() + "=" + identifierValue + "))", sc,
						this.userMapper);
			} catch (Throwable t) {
				log.error(t.getMessage(), t);
			}

			if (userList.isEmpty() == false) {
				LdapUser ldapUser = userList.get(0);
				return ldapUser;
			}
		}

		return null;
	}

	/**
	 * Returning all users being found on userBase List
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<LdapGroup> getAllGroups() {
		SearchControls sc = new SearchControls();
		sc.setSearchScope(SearchControls.SUBTREE_SCOPE);

		List<LdapGroup> groups = new LinkedList<LdapGroup>();

		ArrayList<String> groupBases = this.ldapUserGroupContext.getGroupBase();

		for (String groupDN : groupBases) {
			List<LdapGroup> groupList = this.ldapTemplate.search(groupDN, "objectClass="
					+ this.ldapUserGroupContext.getGroupClass(), sc, this.groupMapper);
			groups.addAll(groupList);
		}

		return groups;
	}

	/**
	 * Returning all users being found on groupBase List
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<LdapUser> getAllUsers() {
		SearchControls sc = new SearchControls();
		sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
		List<LdapUser> users = new LinkedList<LdapUser>();

		ArrayList<String> userBases = this.ldapUserGroupContext.getUserBase();

		for (String userDN : userBases) {
			List<LdapUser> userList = this.ldapTemplate.search(userDN, "objectClass="
					+ ldapUserGroupContext.getUserClass(), sc, this.userMapper);
			users.addAll(userList);

		}

		return users;
	}

	public LDAPUserGroupContext getLdapUserGroupContext() {
		return ldapUserGroupContext;
	}
}