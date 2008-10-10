package com.logicaldoc.core.document.dao;

import java.util.Collection;

import com.logicaldoc.core.document.Term;
import com.logicaldoc.core.document.TermID;

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
	 * @param menuId - Id of the document.
	 * @return
	 */
	public boolean delete(int menuId);

	/**
	 * This method selects one term of a document which has a same stem like a
	 * reference document.
	 * 
	 * @param docId - ID of reference document.
	 * @param maxResults - The maximum number of results
	 * @return
	 */
	public Collection<Term> findByStem(int menuId, int maxResults);

	/**
	 * Finds all term entries of a document.
	 * 
	 * @param docId ID of the document
	 * @return
	 */
	public Collection<Term> findByMenuId(int menuId);

	/**
	 * Finds the instance given it's primary key.
	 */
	public Term findById(TermID termId);
}