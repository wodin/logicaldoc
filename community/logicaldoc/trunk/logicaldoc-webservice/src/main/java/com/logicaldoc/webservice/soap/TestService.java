package com.logicaldoc.webservice.soap;

import javax.activation.DataHandler;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.logicaldoc.webservice.model.WSDocument;

/**
 * Document Web Service definition interface
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 5.2
 */
@WebService(name = "Test", serviceName = "Test", targetNamespace = "http://ws.logicaldoc.com")
public interface TestService {

	/**
	 * Create a new document. The user can completely customize the document
	 * through a value object containing the document's metadata.
	 * 
	 * @param sid Session identifier
	 * @param document Web service value object containing the document's
	 *        metadata
	 * @param content The document's binary content
	 * @return The value object containing the document's metadata.
	 * @throws Exception
	 */
	@WebMethod
	@WebResult(name = "document")
	public WSDocument create(@WebParam(name = "sid") String sid, @WebParam(name = "documents") WSDocument[] documents,
			@WebParam(name = "content") DataHandler content) throws Exception;
}