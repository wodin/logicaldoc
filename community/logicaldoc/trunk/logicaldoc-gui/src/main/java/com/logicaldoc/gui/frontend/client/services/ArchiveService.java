package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.common.client.beans.GUIArchive;

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
	public void deleteVersions(String sid, long archiveId, long versionIds[]);

	/**
	 * Change the status of the given Archive
	 */
	public void setStatus(String sid, long archiveId, int status);

	/**
	 * Saves/Updates a given archive
	 */
	public GUIArchive save(String sid, GUIArchive archive);
}