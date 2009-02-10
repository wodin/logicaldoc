package com.logicaldoc.core.searchengine.store;

import java.io.InputStream;

/**
 * The Storer manages the filesystem where document files are maintained.o
 * 
 * @author Michael Scholz
 */
public interface Storer {
	/**
	 * This method has to store a document. The location where (DBMS,
	 * Filesystem, other) the document should be stored is defined by the
	 * concret implementation. It is possible to store a new document or a new
	 * version of an existing document.
	 * 
	 * @param stream Document as InputStream
	 * @param docPath Path in logicaldoc containing the menuIds of all parent
	 *        items.
	 * @param filename Name of the document.
	 * @return ResultImpl of the storing process.
	 */
	boolean store(InputStream stream, String docPath, String filename);

	/**
	 * Deletes a document from the documentpool.
	 * 
	 * @param docPath Path in logicaldoc containing the menuIds of all parent
	 *        folders.
	 */
	void delete(String docPath);
}
