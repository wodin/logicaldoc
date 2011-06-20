package com.logicaldoc.gui.setup.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.logicaldoc.gui.setup.client.SetupInfo;

/**
 * The client side stub for the Setup Service. This service allows the installation of an instance of LogicalDOC.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
@RemoteServiceRelativePath("setup")
public interface SetupService extends RemoteService {
	
	/**
	 * Performs a system setup.
	 * 
	 * @param data The intallation data
	 */
	public void setup(SetupInfo data);
}
