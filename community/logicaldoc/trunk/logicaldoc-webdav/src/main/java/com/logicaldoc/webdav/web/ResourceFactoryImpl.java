package com.logicaldoc.webdav.web;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavMethods;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.DavServletRequest;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.lock.LockManager;

import com.logicaldoc.util.Context;
import com.logicaldoc.webdav.resource.DavResourceFactory;
import com.logicaldoc.webdav.resource.VersionControlledResourceImpl;
import com.logicaldoc.webdav.resource.model.Resource;
import com.logicaldoc.webdav.resource.service.ResourceService;
import com.logicaldoc.webdav.session.DavSession;

/**
 * For more informations, please visit
 * {@link org.apache.jackrabbit.webdav.simple.ResourceFactoryImpl}
 * 
 * @author Sebastian Wenzky
 * 
 */
public class ResourceFactoryImpl implements DavResourceFactory {

	protected static Log log = LogFactory.getLog(ResourceFactoryImpl.class);

	private static final Pattern versionRequestPattern = Pattern.compile("/vstore/([0-9].[0-9])/(.*)");

	private final ResourceConfig resourceConfig;

	private ResourceService resourceService;

	public ResourceFactoryImpl(LockManager lockMgr) {
		this.resourceConfig = (ResourceConfig) Context.getInstance().getBean("ResourceConfig");
		this.resourceService = (ResourceService) Context.getInstance().getBean("ResourceService");
	}

	public ResourceFactoryImpl(LockManager lockMgr, ResourceConfig resourceConfig) {
		this.resourceConfig = (resourceConfig != null) ? resourceConfig : (ResourceConfig) Context.getInstance()
				.getBean("ResourceConfig");
		this.resourceService = (ResourceService) Context.getInstance().getBean("ResourceService");
	}

	public DavResource createResource(DavResourceLocator locator, DavServletRequest request, DavServletResponse response)
			throws DavException {
		return createResource(locator, request, response, (DavSession) request.getDavSession());
	}

	public DavResource createResource(DavResourceLocator locator, DavServletRequest request,
			DavServletResponse response, DavSession session) throws DavException {

		try {
			String resourcePath = locator.getResourcePath();
			Matcher matcher = versionRequestPattern.matcher(locator.getResourcePath());

			String version = null;
			if (matcher.matches() == true) {
				version = matcher.group(1);
				resourcePath = resourcePath.replaceFirst("/vstore/" + version, "");
			}

			DavResource resource = getFromCache(session.getObject("id") + ";" + resourcePath);
			if (resource != null)
				return resource;

			Resource repositoryResource = resourceService.getResource(resourcePath, session);

			if (repositoryResource == null) {
				boolean isCollection = DavMethods.isCreateCollectionRequest(request);
				resource = createNullResource(locator, session, isCollection);
			} else {
				repositoryResource.setVersionLabel(version);

				repositoryResource.setRequestedPerson(Long.parseLong(session.getObject("id").toString()));
				resource = new VersionControlledResourceImpl(locator, this, session, resourceConfig, repositoryResource);
			}

			putInCache(session.getObject("id") + ";" + resourcePath, resource);
			return resource;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new DavException(DavServletResponse.SC_INTERNAL_SERVER_ERROR, e);
		}
	}

	public DavResource createResource(DavResourceLocator locator, DavSession session) throws DavException {
		try {
			DavResource resource = getFromCache(session.getObject("id") + ";"
					+ locator.getResourcePath());
			if (resource != null)
				return resource;
			
			Resource res = resourceService.getResource(locator.getResourcePath(), session);
			resource = createResource(locator, session, res);

			putInCache(session.getObject("id") + ";" + locator.getResourcePath(), resource);
			return resource;
		} catch (Exception e) {
			throw new DavException(DavServletResponse.SC_INTERNAL_SERVER_ERROR, e);
		}
	}

	/**
	 * Puts an entry in the cache
	 * 
	 * @param key The entry ID as <userid>;<path>
	 * @param resource The entry to be cached
	 */
	private void putInCache(String key, DavResource resource) {
		Cache cache = ((CacheManager) Context.getInstance().getBean("DavCacheManager")).getCache("dav-resources");
		Element element = new Element(key, resource);
		cache.put(element);
		System.out.println("** put "+key);
	}

	/**
	 * Retrieves an entry from cache
	 * 
	 * @param key The entry ID as <userid>;<path>
	 * @return The cached entry
	 */
	private DavResource getFromCache(String key) {
		Cache cache = ((CacheManager) Context.getInstance().getBean("DavCacheManager")).getCache("dav-resources");
		Element element = cache.get(key);
		DavResource resource = null;
		if (element != null) {
			resource = (DavResource) element.getValue();
			return resource;
		} else
			return null;
	}

	private DavResource createNullResource(DavResourceLocator locator, DavSession session, boolean isCollection)
			throws DavException {

		return new VersionControlledResourceImpl(locator, this, session, resourceConfig, isCollection);
	}

	public DavResource createResource(DavResourceLocator locator, DavSession session, Resource resource)
			throws DavException {
		DavResource res = getFromCache(session.getObject("id") + ";" + locator.getResourcePath());
		if (res != null)
			return res;

		res = new VersionControlledResourceImpl(locator, this, session, resourceConfig, resource);
		putInCache(session.getObject("id") + ";" + locator.getResourcePath(), res);
		return res;
	}
}