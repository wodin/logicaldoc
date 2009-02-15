package com.logicaldoc.authentication.ldap;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import org.springframework.ldap.AttributesMapper;

/**
 * 
 * @author Sebastian Wenzky
 *
 */
@SuppressWarnings("unchecked")
public class GroupAttributeMapper implements AttributesMapper{
	
	
	@Override
	public LdapGroup mapFromAttributes(Attributes attributes)
			throws NamingException {
		
		LdapGroup ldapGroup = new LdapGroup();
	
		ldapGroup.name = attributes.get("cn").get().toString();
		Attribute members = attributes.get("member");
		//System.out.println(ldapGroup.dnName);
		if(members != null){
			NamingEnumeration listedMembers = members.getAll();
			while (listedMembers.hasMoreElements()) {
				String user = (String)listedMembers.nextElement();
				ldapGroup.users.add(user);
			}
		}
		
		return ldapGroup;
	}

}
