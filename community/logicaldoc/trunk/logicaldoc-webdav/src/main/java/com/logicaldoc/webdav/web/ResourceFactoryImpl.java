package com.logicaldoc.webdav.web;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import com.logicaldoc.webdav.resource.service.ResourceServiceImpl;
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

			Resource repositoryResource = resourceService.getResource(resourcePath, session);

			DavResource resource;
			if (repositoryResource == null) {
				boolean isCollection = DavMethods.isCreateCollectionRequest(request);
				resource = createNullResource(locator, session, isCollection);
			} else {
				repositoryResource.setVersionLabel(version);

				repositoryResource.setRequestedPerson(Long.parseLong(session.getObject("id").toString()));
				resource = new VersionControlledResourceImpl(locator, this, session, resourceConfig, repositoryResource);
			}

			return resource;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new DavException(DavServletResponse.SC_INTERNAL_SERVER_ERROR, e);
		}
	}

	public DavResource createResource(DavResourceLocator locator, DavSession session) throws DavException {
		try {
			Resource resource = resourceService.getResource(locator.getResourcePath(), session);

			return createResource(locator, session, resource);
		} catch (Exception e) {
			throw new DavException(DavServletResponse.SC_INTERNAL_SERVER_ERROR, e);
		}
	}

	private DavResource createNullResource(DavResourceLocator locator, DavSession session, boolean isCollection)
			throws DavException {

		return new VersionControlledResourceImpl(locator, this, session, resourceConfig, isCollection);
	}

	public DavResource createResource(DavResourceLocator locator, DavSession session, Resource resource)
			throws DavException {

		return new VersionControlledResourceImpl(locator, this, session, resourceConfig, resource);
	}
}
