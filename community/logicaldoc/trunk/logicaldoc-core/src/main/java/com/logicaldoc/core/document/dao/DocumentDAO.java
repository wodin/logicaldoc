package com.logicaldoc.core.document.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.logicaldoc.core.document.Document;

/**
 * This class is a DAO-service for documents.
 * 
 * @author Michael Scholz
 * @author Marco Meschieri
 * @version 1.0
 */
public interface DocumentDAO {
	/**
	 * This method persists a document object.
	 * 
	 * @param doc Document to be stored.
	 * @return True if successfully stored in a database.
	 */
	public boolean store(Document doc);

	/**
	 * This method deletes a document.
	 * 
	 * @param docId DocID of the document which should be delete.
	 */
	public boolean delete(long docId);

	/**
	 * This method finds a document by primarykey.
	 * 
	 * @param docId Primarykey of the document.
	 * @return Document with given primarykey.
	 */
	public Document findByPrimaryKey(long docId);

	/**
	 * This method selects all documents.
	 */
	public Collection<Document> findAll();

	/**
	 * Finds all documents for an user.
	 * 
	 * @param userId ID of the user.
	 * @return Collection of all documentId required for the specified user.
	 */
	public Collection<Long> findByUserId(long userId);

	/**
	 * Finds all document ids inside the given folder.
	 * 
	 * @param folderId Folder identifier
	 * @return Collection of all document id in the folder.
	 */
	public Collection<Long> findDocIdByFolder(long folderId);

	/**
	 * Finds all documents inside the given folder.
	 * 
	 * @param folderId Folder identifier
	 * @return Collection of all documents in the folder.
	 */
	public Collection<Document> findByFolder(long folderId);

	/**
	 * Finds all documents checked-out for an user.
	 * 
	 * @param username Name of the user.
	 * @return Collection of all Documents checked out by the specified user.
	 */
	public Collection<Document> findCheckoutByUserName(String username);

	/**
	 * Finds a max number of documents last modified by an user.
	 * 
	 * @param userId ID of the user.
	 * @return Collection of the last documents changed by the specified user.
	 */
	public List<Document> findLastModifiedByUserId(long userId, int maxResults);

	/**
	 * Finds the last downloaded documents by the given user
	 * 
	 * @param userId id of the user
	 * @param maxResults maximum number of returned elements
	 * @return
	 */
	public List<Document> findLastDownloadsByUserId(long userId, int maxResults);

	/**
	 * This method finds all Doc Ids by a keyword.
	 * 
	 * @param keyword Keyword of the document.
	 * @return Document with specified keyword.
	 */
	public Collection<Long> findDocIdByKeyword(String keyword);

	/**
	 * Converts the passed string into a collection of keywords
	 * 
	 * @param words the string to be considered
	 * @return The resulting keywords collection
	 */
	public Set<String> toKeywords(String words);

	/**
	 * This method selects all keywords starting with a specified letter.
	 * 
	 * @param letter - First letter of the wanted keywords.
	 */
	public Collection<String> findKeywords(String firstLetter, String username);

	/**
	 * This method selects all keywords and counts the occurrences.
	 */
	public Map<String, Integer> findAllKeywords();

	/**
	 * Finds authorized documents for a user having a specified keyword.
	 * 
	 * @param username Name of the user.
	 * @param keyword Keyword of the document
	 * @return Collection of found menus.
	 */
	public Collection<Document> findByUserNameAndKeyword(String username, String keyword);

	/**
	 * Finds authorized documents ids for a user having a specified keyword.
	 * 
	 * @param username Name of the user.
	 * @param keyword Keyword of the document
	 * @return Set of found ids.
	 */
	public Set<Long> findDocIdByUsernameAndKeyword(String username, String keyword);
}