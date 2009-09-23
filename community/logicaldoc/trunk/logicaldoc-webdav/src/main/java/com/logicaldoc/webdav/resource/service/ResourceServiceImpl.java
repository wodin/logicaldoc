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
import com.logicaldoc.core.document.Version;
import com.logicaldoc.core.document.Version.VERSION_TYPE;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.document.dao.VersionDAO;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.Permission;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.webdav.context.ImportContext;
import com.logicaldoc.webdav.exception.DavResourceIOException;
import com.logicaldoc.webdav.resource.model.Resource;
import com.logicaldoc.webdav.resource.model.ResourceImpl;

/**
 * 
 * @author Sebastian Wenzky
 * 
 */
public class ResourceServiceImpl implements ResourceService {

	protected static Log log = LogFactory.getLog(ResourceServiceImpl.class);

	private static final String FOLDER_PREFIX = "menu.documents";

	private DocumentDAO documentDAO;

	private VersionDAO versionDAO;

	private MenuDAO menuDAO;

	private DocumentManager documentManager;

	private UserDAO userDAO;

	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	public void setDocumentDAO(DocumentDAO documentDAO) {
		this.documentDAO = documentDAO;
	}

	public void setMenuDAO(MenuDAO menuDAO) {
		this.menuDAO = menuDAO;
	}

	public void setDocumentManager(DocumentManager documentManager) {
		this.documentManager = documentManager;
	}

	public ResourceServiceImpl() {
	}

	private Resource marshallFolder(Menu menu, long userId) {

		Resource resource = new ResourceImpl();
		resource.setID(new Long(menu.getId()).toString());
		resource.setContentLength(new Long(0));
		resource.setName(menu.getText());
		resource.setPath(menu.getPathExtended());
		resource.setLastModified(menu.getLastModified());
		resource.isFolder(true);

		MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);

		// define the security policies for this folder
		boolean writeEnabled = mdao.isPermissionEnabled(Permission.WRITE, menu.getId(), userId);
		resource.setWriteEnabled(writeEnabled);

		boolean renameEnabled = mdao.isPermissionEnabled(Permission.RENAME, menu.getId(), userId);
		resource.setRenameEnabled(renameEnabled);

		boolean deleteEnabled = mdao.isPermissionEnabled(Permission.DELETE, menu.getId(), userId);
		resource.setDeleteEnabled(deleteEnabled);

		boolean addChildEnabled = mdao.isPermissionEnabled(Permission.ADD_CHILD, menu.getId(), userId);
		resource.setAddChildEnabled(addChildEnabled);

		return resource;
	}

	private Resource marshallDocument(Document document) {

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

		return resource;
	}

	public List<Resource> getChildResources(Resource parentResource) {

		List<Resource> resourceList = new LinkedList<Resource>();
		final Long folderID = Long.parseLong(parentResource.getID());
		boolean hasAccess = menuDAO.isReadEnable(folderID, parentResource.getRequestedPerson());

		if (hasAccess == false)
			return resourceList;

		Collection<Menu> folders = menuDAO.findChildren(folderID);
		if (folders != null) {
			for (Iterator<Menu> iterator = folders.iterator(); iterator.hasNext();) {
				Menu currentMenu = iterator.next();
				resourceList.add(marshallFolder(currentMenu, parentResource.getRequestedPerson()));
			}
		}

		Collection<Document> documents = documentDAO.findByFolder(folderID);
		for (Iterator<Document> iterator = documents.iterator(); iterator.hasNext();) {
			Document document = iterator.next();
			resourceList.add(marshallDocument(document));
		}

		return resourceList;
	}

	public Resource getResource(String requestPath, long userId) throws DavException {

		if (requestPath == null)
			requestPath = "/";

		requestPath = requestPath.replace("/store", "");

		if (requestPath.length() > 0 && requestPath.substring(0, 1).equals("/"))
			requestPath = requestPath.substring(1);

		String path = "/" + FOLDER_PREFIX + "/" + requestPath;
		String currentStablePath = path;
		String name = null;
		int lastidx = path.lastIndexOf("/");
		if (lastidx > -1) {
			name = path.substring(lastidx + 1, path.length());
			path = path.substring(0, lastidx + 1);
		}
		if (path.equals("/" + FOLDER_PREFIX + "/") && name.equals("")) {
			name = FOLDER_PREFIX;
			path = "/";
		}

		Menu menu = menuDAO.findFolder(name, path);

		// if this resource request is a folder
		if (menu != null)
			return marshallFolder(menu, userId);

		Resource parentMenu = this.getParentResource(currentStablePath, userId);

		Collection<Document> docs = documentDAO.findByFileNameAndParentFolderId(Long.parseLong(parentMenu.getID()),
				name, null);
		if (docs.isEmpty())
			return null;
		Document document = docs.iterator().next();
		boolean hasAccess = menuDAO.isReadEnable(document.getFolder().getId(), userId);

		if (hasAccess == false)
			throw new DavException(DavServletResponse.SC_FORBIDDEN,
					"You have no appropriated rights to read this document");

		return marshallDocument(document);
	}

	public Resource getParentResource(String resourcePath, long userId) {

		resourcePath = resourcePath.replace("/store", "").replace("/vstore", "");
		if (resourcePath.startsWith("/" + FOLDER_PREFIX + "/") == false)
			resourcePath = "/" + FOLDER_PREFIX + resourcePath;

		String name = "";
		String path = resourcePath;
		for (int i = 0; i < 2; i++) {
			int lastidx = resourcePath.lastIndexOf("/");
			if (lastidx > -1) {
				name = resourcePath.substring(lastidx + 1, resourcePath.length());
				resourcePath = resourcePath.substring(0, lastidx);
			}
			if (path.equals("/" + FOLDER_PREFIX + "/") && name.equals("")) {
				name = FOLDER_PREFIX;
				resourcePath = "";
				break;
			}
		}
		resourcePath = resourcePath + "/";

		Menu menu = menuDAO.findFolder(name, resourcePath);
		return marshallFolder(menu, userId);
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
	}

	public Resource createResource(Resource parentResource, String name, boolean isCollection, ImportContext context)
			throws DavException {
	
		Menu parentMenu = menuDAO.findById(Long.parseLong(parentResource.getID()));

		if (isCollection) {
			// check permission to add folder
			boolean addChildEnabled = parentResource.isAddChildEnabled();
			if (!addChildEnabled) {
				throw new DavException(DavServletResponse.SC_FORBIDDEN, "Add Folder not granted to this user");
			}
			Menu createdMenu = menuDAO.createFolder(parentMenu, name);
			return this.marshallFolder(createdMenu, parentResource.getRequestedPerson());
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
				documentManager.create(is, name, parentMenu, user, user.getLocale(), false);
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

	public void updateResource(Resource resource, ImportContext context) throws DavException {
		User user = userDAO.findById(resource.getRequestedPerson());
		Document document = documentDAO.findById(Long.parseLong(resource.getID()));

		try {
			// verify the write permission on the parent folder
			Resource parent = getParentResource(resource);
			if (!parent.isWriteEnabled())
				throw new DavException(DavServletResponse.SC_FORBIDDEN, "No rights to write resource.");

			if ((document.getStatus() == Document.DOC_CHECKED_OUT || document.getStatus() == Document.DOC_LOCKED)
					&& (user.getId() != document.getLockUserId() && !"admin".equals(user.getUserName()))) {
				throw new DavException(DavServletResponse.SC_FORBIDDEN, "User didn't locked the document");
			}

			
			
			documentManager.checkin(Long.parseLong(resource.getID()), context.getInputStream(), resource.getName(),
					user, VERSION_TYPE.NEW_SUBVERSION, "", false);

		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (DavException de) {
			throw de;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Resource getChildByName(Resource parentResource, String name) {
		Menu parentMenu = menuDAO.findById(Long.parseLong(parentResource.getID()));
		Collection<Document> docs = documentDAO.findByFileNameAndParentFolderId(parentMenu.getId(), name, null);
		if (!docs.isEmpty()) {
			Document document = docs.iterator().next();
			return marshallDocument(document);
		}
		return null;
	}

	public boolean resourceExists(Resource resource) {
		return true;
	}

	public Resource updateResource(Resource resource) {
		return null;
	}

	public Resource move(Resource source, Resource destination) throws DavException {

		if (source.isFolder()) {
			if (!source.isRenameEnabled())
				throw new DavException(DavServletResponse.SC_FORBIDDEN, "Rename Rights not granted to this user");

			Menu currentMenu = menuDAO.findById(Long.parseLong(source.getID()));

			long currentParentFolder = currentMenu.getParentId();
			long destinationParentFolder = Long.parseLong(destination.getID());
			// renaming is allowed
			if (currentParentFolder != destinationParentFolder)
				throw new UnsupportedOperationException();

			currentMenu.setText(source.getName());

			if (destination != null)
				currentMenu.setParentId(Long.parseLong(destination.getID()));

			menuDAO.store(currentMenu);
			return this.marshallFolder(currentMenu, source.getRequestedPerson());

		} else {

			// if the destination is null we can't do anything
			if (destination == null)
				throw new UnsupportedOperationException();

			// verify the write permission on source folders
			Resource folder = getParentResource(source);

			boolean writeEnabled = folder.isWriteEnabled();
			if (!writeEnabled)
				throw new DavException(DavServletResponse.SC_FORBIDDEN, "Rename Rights not granted to this user");

			Document document = documentDAO.findById(Long.parseLong(source.getID()));
			documentDAO.initialize(document);
			User user = userDAO.findById(source.getRequestedPerson());

			if (!source.getName().equals(document.getFileName())) {
				// we are doing a file rename
				try {
					documentManager.rename(document, user, source.getName(), false);
				} catch (Exception e) {
					log.warn(e.getMessage(), e);
					throw new RuntimeException(e);
				}
			} else {
				// moving the document to another folder
				boolean destWriteEnabled = destination.isWriteEnabled();
				if (!destWriteEnabled)
					throw new DavException(DavServletResponse.SC_FORBIDDEN, "Write Rights not granted to this user");

				Menu menu = menuDAO.findById(Long.parseLong(destination.getID()));

				try {
					documentManager.moveToFolder(document, menu, user);
				} catch (Exception e) {
					log.warn(e.getMessage(), e);
				}
			}

			return this.marshallDocument(document);
		}
	}

	public void deleteResource(Resource resource) throws DavException {
		try {
			if (resource.isFolder()) {

				if (!resource.isDeleteEnabled())
					throw new DavException(DavServletResponse.SC_FORBIDDEN, "No rights to delete resource.");

				Menu menu = menuDAO.findById(Long.parseLong(resource.getID()));
				User user = userDAO.findById(resource.getRequestedPerson());

				List<Menu> notDeletableFolders = documentManager.deleteFolder(menu, user);
				if (notDeletableFolders.size() > 0) {
					throw new RuntimeException("Unable to delete some subfolders.");
				}
			} else if (!resource.isFolder()) {
				// verify the write permission on the parent folder
				Resource parent = getParentResource(resource);
				if (!parent.isWriteEnabled())
					throw new DavException(DavServletResponse.SC_FORBIDDEN, "No rights to delete resource.");

				documentDAO.delete(Long.parseLong(resource.getID()));
			}
		} catch (DavException de) {
			throw de;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void copyResource(Resource destinationResource, Resource resource) throws DavException {

		if (resource.isFolder() == true) {
			throw new RuntimeException("FolderCopy not supported");
		} else {
			try {
				boolean writeEnabled = destinationResource.isWriteEnabled();
				if (!writeEnabled)
					throw new DavException(DavServletResponse.SC_FORBIDDEN, "No rights to write resource.");

				Document document = documentDAO.findById(Long.parseLong(resource.getID()));
				Menu menu = menuDAO.findById(Long.parseLong(destinationResource.getID()));

				User user = userDAO.findById(resource.getRequestedPerson());

				documentManager.copyToFolder(document, menu, user);
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
		return this.marshallFolder(document.getFolder(), resource.getRequestedPerson());
	}

	@Override
	public void streamIn(Resource resource, InputStream is) {
		throw new AbstractMethodError();
	}

	@Override
	public InputStream streamOut(Resource resource) {
		String version = resource.getVersionLabel();
		Document document = documentDAO.findById(Long.parseLong(resource.getID()));

		if (document.getVersion().equals(resource.getVersionLabel()))
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
	public void checkout(Resource resource) throws DavException {

		User user = userDAO.findById(resource.getRequestedPerson());

		// verify the write permission on the parent folder
		Resource parent = getParentResource(resource);
		if (!parent.isWriteEnabled())
			throw new DavException(DavServletResponse.SC_FORBIDDEN, "No rights to checkout resource.");

		try {
			documentManager.checkout(Long.parseLong(resource.getID()), user);
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
			Resource res = marshallDocument(document);
			res.setVersionLabel(version.getVersion());
			res.setVersionDate(version.getDate());
			res.setAuthor(version.getUsername());
			res.setIsCheckedOut(true);
			resourceHistory.add(res);
		}

		return resourceHistory;
	}

	public void uncheckout(Resource resource) {
		try {
			User user = userDAO.findById(resource.getRequestedPerson());
			documentManager.unlock(Long.parseLong(resource.getID()), user, "");

			resource.setIsCheckedOut(false);

		} catch (Exception e) {
			log.error(e);
			throw new RuntimeException(e);
		}
	}

	public void setVersionDAO(VersionDAO versionDAO) {
		this.versionDAO = versionDAO;
	}

}
