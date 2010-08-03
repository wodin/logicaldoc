package com.logicaldoc.ldap.model;

import java.util.ArrayList;

/**
 * Mapping-Class of an LDAP-Group. Member users stores 
 * the DN of all users been assigned to the current user
 * 
 * @author Sebastian Wenzky
 * @since 4.5
 */
public class LdapGroup {
	public String name;
	public String dn;
	public ArrayList<String> users = new ArrayList<String>();
}
