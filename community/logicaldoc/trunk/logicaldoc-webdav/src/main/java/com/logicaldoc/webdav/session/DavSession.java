package com.logicaldoc.webdav.session;

import com.logicaldoc.core.security.Tenant;
import com.logicaldoc.webdav.AuthenticationUtil.Credentials;

/**
 * For more informations, please visit
 * {@link org.apache.jackrabbit.webdav.DavSession}
 * 
 * @author Sebastian Wenzky
 * 
 */
public interface DavSession extends org.apache.jackrabbit.webdav.DavSession {

	/**
	 * Sets a new Credentials Object to the DavSession
	 * 
	 * @param credentials Credentials
	 */
	public void setCredentials(Credentials credentials);

	/**
	 * Getting back the current set credentials
	 * 
	 * @return
	 */
	public Credentials getCredentials();

	/**
	 * Puts an object to the session map
	 * 
	 * @param key The Key
	 * @param value the corresponding object
	 */
	public void putObject(String key, Object value);

	/**
	 * Gets an object by passing the appropiated key
	 * 
	 * @param key The Key
	 * @return the corresponding object
	 */
	public Object getObject(String key);

	/**
	 * The tenant of this session
	 */
	public long getTenantId();
}
