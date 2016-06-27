package com.logicaldoc.webservice.soap;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.logicaldoc.webservice.doc.WSDoc;
import com.logicaldoc.webservice.model.WSDocument;
import com.logicaldoc.webservice.model.WSFolder;
import com.logicaldoc.webservice.model.WSSearchOptions;
import com.logicaldoc.webservice.model.WSSearchResult;
import com.logicaldoc.webservice.model.WSTagCloud;

/**
 * Search Web Service definition interface
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 5.2
 */
@WSDoc(description = "search operations")
@WebService(name = "Search", serviceName = "Search", targetNamespace = "http://ws.logicaldoc.com")
public interface SearchService {
	/**
	 * Performs a search by the search options.
	 * 
	 * @param sid Session Identifier
	 * @param options Search options
	 * @return The search result
	 */
	@WebMethod
	@WebResult(name = "searchResult")
	@WSDoc(description = "performs a search by the search options")
	public WSSearchResult find(@WebParam(name = "sid") String sid, @WebParam(name = "options") WSSearchOptions options)
			throws Exception;

	/**
	 * Retrieves all the tags in the repository.
	 * 
	 * @param sid Session Identifier
	 * @return The tags in the repository
	 */
	@WebMethod
	@WebResult(name = "tag")
	@WSDoc(description = "retrieves all the tags in the repository")
	public String[] getTags(@WebParam(name = "sid") String sid) throws Exception;

	/**
	 * Retrieves all tag clouds in the repository.
	 * 
	 * @param sid Session Identifier
	 * @return The tags in the repository
	 */
	@WebMethod
	@WebResult(name = "tagCloud")
	@WSDoc(description = "retrieves all tag clouds in the repository")
	public WSTagCloud[] getTagCloud(@WebParam(name = "sid") String sid) throws Exception;

	/**
	 * Finds authorized documents for the current user having a specified tag.
	 * 
	 * @param tag Tag of the document
	 * @return Collection of found folders.
	 */
	@WebMethod
	@WebResult(name = "document")
	@WSDoc(description = "finds authorized documents for the current user having a specified tag")
	public WSDocument[] findByTag(@WebParam(name = "sid") String sid, @WebParam(name = "tag") String tag)
			throws Exception;

	/**
	 * Finds authorized documents for the current user the given filename (like
	 * operator is used).
	 * 
	 * @param filename Filename of the document
	 * @return Collection of found documents.
	 */
	@WebMethod
	@WebResult(name = "document")
	@WSDoc(description = "finds authorized documents for the current user with the given filename (like '%' operator is used)")
	public WSDocument[] findByFilename(@WebParam(name = "sid") String sid, @WebParam(name = "filename") String filename)
			throws Exception;

	/**
	 * Finds authorized folders for the current user containing the given name
	 * (like operator is used).
	 * 
	 * @param name Name of the folder
	 * @return Collection of found folders.
	 */
	@WebMethod
	@WebResult(name = "folder")
	@WSDoc(description = "finds authorized folders for the current user containing the given name (like '%' operator is used)")
	public WSFolder[] findFolders(@WebParam(name = "sid") String sid, @WebParam(name = "name") String name)
			throws Exception;
}