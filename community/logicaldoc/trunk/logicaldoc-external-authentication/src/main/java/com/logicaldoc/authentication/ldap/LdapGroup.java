package com.logicaldoc.authentication.ldap;

import java.util.ArrayList;

/**
 * 
 * @author Sebastian Wenzky
 *
 */
public class LdapGroup {
	public String name;
	public TrimmedDistinguishedName dn;
	public ArrayList<String> users = new ArrayList<String>();
}
