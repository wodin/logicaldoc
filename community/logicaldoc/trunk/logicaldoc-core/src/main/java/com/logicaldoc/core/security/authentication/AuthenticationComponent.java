package com.logicaldoc.core.security.authentication;

/**
 * 
 * @author Sebastian Wenzky
 * @since 4.5
 */
public interface AuthenticationComponent {
	
	public boolean authenticate(String username, String password);
	
}
