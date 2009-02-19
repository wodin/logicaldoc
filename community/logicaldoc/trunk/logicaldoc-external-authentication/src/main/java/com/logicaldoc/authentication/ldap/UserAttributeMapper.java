package com.logicaldoc.authentication.ldap;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import org.springframework.ldap.AttributesMapper;

import com.logicaldoc.core.security.User;

/**
 * Maps LDAP attributes into User attributes
 * 
 * @author Sebastian Wenzky
 * @since 4.5
 */
public class UserAttributeMapper implements AttributesMapper {

	private LDAPUserGroupContext ldapUserGroupContext;

	public void setLdapUserGroupContext(LDAPUserGroupContext ldapUserGroupContext) {
		this.ldapUserGroupContext = ldapUserGroupContext;
	}

	@SuppressWarnings("unused")
	private void debugPrint(Attributes attributes) {
		NamingEnumeration<? extends Attribute> atts = attributes.getAll();
		while (atts.hasMoreElements()) {
			Attribute attribute = (Attribute) atts.nextElement();
			System.out.println(attribute);
		}
	}

	@Override
	public Object mapFromAttributes(Attributes attributes) throws NamingException {
		LdapUser ldapUser = new LdapUser();
		User user = new User();
		Attribute val = null;

		val = attributes.get(ldapUserGroupContext.getLogonAttribute());
		user.setUserName(val.get().toString());

		val = attributes.get("givenName");
		if (val != null)
			user.setFirstName(val.get().toString());

		val = attributes.get("sn");
		if (val != null)
			user.setName(val.get().toString());

		val = attributes.get("streetAddress");
		if (val != null)
			user.setStreet(val.get().toString());

		val = attributes.get("l");
		if (val != null)
			user.setCity(val.get().toString());

		val = attributes.get("co");
		if (val != null)
			user.setCountry(val.get().toString());

		val = attributes.get("c");
		if (val != null)
			user.setLanguage(val.get().toString());
		else
			user.setLanguage("en");

		val = attributes.get("mail");
		if (val != null)
			user.setEmail(val.get().toString());

		val = attributes.get("telephoneNumber");
		if (val != null)
			user.setTelephone(val.get().toString());

		val = attributes.get("postalCode");
		if (val != null)
			user.setPostalcode(val.get().toString());

		ldapUser.rdn = attributes.get(ldapUserGroupContext.getUserIdentiferAttribute()).get().toString();
		ldapUser.user = user;

		return ldapUser;
	}

}
