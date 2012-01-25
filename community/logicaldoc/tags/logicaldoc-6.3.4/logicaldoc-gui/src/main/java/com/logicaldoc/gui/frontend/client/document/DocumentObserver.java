package com.logicaldoc.gui.frontend.client.document;

import com.logicaldoc.gui.common.client.beans.GUIDocument;

/**
 * Listener on document events
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public interface DocumentObserver {

	/**
	 * Invoked after the document has been saved
	 * 
	 * @param document The updated document
	 */
	public void onDocumentSaved(GUIDocument document);
}
