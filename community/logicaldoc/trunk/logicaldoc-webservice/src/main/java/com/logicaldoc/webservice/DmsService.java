package com.logicaldoc.webservice;

import javax.activation.DataHandler;
import javax.jws.WebParam;
import javax.jws.WebService;

import com.logicaldoc.webservice.DocumentInfo;

/**
 * Dms Webservice definition interface
 * 
 * @author Matteo Caruso
 * @version $Id:$
 * @since 3.6
 */
@WebService
public interface DmsService {

	/**
	 * Creates a new folder
	 * 
	 * @param username
	 * @param password
	 * @param name Name of the folder
	 * @param parent Parent identifier
	 * @return 'error' if error occurred, the folder identifier if it was
	 *         created
	 * @throws Exception
	 */
	public String createFolder(@WebParam(name = "username")
	String username, @WebParam(name = "password")
	String password, @WebParam(name = "name")
	String name, @WebParam(name = "parent")
	int parent) throws Exception;

	/**
	 * Deletes an existing folder and all it's contained elements
	 * 
	 * @param username
	 * @param password
	 * @param folder Folder identifier
	 * @return A return code('ok' if all went ok, 'error' if some errors
	 *         occurred)
	 * @throws Exception
	 */
	public String deleteFolder(@WebParam(name = "username")
	String username, @WebParam(name = "password")
	String password, @WebParam(name = "folder")
	int folder) throws Exception;

	public String createDocument(@WebParam(name = "username")
	String username, @WebParam(name = "password")
	String password, @WebParam(name = "parent")
	int parent, @WebParam(name = "docName")
	String docName, @WebParam(name = "source")
	String source, @WebParam(name = "sourceDate")
	String sourceDate, @WebParam(name = "author")
	String author, @WebParam(name = "sourceType")
	String sourceType, @WebParam(name = "coverage")
	String coverage, @WebParam(name = "language")
	String language, @WebParam(name = "keywords")
	String keywords, @WebParam(name = "versionDesc")
	String versionDesc, @WebParam(name = "filename")
	String filename, @WebParam(name = "groups")
	String groups, @WebParam(name = "content")
	DataHandler content) throws Exception;

	/**
	 * Downloads a document. The document content is sent as attachment
	 * identified by 'document'.
	 * 
	 * @param username
	 * @param password
	 * @param id The document menu id
	 * @param version The specific version(it can be empty)
	 * @return The requested document's binary
	 * @throws Exception
	 */
	public DataHandler downloadDocument(@WebParam(name = "username")
	String username, @WebParam(name = "password")
	String password, @WebParam(name = "id")
	int id, @WebParam(name = "version")
	String version) throws Exception;

	/**
	 * Retrieves the document meta-data
	 * 
	 * @param username
	 * @param password
	 * @param id The document menu id
	 * @return
	 * @throws Exception
	 */
	public DocumentInfo downloadDocumentInfo(@WebParam(name = "username")
	String username, @WebParam(name = "password")
	String password, @WebParam(name = "id")
	int id) throws Exception;

	/**
	 * Downloads folder metadata
	 * 
	 * @param username
	 * @param password
	 * @param folder The folder identifier
	 * @return The folder metadata
	 * @throws Exception
	 */
	public FolderContent downloadFolderContent(@WebParam(name = "username")
	String username, @WebParam(name = "password")
	String password, @WebParam(name = "folder")
	int folder) throws Exception;

	/**
	 * Deletes a document
	 * 
	 * @param username
	 * @param password
	 * @param id The document menu id
	 * @return A return code('ok' if all went ok)
	 * @throws Exception
	 */
	public String deleteDocument(@WebParam(name = "username")
	String username, @WebParam(name = "password")
	String password, @WebParam(name = "id")
	int id) throws Exception;

	/**
	 * Marks the document as checked out
	 * 
	 * @param username
	 * @param password
	 * @param id The document menu id
	 * @return A return code('ok' if all went ok)
	 * @throws Exception
	 */
	public String checkout(@WebParam(name = "username")
	String username, @WebParam(name = "password")
	String password, @WebParam(name = "id")
	int id) throws Exception;

	/**
	 * Uploads a new version of an already checked out document
	 * 
	 * @param username
	 * @param password
	 * @param id
	 * @param filename
	 * @param description
	 * @param type
	 * @param content Contains the document's binary content
	 * @return ok if all went right
	 * @throws Exception
	 */
	public String checkin(@WebParam(name = "username")
	String username, @WebParam(name = "password")
	String password, @WebParam(name = "id")
	int id, @WebParam(name = "filename")
	String filename, @WebParam(name = "description")
	String description, @WebParam(name = "type")
	String type, @WebParam(name = "content")
	DataHandler content) throws Exception;

	/**
	 * Search for a documents
	 * 
	 * @param username
	 * @param password
	 * @param query The query string
	 * @param indexLanguage The index language, if null all indexes are
	 *        considered
	 * @param queryLanguage The language in which the query is expressed
	 * @param maxHits The maximum number of hits to be returned
	 * @return The objects representing the search result
	 * @throws Exception
	 */
	public SearchResult search(@WebParam(name = "username")
	String username, @WebParam(name = "password")
	String password, @WebParam(name = "query")
	String query, @WebParam(name = "indexLanguage")
	String indexLanguage, @WebParam(name = "queryLanguage")
	String queryLanguage, @WebParam(name = "maxHits")
	int maxHits) throws Exception;
}