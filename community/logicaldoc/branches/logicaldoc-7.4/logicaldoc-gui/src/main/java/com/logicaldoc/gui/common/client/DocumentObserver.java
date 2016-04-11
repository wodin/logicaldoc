package com.logicaldoc.gui.common.client;

import com.logicaldoc.gui.common.client.beans.GUIDocument;

/**
 * Listener on documents events
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.7
 */
public interface DocumentObserver {
	/**
	 * Invoked when the user selects a single document
	 * 
	 * @param document The newly selected document
	 */
	public void onDocumentSelected(GUIDocument document);

	/**
	 * Invoked after the document has been saved
	 * 
	 * @param document The updated document
	 */
	public void onDocumentSaved(GUIDocument document);
}
