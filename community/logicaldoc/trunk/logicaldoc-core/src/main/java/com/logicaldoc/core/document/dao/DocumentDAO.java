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
	 * This method finds a document by ID.
	 * 
	 * @param docId ID of the document.
	 * @return Document with given ID.
	 */
	public Document findById(long docId);

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
	public Collection<String> findKeywords(String firstLetter, long userId);

	/**
	 * This method selects all keywords and counts the occurrences.
	 */
	public Map<String, Integer> findAllKeywords();

	/**
	 * Finds authorized documents for a user having a specified keyword.
	 * 
	 * @param userId ID of the user.
	 * @param keyword Keyword of the document
	 * @return Collection of found menus.
	 */
	public Collection<Document> findByUserIdAndKeyword(long userId, String keyword);

	/**
	 * Finds authorized documents ids for a user having a specified keyword.
	 * 
	 * @param userId ID of the user.
	 * @param keyword Keyword of the document
	 * @return Set of found ids.
	 */
	public Set<Long> findDocIdByUserIdAndKeyword(long userId, String keyword);

	/**
	 * This method enlists documents linked to the given document.
	 * <p>
	 * <b>Important:</b> The attribute <code>direction</code> defines the
	 * search logic as follows:
	 * <ul>
	 * <li>1: docId will be compared to link's document1</li>
	 * <li>2: docId will be compared to link's document2</li>
	 * <li>null: docId will be compared to both document1 and document2</li>
	 * </ul>
	 * 
	 * @param docId All documents linked to this one will be searched
	 * @param linkType Type of the link (optional)
	 * @param direction if 1 docId will be compared to link's document1, id 2
	 *        docId will be compared to link's document2, if null docId will be
	 *        compared to both document1 and document2 of the link.
	 * @return The collection of linked documents
	 */
	public Collection<Document> findLinkedDocuments(long docId, String linkType, Integer direction);

	/**
	 * Finds that document that lies under a specific folder (given by the id)
	 * an with a given fileName(like operator is used)
	 * 
	 * @param folderId
	 * @param fileName
	 * @return
	 */
	public Collection<Document> findByFileNameAndParentFolderId(long folderId, String fileName);

	/**
	 * Finds that document that lies under a specific folder (given by the id)
	 * an with a given title(like operator is used)
	 * 
	 * @param folderId
	 * @param title
	 * @return
	 */
	public Collection<Document> findByTitleAndParentFolderId(long folderId, String title);

	/**
	 * Initializes lazy loaded collections
	 * 
	 * @param doc The document to be initialized
	 */
	public void initialize(Document doc);

	/**
	 * Obtains the total size of the archive, that is the sum of sizes of all
	 * documents
	 * 
	 * @param computeDeleted If true, even deleted documents are considered
	 * @return
	 */
	public long getTotalSize(boolean computeDeleted);

	/**
	 * Gets the collection of deleted document ids
	 */
	public Collection<Long> getDeletedDocIds();
	
	/**
	 * Counts the number of documents
	 * 
	 * @param computeDeleted If true, even deleted documents are considered
	 */
	public long getDocumentCount(boolean computeDeleted);
	
	/**
	 * Finds all documents by the indexed state. Order by ascending lastModifed
	 * 
	 * @param indexed the indexed property
	 * @return Collection of all documents
	 */
	public List<Document> findByIndexed(int indexed);
}