package com.logicaldoc.gui.frontend.client.search;

/**
 * Listener on search events
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public interface SearchObserver {

	/**
	 * Invoked when a new result is returned by the server.
	 */
	public void onResult();
}
