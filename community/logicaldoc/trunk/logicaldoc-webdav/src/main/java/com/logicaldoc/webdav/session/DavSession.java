package com.logicaldoc.webdav.session;

import com.logicaldoc.webdav.AuthenticationUtil.Credentials;

public interface DavSession {
	
	public void setCredentials(Credentials credentials);
	
	public Credentials getCredentials();
	
	public void putObject(String key, Object value);
	
	public Object getObject(String key);
}
