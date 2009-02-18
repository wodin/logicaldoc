package com.logicaldoc.authentication.ldap;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.naming.directory.SearchControls;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.ldap.AttributesMapper;
import org.springframework.ldap.LdapTemplate;

/**
 * 
 * @author Sebastian Wenzky
 * 
 */
@SuppressWarnings("unchecked")
public class UserGroupMappingImpl implements UserGroupMapping {
	protected static Log log = LogFactory.getLog(UserGroupMappingImpl.class);

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
				
				System.out.println("*** userList="+userList);
			} catch (Throwable t) {
				log.error(t.getMessage(), t);
			}

			if (userList.isEmpty() == false) {
				LdapUser ldapUser = userList.get(0);
				ldapUser.dn = new TrimmedDistinguishedName(ldapUserGroupContext.getUserIdentiferAttribute() + "="
						+ ldapUser.rdn + "," + groupDN);
				return ldapUser;
			}
		}

		return null;
	}

	@Override
	public List<LdapGroup> getAllGroups() {
		SearchControls sc = new SearchControls();
		sc.setSearchScope(SearchControls.SUBTREE_SCOPE);

		List<LdapGroup> groups = new LinkedList<LdapGroup>();

		ArrayList<String> groupBases = this.ldapUserGroupContext.getGroupBase();

		for (String groupDN : groupBases) {

			List<LdapGroup> groupList = this.ldapTemplate.search(groupDN, "objectClass="
					+ this.ldapUserGroupContext.getGroupClass(), sc, this.groupMapper);

			for (LdapGroup _ldapGroup : groupList) {
				_ldapGroup.dn = new TrimmedDistinguishedName(this.ldapUserGroupContext.getGroupIdentiferAttribute()
						+ "=" + _ldapGroup.name + "," + groupDN);
				System.out.println("" + _ldapGroup.dn);
			}

			groups.addAll(groupList);

		}

		return groups;
	}

	@Override
	public List<LdapUser> getAllUsers() {
		SearchControls sc = new SearchControls();
		sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
		List<LdapUser> users = new LinkedList<LdapUser>();

		ArrayList<String> groupBases = this.ldapUserGroupContext.getUserBase();

		for (String groupDN : groupBases) {
			System.out.println("DN:" + groupDN);
			List<LdapUser> groupList = this.ldapTemplate.search(groupDN, "objectClass="
					+ ldapUserGroupContext.getUserClass(), sc, this.userMapper);

			// postprocessing user to retrieve users dn
			for (LdapUser _ldapUser : groupList)
				_ldapUser.dn = new TrimmedDistinguishedName(ldapUserGroupContext.getUserIdentiferAttribute() + "="
						+ _ldapUser.rdn + "," + groupDN);

			users.addAll(groupList);

		}

		return users;
	}
}