package com.logicaldoc.webservice.system;

import java.io.IOException;

import com.logicaldoc.webservice.SoapClient;
import com.logicaldoc.webservice.WSParameter;

/**
 * System Web Service client.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.1
 */
public class SystemClient extends SoapClient<SystemService> implements SystemService {

	public SystemClient(String endpoint) throws IOException {
		super(endpoint, SystemService.class, -1, true, -1);
	}

	@Override
	public WSParameter[] getStatistics(String sid) throws Exception {
		return client.getStatistics(sid);
	}

	@Override
	public String[] getLanguages(String tenantOrSid) throws Exception {
		return client.getLanguages(tenantOrSid);
	}

	@Override
	public WSSystemInfo getInfo() throws Exception {
		return client.getInfo();
	}
}