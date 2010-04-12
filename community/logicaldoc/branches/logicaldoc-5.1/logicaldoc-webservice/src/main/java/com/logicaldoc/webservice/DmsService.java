package com.logicaldoc.webservice;

import javax.activation.DataHandler;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * Dms Webservice definition interface
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 3.6
 */
@WebService
public interface DmsService {

	/**
	 * Starts a new user session.
	 * 
	 * @param username The username
	 * @param password The password
	 * @return The newly created session identifier(sid)
	 */
	public String login(@WebParam(name = "username")
	String username, @WebParam(name = "password")
	String password) throws Exception;

	/**
	 * Closes a user session.
	 * 
	 * @param sid The session identifier
	 */
	public void logout(@WebParam(name = "sid")
	String sid);

	/**
	 * Create a new folder with a given name and under a specific folder. It
	 * also assigns the same folder permissions of the parent folder
	 * 
	 * @param sid Session identifier
	 * @param name Name of the folder
	 * @param parent Parent identifier
	 * @return 'error' if error occurred, the folder identifier if it was
	 *         created
	 * @throws Exception
	 */
	public String createFolder(@WebParam(name = "sid")
	String sid, @WebParam(name = "name")
	String name, @WebParam(name = "parent")
	long parent) throws Exception;

	/**
	 * Deletes an existing folder and all it's contained elements
	 * 
	 * @param sid Session identifier
	 * @param folder Folder identifier
	 * @return A return code('ok' if all went ok, 'error' if some errors
	 *         occurred)
	 * @throws Exception
	 */
	public String deleteFolder(@WebParam(name = "sid")
	String sid, @WebParam(name = "folder")
	long folder) throws Exception;

	/**
	 * Create a new document. The user can completely customize the document
	 * through many optional fields.
	 * 
	 * @param sid Session identifier
	 * @param folder
	 * @param docTitle
	 * @param source
	 * @param sourceDate
	 * @param author
	 * @param sourceType
	 * @param coverage
	 * @param language
	 * @param tags
	 * @param versionDesc
	 * @param filename
	 * @param content
	 * @param templateName
	 * @param templateFields
	 * @param sourceId
	 * @param object
	 * @param recipient
	 * @param customId
	 * @return The document identifier or 'error' if some errors occurred)
	 * @throws Exception
	 */
	public String createDocument(@WebParam(name = "sid")
	String sid, @WebParam(name = "folder")
	long folder, @WebParam(name = "docTitle")
	String docTitle, @WebParam(name = "source")
	String source, @WebParam(name = "sourceDate")
	String sourceDate, @WebParam(name = "sourceAuthor")
	String author, @WebParam(name = "sourceType")
	String sourceType, @WebParam(name = "coverage")
	String coverage, @WebParam(name = "language")
	String language, @WebParam(name = "tags")
	String tags, @WebParam(name = "versionDesc")
	String versionDesc, @WebParam(name = "filename")
	String filename, @WebParam(name = "content")
	DataHandler content, @WebParam(name = "templateName")
	String templateName, @WebParam(name = "templateFields")
	Attribute[] extendedAttributes, @WebParam(name = "sourceId")
	String sourceId, @WebParam(name = "object")
	String object, @WebParam(name = "recipient")
	String recipient, @WebParam(name = "customId")
	String customId) throws Exception;

	/**
	 * Updates an existing document and marks it to be re-indexed
	 */
	public String update(@WebParam(name = "sid")
	String sid, @WebParam(name = "id")
	long id, @WebParam(name = "title")
	String title, @WebParam(name = "source")
	String source, @WebParam(name = "sourceAuthor")
	String sourceAuthor, @WebParam(name = "sourceDate")
	String sourceDate, @WebParam(name = "sourceType")
	String sourceType, @WebParam(name = "coverage")
	String coverage, @WebParam(name = "language")
	String language, @WebParam(name = "tags")
	String[] tags, @WebParam(name = "sourceId")
	String sourceId, @WebParam(name = "object")
	String object, @WebParam(name = "recipient")
	String recipient, @WebParam(name = "templateName")
	String templateName, @WebParam(name = "templateFields")
	Attribute[] extendedAttribute) throws Exception;

	/**
	 * Downloads a document. The document content is sent as attachment
	 * identified by 'document'
	 * 
	 * @param sid Session identifier
	 * @param id The document menu id
	 * @param version The specific version(it can be empty)
	 * @return The requested document's binary
	 * @throws Exception
	 */
	public DataHandler downloadDocument(@WebParam(name = "sid")
	String sid, @WebParam(name = "id")
	long id, @WebParam(name = "version")
	String version) throws Exception;

	/**
	 * Retrieves the document meta-data
	 * 
	 * @param sid Session identifier
	 * @param id The document id
	 * @return
	 * @throws Exception
	 */
	public DocumentInfo downloadDocumentInfo(@WebParam(name = "sid")
	String sid, @WebParam(name = "id")
	long id) throws Exception;

	/**
	 * Requests the indexing of a particular document. If the document is
	 * already indexed, it will be re-indexed.
	 * 
	 * @param sid
	 * @param id
	 * @throws Exception
	 */
	public void indexDocument(@WebParam(name = "sid")
	String sid, @WebParam(name = "id")
	long id) throws Exception;

	/**
	 * Renames a folder
	 * 
	 * @param sid Session identifier
	 * @param folder The folder identifier
	 * @param name the new name for the folder
	 * @throws Exception
	 */
	public String renameFolder(@WebParam(name = "sid")
	String sid, @WebParam(name = "folder")
	long folder, @WebParam(name = "name")
	String name) throws Exception;

	/**
	 * Downloads folder metadata
	 * 
	 * @param sid Session identifier
	 * @param folder The folder identifier
	 * @throws Exception
	 */
	public FolderContent downloadFolderContent(@WebParam(name = "sid")
	String sid, @WebParam(name = "folder")
	long folder) throws Exception;

	/**
	 * Deletes an existing document with the given identifier
	 * 
	 * @param sid Session identifier
	 * @param id The document id
	 * @return A return code('ok' if all went ok)
	 * @throws Exception
	 */
	public String deleteDocument(@WebParam(name = "sid")
	String sid, @WebParam(name = "id")
	long id) throws Exception;

	/**
	 * Marks the document as checked out
	 * 
	 * @param sid Session identifier
	 * @param id The document menu id
	 * @return A return code('ok' if all went ok)
	 * @throws Exception
	 */
	public String checkout(@WebParam(name = "sid")
	String sid, @WebParam(name = "id")
	long id) throws Exception;

	/**
	 * Uploads a new version of an already checked out document
	 * 
	 * @param sid Session identifier
	 * @param id
	 * @param filename
	 * @param description
	 * @param type
	 * @param content Contains the document's binary content
	 * @return ok if all went right
	 * @throws Exception
	 */
	public String checkin(@WebParam(name = "sid")
	String sid, @WebParam(name = "id")
	long id, @WebParam(name = "filename")
	String filename, @WebParam(name = "description")
	String description, @WebParam(name = "type")
	String type, @WebParam(name = "content")
	DataHandler content) throws Exception;

	/**
	 * Search for a documents
	 * 
	 * @param sid Session identifier
	 * @param query The query string
	 * @param indexLanguage The index language, if null all indexes are
	 *        considered
	 * @param queryLanguage The language in which the query is expressed
	 * @param maxHits The maximum number of hits to be returned
	 * @param templateName The name of template to search
	 * @param templateFields The template's fields
	 * @return The objects representing the search result
	 * @throws Exception
	 */
	public SearchResult search(@WebParam(name = "sid")
	String sid, @WebParam(name = "query")
	String query, @WebParam(name = "indexLanguage")
	String indexLanguage, @WebParam(name = "queryLanguage")
	String queryLanguage, @WebParam(name = "maxHits")
	int maxHits, @WebParam(name = "templateName")
	String templateName, @WebParam(name = "templateFields")
	String[] templateFields) throws Exception;
}