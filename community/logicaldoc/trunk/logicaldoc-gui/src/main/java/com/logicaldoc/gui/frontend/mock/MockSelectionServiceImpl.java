package com.logicaldoc.gui.frontend.mock;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.gui.frontend.client.services.SelectionService;

/**
 * Implementation of the SelectionService
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class MockSelectionServiceImpl extends RemoteServiceServlet implements SelectionService {

	private static final long serialVersionUID = 1L;

	@Override
	public Map<String, String> getLanguages() {
		System.out.println(getThreadLocalRequest().getQueryString());
		
		Map<String,String> langs=new HashMap<String,String>();
		langs.put("en", "English");
		langs.put("it", "Italian");
		return null;
	}
}
