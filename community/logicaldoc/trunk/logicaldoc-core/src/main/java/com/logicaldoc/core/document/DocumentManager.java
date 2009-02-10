package com.logicaldoc.core.document;

import java.io.File;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.logicaldoc.core.document.Version.VERSION_TYPE;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.User;

/**
 * A general manager for documents handling issues
 * 
 * @author Marco Meschieri - Logical Objects
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
	 * @param immediateIndexing if true the document is immediately indexed
	 * @throws Exception if an error occurs, this exception is thrown
	 */
	public void checkin(long docId, InputStream fileInputStream, String filename, User user,
			Version.VERSION_TYPE versionType, String versionDesc, boolean immediateIndexing) throws Exception;

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
	 * @param immediateIndexing if true the document is immediately indexed
	 * @throws Exception if an error occurs, this exception is thrown
	 */
	void checkin(long docId, File file, String filename, User user, VERSION_TYPE versionType, String versionDesc,
			boolean immediateIndexing) throws Exception;

	/**
	 * Checks out the given document
	 * 
	 * @param docId the document to be checked out
	 * @param user the user downloading the document
	 * @throws Exception if an error occurs, this exception is thrown
	 */
	public void checkout(long docId, User user) throws Exception;

	/**
	 * UNChecks out the given document
	 * 
	 * @param docId the document to be unchecked out
	 * @param user the user uncheking the document
	 * @throws Exception if an error occurs, this exception is thrown
	 */
	public void uncheckout(long docId, User user) throws Exception;

	/**
	 * Creates a new document in the parent menu
	 * 
	 * @param file document's file
	 * @param folder the parent folder
	 * @param user the current user
	 * @param language the document's language
	 * @param immediateIndexing if true the document is immediately indexed
	 * @return The newly created document
	 * @throws Exception if an error occurs, this exception is thrown
	 */
	public Document create(File file, Menu folder, User user, String language, boolean immediateIndexing)
			throws Exception;

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
	 * @param immediateIndexing
	 * @return The created document
	 * @throws Exception
	 */
	public Document create(File file, Menu folder, User user, String language, String title, Date sourceDate,
			String source, String sourceAuthor, String sourceType, String coverage, String versionDesc,
			Set<String> keywords, boolean immediateIndexing) throws Exception;

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
	 * @param templateId
	 * @param extendedAttributes
	 * @param immediateIndexing if true the document is immediately indexed
	 * @return The created document
	 * @throws Exception
	 */
	public Document create(File file, Menu folder, User user, String language, String title, Date sourceDate,
			String source, String sourceAuthor, String sourceType, String coverage, String versionDesc,
			Set<String> keywords, Long templateId, Map<String, String> extendedAttributes, boolean immediateIndexing)
			throws Exception;

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
	 * @param templateId
	 * @param extendedAttributes
	 * @param sourceId
	 * @param object
	 * @param immediateIndexing if true the document is immediately indexed
	 * @return The created document
	 * @throws Exception
	 */
	public Document create(File file, Menu folder, User user, String language, String title, Date sourceDate,
			String source, String sourceAuthor, String sourceType, String coverage, String versionDesc,
			Set<String> keywords, Long templateId, Map<String, String> extendedAttributes, String sourceId,
			String object, String recipient, boolean immediateIndexing) throws Exception;

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
	 * @param templateId
	 * @param extendedAttributes
	 * @param sourceId
	 * @param object
	 * @param recipient
	 * @param immediateIndexing if true the document is immediately indexed
	 * @return The created document
	 * @throws Exception
	 */
	public Document create(InputStream content, String filename, Menu folder, User user, String language, String title,
			Date sourceDate, String source, String sourceAuthor, String sourceType, String coverage,
			String versionDesc, Set<String> keywords, Long templateId, Map<String, String> extendedAttributes,
			String sourceId, String object, String recipient, boolean immediateIndexing) throws Exception;

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
	 * @param templateId
	 * @param extendedAttributes
	 * @param immediateIndexing if true the document is immediately indexed
	 * @return The created document
	 * @throws Exception
	 */
	public Document create(InputStream content, String filename, Menu folder, User user, String language, String title,
			Date sourceDate, String source, String sourceAuthor, String sourceType, String coverage,
			String versionDesc, Set<String> keywords, Long templateId, Map<String, String> extendedAttributes,
			boolean immediateIndexing) throws Exception;

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
	 * @param immediateIndexing if true the document is immediately indexed
	 * @return The created document
	 * @throws Exception
	 */
	public Document create(InputStream content, String filename, Menu folder, User user, String language, String title,
			Date sourceDate, String source, String sourceAuthor, String sourceType, String coverage,
			String versionDesc, Set<String> keywords, boolean immediateIndexing) throws Exception;

	/**
	 * Creates a new Document. Saves the information provided. That also
	 * includes updating the search index for example.
	 * 
	 * @param content
	 * @param filename
	 * @param folder
	 * @param user
	 * @param language
	 * @param immediateIndexing if true the document is immediately indexed
	 * @return
	 * @throws Exception
	 */
	public Document create(InputStream content, String filename, Menu folder, User user, String language,
			boolean immediateIndexing) throws Exception;

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
	 * @param fileVersion The file version (use null for the latest version)
	 * @return The document file
	 */
	public File getDocumentFile(Document doc, String fileVersion);

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
	 * Rename an existing document filename.
	 * 
	 * @param doc The document to be renamed
	 * @param user The user requesting the operation
	 * @param newFilename The new filename of the document
	 * @throws Exception if an error occurs, this exception is thrown
	 */
	public void rename(Document doc, User user, String newFilename) throws Exception;

	/**
	 * Updates an existing document and marks it to be re-indexed
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
	 * @param sourceId
	 * @param object
	 * @param recipient
	 * @param immediateIndexing If true the document is immediately indexed
	 * @throws Exception
	 */
	public void update(Document doc, User user, String title, String source, String sourceAuthor, Date sourceDate,
			String sourceType, String coverage, String language, Set<String> keywords, String sourceId, String object,
			String recipient) throws Exception;

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
	 * Marks the document, with the given docId, as immutable and save the given
	 * reason on the document history
	 * 
	 * @param docId
	 * @param user
	 * @param reason
	 * @throws Exception
	 */
	public void makeImmutable(long docId, User user, String reason) throws Exception;

	/**
	 * Moves a document to the specified folder. All stores(db, file system,
	 * index) will be consequently altered.
	 * 
	 * @param doc The document to move
	 * @param folder The target folder
	 * @throws Exception
	 */
	public void moveToFolder(Document doc, Menu folder) throws Exception;

	/**
	 * Copy a document to the specified folder.
	 * 
	 * @param doc The document to move
	 * @param folder The target folder
	 * @return The created document
	 * @throws Exception
	 */
	public Document copyToFolder(Document doc, Menu folder, User user) throws Exception;

	/**
	 * Delete a folder and all its sub-folders that a user can delete. After
	 * recovering of all sub-folders inside the folder, will be cancelled all
	 * folders for which the user has the delete permission or there isn't an
	 * immutable document inside it.
	 * 
	 * @param menu Folder to delete
	 * @param user User that wants to delete the folder
	 * @return List of folders that the user cannot delete(permissions, o
	 *         immutable documents presents)
	 * @throws Exception
	 */
	public List<Menu> deleteFolder(Menu menu, User user) throws Exception;
}