package com.logicaldoc.webservice.system;

import java.io.IOException;

import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import com.logicaldoc.core.SystemInfo;
import com.logicaldoc.webservice.WSParameter;

/**
 * System Web Service client.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.1
 */
public class SystemClient implements SystemService {

	private SystemService client;

	public SystemClient(String endpoint) throws IOException {
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();

		factory.getInInterceptors().add(new LoggingInInterceptor());
		factory.getOutInterceptors().add(new LoggingOutInterceptor());
		factory.setServiceClass(SystemService.class);
		factory.setAddress(endpoint);
		client = (SystemService) factory.create();
	}

	@Override
	public SystemInfo getInfo(String sid) throws Exception {
		return client.getInfo(sid);
	}

	@Override
	public WSParameter[] getStatistics(String sid) throws Exception {
		return client.getStatistics(sid);
	}
}
