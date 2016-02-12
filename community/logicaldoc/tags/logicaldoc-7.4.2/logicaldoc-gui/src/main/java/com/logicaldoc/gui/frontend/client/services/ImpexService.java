package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.logicaldoc.gui.common.client.ServerException;
import com.logicaldoc.gui.common.client.beans.GUIArchive;
import com.logicaldoc.gui.common.client.beans.GUIIncrementalArchive;

/**
 * The client side stub for the Impex Service. This service allows r/w
 * operations on export archives.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
@RemoteServiceRelativePath("impex")
public interface ImpexService extends RemoteService {
	/**
	 * Deletes a specific archive by its ID
	 */
	public void delete(String sid, long archiveId) throws ServerException;

	/**
	 * Deletes a set of versions from the given archive
	 */
	public GUIArchive deleteVersions(String sid, long archiveId, Long versionIds[]) throws ServerException;

	/**
	 * Change the status of the given Archive
	 */
	public void setStatus(String sid, long archiveId, int status) throws ServerException;

	/**
	 * Saves/Updates a given archive
	 */
	public GUIArchive save(String sid, GUIArchive archive) throws ServerException;

	/**
	 * Loads a given archive
	 */
	public GUIArchive load(String sid, long archiveId) throws ServerException;

	/**
	 * Adds a set of documents(their current versions) to the given archive
	 */
	public void addDocuments(String sid, long archiveId, long[] documentIds) throws ServerException;

	/**
	 * Adds a a folder(the current version of the contained documents at any level).
	 */
	public void addFolder(String sid, long archiveId, long rootId) throws ServerException;
	
	/**
	 * Deletes a given incremental configuration
	 */
	public void deleteIncremental(String sid, long id) throws ServerException;

	/**
	 * Loads an incremental configuration
	 */
	public GUIIncrementalArchive loadIncremental(String sid, long id) throws ServerException;

	/**
	 * Saves the passed incremental configuration
	 */
	public GUIIncrementalArchive saveIncremental(String sid, GUIIncrementalArchive incremental)
			throws ServerException;

	/**
	 * Deletes a folder in impex/in folder.
	 */
	public void deleteFolder(String sid, String folderName) throws ServerException;

	/**
	 * Create a new import archive for the specified bundle folder
	 */
	public void startImport(String sid, String folderName) throws ServerException;
}