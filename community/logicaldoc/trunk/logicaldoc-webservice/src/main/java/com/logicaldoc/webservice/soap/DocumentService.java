package com.logicaldoc.webservice.soap;

import javax.activation.DataHandler;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.logicaldoc.webservice.doc.WSDoc;
import com.logicaldoc.webservice.model.WSDocument;
import com.logicaldoc.webservice.model.WSLink;

/**
 * Document Web Service definition interface
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 5.2
 */
@WSDoc(description = "documents handling and CRUD operations")
@WebService(name = "Document", serviceName = "Document", targetNamespace = "http://ws.logicaldoc.com")
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
	@WebMethod
	@WebResult(name = "document")
	@WSDoc(description = "creates a new document; the user can completely customize the document through a value object containing the document's metadata; returns the newly created document.")
	public WSDocument create(@WebParam(name = "sid") String sid, @WebParam(name = "document") WSDocument document,
			@WSDoc(description = "the raw content of the file") @WebParam(name = "content") DataHandler content)
			throws Exception;

	/**
	 * Deletes an existing document with the given identifier.
	 * 
	 * @param sid Session identifier
	 * @param docId The document id
	 * @throws Exception
	 */
	@WebMethod
	@WSDoc(description = "deletes an existing document with the given identifier")
	public void delete(@WebParam(name = "sid") String sid, @WebParam(name = "docId") long docId) throws Exception;

	/**
	 * Locks an existing document with the given identifier.
	 * 
	 * @param sid Session identifier
	 * @param docId The document id
	 * @throws Exception
	 */
	@WebMethod
	@WSDoc(description = "locks an existing document with the given identifier")
	public void lock(@WebParam(name = "sid") String sid, @WebParam(name = "docId") long docId) throws Exception;

	/**
	 * Unlocks an existing document with the given identifier.
	 * 
	 * @param sid Session identifier
	 * @param docId The document id
	 * @throws Exception
	 */
	@WebMethod
	@WSDoc(description = "unlocks an existing document with the given identifier")
	public void unlock(@WebParam(name = "sid") String sid, @WebParam(name = "docId") long docId) throws Exception;

	/**
	 * Renames the title of an existing document with the given identifier.
	 * 
	 * @param sid Session identifier
	 * @param docId The document id
	 * @param name The new document title
	 * @throws Exception
	 */
	@WebMethod
	@WSDoc(description = "renames the title of an existing document with the given identifier")
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
	@WebMethod
	@WSDoc(description = "renames the filename of an existing document with the given identifier")
	public void renameFile(@WebParam(name = "sid") String sid, @WebParam(name = "docId") long docId,
			@WebParam(name = "name") String name) throws Exception;

	/**
	 * Moves an existing document with the given identifier.
	 * 
	 * @param sid Session identifier
	 * @param docId The document id
	 * @param folderId Identifier of the new document's folder
	 * @throws Exception
	 */
	@WebMethod
	@WSDoc(description = "moves an existing document with the given identifier")
	public void move(@WebParam(name = "sid") String sid, @WebParam(name = "docId") long docId,
			@WSDoc(description = "identifier of the new document's folder") @WebParam(name = "folderId") long folderId)
			throws Exception;

	/**
	 * Gets the metadata of an existing document with the given identifier.
	 * 
	 * @param sid Session identifier
	 * @param docId The document id
	 * @return A value object containing the document's metadata.
	 * @throws Exception
	 */
	@WebMethod
	@WebResult(name = "document")
	@WSDoc(description = "gets the metadata of an existing document with the given identifier; returns the document's representation")
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
	@WebMethod
	@WebResult(name = "document")
	@WSDoc(description = "gets document metadata of an existing document with the given custom identifier")
	public WSDocument getDocumentByCustomId(@WebParam(name = "sid") String sid,
			@WebParam(name = "customId") String customId) throws Exception;

	/**
	 * Gets document metadata of a collection of existing documents with the
	 * given identifiers.
	 * 
	 * @param sid Session identifier
	 * @param docIds The documents ids
	 * @return the list of documents
	 * @throws Exception
	 */
	@WebMethod
	@WebResult(name = "documents")
	@WSDoc(description = "gets document metadata of a collection of existing documents with the given identifiers; returns an array of WSDocument")
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
	@WebMethod
	@WebResult(name = "aliases")
	@WSDoc(description = "gets the aliases of the given document; returns an array of WSDocument that are aliases")
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
	@WebMethod
	@WSDoc(description = "updates an existing document with the value object containing the document's metadata")
	public void update(@WebParam(name = "sid") String sid, @WebParam(name = "document") WSDocument document)
			throws Exception;

	/**
	 * Gets the content of an existing document with the given identifier
	 * 
	 * @param sid Session identifier
	 * @param docId The document id
	 * @return The requested document's binary
	 * @throws Exception
	 */
	@WebMethod
	@WSDoc(description = "gets the content of an existing document with the given identifier; returns the raw content of the file")
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
	@WebMethod
	@WebResult(name = "text")
	@WSDoc(description = "gets the document's text stored in the full-text index")
	public String getExtractedText(@WebParam(name = "sid") String sid, @WebParam(name = "docId") long docId)
			throws Exception;

	/**
	 * Gets the content of a specific version of a document
	 * 
	 * @param sid Session identifier
	 * @param docId The document id
	 * @param version The specific version(it can be empty)
	 * @return The requested version's binary
	 * @throws Exception
	 */
	@WebMethod
	@WSDoc(description = "gets the content of a specific version of a document; returns the raw content of the file")
	public DataHandler getVersionContent(@WebParam(name = "sid") String sid, @WebParam(name = "docId") long docId,
			@WSDoc(description = "the version to retrieve, eg: '1.0', '2.3'") @WebParam(name = "version") String version)
			throws Exception;

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
	@WebMethod
	@WSDoc(description = "gets the content of a resource associated to the given document; returns the raw content of the file")
	public DataHandler getResource(
			@WebParam(name = "sid") String sid,
			@WebParam(name = "docId") long docId,
			@WSDoc(description = "the file version to retrieve, eg: '1.0', '2.3'") @WebParam(name = "fileVersion") String fileVersion,
			@WSDoc(description = "suffix specification(it cannot be empty, use 'conversion.pdf' to get the PDF conversion)") @WebParam(name = "suffix") String suffix)
			throws Exception;

	/**
	 * Checks out an existing document with the given identifier.
	 * 
	 * @param sid Session identifier
	 * @param docId The document id
	 * @throws Exception
	 */
	@WebMethod
	@WSDoc(description = "checks out an existing document with the given identifier")
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
	@WebMethod
	@WSDoc(description = "checks in an existing document to create a new version")
	public void checkin(
			@WebParam(name = "sid") String sid,
			@WebParam(name = "docId") long docId,
			@WebParam(name = "comment") String comment,
			@WebParam(name = "filename") String filename,
			@WSDoc(description = "true if this is a new release(eg: 2.0) rather than a subversion(eg: 1.1)") @WebParam(name = "release") boolean release,
			@WebParam(name = "content") DataHandler content) throws Exception;

	/**
	 * Creates a new document or updates an existing one.
	 * 
	 * @param sid Session identifier
	 * @param docId The document id (optional)
	 * @param folderId The folder id (optional)
	 * @param release True if this is a major release(eg: 2.0) rather than a
	 *        minor release(eg: 1.12)
	 * @param filename The document file name
	 * @param language The language for the document
	 * @param content The document's binary content
	 * @return The created/updated document's ID
	 * @throws Exception
	 */
	@WebMethod
	@WebResult(name = "docId")
	@WSDoc(description = "creates a new document or updates an existing one; returns the newly created document's ID")
	public long upload(
			@WebParam(name = "sid") String sid,
			@WSDoc(description = "id of the document to update", required = false) @WebParam(name = "docId") Long docId,
			@WSDoc(description = "the folder's id, used in case of creation", required = false) @WebParam(name = "folderId") Long folderId,
			@WSDoc(description = "true if this is a major release(eg: 2.0) rather than a minor release(eg: 1.12)") @WebParam(name = "release") boolean release,
			@WSDoc(description = "used in case of creation", required = false) @WebParam(name = "filename") String filename,
			@WebParam(name = "language") String language,
			@WSDoc(description = "raw content of the file") @WebParam(name = "content") DataHandler content)
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
	@WebMethod
    @WSDoc(description = "uploads a new resource attached to the given document. If the resource already exists it is overwritten")
	public void uploadResource(
			@WebParam(name = "sid") String sid,
			@WebParam(name = "docId") long docId,
			@WSDoc(description = "the specific file version", required = false) @WebParam(name = "fileVersion") String fileVersion,
			@WSDoc(description = "suffix specification(it cannot be empty, use 'conversion.pdf' to put the PDF conversion)") @WebParam(name = "suffix") String suffix,
			@WSDoc(description = "taw content of the file") @WebParam(name = "content") DataHandler content)
			throws Exception;

	/**
	 * Tests if a document is readable.
	 * 
	 * @param sid Session identifier
	 * @param docId The document id
	 * @return True if the identifier denotes a document, otherwise false.
	 * @throws Exception
	 */
	@WebMethod
	@WSDoc(description = "tests if a document is readable")
	public boolean isReadable(@WebParam(name = "sid") String sid, @WebParam(name = "docId") long docId)
			throws Exception;

	/**
	 * Restores a deleted document.
	 * 
	 * @param sid Session identifier
	 * @param docId The document id
	 * @param folderId Id of the folder in which the document must be restored
	 * @throws Exception
	 */
	@WebMethod
	@WSDoc(description = "restores a deleted document")
	public void restore(@WebParam(name = "sid") String sid, @WebParam(name = "docId") long docId,
			@WSDoc(description = "id of the folder in which the document must be restored") @WebParam(name = "folderId") long folderId) throws Exception;

	/**
	 * Gets the version history of an existing document with the given
	 * identifier.
	 * 
	 * @param sid Session identifier
	 * @param docId The document id
	 * @return Array of versions
	 * @throws Exception
	 */
	@WebMethod
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
	@WebMethod
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
	@WebMethod
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
	@WebMethod
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
	@WebMethod
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
	@WebMethod
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
	@WebMethod
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
	@WebMethod
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
	@WebMethod
	public void createPdf(@WebParam(name = "sid") String sid, @WebParam(name = "docId") long docId,
			@WebParam(name = "fileVersion") String fileVersion) throws Exception;
}