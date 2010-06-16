package com.logicaldoc.webdav.resource.service;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavServletResponse;

import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.DocumentManager;
import com.logicaldoc.core.document.History;
import com.logicaldoc.core.document.Version;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.document.dao.FolderDAO;
import com.logicaldoc.core.document.dao.VersionDAO;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.webdav.context.ImportContext;
import com.logicaldoc.webdav.exception.DavResourceIOException;
import com.logicaldoc.webdav.resource.model.Resource;
import com.logicaldoc.webdav.resource.model.ResourceImpl;
import com.logicaldoc.webdav.session.DavSession;

/**
 * 
 * @author Sebastian Wenzky
 * 
 */
public class ResourceServiceImpl implements ResourceService {

	protected static Log log = LogFactory.getLog(ResourceServiceImpl.class);

	private DocumentDAO documentDAO;

	private VersionDAO versionDAO;

	private FolderDAO folderDAO;

	private DocumentManager documentManager;

	private UserDAO userDAO;

	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	public void setDocumentDAO(DocumentDAO documentDAO) {
		this.documentDAO = documentDAO;
	}

	public void setDocumentManager(DocumentManager documentManager) {
		this.documentManager = documentManager;
	}

	public ResourceServiceImpl() {
	}

	private Resource marshallFolder(Menu menu, long userId, DavSession session) {
		Resource resource = new ResourceImpl();
		resource.setID(new Long(menu.getId()).toString());
		resource.setContentLength(new Long(0));
		resource.setName(menu.getText());
		resource.setPath(folderDAO.computePathExtended(menu.getId()));
		resource.setLastModified(menu.getLastModified());
		resource.setSession(session);
		resource.isFolder(true);

		// MenuDAO mdao = (MenuDAO)
		// Context.getInstance().getBean(MenuDAO.class);

		// // define the security policies for this folder
		// boolean writeEnabled = mdao.isPermissionEnabled(Permission.WRITE,
		// menu.getId(), userId);
		// resource.setWriteEnabled(writeEnabled);
		//
		// boolean renameEnabled = mdao.isPermissionEnabled(Permission.RENAME,
		// menu.getId(), userId);
		// resource.setRenameEnabled(renameEnabled);
		//
		// boolean deleteEnabled = mdao.isPermissionEnabled(Permission.DELETE,
		// menu.getId(), userId);
		// resource.setDeleteEnabled(deleteEnabled);
		//
		// boolean addChildEnabled =
		// mdao.isPermissionEnabled(Permission.ADD_CHILD, menu.getId(), userId);
		// resource.setAddChildEnabled(addChildEnabled);

		return resource;
	}

	private Resource marshallDocument(Document document, DavSession session) {
		Resource resource = new ResourceImpl();
		resource.setID(new Long(document.getId()).toString());
		// We cannot use the title because it can contain
		// resource.setName(document.getTitle() + "." + document.getType());
		resource.setName(document.getFileName());
		resource.setContentLength(document.getFileSize());
		resource.setCreationDate(document.getCreation());
		resource.setLastModified(document.getDate());
		resource.isFolder(false);
		resource.setIsCheckedOut(document.getStatus() == Document.DOC_CHECKED_OUT
				|| document.getStatus() == Document.DOC_LOCKED);
		resource.setVersionLabel(document.getVersion());
		resource.setAuthor(document.getPublisher());
		resource.setSession(session);

		return resource;
	}

	public List<Resource> getChildResources(Resource parentResource) {
		List<Resource> resourceList = new LinkedList<Resource>();
		final Long folderID = Long.parseLong(parentResource.getID());
		boolean hasAccess = folderDAO.isReadEnable(folderID, parentResource.getRequestedPerson());

		if (hasAccess == false)
			return resourceList;

		// Find children visible by the current user
		MenuDAO menuDAO = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		Collection<Menu> folders = menuDAO.findChildren(folderID, parentResource.getRequestedPerson());
		if (folders != null) {
			for (Iterator<Menu> iterator = folders.iterator(); iterator.hasNext();) {
				Menu currentMenu = iterator.next();
				resourceList.add(marshallFolder(currentMenu, parentResource.getRequestedPerson(), null));
			}
		}

		Collection<Document> documents = documentDAO.findByFolder(folderID);
		for (Iterator<Document> iterator = documents.iterator(); iterator.hasNext();) {
			Document document = iterator.next();
			resourceList.add(marshallDocument(document, null));
		}

		return resourceList;
	}

	public Resource getResource(String requestPath, DavSession session) throws DavException {
		log.debug("Find DAV resource: " + requestPath);

		long userId = (Long) session.getObject("id");
		if (requestPath == null)
			requestPath = "/";

		requestPath = requestPath.replace("/store", "");

		if (requestPath.length() > 0 && requestPath.substring(0, 1).equals("/"))
			requestPath = requestPath.substring(1);

		String path = "/" + requestPath;
		String currentStablePath = path;
		String name = null;
		int lastidx = path.lastIndexOf("/");
		if (lastidx > -1) {
			name = path.substring(lastidx + 1, path.length());
			path = path.substring(0, lastidx + 1);
		}

		Menu menu = null;

		if (path.equals("/") && name.equals("")) {
			menu = folderDAO.findById(Menu.MENUID_DOCUMENTS);
		} else
			menu = folderDAO.find(name, path);

		// if this resource request is a folder
		if (menu != null)
			return marshallFolder(menu, userId, session);

		Resource parentFolder = this.getParentResource(currentStablePath, userId);

		Collection<Document> docs = documentDAO.findByFileNameAndParentFolderId(Long.parseLong(parentFolder.getID()),
				name, null);

		if (docs.isEmpty())
			return null;
		Document document = docs.iterator().next();
		boolean hasAccess = folderDAO.isReadEnable(document.getFolder().getId(), userId);

		if (hasAccess == false)
			throw new DavException(DavServletResponse.SC_FORBIDDEN,
					"You have no appropriated rights to read this document");

		return marshallDocument(document, session);
	}

	/**
	 * @see com.logicaldoc.webdav.resource.service.ResourceService#getParentResource(java.lang.String,
	 *      long)
	 */
	public Resource getParentResource(String resourcePath, long userId) {
		log.debug("Find parent DAV resource: " + resourcePath);

		resourcePath = resourcePath.replaceFirst("/store", "").replaceFirst("/vstore", "");
		if (!resourcePath.startsWith("/"))
			resourcePath = "/" + resourcePath;

		String name = "";
		resourcePath = resourcePath.substring(0, resourcePath.lastIndexOf('/'));
		if (!resourcePath.isEmpty()) {
			name = resourcePath.substring(resourcePath.lastIndexOf('/'));
			resourcePath = resourcePath.substring(0, resourcePath.lastIndexOf('/'));
		}

		resourcePath = resourcePath + "/";
		if (name.startsWith("/"))
			name = name.substring(1);

		log.debug("Find DMS resource " + name + " in path " + resourcePath);

		Menu menu = null;
		if ("/".equals(resourcePath.trim()) && "".equals(name))
			menu = folderDAO.findById(Menu.MENUID_DOCUMENTS);
		else
			menu = folderDAO.find(name, resourcePath);

		return marshallFolder(menu, userId, null);
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
	}

	public Resource createResource(Resource parentResource, String name, boolean isCollection, ImportContext context,
			DavSession session) throws DavException {

		Menu parentMenu = folderDAO.findById(Long.parseLong(parentResource.getID()));
		String sid = null;
		if (session != null)
			sid = (String) session.getObject("sid");

		if (isCollection) {
			// check permission to add folder
			boolean addChildEnabled = parentResource.isAddChildEnabled();
			if (!addChildEnabled) {
				throw new DavException(DavServletResponse.SC_FORBIDDEN, "Add Folder not granted to this user");
			}

			User user = (User) session.getObject("user");
			// Add a folder history entry
			History transaction = new History();
			transaction.setUser(user);
			transaction.setSessionId(sid);
			Menu createdMenu = folderDAO.create(parentMenu, name, transaction);
			return this.marshallFolder(createdMenu, parentResource.getRequestedPerson(), session);
		}

		// check permission to add document
		boolean writeEnabled = parentResource.isWriteEnabled();
		if (!writeEnabled) {
			throw new DavException(DavServletResponse.SC_FORBIDDEN, "Write Access not granted to this user");
		}

		User user = userDAO.findById(parentResource.getRequestedPerson());

		InputStream is = context.getInputStream();
		try {
			try {
				// Create the document history event
				History transaction = new History();
				transaction.setSessionId(sid);
				transaction.setEvent(History.EVENT_STORED);
				transaction.setComment("");
				transaction.setUser(user);

				Document doc = new Document();
				doc.setFileName(name);
				doc.setFolder(parentMenu);
				doc.setLocale(user.getLocale());

				documentManager.create(is, doc, transaction, false);
			} catch (Exception e) {
				log.error(e);
			} finally {
				is.close();
			}
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}

		return null;
	}

	public void updateResource(Resource resource, ImportContext context, DavSession session) throws DavException {
		User user = userDAO.findById(resource.getRequestedPerson());
		Document document = documentDAO.findById(Long.parseLong(resource.getID()));
		String sid = (String) session.getObject("sid");

		try {
			// verify the write permission on the parent folder
			Resource parent = getParentResource(resource);
			if (!parent.isWriteEnabled())
				throw new DavException(DavServletResponse.SC_FORBIDDEN, "No rights to write resource.");

			if ((document.getStatus() == Document.DOC_CHECKED_OUT || document.getStatus() == Document.DOC_LOCKED)
					&& (user.getId() != document.getLockUserId() && !"admin".equals(user.getUserName()))) {
				throw new DavException(DavServletResponse.SC_FORBIDDEN, "User didn't locked the document");
			}

			if (document.getImmutable() == 1 && !user.isInGroup("admin"))
				throw new DavException(DavServletResponse.SC_FORBIDDEN, "The document is immutable");

			// Create the document history event
			History transaction = new History();
			transaction.setSessionId(sid);
			transaction.setUser(user);
			transaction.setComment("");

			documentManager.checkin(Long.parseLong(resource.getID()), context.getInputStream(), resource.getName(),
					false, false, transaction);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (DavException de) {
			throw de;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Resource getChildByName(Resource parentResource, String name) {
		Menu parentMenu = folderDAO.findById(Long.parseLong(parentResource.getID()));
		Collection<Document> docs = documentDAO.findByFileNameAndParentFolderId(parentMenu.getId(), name, null);
		if (!docs.isEmpty()) {
			Document document = docs.iterator().next();
			return marshallDocument(document, null);
		}
		return null;
	}

	public boolean resourceExists(Resource resource) {
		return true;
	}

	public Resource updateResource(Resource resource) {
		return null;
	}

	public Resource move(Resource source, Resource destination, DavSession session) throws DavException {
		String sid = (String) session.getObject("sid");

		if (source.isFolder()) {
			return folderRenameOrMove(source, destination, session, sid);
		} else {
			return fileRenameOrMove(source, destination, session, sid);
		}
	}

	private Resource fileRenameOrMove(Resource source, Resource destination, DavSession session, String sid)
			throws DavException {
		// The source is a file so the requested operation can be: file
		// rename/file move

		// if the destination is null we can't do anything
		if (destination == null)
			throw new UnsupportedOperationException();

		// verify the write permission on source folders
		Resource folder = getParentResource(source);

		boolean writeEnabled = folder.isWriteEnabled();
		if (!writeEnabled)
			throw new DavException(DavServletResponse.SC_FORBIDDEN, "Rename Rights not granted to this user");

		Document document = documentDAO.findById(Long.parseLong(source.getID()));
		User user = userDAO.findById(source.getRequestedPerson());
		if (document.getImmutable() == 1 && !user.isInGroup("admin"))
			throw new DavException(DavServletResponse.SC_FORBIDDEN, "The document is immutable");

		documentDAO.initialize(document);

		// Create the document history event
		History transaction = new History();
		transaction.setSessionId(sid);
		transaction.setUser(user);

		if (!source.getName().equals(document.getFileName())) {
			// we are doing a file rename
			try {
				documentManager.rename(document, source.getName(), false, transaction);
			} catch (Exception e) {
				log.warn(e.getMessage(), e);
				throw new RuntimeException(e);
			}
		} else {
			// moving the document to another folder
			boolean destWriteEnabled = destination.isWriteEnabled();
			if (!destWriteEnabled)
				throw new DavException(DavServletResponse.SC_FORBIDDEN, "Write Rights not granted to this user");

			Menu menu = folderDAO.findById(Long.parseLong(destination.getID()));

			try {
				if (document.getDocRef() != null)
					transaction.setEvent(History.EVENT_SHORTCUT_MOVED);
				documentManager.moveToFolder(document, menu, transaction);
			} catch (Exception e) {
				log.warn(e.getMessage(), e);
			}
		}

		return this.marshallDocument(document, session);
	}

	private Resource folderRenameOrMove(Resource source, Resource destination, DavSession session, String sid)
			throws DavException {

		if (!source.isRenameEnabled())
			throw new DavException(DavServletResponse.SC_FORBIDDEN, "Rename Rights not granted to this user");

		Menu currentMenu = folderDAO.findById(Long.parseLong(source.getID()));

		long currentParentFolder = currentMenu.getParentId();
		long destinationParentFolder = Long.parseLong(destination.getID());

		// distinction between folder move and folder rename
		if (currentParentFolder != destinationParentFolder) {
			// Folder Move

			Menu destParentMenu = folderDAO.findById(Long.parseLong(destination.getID()));

			// check the delete on the parent of the source to move
			Resource sourceParent = getParentResource(source);
			if (!sourceParent.isDeleteEnabled())
				throw new DavException(DavServletResponse.SC_FORBIDDEN, "No rights to delete resource.");

			// verify the addchild permission on destination folders
			boolean addchildEnabled = destination.isAddChildEnabled();
			if (!addchildEnabled)
				throw new DavException(DavServletResponse.SC_FORBIDDEN, "AddChild Rights not granted to this user");

			User user = (User) session.getObject("user");
			// Add a folder history entry
			History transaction = new History();
			transaction.setSessionId(sid);
			transaction.setUser(user);

			// we are doing a file rename
			try {
				folderDAO.move(currentMenu, destParentMenu, transaction);
			} catch (Exception e) {
				log.warn(e.getMessage(), e);
				throw new DavException(DavServletResponse.SC_INTERNAL_SERVER_ERROR, "Error during Folder Move");
			}

			return this.marshallFolder(currentMenu, source.getRequestedPerson(), session);
		} else {
			// Folder Rename
			currentMenu.setText(source.getName());

			User user = (User) session.getObject("user");
			// Add a folder history entry
			History transaction = new History();
			transaction.setUser(user);
			transaction.setEvent(History.EVENT_FOLDER_RENAMED);
			transaction.setSessionId(sid);
			folderDAO.store(currentMenu, transaction);

			if (destination != null)
				currentMenu.setParentId(Long.parseLong(destination.getID()));

			folderDAO.store(currentMenu);
			return this.marshallFolder(currentMenu, source.getRequestedPerson(), session);
		}
	}

	public void deleteResource(Resource resource, DavSession session) throws DavException {
		String sid = (String) session.getObject("sid");
		Menu menu = folderDAO.findById(Long.parseLong(resource.getID()));
		User user = userDAO.findById(resource.getRequestedPerson());

		// Add a folder history entry
		History transaction = new History();
		transaction.setUser(user);
		transaction.setSessionId(sid);
		transaction.setUser(user);

		try {
			if (resource.isFolder()) {
				if (!resource.isDeleteEnabled())
					throw new DavException(DavServletResponse.SC_FORBIDDEN, "No rights to delete resource.");

				transaction.setEvent(History.EVENT_FOLDER_DELETED);
				List<Menu> notDeletableFolders = folderDAO.deleteTree(menu, transaction);
				if (notDeletableFolders.size() > 0) {
					throw new RuntimeException("Unable to delete some subfolders.");
				}
			} else if (!resource.isFolder()) {
				// verify the write permission on the parent folder
				Resource parent = getParentResource(resource);
				if (!parent.isWriteEnabled())
					throw new DavException(DavServletResponse.SC_FORBIDDEN, "No rights to delete resource.");
				transaction.setEvent(History.EVENT_DELETED);

				if (documentDAO.findById(Long.parseLong(resource.getID())).getImmutable() == 1
						&& !user.isInGroup("admin"))
					throw new DavException(DavServletResponse.SC_FORBIDDEN, "The document is immutable");

				// Check if there are some shortcuts associated to the
				// deleting document. All the shortcuts must be deleted.
				if (documentDAO.findShortcutIds(Long.parseLong(resource.getID())).size() > 0)
					for (Long shortcutId : documentDAO.findShortcutIds(Long.parseLong(resource.getID()))) {
						documentDAO.delete(shortcutId);
					}
				documentDAO.delete(Long.parseLong(resource.getID()));
			}
		} catch (DavException de) {
			throw de;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void copyResource(Resource destinationResource, Resource resource, DavSession session) throws DavException {
		String sid = (String) session.getObject("sid");

		if (resource.isFolder() == true) {
			throw new RuntimeException("FolderCopy not supported");
		} else {
			try {
				boolean writeEnabled = destinationResource.isWriteEnabled();
				if (!writeEnabled)
					throw new DavException(DavServletResponse.SC_FORBIDDEN, "No rights to write resource.");

				Document document = documentDAO.findById(Long.parseLong(resource.getID()));
				Menu menu = folderDAO.findById(Long.parseLong(destinationResource.getID()));

				User user = userDAO.findById(resource.getRequestedPerson());

				if (documentDAO.findById(Long.parseLong(resource.getID())).getImmutable() == 1
						&& !user.isInGroup("admin"))
					throw new DavException(DavServletResponse.SC_FORBIDDEN, "The document is immutable");

				// Create the document history event
				History transaction = new History();
				transaction.setSessionId(sid);
				transaction.setEvent(History.EVENT_STORED);
				transaction.setComment("");
				transaction.setUser(user);

				if (document.getDocRef() != null) {
					document = documentDAO.findById(document.getDocRef());
					documentManager.createShortcut(document, menu, transaction);
				} else {
					documentManager.copyToFolder(document, menu, transaction);
				}
			} catch (DavException de) {
				log.info(de.getMessage(), de);
				throw de;
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public Resource getParentResource(Resource resource) {
		Document document = documentDAO.findById(Long.parseLong(resource.getID()));
		return this.marshallFolder(document.getFolder(), resource.getRequestedPerson(), null);
	}

	@Override
	public void streamIn(Resource resource, InputStream is) {
		throw new AbstractMethodError();
	}

	@Override
	public InputStream streamOut(Resource resource) {
		String version = resource.getVersionLabel();
		Document document = documentDAO.findById(Long.parseLong(resource.getID()));

		if (document.getVersion() != null && document.getVersion().equals(resource.getVersionLabel()))
			version = null;

		if (document == null) {
			// Document not found
			return new ByteArrayInputStream(new String("not found").getBytes());
		}

		File file = null;

		if (version == null || version.equals(""))
			file = documentManager.getDocumentFile(document, null);
		else
			file = documentManager.getDocumentFile(document, resource.getVersionLabel());

		try {
			FileInputStream fis = new FileInputStream(file);
			return new BufferedInputStream(fis, 2048);
		} catch (IOException e) {
			throw new DavResourceIOException(e.getMessage());
		}
	}

	@Override
	public void checkout(Resource resource, DavSession session) throws DavException {
		String sid = (String) session.getObject("sid");

		User user = userDAO.findById(resource.getRequestedPerson());

		// verify the write permission on the parent folder
		Resource parent = getParentResource(resource);
		if (!parent.isWriteEnabled())
			throw new DavException(DavServletResponse.SC_FORBIDDEN, "No rights to checkout resource.");

		try {
			// Create the document history event
			History transaction = new History();
			transaction.setSessionId(sid);
			transaction.setEvent(History.EVENT_CHECKEDOUT);
			transaction.setComment("");
			transaction.setUser(user);

			documentManager.checkout(Long.parseLong(resource.getID()), transaction);
		} catch (NumberFormatException e) {
			throw new RuntimeException(e);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public boolean isCheckedOut(Resource resource) {
		Document document = documentDAO.findById(Long.parseLong(resource.getID()));
		return document.getStatus() == Document.DOC_CHECKED_OUT;
	}

	public List<Resource> getHistory(Resource resource) {
		List<Resource> resourceHistory = new LinkedList<Resource>();

		Document document = documentDAO.findById(Long.parseLong(resource.getID()));
		documentDAO.initialize(document);

		Collection<Version> tmp = versionDAO.findByDocId(document.getId());
		Version[] sortIt = (Version[]) tmp.toArray(new Version[0]);

		// clear collection and add sorted elements
		Arrays.sort(sortIt);

		for (Version version : sortIt) {
			Resource res = marshallDocument(document, null);
			res.setVersionLabel(version.getVersion());
			res.setVersionDate(version.getDate());
			res.setAuthor(version.getUsername());
			res.setIsCheckedOut(true);
			resourceHistory.add(res);
		}

		return resourceHistory;
	}

	public void uncheckout(Resource resource, DavSession session) {
		String sid = (String) session.getObject("sid");
		try {
			User user = userDAO.findById(resource.getRequestedPerson());
			// Create the document history event
			History transaction = new History();
			transaction.setSessionId(sid);
			transaction.setUser(user);

			documentManager.unlock(Long.parseLong(resource.getID()), transaction);

			resource.setIsCheckedOut(false);

		} catch (Exception e) {
			log.error(e);
			throw new RuntimeException(e);
		}
	}

	public void setVersionDAO(VersionDAO versionDAO) {
		this.versionDAO = versionDAO;
	}

	public void setFolderDAO(FolderDAO folderDAO) {
		this.folderDAO = folderDAO;
	}
}
