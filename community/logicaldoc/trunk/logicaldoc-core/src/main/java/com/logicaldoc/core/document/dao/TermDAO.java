package com.logicaldoc.core.document.dao;

import java.util.Collection;
import java.util.List;

import com.logicaldoc.core.document.Term;

/**
 * DAO for <code>Term</code> handling
 * 
 * @author Michael Scholz
 * @author Marco Meschieri
 */
public interface TermDAO {
	/**
	 * This method persist a term-object.
	 * 
	 * @param term
	 * @return
	 */
	public boolean store(Term term);

	/**
	 * This method deletes all terms of a document.
	 * 
	 * @param docId - Id of the document.
	 * @return
	 */
	public boolean delete(long docId);

	/**
	 * This method selects one term of a document which has a same stem like a
	 * reference document.
	 * 
	 * @param docId - ID of reference document.
	 * @param maxResults - The maximum number of results
	 * @return
	 */
	public List<Term> findByStem(long docId, int maxResults);

	/**
	 * Finds all term entries of a document.
	 * 
	 * @param docId ID of the document
	 * @return
	 */
	public Collection<Term> findByDocId(long docId);

	/**
	 * Finds the instance given it's primary key.
	 */
	public Term findByPrimaryKey(long termId);
}