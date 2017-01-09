package com.logicaldoc.core.automation;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.DocumentManager;
import com.logicaldoc.core.document.History;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.folder.Folder;
import com.logicaldoc.core.folder.FolderDAO;
import com.logicaldoc.core.security.Tenant;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.TenantDAO;
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

		TenantDAO tenantDao = (TenantDAO) Context.get().getBean(TenantDAO.class);
		Tenant tenant = tenantDao.findById(tenantId);

		url += "display?tenant=" + tenant.getName() + "&docId=" + docId;
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

	public void store(Document doc) {
		DocumentDAO docDao = (DocumentDAO) Context.get().getBean(DocumentDAO.class);
		docDao.store(doc);
	}

	public void store(Document doc, String username) {
		UserDAO userDao = (UserDAO) Context.get().getBean(UserDAO.class);
		DocumentDAO docDao = (DocumentDAO) Context.get().getBean(DocumentDAO.class);

		History transaction = new History();
		transaction.setDocId(doc.getId());
		transaction.setDate(new Date());
		transaction.setUser(userDao.findByUsername(username));

		docDao.store(doc, transaction);
	}

	public void move(Document doc, String targetPath, String username) throws Exception {
		UserDAO userDao = (UserDAO) Context.get().getBean(UserDAO.class);
		DocumentManager manager = (DocumentManager) Context.get().getBean(DocumentManager.class);

		User user = userDao.findByUsername(username);

		Folder folder = createPath(doc, targetPath);

		History transaction = new History();
		transaction.setDocId(doc.getId());
		transaction.setTenantId(doc.getTenantId());
		transaction.setDate(new Date());
		transaction.setUser(user);

		manager.moveToFolder(doc, folder, transaction);
	}

	public void copy(Document doc, String targetPath, String username) throws Exception {
		UserDAO userDao = (UserDAO) Context.get().getBean(UserDAO.class);
		DocumentManager manager = (DocumentManager) Context.get().getBean(DocumentManager.class);

		Folder folder = createPath(doc, targetPath);

		History transaction = new History();
		transaction.setDocId(doc.getId());
		transaction.setDate(new Date());
		transaction.setUser(userDao.findByUsername(username));

		manager.copyToFolder(doc, folder, transaction);
	}

	private Folder createPath(Document doc, String targetPath) {
		FolderDAO fdao = (FolderDAO) Context.get().getBean(FolderDAO.class);
		Folder parent = doc.getFolder();
		if (targetPath.startsWith("/")) {
			targetPath = targetPath.substring(1);
			/*
			 * Cannot write in the root so if the parent is the root, we have to
			 * guarantee that the first element in the path is a workspace. If
			 * not the Default one will be used.
			 */
			parent = fdao.findRoot(doc.getTenantId());
			Folder workspace = null;

			/*
			 * Check if the path contains the workspace specification
			 */
			for (Folder w : fdao.findWorkspaces(doc.getTenantId())) {
				if (targetPath.startsWith(w.getName())) {
					workspace = fdao.findById(w.getId());
					break;
				}
			}

			if (workspace == null) {
				parent = fdao.findDefaultWorkspace(doc.getTenantId());
			}
		}

		Folder folder = fdao.createPath(parent, targetPath, true, null);
		return folder;
	}
}