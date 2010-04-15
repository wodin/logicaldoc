package com.logicaldoc.gui.frontend.client.services;

import java.util.Map;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the Selection Service. This service collects some
 * lists for selections population.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0.0
 */
@RemoteServiceRelativePath("selection")
public interface SelectionService extends RemoteService {
	/**
	 * Gets the map language-description of all supported languages
	 */
	public Map<String, String> getLanguages();
}
