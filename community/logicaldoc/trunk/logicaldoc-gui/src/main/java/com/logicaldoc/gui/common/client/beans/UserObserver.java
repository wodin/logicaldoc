package com.logicaldoc.gui.common.client.beans;

/**
 * Definition of a generic observer on user's attributes.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public interface UserObserver {
	/**
	 * Invoked when some changes on the user appens
	 */
	public void onUserChanged(GUIUser user);
}
