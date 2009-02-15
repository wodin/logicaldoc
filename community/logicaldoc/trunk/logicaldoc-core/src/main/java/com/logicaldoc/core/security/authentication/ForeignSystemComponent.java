package com.logicaldoc.core.security.authentication;

/**
 * 
 * @author Sebastian Wenzky
 * @since 4.5
 */
public interface ForeignSystemComponent extends AuthenticationComponent{
	
	public int getOrderId();
	
	public boolean validateOnUser(String user);
	
}
