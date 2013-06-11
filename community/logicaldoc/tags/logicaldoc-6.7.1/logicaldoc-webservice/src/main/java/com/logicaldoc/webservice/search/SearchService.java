package com.logicaldoc.webservice.search;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.logicaldoc.webservice.document.WSDocument;
import com.logicaldoc.webservice.folder.WSFolder;

/**
 * Search Web Service definition interface
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 5.2
 */
@WebService
public interface SearchService {
	/**
	 * Performs a search by the search options.
	 * 
	 * @param sid Session Identifier
	 * @param options Search options
	 * @return The search result
	 */
	@WebResult(name = "searchResult")
	public WSSearchResult find(@WebParam(name = "sid") String sid, @WebParam(name = "options") WSSearchOptions options)
			throws Exception;

	/**
	 * Retrieves all tags in the repository.
	 * 
	 * @param sid Session Identifier
	 * @return The tags in the repository
	 */
	public String[] getTags(@WebParam(name = "sid") String sid) throws Exception;

	/**
	 * Retrieves all tag clouds in the repository.
	 * 
	 * @param sid Session Identifier
	 * @return The tags in the repository
	 */
	@WebResult(name = "tagCloud")
	public WSTagCloud[] getTagCloud(@WebParam(name = "sid") String sid) throws Exception;

	/**
	 * Finds authorized documents for the current user having a specified tag.
	 * 
	 * @param tag Tag of the document
	 * @return Collection of found folders.
	 */
	@WebResult(name = "document")
	public WSDocument[] findByTag(@WebParam(name = "sid") String sid, @WebParam(name = "tag") String tag)
			throws Exception;

	/**
	 * Finds authorized documents for the current user the given filename (like
	 * operator is used).
	 * 
	 * @param filename Filename of the document
	 * @return Collection of found documents.
	 */
	@WebResult(name = "document")
	public WSDocument[] findByFilename(@WebParam(name = "sid") String sid, @WebParam(name = "filename") String filename)
			throws Exception;

	/**
	 * Finds authorized folders for the current user containing the given name
	 * (like operator is used).
	 * 
	 * @param name Name of the folder
	 * @return Collection of found folders.
	 */
	@WebResult(name = "folder")
	public WSFolder[] findFolders(@WebParam(name = "sid") String sid, @WebParam(name = "name") String name)
			throws Exception;
}