package com.logicaldoc.core.document.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.logicaldoc.core.PersistentObjectDAO;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.History;

/**
 * This class is a DAO-service for documents.
 * 
 * @author Michael Scholz
 * @author Marco Meschieri
 * @version 1.0
 */
public interface DocumentDAO extends PersistentObjectDAO<Document> {
	/**
	 * This method finds a document by the custom ID.
	 * 
	 * @param customId custom ID of the document.
	 * @return Document with given ID.
	 */
	public Document findByCustomId(String customId);

	/**
	 * Finds all documents for an user.
	 * 
	 * @param userId ID of the user.
	 * @return Collection of all documentId required for the specified user.
	 */
	public List<Long> findByUserId(long userId);

	/**
	 * Finds all document ids inside the given folder.
	 * 
	 * @param folderId Folder identifier
	 * @return Collection of all document id in the folder.
	 */
	public List<Long> findDocIdByFolder(long folderId);

	/**
	 * Finds all documents inside the given folder.
	 * 
	 * @param folderId Folder identifier
	 * @return Collection of all documents in the folder.
	 */
	public List<Document> findByFolder(long folderId);

	/**
	 * Finds all documents locked by a user.
	 * 
	 * @param userId Id of the user.
	 * @return Collection of all Documents locked by the specified user.
	 */
	public List<Document> findLockedByUserId(long userId);

	/**
	 * Finds all document of the specified status and locked by the specified
	 * user
	 * 
	 * @param userId The user id(optional)
	 * @param status The status code(optional)
	 * @return Collection of all Documents locked by the specified user and of
	 *         the specified status.
	 */
	public List<Document> findByLockUserAndStatus(Long userId, Integer status);

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
	 * This method finds all Doc Ids by a tag.
	 * 
	 * @param tag Tag of the document.
	 * @return Document with specified tag.
	 */
	public List<Long> findDocIdByTag(String tag);

	/**
	 * This method selects all tags and counts the occurrences.
	 */
	public Map<String, Integer> findTags(String firstLetter);

	/**
	 * Searches for all tags,
	 * 
	 * @param firstLetter Optional first letter hint
	 * @return The list of all tags in the system
	 */
	public List<Object> findAllTags(String firstLetter);

	/**
	 * Finds authorized documents for a user having a specified tag.
	 * 
	 * @param userId ID of the user.
	 * @param tag Tag of the document
	 * @param max Optional, defines the maximum records number
	 * @return Collection of found menus.
	 */
	public List<Document> findByUserIdAndTag(long userId, String tag, Integer max);

	/**
	 * Finds authorized documents ids for a user having a specified tag.
	 * 
	 * @param userId ID of the user.
	 * @param tag Tag of the document
	 * @return Set of found ids.
	 */
	public Set<Long> findDocIdByUserIdAndTag(long userId, String tag);

	/**
	 * This method enlists documents linked to the given document.
	 * <p>
	 * <b>Important:</b> The attribute <code>direction</code> defines the search
	 * logic as follows:
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
	public List<Document> findLinkedDocuments(long docId, String linkType, Integer direction);

	/**
	 * Finds that document that lies under a specific folder (given by the id)
	 * an with a given fileName(like operator is used)
	 * 
	 * @param folderId The folder id (it can be null).
	 * @param fileName
	 * @param excludeId Optional id of a document that must not be considered
	 * @return The list of documents with the given fileName. If the folder id
	 *         is null, the searched document can belong to any folder in the
	 *         repository.
	 */
	public List<Document> findByFileNameAndParentFolderId(Long folderId, String fileName, Long excludeId);

	/**
	 * Finds that document that lies under a specific folder (given by the id)
	 * an with a given title(like operator is used)
	 * 
	 * @param folderId The folder id (it can be null).
	 * @param title
	 * @param excludeId Optional id of a document that must not be considered
	 * @return
	 */
	public List<Document> findByTitleAndParentFolderId(long folderId, String title, Long excludeId);

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
	public List<Long> findDeletedDocIds();

	/**
	 * Finds the list of deleted documents.
	 * <p>
	 * <b>Attention:</b> The returded objects are not fully operative and are
	 * populated with a minimal set of data.
	 */
	public List<Document> findDeletedDocs();

	/**
	 * Counts the number of documents
	 * 
	 * @param computeDeleted If true, even deleted documents are considered
	 */
	public long count(boolean computeDeleted);

	/**
	 * Finds all documents by the indexed state. Order by ascending lastModifed
	 * 
	 * @param indexed the indexed property
	 * @return Collection of all documents
	 */
	public List<Document> findByIndexed(int indexed);

	/**
	 * Counts the number of documents indexed or not
	 */
	public long countByIndexed(int indexed);

	/**
	 * Restores a previously deleted document
	 * 
	 * @param docId Id of the document to be restored
	 */
	public void restore(long docId);

	/**
	 * Marks the document, with the given docId, as immutable. Unlocks the
	 * document if it was locked.
	 * 
	 * @param docId
	 * @param transaction entry to log the event
	 */
	public void makeImmutable(long docId, History transaction);

	/**
	 * Deletes all documents form the database and modifies the custom ids of
	 * all documents
	 * 
	 * @param documents The documents to be deleted
	 * @param transaction entry to log the event
	 */
	public void deleteAll(Collection<Document> documents, History transaction);

	/**
	 * This method persists the document object and insert a new document
	 * history entry.
	 * 
	 * @param doc
	 * @param transaction entry to log the event
	 * @return True if successfully stored in a database.
	 */
	public boolean store(final Document doc, final History transaction);

	/**
	 * This method deletes the document object and insert a new document history
	 * entry.
	 * 
	 * @param docId The id of the document to delete
	 * @param transaction entry to log the event
	 * @return True if successfully deleted from the database.
	 */
	public boolean delete(long docId, History transaction);

	/**
	 * Gets the ids of all shortcuts associated to the document with the given
	 * docId
	 * 
	 * @param docId The document Id
	 */
	public List<Long> findShortcutIds(long docId);

	/**
	 * Finds all deleted docs of a specific user.
	 * 
	 * @param userId The user that performed the deletion
	 * @param maxHits Optional defines the max number of returned hits
	 * @return The documents list ordered by descending lastModified
	 */
	public List<Document> findDeleted(long userId, Integer maxHits);
}