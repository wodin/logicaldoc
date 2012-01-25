package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.common.client.beans.GUITag;

/**
 * Tag handling service
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
@RemoteServiceRelativePath("tag")
public interface TagService extends RemoteService {

	/**
	 * Loads the tag cloud from the server
	 */
	public GUITag[] getTagCloud();

	/**
	 * Deletes an existing tag
	 */
	public void delete(String sid, String tag) throws InvalidSessionException;

	/**
	 * Rename an existing tag to another label
	 */
	public void rename(String sid, String tag, String newTag) throws InvalidSessionException;

	/**
	 * Adds a new tag in the list of available tags.
	 */
	public void addTag(String sid, String tag) throws InvalidSessionException;

	/**
	 * Removes an available from the list of available tags.
	 */
	public void removeTag(String sid, String tag) throws InvalidSessionException;

	/**
	 * Checks the tags input mode, can be 'free' or 'preset'
	 */
	public String getMode(String sid) throws InvalidSessionException;

	/**
	 * Checks the tags input mode, can be 'free' or 'preset'
	 */
	public void setMode(String sid, String mode) throws InvalidSessionException;
}