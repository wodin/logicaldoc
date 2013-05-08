package com.logicaldoc.gui.frontend.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.common.client.beans.GUIValuePair;
import com.logicaldoc.gui.frontend.client.services.FolderService;

/**
 * Implementation of the FolderService
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class MockFolderServiceImpl extends RemoteServiceServlet implements FolderService {

	private static final long serialVersionUID = 1L;

	@Override
	public GUIFolder save(String sid, GUIFolder folder) {
		return folder;
	}

	@Override
	public void applyRights(String sid, GUIFolder folder, boolean recursive) {
	}

	@Override
	public GUIFolder getFolder(String sid, long folderId, boolean computePath) {
		GUIFolder folder = new GUIFolder();
		folder.setId(folderId);
		folder.setName(folderId != Constants.DOCUMENTS_FOLDERID ? "Folder " + folderId : "/");

		if (computePath) {
			GUIFolder[] path = new GUIFolder[] { getFolder(sid, 5, false), getFolder(sid, 1007, false),
					getFolder(sid, 2009, false) };
			folder.setPath(path);
		}

		if (folderId % 2 == 0 || folderId == Constants.DOCUMENTS_FOLDERID)
			folder.setPermissions(new String[] { "read", "write", "add", "security", "delete", "rename",
					"import", "export", "sign", "archive", "workflow", "immutability" });
		else
			folder.setPermissions(new String[] { "read" });
		return folder;
	}

	@Override
	public void delete(String sid, long folderId) {

	}

	@Override
	public void move(String sid, long folderId, long targetId) {

	}

	@Override
	public void rename(String sid, long folderId, String name) {

	}

	@Override
	public void paste(String sid, long[] docIds, long folderId, String action) {

	}

	@Override
	public void pasteAsAlias(String sid, long[] docIds, long folderId) {

	}

	@Override
	public GUIValuePair[] loadTemplates(String sid) throws InvalidSessionException {
		return null;
	}

	@Override
	public void saveTemplates(String sid, GUIValuePair[] templates) throws InvalidSessionException {

	}

	@Override
	public void applyTemplate(String sid, long folderId, long templateId) throws InvalidSessionException {

	}

	@Override
	public void applyMetadata(String sid, long parentId) throws InvalidSessionException {

	}

	@Override
	public GUIFolder create(String sid, long parentId, String name, boolean inheritSecurity)
			throws InvalidSessionException {
		return null;
	}
}