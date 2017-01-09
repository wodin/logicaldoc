package com.logicaldoc.gui.common.client;

import com.logicaldoc.gui.common.client.beans.GUIFolder;

/**
 * Listener on folders events
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public interface FolderObserver {
	/**
	 * Invoked when the user selects a new folder.
	 * 
	 * @param folder The newly selected folder
	 */
	public void onFolderSelected(GUIFolder folder);

	/**
	 * Invoked after the folder has been saved
	 * 
	 * @param folder The updated folder
	 * @param positionChanged True if the position changed
	 */
	public void onFolderSaved(GUIFolder folder, boolean positionChanged);
}
