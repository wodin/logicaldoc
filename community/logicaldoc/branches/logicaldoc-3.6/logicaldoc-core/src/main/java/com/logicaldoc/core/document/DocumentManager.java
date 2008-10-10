package com.logicaldoc.core.document;

import java.io.File;
import java.io.InputStream;
import java.util.Date;
import java.util.Set;

import com.logicaldoc.core.document.Version.VERSION_TYPE;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.MenuGroup;

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
	 * @param docId
	 *            the document to be checked in
	 * @param fileInputStream
	 *            input stream pointing to the new document version
	 * @param filename
	 *            new filename (can also be the old one)
	 * @param username
	 *            user uploading the new document version
	 * @param versionType
	 *            specifies if this is a new release, a subversion or the old
	 *            version
	 * @param versionDesc
	 *            a change description
	 * @throws Exception
	 *             if an error occurs, this exception is thrown
	 */
	public void checkin(int docId, InputStream fileInputStream,
			String filename, String username, Version.VERSION_TYPE versionType,
			String versionDesc) throws Exception;

	/**
	 * Checks in the given document
	 * 
	 * @param docId
	 *            the document to be checked in
	 * @param file
	 *            of the new document version
	 * @param filename
	 *            new filename (can also be the old one)
	 * @param username
	 *            user uploading the new document version
	 * @param versionType
	 *            specifies if this is a new release, a subversion or the old
	 *            version
	 * @param versionDesc
	 *            a change description
	 * @throws Exception
	 *             if an error occurs, this exception is thrown
	 */
	void checkin(int docId, File file, String filename, String username,
			VERSION_TYPE versionType, String versionDesc) throws Exception;

	/**
	 * Checks out the given document
	 * 
	 * @param docId
	 *            the document to be checked out
	 * @param username
	 *            the user downloading the document
	 * @throws Exception
	 *             if an error occurs, this exception is thrown
	 */
	public void checkout(int docId, String username) throws Exception;

	/**
	 * Creates a new document in the parent menu
	 * 
	 * @param file
	 *            document's file
	 * @param parent
	 *            the parent menu
	 * @param username
	 *            the current username
	 * @param language
	 *            the document's language
	 * @return The newly created document
	 * @throws Exception
	 *             if an error occurs, this exception is thrown
	 */
	public Document create(File file, Menu parent, String username,
			String language) throws Exception;

	/**
	 * Creates a new Document. Saves the information provided. That also
	 * includes updating the search index for example.
	 * 
	 * @param username
	 * @param parent
	 * @param name
	 *            The document name, if null the file name is used
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
	public Document create(File file, Menu parent, String username,
			String language, String name, Date sourceDate, String source,
			String sourceAuthor, String sourceType, String coverage,
			String versionDesc, Set<String> keywords, Set<MenuGroup> groups)
			throws Exception;

	/**
	 * Creates a new Document. Saves the information provided. That also
	 * includes updating the search index for example.
	 * 
	 * @param content
	 *            The document content stream
	 * @param filename
	 *            The original document file name
	 * @param parent
	 * @param username
	 * @param language
	 * @param name
	 *            The document name, if null the filename is used
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
	public Document create(InputStream content, String filename, Menu parent,
			String username, String language, String name, Date sourceDate,
			String source, String sourceAuthor, String sourceType,
			String coverage, String versionDesc, Set<String> keywords,
			Set<MenuGroup> groups) throws Exception;

	/**
	 * Creates a new folder in the parent menu
	 * 
	 * @param parent
	 *            The parent menu
	 * @param name
	 *            The folder name
	 * @return The newly created folder
	 */
	public Menu createFolder(Menu parent, String name);

	/**
	 * Creates the folder for the specified path. All unexisting nodes specified
	 * in the path will be created.
	 * 
	 * @param parent
	 *            The parent menu
	 * @param path
	 *            The folder path(for example /dog/cat/mouse)
	 * 
	 * @return The created folder
	 */
	public Menu createFolders(Menu parent, String path);

	/**
	 * Deletes the given menu and all sub-menus; does not perform an access
	 * check
	 * 
	 * @param menuId
	 *            menu to be deleted; can be a folder, menu or document
	 * @param username
	 *            the user requesting the delete action
	 * @throws Exception
	 *             if an error occurs, this exception is thrown
	 */
	public void delete(int menuId, String username) throws Exception;

	/**
	 * Obtains the document's file
	 * 
	 * @param doc
	 *            The document representation
	 * @return The document file
	 */
	public File getDocumentFile(Document doc);

	/**
	 * Obtains the document's file for the specified fersion
	 * 
	 * @param doc
	 *            The document representation
	 * @param version
	 *            The document version (use null for the latest version)
	 * @return The document file
	 */
	public File getDocumentFile(Document doc, String version);

	/**
	 * Retrieves the document's content as a string
	 * 
	 * @param doc
	 *            The document representation
	 * @return The document's content
	 */
	public String getDocumentContent(Document doc);

	/**
	 * Reindexes an existing document in the full-text index.
	 * 
	 * @param doc
	 *            The document to be reindexed
	 * @param originalLanguage
	 *            The original language of the document
	 * @throws Exception
	 *             if an error occurs, this exception is thrown
	 */
	public void reindex(Document doc, String originalLanguage) throws Exception;

	/**
	 * Updates an existing document and reindex it
	 * 
	 * @param doc
	 *            The document to be updated
	 * @param username
	 * @param name
	 * @param source
	 * @param sourceAuthor
	 * @param sourceDate
	 * @param sourceType
	 * @param coverage
	 * @param language
	 * @param keywords
	 * @throws Exception
	 */
	public void update(Document doc, String username, String name,
			String source, String sourceAuthor, Date sourceDate,
			String sourceType, String coverage, String language,
			Set<String> keywords) throws Exception;

	/**
	 * Retrieves the full-text document content
	 * 
	 * @param docId
	 *            The document identifier
	 * @return The value of the 'content' field in the full-text index
	 */
	public String getDocumentContent(int docId);
}