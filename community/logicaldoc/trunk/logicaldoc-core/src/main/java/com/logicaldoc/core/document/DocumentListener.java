package com.logicaldoc.core.document;

import java.util.Map;

/**
 * This interface defines hooks called before and after a particular event
 * occurs on the specified document.
 * <p>
 * Each methods has access to a dictionary map that can be used through the
 * execution pipeline in order to carry needed informations among all listeners.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.0
 */
public interface DocumentListener {
	/**
	 * Called before a document is stored in the database
	 * 
	 * @param document The document to be stored
	 * @param dictionary Dictionary of the execution pipeline
	 */
	public void beforeStore(Document document, Map<String, Object> dictionary);

	/**
	 * Called after a document is stored in the database
	 * 
	 * @param document The document to be stored
	 * @param dictionary Dictionary of the execution pipeline
	 */
	public void afterStore(Document document, Map<String, Object> dictionary);
}
