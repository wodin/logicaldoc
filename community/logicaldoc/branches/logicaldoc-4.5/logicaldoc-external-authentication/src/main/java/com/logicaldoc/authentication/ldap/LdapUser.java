package com.logicaldoc.authentication.ldap;

import com.logicaldoc.core.security.User;

/**
 * Mapping Class for an user. 
 * @author Sebastian Wenzky
 * @since 4.5
 */
public class LdapUser {
	public User user;
	public TrimmedDistinguishedName dn;
	public String rdn;
}
