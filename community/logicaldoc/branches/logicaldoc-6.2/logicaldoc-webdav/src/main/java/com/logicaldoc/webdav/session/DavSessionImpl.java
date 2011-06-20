package com.logicaldoc.webdav.session;

import java.util.HashMap;

import com.logicaldoc.webdav.AuthenticationUtil.Credentials;

/**
 * For more informations, please visit
 * {@link org.apache.jackrabbit.webdav.simple.DavSessionImpl}
 * 
 * @author Sebastian Wenzky
 * 
 */
public class DavSessionImpl implements DavSession{

	private HashMap<String,Object> map = new HashMap<String, Object>();
	
	private Credentials credentials;
	
	/**
	 * @see DavSession#setCredentials(Credentials)
	 */
	@Override
	public void setCredentials(Credentials credentials) {
		this.credentials = credentials;
		
	}
	
	/**
	 * @see DavSession#getCredentials()
	 */
	@Override
	public Credentials getCredentials() {
		return this.credentials;
	}

	/**
	 * @see DavSession#getObject(String)
	 */
	@Override
	public Object getObject(String key) {
		return map.get(key);
	}

	/**
	 * @see DavSession#putObject(String, Object)
	 */
	@Override
	public void putObject(String key, Object value) {
		map.put(key, value);
	}

	@Override
	public void addLockToken(String arg0) {
	}

	@Override
	public void addReference(Object arg0) {
		throw new UnsupportedOperationException();	
	}

	@Override
	public String[] getLockTokens() {
		return new String[]{};
	}

	@Override
	public void removeLockToken(String arg0) {
		throw new UnsupportedOperationException();	
	}

	@Override
	public void removeReference(Object arg0) {
		throw new UnsupportedOperationException();	
	}
}
