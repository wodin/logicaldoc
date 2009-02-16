package com.logicaldoc.core.security.authentication;

/**
 * This is the extension of an authentication component able to authenticate
 * users against external sources. Implementation of this interface can provide
 * access to external directories like LDAP or ActiveDirectory.
 * 
 * @author Sebastian Wenzky
 * @since 4.5
 */
public interface ForeignSystemComponent extends AuthenticationComponent {
	public int getOrderId();

	public boolean validateOnUser(String user);
}