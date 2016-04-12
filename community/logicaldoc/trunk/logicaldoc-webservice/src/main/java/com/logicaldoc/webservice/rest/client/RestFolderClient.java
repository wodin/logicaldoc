package com.logicaldoc.webservice.rest.client;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.webservice.model.Right;
import com.logicaldoc.webservice.model.WSFolder;
import com.logicaldoc.webservice.services.FolderService;

public class RestFolderClient extends AbstractRestClient implements FolderService {

	protected static Logger log = LoggerFactory.getLogger(RestFolderClient.class);

	public RestFolderClient(String endpoint) {
		super(endpoint);
	}

	@Override
	public WSFolder create(String sid, WSFolder folder) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long createFolder(String sid, long parentId, String name) throws Exception {
		String url = endpoint + "/createFolder";
		String output = null;
		PostMethod post = preparePostMethod(url);
		try {
			post.setParameter("sid", sid);
			post.setParameter("parentId", Long.toString(parentId));
			post.setParameter("name", name);

			int statusCode = client.executeMethod(post);

			if (statusCode == HttpStatus.SC_OK)
				output = post.getResponseBodyAsString();
			else
				throw new Exception("Server Error");
		} finally {
			post.releaseConnection();
		}

		if (StringUtils.isEmpty(output))
			throw new Exception("Error in folder creation");
		else
			return Long.parseLong(output);
	}

	@Override
	public void delete(String sid, long folderId) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void rename(String sid, long folderId, String name) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(String sid, WSFolder folder) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void move(String sid, long folderId, long parentId) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void copy(String sid, long folderId, long targetId, int foldersOnly, int inheritSecurity) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public WSFolder getFolder(String sid, long folderId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WSFolder getRootFolder(String sid) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WSFolder getDefaultWorkspace(String sid) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isReadable(String sid, long folderId) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isWriteable(String sid, long folderId) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isGranted(String sid, long folderId, int permission) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public WSFolder[] listChildren(String sid, long folderId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WSFolder[] getPath(String sid, long folderId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void grantUser(String sid, long folderId, long userId, int permissions, boolean recursive) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void grantGroup(String sid, long folderId, long groupId, int permissions, boolean recursive)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public Right[] getGrantedUsers(String sid, long folderId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Right[] getGrantedGroups(String sid, long folderId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WSFolder createPath(String sid, long parentId, String path) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WSFolder findByPath(String sid, String path) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WSFolder[] listWorkspaces(String sid) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WSFolder createAlias(String sid, long parentId, long foldRef) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
