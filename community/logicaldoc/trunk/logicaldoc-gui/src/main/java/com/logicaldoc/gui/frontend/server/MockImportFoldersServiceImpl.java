package com.logicaldoc.gui.frontend.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.common.client.beans.GUIShare;
import com.logicaldoc.gui.frontend.client.services.ImportFoldersService;

/**
 * Mock implementation of the ImportFoldersService
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class MockImportFoldersServiceImpl extends RemoteServiceServlet implements ImportFoldersService {

	private static final long serialVersionUID = 1L;

	@Override
	public void delete(String sid, long id) throws InvalidSessionException {

	}

	@Override
	public GUIShare save(String sid, GUIShare share) throws InvalidSessionException {
		if (share.getId() == 0)
			share.setId(9999);
		
		return share;
	}

	@Override
	public GUIShare getShare(String sid, long id) throws InvalidSessionException {
		GUIShare share = new GUIShare();
		share.setId(id);
		share.setPath("\\server\folder" + id);
		GUIFolder folder = new GUIFolder();
		folder.setId(id);
		folder.setName("test" + id);
		share.setTarget(folder);
		share.setLanguage("en");
		return share;
	}

	@Override
	public boolean test(String sid, long id) throws InvalidSessionException {
		return (id % 2) == 0;
	}

	@Override
	public void changeStatus(String sid, long id, boolean enabled) {
	}

	@Override
	public void resetCache(String sid, long id) throws InvalidSessionException {
	}
}