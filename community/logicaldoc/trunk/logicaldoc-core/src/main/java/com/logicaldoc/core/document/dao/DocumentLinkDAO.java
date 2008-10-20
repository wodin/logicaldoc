package com.logicaldoc.core.document.dao;

import java.util.Collection;

import com.logicaldoc.core.document.DocumentLink;

/**
 * DAO for <code>DocumentLink</code> handling.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 4.0
 * 
 */
public interface DocumentLinkDAO {

	/**
	 * This method persists a document link object.
	 * 
	 * @param link DocumentLink to be stored.
	 * @return True if successfully stored in a database.
	 */
	public boolean store(DocumentLink link);

	/**
	 * This method deletes a document link.
	 * 
	 * @param linkId ID of the link which should be delete.
	 */
	public boolean delete(long linkId);

	/**
	 * This method finds a document link by its ID.
	 * 
	 * @param linkId ID of the document link.
	 * @return DocumentLink with given ID.
	 */
	public DocumentLink findById(long linkId);

	/**
	 * This method finds the list of document link in which there is a document
	 * with the given ID.
	 * 
	 * @param docId ID of the document.
	 * @return Collection<DocumentLink> The list of document link.
	 */
	public Collection<DocumentLink> findByDocId(long docId);

	/**
	 * This method finds the list of document link, filtered by the given link
	 * type, in which there is a document with the given ID.
	 * 
	 * @param docId ID of the document.
	 * @param type Type of each document link in the return list.
	 * @return Collection<DocumentLink> The list of document link.
	 */
	public Collection<DocumentLink> findByDocId(long docId, String type);
}
