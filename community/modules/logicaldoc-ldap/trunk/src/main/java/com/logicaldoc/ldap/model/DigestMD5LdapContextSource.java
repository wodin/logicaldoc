package com.logicaldoc.ldap.model;

import java.util.Hashtable;

import javax.naming.Context;

/**
 * Secure authentication source that supports DIGEST-MD5. 
 * @author Sebastian Wenzky
 * @since 4.5
 */
@SuppressWarnings("unchecked")
public class DigestMD5LdapContextSource extends BasicLDAPContextSource {

	private String _localBase;

	@Override
	public void setBase(String base) {
		super.setBase(base);
		this._localBase = base;
	}

	@Override
	protected void setupAuthenticatedEnvironment(Hashtable env) {
		super.setupAuthenticatedEnvironment(env);
		env.put("java.naming.security.sasl.realm", super.getRealm());
		// standard md5 context
		env.put(Context.SECURITY_AUTHENTICATION, "DIGEST-MD5");
		String[] urls = this.getUrls();
		// redefine urls for secured login
		for (int i = 0; i < urls.length; i++)
			urls[i] = urls[i] + "/" + _localBase;
	}
}