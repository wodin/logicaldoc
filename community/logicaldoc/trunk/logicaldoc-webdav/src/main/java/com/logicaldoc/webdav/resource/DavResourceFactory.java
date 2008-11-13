package com.logicaldoc.webdav.resource;

import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.DavServletRequest;
import org.apache.jackrabbit.webdav.DavServletResponse;

import com.logicaldoc.webdav.session.DavSession;


public interface DavResourceFactory {
	
	/**
	 * 
	 * @param locator
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 * @throws DavException
	 */
	public DavResource createResource(DavResourceLocator locator, DavServletRequest request, DavServletResponse response, DavSession session) throws DavException;
	
	/**
     * Create a {@link DavResource} object from the given locator, request and response
     * objects.
     *
     * @param locator locator of the resource
     * @param request
     * @param response
     * @return a new <code>DavResource</code> object.
     * @throws DavException
     */
    public DavResource createResource(DavResourceLocator locator, DavServletRequest request, DavServletResponse response) throws DavException;

    /**
     * Create a new {@link DavResource} object from the given locator and session.
     *
     * @param locator
     * @param session
     * @return a new <code>DavResource</code> object. 
     * @throws DavException
     */
    public DavResource createResource(DavResourceLocator locator, DavSession session) throws DavException;
}
