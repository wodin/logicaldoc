package com.logicaldoc.authentication.ldap;

import com.logicaldoc.core.security.User;

/**
 * 
 * @author Sebastian Wenzky
 *
 */
public class LdapUser {
	public User user;
	public TrimmedDistinguishedName dn;
	public String rdn;
}
