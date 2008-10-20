package com.logicaldoc.core.document;

import java.io.File;
import java.io.InputStream;
import java.util.Date;
import java.util.Set;

import com.logicaldoc.core.document.Version.VERSION_TYPE;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.User;

/**
 * A general manager for documents handling issues
 * 
 * @author Marco Meschieri
 * @version $Id:$
 * @since 3.5
 */
public interface DocumentManager {

	/**
	 * Checks in the given document
	 * 
	 * @param docId the document to be checked in
	 * @param fileInputStream input stream pointing to the new document version
	 * @param filename new filename (can also be the old one)
	 * @param user user uploading the new document version
	 * @param versionType specifies if this is a new release, a subversion or
	 *        the old version
	 * @param versionDesc a change description
	 * @throws Exception if an error occurs, this exception is thrown
	 */
	public void checkin(long docId, InputStream fileInputStream, String filename, User user,
			Version.VERSION_TYPE versionType, String versionDesc) throws Exception;

	/**
	 * Checks in the given document
	 * 
	 * @param docId the document to be checked in
	 * @param file of the new document version
	 * @param filename new filename (can also be the old one)
	 * @param user user uploading the new document version
	 * @param versionType specifies if this is a new release, a subversion or
	 *        the old version
	 * @param versionDesc a change description
	 * @throws Exception if an error occurs, this exception is thrown
	 */
	void checkin(long docId, File file, String filename, User user, VERSION_TYPE versionType, String versionDesc)
			throws Exception;

	/**
	 * Checks out the given document
	 * 
	 * @param docId the document to be checked out
	 * @param user the user downloading the document
	 * @throws Exception if an error occurs, this exception is thrown
	 */
	public void checkout(long docId, User user) throws Exception;

	/**
	 * Creates a new document in the parent menu
	 * 
	 * @param file document's file
	 * @param folder the parent folder
	 * @param user the current user
	 * @param language the document's language
	 * @return The newly created document
	 * @throws Exception if an error occurs, this exception is thrown
	 */
	public Document create(File file, Menu folder, User user, String language) throws Exception;

	/**
	 * Creates a new Document. Saves the information provided. That also
	 * includes updating the search index for example.
	 * 
	 * @param user
	 * @param folder
	 * @param title If not provided the filename must be used instead
	 * @param file
	 * @param sourceDate
	 * @param source
	 * @param sourceAuthor
	 * @param sourceType
	 * @param coverage
	 * @param language
	 * @param versionDesc
	 * @param keywords
	 * @return The created document
	 * @throws Exception
	 */
	public Document create(File file, Menu folder, User user, String language, String title, Date sourceDate,
			String source, String sourceAuthor, String sourceType, String coverage, String versionDesc,
			Set<String> keywordso) throws Exception;

	/**
	 * Creates a new Document. Saves the information provided. That also
	 * includes updating the search index for example.
	 * 
	 * @param content The document content stream
	 * @param filename The original document file name
	 * @param folder
	 * @param user
	 * @param language
	 * @param title If not provided the filename must be used instead
	 * @param sourceDate
	 * @param source
	 * @param sourceAuthor
	 * @param sourceType
	 * @param coverage
	 * @param versionDesc
	 * @param keywords
	 * @param groups
	 * @return The created document
	 * @throws Exception
	 */
	public Document create(InputStream content, String filename, Menu folder, User user, String language,
			String title, Date sourceDate, String source, String sourceAuthor, String sourceType, String coverage,
			String versionDesc, Set<String> keywords) throws Exception;

	/**
	 * Obtains the document's file
	 * 
	 * @param doc The document representation
	 * @return The document file
	 */
	public File getDocumentFile(Document doc);

	/**
	 * Obtains the document's file for the specified version
	 * 
	 * @param doc The document representation
	 * @param version The document version (use null for the latest version)
	 * @return The document file
	 */
	public File getDocumentFile(Document doc, String version);

	/**
	 * Retrieves the document's content as a string
	 * 
	 * @param doc The document representation
	 * @return The document's content
	 */
	public String getDocumentContent(Document doc);

	/**
	 * Reindexes an existing document in the full-text index.
	 * 
	 * @param doc The document to be reindexed
	 * @param originalLanguage The original language of the document
	 * @throws Exception if an error occurs, this exception is thrown
	 */
	public void reindex(Document doc, String originalLanguage) throws Exception;

	/**
	 * Updates an existing document and reindex it
	 * 
	 * @param doc The document to be updated
	 * @param user
	 * @param title
	 * @param source
	 * @param sourceAuthor
	 * @param sourceDate
	 * @param sourceType
	 * @param coverage
	 * @param language
	 * @param keywords
	 * @throws Exception
	 */
	public void update(Document doc, User user, String title, String source, String sourceAuthor,
			Date sourceDate, String sourceType, String coverage, String language, Set<String> keywords)
			throws Exception;

	/**
	 * Retrieves the full-text document content
	 * 
	 * @param docId The document identifier
	 * @return The value of the 'content' field in the full-text index
	 */
	public String getDocumentContent(long docId);

	/**
	 * Deletes the document from DB, index and filesystem
	 * 
	 * @param docId
	 * @throws Exception
	 */
	public void delete(long docId) throws Exception;

	/**
	 * Moves a document to the specified folder. All stores(db, file system,
	 * index) will be consequently altered.
	 * 
	 * @param doc The document to move
	 * @param folder The target folder
	 * @throws Exception 
	 */
	public void moveToFolder(Document doc, Menu folder) throws Exception;
}