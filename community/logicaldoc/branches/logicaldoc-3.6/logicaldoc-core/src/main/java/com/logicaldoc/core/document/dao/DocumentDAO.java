package com.logicaldoc.core.document.dao;

import java.util.Collection;
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
	public boolean delete(int docId);

	/**
	 * This method deletes a document.
	 * 
	 * @param menuId of the document which should be deleted.
	 */
	public boolean deleteByMenuId(int menuId);

	/**
	 * This method finds a document by primarykey.
	 * 
	 * @param docId Primarykey of the document.
	 * @return Document with given primarykey.
	 */
	public Document findByPrimaryKey(int docId);

	/**
	 * This method finds a document by its menuId.
	 * 
	 * @param menuId MenuId of the document.
	 * @return Document with specified menuId.
	 */
	public Document findByMenuId(int menuId);

	/**
	 * This method selects all documents.
	 */
	public Collection<Document> findAll();

	/**
	 * Finds all documents for an user.
	 * 
	 * @param username Name of the user.
	 * @return Collection of all documentId required for the specified user.
	 */
	public Collection<Integer> findByUserName(String username);
	
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
	 * @param username Name of the user.
	 * @return Collection of the latests Documents changed by the specified user.
	 */
	public Collection<Document> findLastModifiedByUserName(String username, int maxElements);

	/**
	 * This method finds all MenuIds by a keyword.
	 * 
	 * @param keyword Keyword of the document.
	 * @return Document with specified keyword.
	 */
	public Collection<Integer> findMenuIdByKeyword(String keyword);
	
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
}
