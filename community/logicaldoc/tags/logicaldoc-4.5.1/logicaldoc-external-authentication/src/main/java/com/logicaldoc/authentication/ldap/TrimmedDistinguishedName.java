package com.logicaldoc.authentication.ldap;

import java.util.List;
import java.util.ListIterator;

import javax.naming.Name;

import org.springframework.ldap.support.DistinguishedName;
import org.springframework.ldap.support.LdapRdn;

/**
 * 
 * @author Sebastian Wenzky
 * @since 4.5
 */
@SuppressWarnings( { "serial", "unchecked" })
public class TrimmedDistinguishedName extends DistinguishedName {

	public TrimmedDistinguishedName(String path) {
		super(path.toLowerCase());
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.ldap.support.DistinguishedName#encode()
	 */
	@Override
	public String encode() {
		// empty path
		List<Name> names = getNames();
		if (names.size() == 0)
			return "";

		StringBuffer buffer = new StringBuffer(256);

		ListIterator i = names.listIterator(names.size());
		while (i.hasPrevious()) {
			LdapRdn rdn = (LdapRdn) i.previous();
			buffer.append(rdn.getLdapEncoded().trim());

			// add comma, except in last iteration
			if (i.hasPrevious())
				buffer.append(",");
		}

		return buffer.toString();
	}
}