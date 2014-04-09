package com.logicaldoc.gui.common.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.logicaldoc.gui.common.client.beans.GUIInfo;
import com.logicaldoc.gui.common.client.beans.GUIParameter;

/**
 * Informations service
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0.0
 */
@RemoteServiceRelativePath("info")
public interface InfoService extends RemoteService {
	/**
	 * Retrieves the system informations
	 */
	public GUIInfo getInfo(String locale, String tenant);

	public GUIParameter[] getSessionInfo(String sid);
}
