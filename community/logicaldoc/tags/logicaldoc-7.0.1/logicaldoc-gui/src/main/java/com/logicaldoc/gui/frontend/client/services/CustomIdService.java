package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.common.client.beans.GUICustomId;
import com.logicaldoc.gui.common.client.beans.GUISequence;

/**
 * The client side stub for the CustomIdService Service. This service gives all
 * needed methods to handle custom ids configutations.
 */
@RemoteServiceRelativePath("customid")
public interface CustomIdService extends RemoteService {
	/**
	 * Deletes a given configuration
	 */
	public void delete(String sid, long templateId) throws InvalidSessionException;

	/**
	 * Creates or updates a configuration
	 */
	public void save(String sid, GUICustomId customid) throws InvalidSessionException;

	/**
	 * Loads a given configuration from the database
	 */
	public GUICustomId get(String sid, long templateId) throws InvalidSessionException;

	/**
	 * Load all CustomIds configutations
	 */
	public GUICustomId[] load(String sid) throws InvalidSessionException;

	/**
	 * Reset the numbering of a given sequence
	 */
	public void resetSequence(String sid, long sequenceId, long value) throws InvalidSessionException;

	/**
	 * Loads the list of sequences
	 */
	public GUISequence[] loadSequences(String sid) throws InvalidSessionException;
	
	
	/**
	 * Deletes the given sequence
	 */
	public void deleteSequence(String sid, long sequenceId) throws InvalidSessionException;
}