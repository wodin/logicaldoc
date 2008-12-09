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

import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.DocumentManager;
import com.logicaldoc.core.document.Version;
import com.logicaldoc.core.document.Version.VERSION_TYPE;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.document.dao.HistoryDAO;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.core.security.dao.UserDAO;
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

	private static final String FOLDER_PREFIX = "db.projects";

	private DocumentDAO documentDAO;

	private MenuDAO menuDAO;

	private DocumentManager documentManager;

	private UserDAO userDAO;

	private HistoryDAO historyDAO;

	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	public void setDocumentDAO(DocumentDAO documentDAO) {
		this.documentDAO = documentDAO;
	}

	public void setMenuDAO(MenuDAO menuDAO) {
		this.menuDAO = menuDAO;
	}

	public void setHistoryDAO(HistoryDAO historyDAO) {
		this.historyDAO = historyDAO;
	}

	public void setDocumentManager(DocumentManager documentManager) {
		this.documentManager = documentManager;
	}

	public ResourceServiceImpl() {
	}

	private Resource marshallFolder(Menu menu) {
		Resource resource = new ResourceImpl();
		resource.setID(new Long(menu.getId()).toString());
		resource.setContentLength(new Long(0));
		resource.setName(menu.getText());
		resource.setPath(menu.getPathExtended());
		resource.setLastModified(menu.getLastModified());
		resource.isFolder(true);
		return resource;
	}

	private Resource marshallDocument(Document document) {
		Resource resource = new ResourceImpl();
		resource.setID(new Long(document.getId()).toString());
		resource.setName(document.getTitle() + "." + document.getType());
		resource.setContentLength(document.getFileSize());
		resource.setCreationDate(document.getCreation());
		resource.setLastModified(document.getDate());
		resource.isFolder(false);
		resource.setIsCheckedOut(document.getStatus() == Document.DOC_CHECKED_OUT);
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
				resourceList.add(marshallFolder(currentMenu));
			}
		}

		Collection<Document> documents = documentDAO.findByFolder(folderID);
		for (Iterator<Document> iterator = documents.iterator(); iterator.hasNext();) {
			Document document = iterator.next();
			resourceList.add(marshallDocument(document));
		}

		return resourceList;
	}

	public Resource getResource(String requestPath, long id) {
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
			return marshallFolder(menu);

		Resource parentMenu = this.getParentResource(currentStablePath);
		String title = name.substring(0, name.lastIndexOf(".") > 0 ? name.lastIndexOf(".") : name.length());
		Collection<Document> docs = documentDAO.findByTitleAndParentFolderId(Long.parseLong(parentMenu.getID()), title);
		if (docs.isEmpty())
			return null;
		Document document = docs.iterator().next();
		boolean hasAccess = menuDAO.isReadEnable(document.getFolder().getId(), id);

		if (hasAccess == false)
			throw new SecurityException("You have no appropriated rights to read this document");

		return marshallDocument(document);
	}

	public Resource getParentResource(String resourcePath) {
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
		return marshallFolder(menu);
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
	}

	public Resource createResource(Resource parentResource, String name, boolean isCollection, ImportContext context) {
		Menu parentMenu = menuDAO.findById(Long.parseLong(parentResource.getID()));

		if (menuDAO.isWriteEnable(parentMenu.getId(), parentResource.getRequestedPerson()) == false)
			throw new SecurityException("Write Access not applied to this user");

		if (isCollection == true) {
			Menu createdMenu = menuDAO.createFolder(parentMenu, name);
			return this.marshallFolder(createdMenu);
		}

		User user = userDAO.findById(parentResource.getRequestedPerson());

		InputStream is = context.getInputStream();
		try {
			try {
				documentManager.create(is, name, parentMenu, user, user.getLanguage(), false);
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

	public void updateResource(Resource resource, ImportContext context) {
		User user = userDAO.findById(resource.getRequestedPerson());

		Document document = documentDAO.findById(Long.parseLong(resource.getID()));

		try {
			if (document.getStatus() == Document.DOC_CHECKED_OUT) {
				documentManager.checkin(Long.parseLong(resource.getID()), context.getInputStream(), resource.getName(),
						user, VERSION_TYPE.NEW_SUBVERSION, "", false);
			} else {
				documentManager.delete(document.getId());

				this.createResource(context.getResource(), context.getSystemId(), false, context);
			}

		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Resource getChildByName(Resource parentResource, String name) {
		Menu parentMenu = menuDAO.findById(Long.parseLong(parentResource.getID()));
		String title = name.substring(0, name.lastIndexOf(".") > 0 ? name.lastIndexOf(".") : name.length());
		Collection<Document> docs = documentDAO.findByTitleAndParentFolderId(parentMenu.getId(), title);
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

	public Resource move(Resource target, Resource destination) {

		if (target.isFolder() == true) {
			Menu currentMenu = menuDAO.findById(Long.parseLong(target.getID()));

			long currentParentFolder = currentMenu.getParentId();
			long destinationParentFolder = Long.parseLong(destination.getID());
			// renaming is allowed
			if (currentParentFolder != destinationParentFolder)
				throw new UnsupportedOperationException();

			currentMenu.setText(target.getName());

			if (destination != null)
				currentMenu.setParentId(Long.parseLong(destination.getID()));

			menuDAO.store(currentMenu);
			return this.marshallFolder(currentMenu);

		} else {
			// if the destination is null, then the user want to change the
			// filename,
			// but this is not supported by logicalDOC
			if (destination == null)
				throw new UnsupportedOperationException();

			Document document = documentDAO.findById(Long.parseLong(target.getID()));
			documentDAO.initialize(document);
			String name = target.getName();
			String ext = "";
			if (name.contains(".")) {
				ext = name.substring(name.lastIndexOf(".") + 1);
				name = name.substring(0, name.lastIndexOf("."));
			}

			document.setTitle(name);
			document.setType(ext);
			documentDAO.store(document);
			Menu menu = menuDAO.findById(Long.parseLong(destination.getID()));

			try {
				documentManager.moveToFolder(document, menu);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}

			return this.marshallDocument(document);
		}
	}

	public void deleteResource(Resource resource) {
		try {
			if (resource.isFolder() == true)
				menuDAO.delete(Long.parseLong(resource.getID()));
			else
				documentManager.delete(Long.parseLong(resource.getID()));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void copyResource(Resource destinationResource, Resource resource) {

		log.error("copyResource");

		if (resource.isFolder() == true) {
			throw new RuntimeException("FolderCopy not supported");
		} else {
			try {
				log.info("resource.getID() = " + resource.getID());
				log.info("destinationResource.getID() = " + destinationResource.getID());
				Document document = documentDAO.findById(Long.parseLong(resource.getID()));
				Menu menu = menuDAO.findById(Long.parseLong(destinationResource.getID()));

				log.info("document = " + document);
				log.info("document.getFileName() = " + document.getFileName());

				log.info("menu = " + menu);
				log.info("menu.getText() = " + menu.getText());
				log.info("menu.getPath() = " + menu.getPath());

				User user = userDAO.findById(resource.getRequestedPerson());
				log.info("user = " + user);

				documentManager.copyToFolder(document, menu, user);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public Resource getParentResource(Resource resource) {
		Document document = documentDAO.findById(Long.parseLong(resource.getID()));
		return this.marshallFolder(document.getFolder());
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
	public void checkout(Resource resource) {
		User user = userDAO.findById(resource.getRequestedPerson());
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

		Collection<Version> tmp = document.getVersions();
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
			documentManager.uncheckout(Long.parseLong(resource.getID()), user);

			resource.setIsCheckedOut(false);

		} catch (Exception e) {
			log.error(e);
			throw new RuntimeException(e);
		}
	}

}
