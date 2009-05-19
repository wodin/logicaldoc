package com.logicaldoc.core.document.dao;

import java.util.List;

import com.logicaldoc.core.PersistentObjectDAO;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.Version;
import com.logicaldoc.core.security.User;

/**
 * This class is a DAO-service for versions.
 * 
 * @author Marco Meschieri - Logical Objects
 * @version 4.5
 */
public interface VersionDAO extends PersistentObjectDAO<Version> {
	/**
	 * This method finds a version by the document's ID an the version code.
	 * 
	 * @param docId ID of the document
	 * @param version the version code
	 * @return the found version
	 */
	public Version findByVersion(long docId, String version);

	/**
	 * Finds all versions of the given document
	 * 
	 * @param docId The document's id
	 * @return The list of versions ordered by descending date
	 */
	public List<Version> findByDocId(long docId);
}