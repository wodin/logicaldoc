package com.logicaldoc.gui.frontend.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.common.client.beans.GUIArchive;
import com.logicaldoc.gui.frontend.client.services.ArchiveService;

/**
 * Implementation of the SecurityService
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class MockArchiveServiceImpl extends RemoteServiceServlet implements ArchiveService {

	private static final long serialVersionUID = 1L;

	@Override
	public GUIArchive save(String sid, GUIArchive archive) {
		return archive;
	}

	@Override
	public void delete(String sid, long archiveId) throws InvalidSessionException {

	}

	@Override
	public void deleteVersions(String sid, long archiveId, long[] versionIds) {

	}

	@Override
	public void setStatus(String sid, long archiveId, int status) {

	}
}