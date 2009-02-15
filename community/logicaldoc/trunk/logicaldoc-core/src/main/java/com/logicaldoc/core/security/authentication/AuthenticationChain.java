package com.logicaldoc.core.security.authentication;

import java.util.List;

/**
 * 
 * 
 * @author Sebastian Wenzky
 * @since 4.5
 */
public class AuthenticationChain implements AuthenticationComponent{
	
	private List<ForeignSystemComponent> authenticationComponents;

	public void setAuthenticationComponents(
			List<ForeignSystemComponent> authenticationComponents) {
		this.authenticationComponents = authenticationComponents;
	}

	@Override
	public boolean authenticate(String username, String password) {
		
		for(ForeignSystemComponent cmp : authenticationComponents){
			
			//validate on user will be true, if an specific component manages this 
			//user explicitcally (e.g. admin is basicAuth)
			if(cmp.validateOnUser(username) == true){
				return cmp.authenticate(username, password);
			}	
			
			boolean loggedIn = cmp.authenticate(username, password);
			
			if(loggedIn == true)
				return true;
		}
		
		return false;
	}

}
