package com.logicaldoc.ldap.model;

import com.logicaldoc.core.security.User;

/**
 * Mapping Class for an user. 
 * @author Sebastian Wenzky
 * @since 4.5
 */
public class LdapUser {
	public User user;
	public String dn;
	public String rdn;
}
