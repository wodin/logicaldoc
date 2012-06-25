package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.common.client.beans.GUIArchive;
import com.logicaldoc.gui.common.client.beans.GUIIncrementalArchive;
import com.logicaldoc.gui.common.client.beans.GUIVersion;

/**
 * The client side stub for the Archive Service. This service allows r/w
 * operations on documents.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
@RemoteServiceRelativePath("archive")
public interface ArchiveService extends RemoteService {
	/**
	 * Deletes a specific archive by its ID
	 */
	public void delete(String sid, long archiveId) throws InvalidSessionException;

	/**
	 * Deletes a set of versions from the given archive
	 */
	public GUIArchive deleteVersions(String sid, long archiveId, Long versionIds[]) throws InvalidSessionException;

	/**
	 * Change the status of the given Archive
	 */
	public void setStatus(String sid, long archiveId, int status) throws InvalidSessionException;

	/**
	 * Saves/Updates a given archive
	 */
	public GUIArchive save(String sid, GUIArchive archive) throws InvalidSessionException;

	/**
	 * Loads a given archive
	 */
	public GUIArchive load(String sid, long archiveId) throws InvalidSessionException;

	/**
	 * Adds a set of documents(their current versions) to the given archive
	 */
	public void addDocuments(String sid, long archiveId, long[] documentIds) throws InvalidSessionException;

	/**
	 * Deletes a given incremental configuration
	 */
	public void deleteIncremental(String sid, long id) throws InvalidSessionException;

	/**
	 * Loads an incremental configuration
	 */
	public GUIIncrementalArchive loadIncremental(String sid, long id) throws InvalidSessionException;

	/**
	 * Saves the passed incremental configuration
	 */
	public GUIIncrementalArchive saveIncremental(String sid, GUIIncrementalArchive incremental)
			throws InvalidSessionException;

	/**
	 * Deletes a folder in impex/in folder.
	 */
	public void deleteFolder(String sid, String folderName) throws InvalidSessionException;

	/**
	 * Create a new import archive for the specified bundle folder
	 */
	public void startImport(String sid, String folderName) throws InvalidSessionException;

	/**
	 * Verifies the user signature file, extracts the original file contained
	 * into the uploaded m7m file and checks if its digest and the archive's
	 * file digest are equals, then signs and marks the archive.
	 * 
	 * @param sid The session identifier
	 * @param userid Identifier of the user that is performing the signature
	 * @param archiveId Identifier of the archive to sign
	 * @return 'ok' if no errors occurred, otherwise returns the error message.
	 * @throws InvalidSessionException
	 */
	public String signArchive(String sid, long userId, long archiveId) throws InvalidSessionException;

	/**
	 * Verifies the archive with the given identifier. First of all, checks if
	 * there are some archived documents that missing a required signature
	 * (looking at their template). Then validates the archive.
	 * 
	 * @param sid The session identifier
	 * @param archiveId Identifier of the archive to verify
	 * @return Array of documents versions that have some errors.
	 * @throws InvalidSessionException
	 */
	public GUIVersion[] verifyArchive(String sid, long archiveId) throws InvalidSessionException;
}