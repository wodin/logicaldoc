package com.logicaldoc.authentication.ldap;

import java.util.List;

/**
 * 
 * @author Sebastian Wenzky
 *
 */
public interface UserGroupMapping {
	
	public List<LdapUser> getAllUsers();
	
	public List<LdapGroup> getAllGroups();
}
