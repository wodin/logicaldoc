package com.logicaldoc.webservice;

import javax.net.ssl.TrustManager;

import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.common.gzip.GZIPInInterceptor;
import org.apache.cxf.transport.common.gzip.GZIPOutInterceptor;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;

import com.logicaldoc.util.io.EasySSLProtocolSocketFactory;

/**
 * Parent for all clients
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.5.1
 */
public abstract class AbstractClient<T> {

	protected T client;

	protected String endpoint;

	/**
	 * Constructor
	 * 
	 * @param endpoint Connection URL
	 * @param serviceClass The class of the service
	 * @param gzipThreshold threshold in KB, all packets greater than this one
	 *        will be gzipped(use -1 to disable in any case)
	 * @param log True if you want the requests to be logged
	 * @param timeout Timeout for the SOAP requests (in seconds, default 60)
	 */
	public AbstractClient(String endpoint, Class<T> serviceClass, int gzipThreshold, boolean log, int timeout) {
		this.endpoint = endpoint;
		initClient(serviceClass, gzipThreshold, log);
		
		if (endpoint.toLowerCase().startsWith("https"))
			configureSSL();

		if (timeout > 0)
			configureTimeout(timeout);
	}

	/**
	 * Standard service initialization. Concrete implementations can change the
	 * client initialization logic
	 */
	@SuppressWarnings("unchecked")
	protected void initClient(Class<T> serviceClass, int gzipThreshold, boolean log) {
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();

		if (log) {
			factory.getInInterceptors().add(new LoggingInInterceptor());
			factory.getOutInterceptors().add(new LoggingOutInterceptor());
		}

		if (gzipThreshold >= 0) {
			factory.getInInterceptors().add(new GZIPInInterceptor());
			factory.getOutInterceptors().add(new GZIPOutInterceptor(gzipThreshold));
		}

		factory.setServiceClass(serviceClass);
		factory.setAddress(endpoint);
		client = (T) factory.create();
	}

	/**
	 * Configures the SSL environment.
	 */
	protected void configureSSL() {
		TLSClientParameters tlsParams = new TLSClientParameters();
		tlsParams.setDisableCNCheck(true);
		tlsParams.setTrustManagers(new TrustManager[] { new EasySSLProtocolSocketFactory.EasyX509TrustManager() });

		org.apache.cxf.endpoint.Client cl = ClientProxy.getClient(client);
		HTTPConduit httpConduit = (HTTPConduit) cl.getConduit();
		httpConduit.setTlsClientParameters(tlsParams);
	}

	/**
	 * Configures the timeout (in seconds)
	 */
	protected void configureTimeout(int timeout) {
		HTTPClientPolicy policy = new HTTPClientPolicy();
		policy.setConnectionTimeout(timeout * 1000);
		policy.setReceiveTimeout(timeout * 1000);

		org.apache.cxf.endpoint.Client cl = ClientProxy.getClient(client);
		HTTPConduit httpConduit = (HTTPConduit) cl.getConduit();
		httpConduit.setClient(policy);
	}
}