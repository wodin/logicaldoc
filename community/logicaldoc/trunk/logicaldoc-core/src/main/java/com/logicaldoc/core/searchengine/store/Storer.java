package com.logicaldoc.core.searchengine.store;

import java.io.File;
import java.io.InputStream;

/**
 * The Storer manages the filesystem where document files are maintained.
 * 
 * @author Michael Scholz, Marco Meschieri
 */
public interface Storer {
	/**
	 * This method has to store a document. The location where (DBMS,
	 * Filesystem, other) the document should be stored is defined by the
	 * concret implementation. It is possible to store a new document or a new
	 * version of an existing document.
	 * 
	 * @param stream Document as InputStream
	 * @param docId The document identifier
	 * @param filename Name of the document.
	 * @return ResultImpl of the storing process.
	 */
	public boolean store(InputStream stream, long docId, String filename);

	/**
	 * Deletes a document from the document storage.
	 * 
	 * @param docId The document identifier
	 */
	public void delete(long docId);
	
	
	/**
	 * Finds the folder where all document's files are stored
	 * 
	 * @param docId The document identifier
	 * @return The document's folder
	 */
	public File getDirectory(long docId);
	
	/**
	 * Finds a specific file of a stored document
	 * 
	 * @param docId The document identifier
	 * @return The document's file
	 */
	public File getFile(long docId, String filename);
}