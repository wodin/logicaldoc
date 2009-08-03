package com.logicaldoc.webdav.resource;

import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.DavServletRequest;
import org.apache.jackrabbit.webdav.DavServletResponse;

import com.logicaldoc.webdav.session.DavSession;

/**
 * For more informations, please visit
 * {@link org.apache.jackrabbit.webdav.DavResourceFactory}
 * 
 * @author Sebastian Wenzky
 * 
 */
public interface DavResourceFactory {
	
	public DavResource createResource(DavResourceLocator locator,
			DavServletRequest request, DavServletResponse response,
			DavSession session) throws DavException;
	
    public DavResource createResource(DavResourceLocator locator,
			DavServletRequest request, DavServletResponse response)
			throws DavException;

    public DavResource createResource(DavResourceLocator locator,
			DavSession session) throws DavException;
}
