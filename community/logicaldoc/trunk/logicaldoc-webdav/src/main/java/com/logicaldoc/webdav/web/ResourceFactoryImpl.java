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

public class ResourceFactoryImpl implements DavResourceFactory{
	

    private final LockManager lockMgr;
    private final ResourceConfig resourceConfig;
    private ResourceService resourceService;

    /**
     * Create a new <code>ResourceFactory</code> that uses the given lock
     * manager and the default {@link ResourceConfig resource config}.
     *
     * @param lockMgr
     */
    public ResourceFactoryImpl(LockManager lockMgr) {
        this.lockMgr = lockMgr;
        this.resourceConfig = (ResourceConfig) Context.getInstance().getBean(
				"ResourceConfig");
		this.resourceService = (ResourceService) Context.getInstance().getBean(
				"ResourceService");
    }

    /**
     * Create a new <code>ResourceFactory</code> that uses the given lock
     * manager and resource filter.
     *
     * @param lockMgr
     * @param resourceConfig
     */
    public ResourceFactoryImpl(LockManager lockMgr, ResourceConfig resourceConfig) {
        this.lockMgr = lockMgr;
        this.resourceConfig = (resourceConfig != null) ? resourceConfig
				: (ResourceConfig) Context.getInstance().getBean(
						"ResourceConfig");
		this.resourceService = (ResourceService) Context.getInstance().getBean(
		"ResourceService");
    }

    public DavResource createResource(DavResourceLocator locator, DavServletRequest request,
            DavServletResponse response){
    	throw new AbstractMethodError();
    }
    
    /**
     * Create a new <code>DavResource</code> from the given locator and
     * request.
     *
     * @param locator
     * @param request
     * @param response
     * @return DavResource
     * @throws DavException
     * @see DavResourceFactory#createResource(DavResourceLocator,
     *      DavServletRequest, DavServletResponse)
     */
    public DavResource createResource(DavResourceLocator locator, DavServletRequest request,
                                      DavServletResponse response, DavSession session) throws DavException {
    	
        try {
            Resource rT = resourceService.getResorce(locator.getResourcePath(), new Long(session.getObject("id").toString()));
            DavResource resource;
            if (rT == null) {
                boolean isCollection = DavMethods.isCreateCollectionRequest(request);
                resource = createNullResource(locator, session, isCollection);
            } else {
            	Resource thisResource = resourceService.getResorce(locator.getResourcePath(), new Long(session.getObject("id").toString()));
            	thisResource.setRequestedPerson(Long.parseLong(session.getObject("id").toString()));
            	resource = new VersionControlledResourceImpl(locator, this, session, resourceConfig, thisResource, "");
            }
            
            resource.addLockManager(lockMgr);
            return resource;
        } catch (Exception e) {
            throw new DavException(DavServletResponse.SC_INTERNAL_SERVER_ERROR, e);
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
    public DavResource createResource(DavResourceLocator locator, DavSession session) throws DavException {
        try {

            Resource resourceA = resourceService.getResorce( locator.getResourcePath(), new Long(session.getObject("id").toString()) );
            
            DavResource resource = createResource(locator, session, resourceA);
            resource.addLockManager(lockMgr);
            return resource;
        } catch (Exception e) {
            throw new DavException(DavServletResponse.SC_INTERNAL_SERVER_ERROR, e);
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
                                           DavSession session,
                                           boolean isCollection) throws DavException {
        DavResource resource = new VersionControlledResourceImpl(locator, this, session, resourceConfig, isCollection);
        return resource;
    }
    
    /**
     * Tries to retrieve the repository item defined by the locator's resource
     * path and build the corresponding WebDAV resource. If the repository
     * supports the versioning option different resources are created for
     * version, versionhistory and common nodes.
     *
     * @param locator
     * @param sessionImpl
     * @return DavResource representing a repository item.
     */
    public DavResource createResource(DavResourceLocator locator,
                                       DavSession session, Resource resourceA) throws DavException {

        DavResource resource = new VersionControlledResourceImpl(locator, this, session, resourceConfig, resourceA, "HOLDER");

        return resource;
    }
}
