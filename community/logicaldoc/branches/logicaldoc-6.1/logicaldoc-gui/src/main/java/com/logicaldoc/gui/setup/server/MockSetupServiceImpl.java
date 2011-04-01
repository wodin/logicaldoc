package com.logicaldoc.gui.setup.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.gui.setup.client.SetupInfo;
import com.logicaldoc.gui.setup.client.services.SetupService;

/**
 * Mock implemetation of the Setup Service
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class MockSetupServiceImpl extends RemoteServiceServlet implements SetupService {

	private static final long serialVersionUID = 1L;

	@Override
	public void setup(SetupInfo data) {
		System.out.println("Received setup request for repository: " + data.getRepositoryFolder());
	}
}
