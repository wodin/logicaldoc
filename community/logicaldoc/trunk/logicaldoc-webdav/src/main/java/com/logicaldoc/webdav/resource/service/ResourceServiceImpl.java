package com.logicaldoc.webdav.resource.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.DocumentManager;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.webdav.context.ImportContext;
import com.logicaldoc.webdav.exception.DavResourceIOException;
import com.logicaldoc.webdav.exception.DavResourceNotFoundException;
import com.logicaldoc.webdav.exception.OperationNotSupportedException;
import com.logicaldoc.webdav.resource.model.Resource;
import com.logicaldoc.webdav.resource.model.ResourceImpl;

/**
 * 
 * @author Sebastian Wenzky
 *
 */
public class ResourceServiceImpl implements ResourceService {

	private static final String FOLDER_PREFIX = "db.projects";

	private DocumentDAO documentDAO;
	
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

	private Resource marshallFolder(Menu menu){
		Resource resource = new ResourceImpl();
		resource.setID( new Long(menu.getId()).toString() );
		resource.setContentLength( new Long(0) );
		resource.setName( menu.getText() );
		resource.setPath( menu.getPathExtended() );
		resource.isFolder(true);
		return resource;
	}
	

	private Resource marshallDocument(Document document){
		Resource resource = new ResourceImpl();
		resource.setID( new Long(document.getId()).toString() );
		resource.setContentLength( document.getFileSize() );
		resource.setName( document.getFileName() );
		resource.isFolder(false);
		
		return resource;
	}
	
	public List<Resource> getChildResources(long folderID) {
		List<Resource> resourceList = new LinkedList<Resource>();
		
		Collection<Menu> folders = menuDAO.findChildren(folderID);	
		if(folders != null){
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
	
	public List<Resource> getChildResources(String requestPath) {
		
		//if we browsing on the root, we have no requestPath greater then zero
		if(requestPath.length() > 0)
			requestPath = requestPath.substring(1);

		
		int idx = requestPath.lastIndexOf("/");
		if(idx+1 == requestPath.length() != true)
			requestPath = requestPath + "/";
		
		if(idx != -1)
			requestPath = requestPath.substring(0, requestPath.lastIndexOf("/")+1);
		
		String path = "/" + FOLDER_PREFIX + "/"+requestPath;
		
		List<Resource> resourceList = new LinkedList<Resource>();
		
		Collection<Menu> folders = menuDAO.findFoldersByPathExtended(path);
		
		if(folders != null){
			for (Iterator<Menu> iterator = folders.iterator(); iterator.hasNext();) {
				Menu currentMenu = iterator.next();
				resourceList.add(marshallFolder(currentMenu));			
			}
		}
		
		//now we have all folders, its time for akquiring documents
		Resource parentFolderResource = this.getResorce(requestPath);
		
		Collection<Document> documents = documentDAO.findByFolder(Integer.parseInt(parentFolderResource.getID()));
		
		for (Iterator<Document> iterator = documents.iterator(); iterator.hasNext();) {
			Document document = iterator.next();
			resourceList.add(marshallDocument(document));
		}
		
		
		return resourceList;
	}

	public Resource getResorce(String requestPath) {
		if(requestPath == null)
			requestPath = "/";

		if( requestPath.length() > 0 && requestPath.substring(0,1).equals("/"))
			requestPath = requestPath.substring(1);
		
		String path = "/" + FOLDER_PREFIX + "/"+requestPath;
		String currentStablePath = path;
		String name = null;
		int lastidx = path.lastIndexOf("/");
		if(lastidx > -1){
			name = path.substring(lastidx+1, path.length());
			path = path.substring(0, lastidx+1);
		}	
		if(path.equals("/" + FOLDER_PREFIX + "/") && name.equals("")){
			name = FOLDER_PREFIX;
			path = "/";
		}

		Menu menu = menuDAO.findFolder(name, path);
		
		//if this resource request is a folder
		if(menu != null)
			return marshallFolder(menu);
		

		Resource parentMenu = this.getParentResource(currentStablePath);
		
		Document document = documentDAO.findDocumentByNameAndParentFolderId(Long.parseLong(parentMenu.getID()), name);
		
		if(document == null)
			return null;
		
		return marshallDocument(document);
	}

	public Resource getParentResource(String resourcePath) {
		if(resourcePath.equals("/" + FOLDER_PREFIX + "/")){
			
		}

		String name = "";
		String path = resourcePath;
		for(int i = 0; i < 2; i ++){
			int lastidx = resourcePath.lastIndexOf("/");
			if(lastidx > -1){
				name = resourcePath.substring(lastidx+1, resourcePath.length());
				resourcePath = resourcePath.substring(0, lastidx);
			}	
			if(path.equals("/" + FOLDER_PREFIX + "/") && name.equals("")){
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

	public Resource createResource(Resource parentResource, String name,
			boolean isCollection, ImportContext context) {

		Menu parentMenu = menuDAO.findById(Long.parseLong(parentResource.getID()));
		
		if(isCollection == true){
			Menu createdMenu = menuDAO.createFolder(parentMenu, name);
			return this.marshallFolder(createdMenu);
		}
		
		DocumentManager documentManager = (DocumentManager) Context.getInstance()
		.getBean(DocumentManager.class);
		
		User user = userDAO.findById(parentResource.getRequestedPerson());
		
		InputStream is = context.getInputStream();
		try {
			try {
				documentManager.create(is, name, parentMenu, user, "en" );
			}
			catch(Exception e){
				e.printStackTrace();
			}
			finally {
				is.close();
			}
		}
		catch(Exception e){
			throw  new RuntimeException(e.getMessage());
		}
		
		return null;
	}

	public void updateResource(String location, Resource resource) {
		throw new AbstractMethodError();
	}

	public Resource getChildByName(Resource parentResource, String name) {
		return null;
	}

	public boolean resourceExists(Resource resource) {
		return true;
	}

	public Resource updateResource(Resource resource) {
		return null;
	}

	public Resource move(Resource target, Resource destination) {
		
		if(target.isFolder() == true){
			Menu currentMenu = menuDAO.findById(Long.parseLong(target.getID()));
			currentMenu.setText(target.getName());
			
			if(destination != null)
				currentMenu.setParentId(Long.parseLong(destination.getID()));
			
			menuDAO.store(currentMenu);
			return this.marshallFolder(currentMenu);
		}
		else {
			//if the destination is null, then the user want to change the filename, 
			//but this is not supported by logicalDOC
			if(destination == null)
				throw new OperationNotSupportedException("Moving on documents is");
					
			
			Document document = documentDAO.findById(Long.parseLong(target.getID()));
			Menu menu = menuDAO.findById(Long.parseLong(destination.getID()));
			
			try {
				documentManager.moveToFolder(document, menu);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			
			
			return this.marshallDocument(document);
		}
	}
	
	public void deleteResource(Resource resource) {
		try {
			if(resource.isFolder() == true)
				menuDAO.delete(Long.parseLong(resource.getID()));
			
			else 
				documentDAO.delete(Long.parseLong(resource.getID()));
		}
		catch(Exception e){
			throw new RuntimeException(e);
		}
	}

	public void copyResource(Resource destinationResource, Resource resource) {
		if(resource.isFolder() == true)
			throw new RuntimeException("FolderCopy not supported");
		
		throw new RuntimeException("Copy of files not supported");

	}

	@Override
	public Resource getParentResource(Resource resource) {
		throw new AbstractMethodError();
	}

	@Override
	public void streamIn(Resource resource, InputStream is) {
		throw new AbstractMethodError();
	}

	@Override
	public InputStream streamOut(Resource resource) {
		Document document = documentDAO.findById(Long.parseLong(resource.getID()));
		File file = null;
		
		if(document.getVersion().equals("1.0"))
			file = documentManager.getDocumentFile(document, null);
		else
			file = documentManager.getDocumentFile(document, document.getVersion());
		
		if (!file.exists()) {
			throw new DavResourceNotFoundException(file.getPath());
		}
		try {
			FileInputStream fis = new FileInputStream(file);
			return new BufferedInputStream(fis, 2048);
		}
		catch(IOException e){
			throw new DavResourceIOException(e.getMessage());
		}
		
	}
}
