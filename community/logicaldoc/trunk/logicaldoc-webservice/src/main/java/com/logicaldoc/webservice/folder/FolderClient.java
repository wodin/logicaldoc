package com.logicaldoc.webservice.folder;

import java.io.IOException;

import com.logicaldoc.webservice.SoapClient;
import com.logicaldoc.webservice.auth.Right;

/**
 * Folder Web Service client.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 5.2
 */
public class FolderClient extends SoapClient<FolderService> implements FolderService {

	public FolderClient(String endpoint, int gzipThreshold, boolean log, int timeout) throws IOException {
		super(endpoint, FolderService.class, gzipThreshold, log, timeout);
	}

	public FolderClient(String endpoint) throws IOException {
		super(endpoint, FolderService.class, -1, true, -1);
	}

	@Override
	public WSFolder create(String sid, WSFolder folder) throws Exception {
		return client.create(sid, folder);
	}

	@Override
	public long createFolder(String sid, long parentId, String name) throws Exception {
		return client.createFolder(sid, parentId, name);
	}

	@Override
	public void delete(String sid, long folderId) throws Exception {
		client.delete(sid, folderId);
	}

	@Override
	public WSFolder getFolder(String sid, long folderId) throws Exception {
		return client.getFolder(sid, folderId);
	}

	@Override
	public boolean isReadable(String sid, long folderId) throws Exception {
		return client.isReadable(sid, folderId);
	}

	@Override
	public void move(String sid, long folderId, long parentId) throws Exception {
		client.move(sid, folderId, parentId);
	}

	@Override
	public void copy(String sid, long folderId, long parentId, int foldersOnly, int inheritSecurity) throws Exception {
		client.copy(sid, folderId, parentId, foldersOnly, inheritSecurity);
	}

	@Override
	public void rename(String sid, long folderId, String name) throws Exception {
		client.rename(sid, folderId, name);
	}

	@Override
	public WSFolder[] listChildren(String sid, long folderId) throws Exception {
		return client.listChildren(sid, folderId);
	}

	@Override
	public WSFolder getRootFolder(String sid) throws Exception {
		return client.getRootFolder(sid);
	}

	@Override
	public WSFolder getDefaultWorkspace(String sid) throws Exception {
		return client.getDefaultWorkspace(sid);
	}

	@Override
	public boolean isWriteable(String sid, long folderId) throws Exception {
		return client.isWriteable(sid, folderId);
	}

	@Override
	public WSFolder[] getPath(String sid, long folderId) throws Exception {
		return client.getPath(sid, folderId);
	}

	@Override
	public Right[] getGrantedGroups(String sid, long folderId) throws Exception {
		return client.getGrantedGroups(sid, folderId);
	}

	@Override
	public Right[] getGrantedUsers(String sid, long folderId) throws Exception {
		return client.getGrantedUsers(sid, folderId);
	}

	@Override
	public void grantGroup(String sid, long folderId, long groupId, int permissions, boolean recursive)
			throws Exception {
		client.grantGroup(sid, folderId, groupId, permissions, recursive);
	}

	@Override
	public void grantUser(String sid, long folderId, long userId, int permissions, boolean recursive) throws Exception {
		client.grantUser(sid, folderId, userId, permissions, recursive);
	}

	@Override
	public void update(String sid, WSFolder folder) throws Exception {
		client.update(sid, folder);
	}

	@Override
	public WSFolder createPath(String sid, long parentId, String path) throws Exception {
		return client.createPath(sid, parentId, path);
	}

	@Override
	public WSFolder[] listWorkspaces(String sid) throws Exception {
		return client.listWorkspaces(sid);
	}

	@Override
	public WSFolder findByPath(String sid, String path) throws Exception {
		return client.findByPath(sid, path);
	}

	@Override
	public boolean isGranted(String sid, long folderId, int permission) throws Exception {
		return client.isGranted(sid, folderId, permission);
	}

	@Override
	public WSFolder createAlias(String sid, long parentId, long foldRef) throws Exception {
		return client.createAlias(sid, parentId, foldRef);
	}
}