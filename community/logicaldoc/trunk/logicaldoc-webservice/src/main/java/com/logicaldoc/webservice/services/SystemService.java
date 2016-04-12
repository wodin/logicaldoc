package com.logicaldoc.webservice.services;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.logicaldoc.webservice.model.WSParameter;
import com.logicaldoc.webservice.model.WSSystemInfo;

/**
 * System Web Service definition interface
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.1
 */
@WebService(name = "System", serviceName = "System", targetNamespace = "http://ws.logicaldoc.com")
public interface SystemService {

	/**
	 * Retrieves the Installation informations.
	 * 
	 * @return The value object containing the installation informations.
	 * @throws Exception
	 */
	@WebResult(name = "info")
	public WSSystemInfo getInfo() throws Exception;

	/**
	 * Retrieves the system statistics.
	 * 
	 * @param sid Session identifier
	 * @return The value object containing the statistics values.
	 * @throws Exception
	 */
	@WebResult(name = "parameters")
	public WSParameter[] getStatistics(@WebParam(name = "sid") String sid) throws Exception;

	/**
	 * Retrieves the languages enabled in the server.
	 * 
	 * @return Array of active languages (en, it, es ....)
	 * @param tenantOrSid Tenant name or session identifier (optional)
	 * 
	 * @throws Exception
	 */
	@WebResult(name = "languages")
	public String[] getLanguages(@WebParam(name = "tenantOrSid") String tenantOrSid) throws Exception;
}