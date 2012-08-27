package com.logicaldoc.gui.frontend.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.common.client.beans.GUIArchive;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.common.client.beans.GUIIncrementalArchive;
import com.logicaldoc.gui.common.client.beans.GUITemplate;
import com.logicaldoc.gui.common.client.beans.GUIVersion;
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
	public GUIArchive save(String sid, GUIArchive archive) throws InvalidSessionException {
		return archive;
	}

	@Override
	public void delete(String sid, long archiveId) throws InvalidSessionException {

	}

	@Override
	public GUIArchive deleteVersions(String sid, long archiveId, Long[] versionIds) throws InvalidSessionException {
		GUIArchive archive = new GUIArchive();
		archive.setId(archiveId);
		return archive;
	}

	@Override
	public void setStatus(String sid, long archiveId, int status) throws InvalidSessionException {
	}

	@Override
	public void deleteIncremental(String sid, long id) throws InvalidSessionException {

	}

	@Override
	public GUIIncrementalArchive loadIncremental(String sid, long id) throws InvalidSessionException {
		GUIIncrementalArchive incremental = new GUIIncrementalArchive();
		incremental.setId(id);
		incremental.setPrefix("Prefix" + id);

		GUIFolder folder = new GUIFolder();
		folder.setId(10);
		folder.setName("Folder_10");
		incremental.setFolder(folder);

		GUITemplate t1 = new GUITemplate();
		t1.setId(1);
		t1.setName("template1");
		GUITemplate t2 = new GUITemplate();
		t2.setId(2);
		t2.setName("template2");
		incremental.setTemplates(new GUITemplate[] { t1, t2 });

		return incremental;
	}

	@Override
	public GUIIncrementalArchive saveIncremental(String sid, GUIIncrementalArchive incremental)
			throws InvalidSessionException {
		return incremental;
	}

	@Override
	public void addDocuments(String sid, long archiveId, long[] documentIds) throws InvalidSessionException {

	}

	@Override
	public void deleteFolder(String sid, String folderName) {

	}

	@Override
	public void startImport(String sid, String folderName) throws InvalidSessionException {
	}

	@Override
	public GUIArchive load(String sid, long archiveId) throws InvalidSessionException {
		GUIArchive archive = new GUIArchive();
		archive.setId(archiveId);
		return archive;
	}

	@Override
	public String signArchive(String sid, long userId, long archiveId) throws InvalidSessionException {
		return "ok";
	}

	@Override
	public GUIVersion[] verifyArchive(String sid, long archiveId) throws InvalidSessionException {
		GUIVersion[] versions = new GUIVersion[2];

		GUIVersion version = new GUIVersion();
		version.setUsername("Marco Meschieri");
		version.setComment("comment");
		version.setId(1);
		version.setTitle("Version " + 1);
		versions[0] = version;
		version = new GUIVersion();
		version.setUsername("Alle Alle");
		version.setComment("comment2");
		version.setId(2);
		version.setTitle("Version " + 2);
		versions[1] = version;
		return versions;
	}
}