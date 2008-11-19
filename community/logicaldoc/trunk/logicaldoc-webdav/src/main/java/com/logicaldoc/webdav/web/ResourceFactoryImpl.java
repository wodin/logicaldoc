package com.logicaldoc.webdav.web;

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

public class ResourceFactoryImpl implements DavResourceFactory {

	private final ResourceConfig resourceConfig;
	private ResourceService resourceService;

	/**
	 * Create a new <code>ResourceFactory</code> that uses the given lock
	 * manager and the default {@link ResourceConfig resource config}.
	 * 
	 * @param lockMgr
	 */
	public ResourceFactoryImpl(LockManager lockMgr) {
		this.resourceConfig = (ResourceConfig) Context.getInstance().getBean(
				"ResourceConfig");
		this.resourceService = (ResourceService) Context.getInstance().getBean(
				"ResourceService");
	}

	/**
	 * @param lockMgr
	 * @param resourceConfig
	 */
	public ResourceFactoryImpl(LockManager lockMgr,
			ResourceConfig resourceConfig) {
		this.resourceConfig = (resourceConfig != null) ? resourceConfig
				: (ResourceConfig) Context.getInstance().getBean(
						"ResourceConfig");
		this.resourceService = (ResourceService) Context.getInstance().getBean(
				"ResourceService");
	}

	/**
	 * 
	 */
	public DavResource createResource(DavResourceLocator locator,
			DavServletRequest request, DavServletResponse response) throws DavException{	
		return createResource(locator, request, response, (DavSession)request.getDavSession());
	}

	/**
	 * @param locator
	 * @param request
	 * @param response
	 * @return DavResource
	 * @throws DavException
	 * @see DavResourceFactory#createResource(DavResourceLocator,
	 *      DavServletRequest, DavServletResponse)
	 */
	public DavResource createResource(DavResourceLocator locator,
			DavServletRequest request, DavServletResponse response,
			DavSession session) throws DavException {

		try {
			Resource repositoryResource = resourceService.getResource(locator
					.getResourcePath(), new Long(session.getObject("id")
					.toString()));
			DavResource resource;
			if (repositoryResource == null) {
				boolean isCollection = DavMethods
						.isCreateCollectionRequest(request);
				resource = createNullResource(locator, session, isCollection);
			} else {

				repositoryResource.setRequestedPerson(Long.parseLong(session
						.getObject("id").toString()));
				resource = new VersionControlledResourceImpl(locator, this,
						session, resourceConfig, repositoryResource);
			}

			return resource;
		} catch (Exception e) {
			throw new DavException(DavServletResponse.SC_INTERNAL_SERVER_ERROR,
					e);
		}
	}

	/**
	 * Create a new <code>DavResource</code> from the given locator and webdav
	 * session.
	 * 
	 * @param locator
	 * @param session
	 * @return
	 * @throws DavException
	 * @see DavResourceFactory#createResource(DavResourceLocator, DavSession)
	 */
	public DavResource createResource(DavResourceLocator locator,
			DavSession session) throws DavException {
		try {
			Resource resource = resourceService.getResource(locator
					.getResourcePath(), new Long(session.getObject("id")
					.toString()));

			return createResource(locator, session, resource);
		} catch (Exception e) {
			throw new DavException(DavServletResponse.SC_INTERNAL_SERVER_ERROR,
					e);
		}
	}

	/**
	 * Create a 'null resource'
	 * 
	 * @param locator
	 * @param session
	 * @param request
	 * @return
	 * @throws DavException
	 */
	private DavResource createNullResource(DavResourceLocator locator,
			DavSession session, boolean isCollection) throws DavException {

		return new VersionControlledResourceImpl(locator, this, session,
				resourceConfig, isCollection);
	}

	/**
	 * 
	 * @param locator
	 * @param sessionImpl
	 * @return DavResource representing a repository item.
	 */
	public DavResource createResource(DavResourceLocator locator,
			DavSession session, Resource resource) throws DavException {

		return new VersionControlledResourceImpl(locator, this, session,
				resourceConfig, resource);
	}
}
