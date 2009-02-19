package com.logicaldoc.authentication.ldap;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import org.springframework.ldap.AttributesMapper;

/**
 * Maps LDAP attributes into Group attributes
 * 
 * @author Sebastian Wenzky
 * @since 4.5
 */
@SuppressWarnings("unchecked")
public class GroupAttributeMapper implements AttributesMapper {

	@Override
	public LdapGroup mapFromAttributes(Attributes attributes) throws NamingException {

		LdapGroup ldapGroup = new LdapGroup();

		ldapGroup.name = attributes.get("cn").get().toString();
		Attribute members = attributes.get("member");
		if (members != null) {
			NamingEnumeration listedMembers = members.getAll();
			while (listedMembers.hasMoreElements()) {
				String user = (String) listedMembers.nextElement();
				ldapGroup.users.add(user);
			}
		}

		return ldapGroup;
	}

}
