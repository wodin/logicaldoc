package com.logicaldoc.core.security.authentication;

/**
 * Implementations of this interface must provide authentication of a user
 * against a defined source.
 * 
 * @author Sebastian Wenzky
 * @since 4.5
 */
public interface AuthenticationComponent {

	/**
	 * Authenticates the user using the given credentials
	 * 
	 * @param username
	 * @param password
	 * @return  True only on successful authentication
	 */
	public boolean authenticate(String username, String password);

	public boolean validateOnUser(String user);
}
