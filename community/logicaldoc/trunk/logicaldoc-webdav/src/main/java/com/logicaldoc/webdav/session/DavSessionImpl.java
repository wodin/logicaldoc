package com.logicaldoc.webdav.session;

import java.util.HashMap;

import com.logicaldoc.webdav.AuthenticationUtil.Credentials;

public class DavSessionImpl implements DavSession{

	private HashMap<String,Object> map = new HashMap<String, Object>();
	
	private Credentials credentials;
	
	@Override
	public void setCredentials(Credentials credentials) {
		this.credentials = credentials;
		
	}
	
	@Override
	public Credentials getCredentials() {
		return this.credentials;
	}


	@Override
	public Object getObject(String key) {
		return map.get(key);
	}

	@Override
	public void putObject(String key, Object value) {
		map.put(key, value);
	}

}
