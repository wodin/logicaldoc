package com.logicaldoc.webservice.document;

import javax.activation.DataHandler;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

/**
 * Document Web Service definition interface
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 5.2
 */
@WebService
public interface DocumentService {

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
	@WebResult(name = "document")
	public WSDocument create(@WebParam(name = "sid") String sid, @WebParam(name = "document") WSDocument document,
			@WebParam(name = "content") DataHandler content) throws Exception;

	/**
	 * Deletes an existing document with the given identifier.
	 * 
	 * @param sid Session identifier
	 * @param docId The document id
	 * @throws Exception
	 */
	public void delete(@WebParam(name = "sid") String sid, @WebParam(name = "docId") long docId) throws Exception;

	/**
	 * Locks an existing document with the given identifier.
	 * 
	 * @param sid Session identifier
	 * @param docId The document id
	 * @throws Exception
	 */
	public void lock(@WebParam(name = "sid") String sid, @WebParam(name = "docId") long docId) throws Exception;

	/**
	 * Unlocks an existing document with the given identifier.
	 * 
	 * @param sid Session identifier
	 * @param docId The document id
	 * @throws Exception
	 */
	public void unlock(@WebParam(name = "sid") String sid, @WebParam(name = "docId") long docId) throws Exception;

	/**
	 * Renames the title of an existing document with the given identifier.
	 * 
	 * @param sid Session identifier
	 * @param docId The document id
	 * @param name The new document title
	 * @throws Exception
	 */
	public void rename(@WebParam(name = "sid") String sid, @WebParam(name = "docId") long docId,
			@WebParam(name = "name") String name) throws Exception;

	/**
	 * Renames the filename of an existing document with the given identifier.
	 * 
	 * @param sid Session identifier
	 * @param docId The document id
	 * @param name The new document file name
	 * @throws Exception
	 */
	public void renameFile(@WebParam(name = "sid") String sid, @WebParam(name = "docId") long docId,
			@WebParam(name = "name") String name) throws Exception;

	/**
	 * Moves an existing document with the given identifier.
	 * 
	 * @param sid Session identifier
	 * @param docId The document id
	 * @param folderId The folder id of the new document folder
	 * @throws Exception
	 */
	public void move(@WebParam(name = "sid") String sid, @WebParam(name = "docId") long docId,
			@WebParam(name = "folderId") long folderId) throws Exception;

	/**
	 * Gets document metadata of an existing document with the given identifier.
	 * 
	 * @param sid Session identifier
	 * @param docId The document id
	 * @return A value object containing the document's metadata.
	 * @throws Exception
	 */
	@WebResult(name = "document")
	public WSDocument getDocument(@WebParam(name = "sid") String sid, @WebParam(name = "docId") long docId)
			throws Exception;

	/**
	 * Gets document metadata of an existing document with the given custom
	 * identifier.
	 * 
	 * @param sid Session identifier
	 * @param customId The custom id
	 * @return A value object containing the document's metadata.
	 * @throws Exception
	 */
	@WebResult(name = "document")
	public WSDocument getDocumentByCustomId(@WebParam(name = "sid") String sid,
			@WebParam(name = "customId") String customId) throws Exception;

	/**
	 * Gets document metadata of a collection of existing documents with the
	 * given identifiers.
	 * 
	 * @param sid Session identifier
	 * @param docIds The documents ids
	 * @return A value object containing the document's metadata.
	 * @throws Exception
	 */
	@WebResult(name = "documents")
	public WSDocument[] getDocuments(@WebParam(name = "sid") String sid, @WebParam(name = "docIds") Long[] docIds)
			throws Exception;

	/**
	 * Gets the aliases of the given document
	 * 
	 * @param sid Session identifier
	 * @param docId The master document ID
	 * @return Arrays of aliases
	 * @throws Exception
	 */
	@WebResult(name = "aliases")
	public WSDocument[] getAliases(@WebParam(name = "sid") String sid, @WebParam(name = "docId") long docId)
			throws Exception;

	/**
	 * Updates an existing document with the value object containing the
	 * document's metadata.
	 * 
	 * @param sid Session identifier
	 * @param doc The value object containing the document's metadata.
	 * @throws Exception
	 */
	public void update(@WebParam(name = "sid") String sid, @WebParam(name = "document") WSDocument document)
			throws Exception;

	/**
	 * Gets the document content of an existing document with the given
	 * identifier.
	 * 
	 * @param sid Session identifier
	 * @param docId The document id
	 * @return The requested document's binary
	 * @throws Exception
	 */
	public DataHandler getContent(@WebParam(name = "sid") String sid, @WebParam(name = "docId") long docId)
			throws Exception;

	/**
	 * Gets the document's text stored in the full-text index
	 * 
	 * @param sid Session identifier
	 * @param docId The document id
	 * @return The requested document's text 
	 * @throws Exception
	 */
	@WebResult(name = "text")
	public String getExtractedText(@WebParam(name = "sid") String sid, @WebParam(name = "docId") long docId)
			throws Exception;
	
	/**
	 * Gets the version content of an existing document with the given
	 * identifier.
	 * 
	 * @param sid Session identifier
	 * @param docId The document id
	 * @param version The specific version(it can be empty)
	 * @return The requested version's binary
	 * @throws Exception
	 */
	public DataHandler getVersionContent(@WebParam(name = "sid") String sid, @WebParam(name = "docId") long docId,
			@WebParam(name = "version") String version) throws Exception;

	/**
	 * Gets the content of a resource associated to the given document.
	 * 
	 * @param sid Session identifier
	 * @param docId The document id
	 * @param fileVersion The specific file version(it can be empty)
	 * @param suffix Suffix specification(it can be empty, conversion.pdf to get
	 *        the PDF conversion)
	 * @return The requested resource's binary
	 * @throws Exception
	 */
	public DataHandler getResource(@WebParam(name = "sid") String sid, @WebParam(name = "docId") long docId,
			@WebParam(name = "fileVersion") String fileVersion, @WebParam(name = "suffix") String suffix)
			throws Exception;

	/**
	 * Checkouts an existing document with the given identifier.
	 * 
	 * @param sid Session identifier
	 * @param docId The document id
	 * @throws Exception
	 */
	public void checkout(@WebParam(name = "sid") String sid, @WebParam(name = "docId") long docId) throws Exception;

	/**
	 * Checks in an existing document with the given identifier to create a new
	 * version.
	 * 
	 * @param sid Session identifier
	 * @param docId The document id
	 * @param comment The check in operation comment
	 * @param filename The document file name
	 * @param release True if this is a new release(eg: 2.0) rather than a
	 *        subversion(eg: 1.1)
	 * @param content The document's binary content
	 * @throws Exception
	 */
	public void checkin(@WebParam(name = "sid") String sid, @WebParam(name = "docId") long docId,
			@WebParam(name = "comment") String comment, @WebParam(name = "filename") String filename,
			@WebParam(name = "release") boolean release, @WebParam(name = "content") DataHandler content)
			throws Exception;

	/**
	 * Checks in an existing document with the given identifier to create a new
	 * version.
	 * 
	 * @param sid Session identifier
	 * @param docId The document id (optional)
	 * @param folderId The folder id (optional)
	 * @param release True if this is a new release(eg: 2.0) rather than a
	 *        subversion(eg: 1.1)
	 * @param filename The document file name
	 * @param content The document's binary content
	 * @return The created/updated document's ID
	 * @throws Exception
	 */
	@WebResult(name = "docId")
	public long upload(@WebParam(name = "sid") String sid, @WebParam(name = "docId") Long docId,
			@WebParam(name = "folderId") Long folderId, @WebParam(name = "release") boolean release,
			@WebParam(name = "filename") String filename, @WebParam(name = "content") DataHandler content)
			throws Exception;

	/**
	 * Uploads a new resource attached to the given document. If the resource
	 * already exists it is overwritten.
	 * 
	 * @param sid Session identifier
	 * @param docId The document id
	 * @param fileVersion The specific file version(it can be empty)
	 * @param suffix Suffix specification(it cannot be empty, use
	 *        'conversion.pdf' to put the PDF conversion)
	 * @param content The resource's binary content
	 * @throws Exception
	 */
	public void uploadResource(@WebParam(name = "sid") String sid, @WebParam(name = "docId") long docId,
			@WebParam(name = "fileVersion") String fileVersion, @WebParam(name = "suffix") String suffix,
			@WebParam(name = "content") DataHandler content) throws Exception;

	/**
	 * Test if a document identifier is valid.
	 * 
	 * @param sid Session identifier
	 * @param docId The document id
	 * @return True if the identifier denotes a document, otherwise false.
	 * @throws Exception
	 */
	public boolean isReadable(@WebParam(name = "sid") String sid, @WebParam(name = "docId") long docId)
			throws Exception;

	/**
	 * Restores an existing document with the given identifier.
	 * 
	 * @param sid Session identifier
	 * @param docId The document id
	 * @param folderId The target folder id
	 * @throws Exception
	 */
	public void restore(@WebParam(name = "sid") String sid, @WebParam(name = "docId") long docId,
			@WebParam(name = "folderId") long folderId) throws Exception;

	/**
	 * Gets the version history of an existing document with the given
	 * identifier.
	 * 
	 * @param sid Session identifier
	 * @param docId The document id
	 * @return Array of versions
	 * @throws Exception
	 */
	@WebResult(name = "document")
	public WSDocument[] getVersions(@WebParam(name = "sid") String sid, @WebParam(name = "docId") long docId)
			throws Exception;

	/**
	 * Lists the documents inside a folder
	 * 
	 * @param sid Session identifier
	 * @param folderId The document id
	 * @return Array of documents contained in the folder
	 * 
	 * @throws Exception
	 */
	@WebResult(name = "document")
	@Deprecated
	public WSDocument[] list(@WebParam(name = "sid") String sid, @WebParam(name = "folderId") long folderId)
			throws Exception;

	/**
	 * Gets a document in a specific folder
	 * 
	 * @param sid Session identifier
	 * @param folderId The folder id
	 * @param fileName Optional file name filter
	 * @return A value object containing the document's metadata.
	 * @throws Exception
	 */
	@WebResult(name = "document")
	public WSDocument[] listDocuments(@WebParam(name = "sid") String sid, @WebParam(name = "folderId") long folderId,
			@WebParam(name = "fileName") String fileName) throws Exception;

	/**
	 * Lists of last modified documents of the current session's user.
	 * 
	 * @param sid Session identifier
	 * @param maxHits Maximum number of returned records
	 * @return Array of documents
	 * 
	 * @throws Exception
	 */
	@WebResult(name = "document")
	public WSDocument[] getRecentDocuments(@WebParam(name = "sid") String sid,
			@WebParam(name = "maxHits") Integer maxHits) throws Exception;

	/**
	 * Sends a set of documents as mail attachments
	 * 
	 * @param sid Session identifiers
	 * @param docIds Set of document ids
	 * @param recipients Set of recipients(comma separated)
	 * @param subject The email subject
	 * @param message The email message body
	 */
	public void sendEmail(@WebParam(name = "sid") String sid, @WebParam(name = "docIds") Long[] docIds,
			@WebParam(name = "recipients") String recipients, String subject, String message) throws Exception;

	/**
	 * Create a new document alias for the given document id and inside the
	 * given folder id.
	 * 
	 * @param sid Session identifier
	 * @param docId The original document id
	 * @param folderId Identifier of the folder in which will be stored the
	 *        alias.
	 * @param type Type of the alias.
	 * @return The value object containing the document's metadata.
	 * @throws Exception
	 */
	@WebResult(name = "document")
	public WSDocument createAlias(@WebParam(name = "sid") String sid, @WebParam(name = "docId") long docId,
			@WebParam(name = "folderId") long folderId, @WebParam(name = "type") String type) throws Exception;

	/**
	 * Creates a new link between two documents.
	 * 
	 * @param sid Session identifier
	 * @param doc1 ID of document 1
	 * @param doc2 ID of document 2
	 * @param type The link type(it can be empty)
	 * 
	 * @return the new link
	 * @throws Exception
	 */
	@WebResult(name = "link")
	public WSLink link(@WebParam(name = "sid") String sid, @WebParam(name = "doc1") long doc1,
			@WebParam(name = "doc2") long doc2, @WebParam(name = "type") String type) throws Exception;

	/**
	 * Gets all the links of a specific document
	 * 
	 * @param sid Session identifier
	 * @param docId ID of the document
	 * 
	 * @return The new links of the document
	 * @throws Exception
	 */
	@WebResult(name = "link")
	public WSLink[] getLinks(@WebParam(name = "sid") String sid, @WebParam(name = "docId") long docId) throws Exception;

	/**
	 * Remove an existing link
	 * 
	 * @param sid Session identifier
	 * @param id ID of the link
	 * 
	 * @throws Exception
	 */
	public void deleteLink(@WebParam(name = "sid") String sid, @WebParam(name = "id") long id) throws Exception;

	/**
	 * Reindexes(or indexes) a document
	 * 
	 * @param sid Session identifier
	 * @param docId The document id
	 * @param content The content to be used (if null the file is parsed)
	 * 
	 * @throws Exception
	 */
	public void reindex(@WebParam(name = "sid") String sid, @WebParam(name = "docId") long docId,
			@WebParam(name = "content") String content) throws Exception;

	/**
	 * Creates the PDF conversion of the given document. If the conversion was
	 * already created, nothing will happen.
	 * 
	 * @param sid Session identifier
	 * @param docId The document id
	 * @param fileVersion The specific file version(it can be empty)
	 * @throws Exception
	 */
	public void createPdf(@WebParam(name = "sid") String sid, @WebParam(name = "docId") long docId,
			@WebParam(name = "fileVersion") String fileVersion) throws Exception;
}