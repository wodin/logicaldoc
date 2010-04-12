package com.logicaldoc.core.document;

import java.io.File;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.logicaldoc.core.ExtendedAttribute;
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
	 * @param transaction entry to log the event
	 * @throws Exception if an error occurs, this exception is thrown
	 */
	public void checkin(long docId, InputStream fileInputStream, String filename, User user,
			Version.VERSION_TYPE versionType, String versionDesc, boolean immediateIndexing, History transaction)
			throws Exception;

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
	 * @param transaction entry to log the event
	 * @throws Exception if an error occurs, this exception is thrown
	 */
	void checkin(long docId, File file, String filename, User user, VERSION_TYPE versionType, String versionDesc,
			boolean immediateIndexing, History transaction) throws Exception;

	/**
	 * Checks out the given document
	 * 
	 * @param docId the document to be checked out
	 * @param user the user downloading the document
	 * @param transaction entry to log the event
	 * @throws Exception if an error occurs, this exception is thrown
	 */
	public void checkout(long docId, User user, History history) throws Exception;

	/**
	 * Locks the given document
	 * 
	 * @param docId the document to be locked
	 * @param status the lock type (used to populate status attribute of the
	 *        document)
	 * @param user the user requesting the lock
	 * @param transaction entry to log the event
	 * @throws Exception if an error occurs, this exception is thrown
	 */
	public void lock(long docId, int status, User user, History transaction) throws Exception;

	/**
	 * UNChecks out the given document
	 * 
	 * @param docId the document to be unchecked out
	 * @param user the user uncheking the document
	 * @param transaction entry to log the event
	 * @throws Exception if an error occurs, this exception is thrown
	 */
	public void unlock(long docId, User user, History transaction) throws Exception;

	/**
	 * Creates a new document in the parent menu
	 * 
	 * @param file document's file
	 * @param filename document's file name
	 * @param folder the parent folder
	 * @param user the current user
	 * @param locale the document's language
	 * @param immediateIndexing if true the document is immediately indexed
	 * @param transaction entry to log the event
	 * @return The newly created document
	 * @throws Exception if an error occurs, this exception is thrown
	 */
	public Document create(File file, String filename, Menu folder, User user, Locale locale,
			boolean immediateIndexing, History transaction) throws Exception;

	/**
	 * Creates a new Document. Saves the information provided. That also
	 * includes updating the search index for example.
	 * 
	 * @param user
	 * @param filename document's file name
	 * @param folder
	 * @param title If not provided the filename must be used instead
	 * @param file
	 * @param sourceDate
	 * @param source
	 * @param sourceAuthor
	 * @param sourceType
	 * @param coverage
	 * @param locale
	 * @param versionDesc
	 * @param tags
	 * @param immediateIndexing
	 * @param transaction entry to log the event
	 * @return The created document
	 * @throws Exception
	 */
	public Document create(File file, String filename, Menu folder, User user, Locale locale, String title,
			Date sourceDate, String source, String sourceAuthor, String sourceType, String coverage,
			String versionDesc, Set<String> tags, boolean immediateIndexing, History transaction) throws Exception;

	/**
	 * Creates a new Document. Saves the information provided. That also
	 * includes updating the search index for example.
	 * 
	 * @param user
	 * @param folder
	 * @param title If not provided the filename must be used instead
	 * @param file
	 * @param filename document's file name
	 * @param sourceDate
	 * @param source
	 * @param sourceAuthor
	 * @param sourceType
	 * @param coverage
	 * @param locale
	 * @param versionDesc
	 * @param tags
	 * @param templateId
	 * @param extendedAttributes
	 * @param immediateIndexing if true the document is immediately indexed
	 * @param transaction entry to log the event
	 * @return The created document
	 * @throws Exception
	 */
	public Document create(File file, String filename, Menu folder, User user, Locale locale, String title,
			Date sourceDate, String source, String sourceAuthor, String sourceType, String coverage,
			String versionDesc, Set<String> tags, Long templateId, Map<String, ExtendedAttribute> extendedAttributes,
			boolean immediateIndexing, History transaction) throws Exception;

	/**
	 * Creates a new Document. Saves the information provided. That also
	 * includes updating the search index for example.
	 * 
	 * @param user
	 * @param folder
	 * @param title If not provided the filename must be used instead
	 * @param file
	 * @param filename document's file name
	 * @param sourceDate
	 * @param source
	 * @param sourceAuthor
	 * @param sourceType
	 * @param coverage
	 * @param locale
	 * @param versionDesc
	 * @param tags
	 * @param templateId
	 * @param extendedAttributes
	 * @param sourceId
	 * @param object
	 * @param recipient
	 * @param customId
	 * @param immediateIndexing if true the document is immediately indexed
	 * @param transaction entry to log the event
	 * @return The created document
	 * @throws Exception
	 */
	public Document create(File file, String filename, Menu folder, User user, Locale locale, String title,
			Date sourceDate, String source, String sourceAuthor, String sourceType, String coverage,
			String versionDesc, Set<String> tags, Long templateId, Map<String, ExtendedAttribute> extendedAttributes,
			String sourceId, String object, String recipient, String customId, boolean immediateIndexing,
			History transaction) throws Exception;

	/**
	 * Creates a new Document. Saves the information provided. That also
	 * includes updating the search index for example.
	 * 
	 * @param content The document content stream
	 * @param filename The original document file name
	 * @param folder
	 * @param user
	 * @param locale
	 * @param title If not provided the filename must be used instead
	 * @param sourceDate
	 * @param source
	 * @param sourceAuthor
	 * @param sourceType
	 * @param coverage
	 * @param versionDesc
	 * @param tags
	 * @param templateId
	 * @param extendedAttributes
	 * @param sourceId
	 * @param object
	 * @param recipient
	 * @param customId
	 * @param immediateIndexing if true the document is immediately indexed
	 * @param transaction entry to log the event
	 * @return The created document
	 * @throws Exception
	 */
	public Document create(InputStream content, String filename, Menu folder, User user, Locale locale, String title,
			Date sourceDate, String source, String sourceAuthor, String sourceType, String coverage,
			String versionDesc, Set<String> tags, Long templateId, Map<String, ExtendedAttribute> extendedAttributes,
			String sourceId, String object, String recipient, String customId, boolean immediateIndexing,
			History transaction) throws Exception;

	/**
	 * Creates a new Document. Saves the information provided. That also
	 * includes updating the search index for example.
	 * 
	 * @param content The document content stream
	 * @param filename The original document file name
	 * @param folder
	 * @param user
	 * @param locale
	 * @param title If not provided the filename must be used instead
	 * @param sourceDate
	 * @param source
	 * @param sourceAuthor
	 * @param sourceType
	 * @param coverage
	 * @param versionDesc
	 * @param tags
	 * @param templateId
	 * @param extendedAttributes
	 * @param immediateIndexing if true the document is immediately indexed
	 * @param transaction entry to log the event
	 * @return The created document
	 * @throws Exception
	 */
	public Document create(InputStream content, String filename, Menu folder, User user, Locale locale, String title,
			Date sourceDate, String source, String sourceAuthor, String sourceType, String coverage,
			String versionDesc, Set<String> tags, Long templateId, Map<String, ExtendedAttribute> extendedAttributes,
			boolean immediateIndexing, History transaction) throws Exception;

	/**
	 * Creates a new Document. Saves the information provided. That also
	 * includes updating the search index for example.
	 * 
	 * @param content The document content stream
	 * @param filename The original document file name
	 * @param folder
	 * @param user
	 * @param locale
	 * @param title If not provided the filename must be used instead
	 * @param sourceDate
	 * @param source
	 * @param sourceAuthor
	 * @param sourceType
	 * @param coverage
	 * @param versionDesc
	 * @param tags
	 * @param immediateIndexing if true the document is immediately indexed
	 * @param transaction entry to log the event
	 * @return The created document
	 * @throws Exception
	 */
	public Document create(InputStream content, String filename, Menu folder, User user, Locale locale, String title,
			Date sourceDate, String source, String sourceAuthor, String sourceType, String coverage,
			String versionDesc, Set<String> tags, boolean immediateIndexing, History transaction) throws Exception;

	/**
	 * Creates a new Document. Saves the information provided. That also
	 * includes updating the search index for example.
	 * 
	 * @param content
	 * @param filename
	 * @param folder
	 * @param user
	 * @param locale
	 * @param immediateIndexing if true the document is immediately indexed
	 * @param transaction entry to log the event
	 * @return The created document
	 * @throws Exception
	 */
	public Document create(InputStream content, String filename, Menu folder, User user, Locale locale,
			boolean immediateIndexing, History transaction) throws Exception;

	/**
	 * Obtains the document's file
	 * 
	 * @param doc The document representation
	 * @return The document file
	 */
	public File getDocumentFile(Document doc);

	/**
	 * Obtains the document's file
	 * 
	 * @param docId The document's identifier
	 * @return The document file
	 */
	public File getDocumentFile(long docId);

	/**
	 * Obtains the document's file for the specified version
	 * 
	 * @param doc The document representation
	 * @param fileVersion The file version (use null for the latest version)
	 * @return The document file
	 */
	public File getDocumentFile(Document doc, String fileVersion);

	/**
	 * Obtains the document's file for the specified version
	 * 
	 * @param doc The document representation
	 * @param fileVersion The file version (use null for the latest version)
	 * @param suffix The file suffix (use null if you want the exact document
	 *        file)
	 * @return The document file
	 */
	public File getDocumentFile(Document doc, String fileVersion, String suffix);

	/**
	 * Obtains the document's file for the specified version
	 * 
	 * @param docId The document's identifier
	 * @param fileVersion The file version (use null for the latest version)
	 * @return The document file
	 */
	public File getDocumentFile(long docId, String fileVersion);

	/**
	 * Obtains the document's file for the specified version
	 * 
	 * @param docId The document's identifier
	 * @param fileVersion The file version (use null for the latest version)
	 * @param suffix The file suffix (use null if you want the exact document
	 *        file)
	 * @return The document file
	 */
	public File getDocumentFile(long docId, String fileVersion, String suffix);

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
	 * @param originalLocale The original locale of the document
	 * @throws Exception if an error occurs, this exception is thrown
	 */
	public void reindex(Document doc, Locale originalLocale) throws Exception;

	/**
	 * Rename an existing document title/filename.
	 * 
	 * @param doc The document to be renamed
	 * @param user The user requesting the operation
	 * @param newName The new title/filename of the document
	 * @param title True if the title must be renamed, False for the filename
	 * @param transaction entry to log the event
	 * @throws Exception if an error occurs, this exception is thrown
	 */
	public void rename(Document doc, User user, String newName, boolean title, History transaction) throws Exception;

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
	 * @param locale
	 * @param tags
	 * @param sourceId
	 * @param object
	 * @param recipient
	 * @param templateId
	 * @param attributes
	 * @param transaction entry to log the event
	 * @throws Exception if an error occurs, this exception is thrown
	 */
	public void update(Document doc, User user, String title, String source, String sourceAuthor, Date sourceDate,
			String sourceType, String coverage, Locale locale, Set<String> tags, String sourceId, String object,
			String recipient, Long templateId, Map<String, ExtendedAttribute> attributes, History transaction)
			throws Exception;

	/**
	 * Retrieves the full-text document content
	 * 
	 * @param docId The document identifier
	 * @return The value of the 'content' field in the full-text index
	 */
	public String getDocumentContent(long docId);

	/**
	 * Utility method for document removal from index and database update(flag
	 * indexed)
	 */
	public void deleteFromIndex(Document doc);

	/**
	 * Marks the document, with the given docId, as immutable and save the given
	 * document history
	 * 
	 * @param docId
	 * @param user
	 * @param transaction entry to log the event
	 * @throws Exception
	 */
	public void makeImmutable(long docId, User user, History transaction) throws Exception;

	/**
	 * Moves a document to the specified folder. All stores(db, file system,
	 * index) will be consequently altered.
	 * 
	 * @param doc The document to move
	 * @param folder The target folder
	 * @param user
	 * @param transaction entry to log the event
	 * @throws Exception
	 */
	public void moveToFolder(Document doc, Menu folder, User user, History transaction) throws Exception;

	/**
	 * Copy a document to the specified folder.
	 * 
	 * @param doc The document to move
	 * @param folder The target folder
	 * @param user
	 * @param transaction entry to log the event
	 * @return The created document
	 * @throws Exception
	 */
	public Document copyToFolder(Document doc, Menu folder, User user, History transaction) throws Exception;

	/**
	 * Delete a folder and all its sub-folders that a user can delete. After
	 * recovering of all sub-folders inside the folder, will be cancelled all
	 * folders for which the user has the delete permission or there isn't an
	 * immutable document inside it.
	 * 
	 * @param menu Folder to delete
	 * @param user User that wants to delete the folder
	 * @param transaction entry to log the event
	 * @return List of folders that the user cannot delete(permissions, o
	 *         immutable documents presents)
	 * @throws Exception
	 */
	public List<Menu> deleteFolder(Menu menu, User user, History transaction) throws Exception;

	/**
	 * Create a shortcut associated to the given doc to the specified folder.
	 * 
	 * @param doc The document for which will be created the shortcut
	 * @param folder The target folder
	 * @param user
	 * @param transaction entry to log the event
	 * @return The created document
	 * @throws Exception
	 */
	public Document createShortcut(Document doc, Menu folder, User user, History transaction) throws Exception;
	
	
	/**
	 * Move a folder from one parent folder to another
	 * 
	 * @param folderToMove The folder to move
	 * @param destParentFolder The target folder
	 * @param user User that wants to delete the folder
	 * @param transaction entry to log the event
	 * @throws Exception
	 */
	public void moveFolder(Menu folderToMove, Menu destParentFolder, User user, History transaction) throws Exception;

}