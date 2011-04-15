package com.logicaldoc.core.store;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import com.logicaldoc.core.document.Document;

/**
 * The Storer manages the repository where document files are maintained.
 * 
 * @author Michael Scholz, Marco Meschieri
 */
public interface Storer {
	/**
	 * This method has to store a resource in the document's container. The
	 * location where (DBMS, Filesystem, other) the document should be stored is
	 * defined by the concrete implementation. It is possible to store a new
	 * document or a new version of an existing document.
	 * 
	 * @param stream Document as InputStream
	 * @param docId The document identifier
	 * @param resource Name of the resource to be stored
	 * @return ResultImpl of the storing process.
	 */
	public boolean store(InputStream stream, long docId, String resource);

	/**
	 * Deletes all resources of a document from the storage.
	 * 
	 * @param docId The document identifier
	 */
	public void delete(long docId);

	/**
	 * Deletes a specific resource of a document from the storage.
	 * 
	 * @param docId The document identifier
	 * @param resource Name of the resource to be deleted
	 */
	public void delete(long docId, String resource);

	/**
	 * Finds the container where all document's files are stored
	 * 
	 * @param docId The document identifier
	 * @return The document's container
	 */
	public File getContainer(long docId);

	/**
	 * Computes the resource name inside the container
	 * 
	 * @param doc The document representation
	 * @param fileVersion The file version (use null for the latest version)
	 * @param suffix The file suffix (use null if you want the exact document
	 *        file)
	 * @return The document's resource name
	 */
	public String getResourceName(Document doc, String fileVersion, String suffix);

	/**
	 * Computes the resource name inside the container
	 * 
	 * @param docId The document identifier
	 * @param fileVersion The file version (use null for the latest version)
	 * @param suffix The file suffix (use null if you want the exact document
	 *        file)
	 * @return The document's resource name
	 */
	public String getResourceName(long docId, String fileVersion, String suffix);

	/**
	 * Lists all resources in the document's container
	 * 
	 * @param docId The document's identifier
	 * @param fileVersion If specified, lists the resources for that specific
	 *        file version only
	 * @return
	 */
	public List<String> listResources(long docId, String fileVersion);

	/**
	 * Computed the size of a specific resource.
	 * 
	 * @param docId The document's identifier
	 * @param resourceName The resource
	 * 
	 * @return the size in bytes
	 */
	public long size(long docId, String resourceName);

	/**
	 * Checks if the passed resource exists in the document's container
	 * 
	 * @param docId ID of the document
	 * @param resourceName Name of the resource
	 * @return true only if the resource already exists
	 */
	public boolean exists(long docId, String resourceName);

	/**
	 * Obtains the document's file for the specified version
	 * 
	 * @param docId The document identifier
	 * @param resource Name of the resource
	 * 
	 * @return The document file
	 */
	public File getFile(long docId, String resource);

	/**
	 * Obtains the document's content for the specified version
	 * 
	 * @param docId The document's identifier
	 * @param resource Name of the resource
	 * 
	 * @return The document file's content
	 */
	public InputStream getStream(long docId, String resource);

	/**
	 * Obtains the document's raw bytes for the specified version
	 * 
	 * @param docId The document's identifier
	 * @param resource Name of the resource
	 * 
	 * @return The document file's bytes
	 */
	public byte[] getBytes(long docId, String resource);

	/**
	 * Computes the total size of the documents repository(in bytes)
	 */
	public long getTotalSize();
}