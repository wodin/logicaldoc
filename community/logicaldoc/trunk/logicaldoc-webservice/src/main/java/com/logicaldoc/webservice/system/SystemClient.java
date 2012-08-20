package com.logicaldoc.webservice.system;

import java.io.IOException;

import com.logicaldoc.core.SystemInfo;
import com.logicaldoc.webservice.AbstractClient;
import com.logicaldoc.webservice.WSParameter;

/**
 * System Web Service client.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.1
 */
public class SystemClient extends AbstractClient<SystemService> implements SystemService {

	public SystemClient(String endpoint) throws IOException {
		super(endpoint, SystemService.class, -1, true);
	}

	@Override
	public WSParameter[] getStatistics(String sid) throws Exception {
		return client.getStatistics(sid);
	}

	@Override
	public String[] getLanguages() throws Exception {
		return client.getLanguages();
	}

	@Override
	public SystemInfo getInfo(String sid) throws Exception {
		return client.getInfo(sid);
	}
}