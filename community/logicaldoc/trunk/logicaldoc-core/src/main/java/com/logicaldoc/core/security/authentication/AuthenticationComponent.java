package com.logicaldoc.core.security.authentication;

/**
 * Implementations of this interface must provide authentication of a user
 * against a defined source.
 * 
 * @author Sebastian Wenzky
 * @since 4.5
 */
public interface AuthenticationComponent {

	public boolean authenticate(String username, String password);

}
