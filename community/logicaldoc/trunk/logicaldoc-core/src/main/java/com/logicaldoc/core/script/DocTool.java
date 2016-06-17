package com.logicaldoc.core.script;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.DocumentManager;
import com.logicaldoc.core.document.History;
import com.logicaldoc.core.folder.Folder;
import com.logicaldoc.core.folder.FolderDAO;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.concurrency.NamedThreadFactory;
import com.logicaldoc.util.config.ContextProperties;

/**
 * Utility methods to handle documents from within Velocity
 * 
 * @author Marco Meschieri - LogicalDOC
 * @since 7.3
 */
public class DocTool {

	/**
	 * This executor will be used to execute operations asynchronously
	 */
	protected static ScheduledExecutorService executor = Executors.newScheduledThreadPool(5, new NamedThreadFactory(
			"DocTool"));

	public String downloadUrl(long docId) {
		ContextProperties config = Context.get().getProperties();
		String url = config.getProperty("server.url");
		if (!url.endsWith("/"))
			url += "/";
		url += "download?docId=" + docId;
		return url;
	}

	public String displayUrl(long tenantId, long docId) {
		ContextProperties config = Context.get().getProperties();
		String url = config.getProperty("server.url");
		if (!url.endsWith("/"))
			url += "/";
		url += "frontend.jsp?tenantId=" + tenantId + "&docId=" + docId;
		return url;
	}

	public String downloadUrl(Document doc) {
		return downloadUrl(doc.getId());
	}

	public String downloadUrl(History history) {
		return downloadUrl(history.getDocId());
	}

	public String displayUrl(Document doc) {
		return displayUrl(doc.getTenantId(), doc.getId());
	}

	public String displayUtl(History history) {
		return displayUrl(history.getTenantId(), history.getDocId());
	}

	public void move(final Document doc, final String targetPath, final String username) throws Exception {
		FolderDAO fdao = (FolderDAO) Context.get().getBean(FolderDAO.class);
		UserDAO userDao = (UserDAO) Context.get().getBean(UserDAO.class);
		DocumentManager manager = (DocumentManager) Context.get().getBean(DocumentManager.class);

		Folder parent = doc.getFolder();
		if (targetPath.startsWith("/"))
			parent = fdao.findRoot(doc.getTenantId());

		Folder folder = fdao.createPath(parent, targetPath, true, null);

		History transaction = new History();
		transaction.setDocId(doc.getId());
		transaction.setDate(new Date());
		transaction.setUser(userDao.findByUsername(username));

		manager.moveToFolder(doc, folder, transaction);
	}

	public void copy(final Document doc, final String targetPath, final String username) throws Exception {
		FolderDAO fdao = (FolderDAO) Context.get().getBean(FolderDAO.class);
		UserDAO userDao = (UserDAO) Context.get().getBean(UserDAO.class);
		DocumentManager manager = (DocumentManager) Context.get().getBean(DocumentManager.class);

		Folder parent = doc.getFolder();
		if (targetPath.startsWith("/"))
			parent = fdao.findRoot(doc.getTenantId());

		Folder folder = fdao.createPath(parent, targetPath, true, null);

		History transaction = new History();
		transaction.setDocId(doc.getId());
		transaction.setDate(new Date());
		transaction.setUser(userDao.findByUsername(username));

		manager.copyToFolder(doc, folder, transaction);
	}
}