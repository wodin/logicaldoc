package com.logicaldoc.webservice.rest.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.webservice.soap.endpoint.SoapAuthService;

/**
 * Parent for all RESTful clients
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.9
 */
public abstract class AbstractRestClient {

	protected static Logger log = LoggerFactory.getLogger(SoapAuthService.class);

	protected String endpoint;

	/**
	 * Constructor
	 * 
	 * @param endpoint Connection URL
	 */
	public AbstractRestClient(String endpoint) {
		this.endpoint = endpoint;
	}

	/**
	 * Constructor
	 * 
	 * @param endpoint Connection URL
	 * @param timeout Timeout for the RESTful requests
	 */
	public AbstractRestClient(String endpoint, int timeout) {
		this(endpoint);
	}

}