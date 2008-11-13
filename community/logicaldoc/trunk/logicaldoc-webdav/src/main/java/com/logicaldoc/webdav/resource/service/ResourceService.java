package com.logicaldoc.webdav.resource.service;

import java.io.InputStream;
import java.util.List;

import com.logicaldoc.webdav.context.ImportContext;
import com.logicaldoc.webdav.resource.model.Resource;

/**
 * 
 * @author Sebastian Wenzky
 *
 */
public interface ResourceService {
	/**
	 * Retrieves the current Resource from a datasource
	 * @param locator
	 * @return
	 */
	public Resource getResorce(String location, long userid);
	
	/**
	 * 
	 * @param parentLocator
	 * @return
	 */
	public List<Resource> getChildResources(Resource parentResource);
	
	/**
	 * 
	 * @param locator
	 * @param name
	 */
	public Resource createResource(Resource parentResource, String name, boolean isCollection, ImportContext context);
	
	/**
	 * 
	 * @param parentResource
	 * @return
	 */
	public Resource updateResource(Resource resource);
	
	/**
	 * 
	 * @param locator
	 * @param resource
	 */
	public void updateResource(Resource resource, ImportContext context);
	
	/**
	 * 
	 * @param resource
	 * @return
	 */
	public boolean resourceExists(Resource resource);
	
	/**
	 * 
	 * @param parentResource
	 * @param name
	 * @return
	 */
	public Resource getChildByName(Resource parentResource, String name);
	
	/**
	 * 
	 * @param resource
	 * @return
	 */
	public Resource getParentResource(Resource resource);
	
	/**
	 * 
	 * @param resource
	 * @return
	 */
	public Resource getParentResource(String location);
	
	/**
	 * 
	 * @param target
	 * @param destination
	 * @return
	 */
	public Resource move(Resource target, Resource destination);

	/**
	 * 
	 * @param resource
	 * @param is
	 */
	public void streamIn(Resource resource, InputStream is);
	
	/**
	 * 
	 * @param resource
	 * @return
	 */
	public InputStream streamOut(Resource resource);
	
	/**
	 * 
	 * @param resource
	 */
	public void deleteResource(Resource resource);

	/**
	 * 
	 * @param destinResource
	 * @param resource
	 */
	public void copyResource(Resource destinResource, Resource resource);
}
