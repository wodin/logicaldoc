package com.logicaldoc.authentication.ldap;

import java.util.List;

/**
 * 
 * @author Sebastian Wenzky
 * @since 4.5 
 */
public interface UserGroupMapping {
	public List<LdapUser> getAllUsers();

	public List<LdapGroup> getAllGroups();
}