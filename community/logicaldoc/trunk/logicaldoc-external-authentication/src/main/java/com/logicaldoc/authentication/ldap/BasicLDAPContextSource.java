package com.logicaldoc.authentication.ldap;

import java.util.Hashtable;

import javax.naming.Context;

import org.springframework.ldap.support.LdapContextSource;

/**
 * 
 * @author Sebastian Wenzky
 *
 */
public class BasicLDAPContextSource extends LdapContextSource{

	private String realm;
	private String dn;
	
	private String userAuthenticationPatern;
	LDAPUserGroupContext userGroupContext;
	
	public void setUserGroupContext(LDAPUserGroupContext userGroupContext) {
		this.userGroupContext = userGroupContext;
	}
	
	public void setUserAuthenticationPatern(String userAuthenticationPatern) {
		this.userAuthenticationPatern = userAuthenticationPatern;
	}
	
	public String getUserAuthenticationPatern() {
		return userAuthenticationPatern;
	}
	
	public void setRealm(String realm) {
		this.realm = realm;
	}
	
	public String getRealm() {
		return realm;
	}
	
	public void setCurrentDN(String dn){
		this.dn = dn;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void setupAuthenticatedEnvironment(Hashtable env) {
		super.setupAuthenticatedEnvironment(env);
		
		String userName = (String)env.get(Context.SECURITY_PRINCIPAL);
		env.put(Context.SECURITY_PRINCIPAL, this.generateUserLogonName(userName));

	}
	
	private String generateUserLogonName(String username){
		
		String tmpUserAuthenticationPatern = userAuthenticationPatern;
		if(tmpUserAuthenticationPatern.contains("{logonAttribute}"))
			tmpUserAuthenticationPatern = tmpUserAuthenticationPatern.replaceAll("\\{logonAttribute\\}", this.userGroupContext.getUserIdentiferAttribute());
		if(tmpUserAuthenticationPatern.contains("{userName}"))
			tmpUserAuthenticationPatern = tmpUserAuthenticationPatern.replaceAll("\\{userName\\}", username);
		if(tmpUserAuthenticationPatern.contains("{userBaseEntry}")) 
			tmpUserAuthenticationPatern = tmpUserAuthenticationPatern.replaceAll("\\{userBaseEntry\\}", this.dn);
		
		return tmpUserAuthenticationPatern;
	}
	
}
