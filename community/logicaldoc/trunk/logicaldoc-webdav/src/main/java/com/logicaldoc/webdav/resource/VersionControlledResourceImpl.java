package com.logicaldoc.webdav.resource;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavMethods;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.MultiStatus;
import org.apache.jackrabbit.webdav.property.DefaultDavProperty;
import org.apache.jackrabbit.webdav.property.HrefProperty;
import org.apache.jackrabbit.webdav.version.LabelInfo;
import org.apache.jackrabbit.webdav.version.MergeInfo;
import org.apache.jackrabbit.webdav.version.UpdateInfo;
import org.apache.jackrabbit.webdav.version.VersionControlledResource;
import org.apache.jackrabbit.webdav.version.VersionHistoryResource;
import org.apache.jackrabbit.webdav.version.VersionResource;
import org.apache.jackrabbit.webdav.version.VersionableResource;
import org.apache.jackrabbit.webdav.version.report.ReportType;
import org.apache.jackrabbit.webdav.version.report.SupportedReportSetProperty;

import com.logicaldoc.webdav.resource.model.Resource;
import com.logicaldoc.webdav.session.DavSession;
import com.logicaldoc.webdav.version.VersionHistoryResourceImpl;
import com.logicaldoc.webdav.web.ResourceConfig;

public class VersionControlledResourceImpl extends DeltaVResourceImpl
implements VersionControlledResource {

	protected static Log log = LogFactory.getLog(VersionControlledResourceImpl.class);

	public VersionControlledResourceImpl(DavResourceLocator locator, DavResourceFactory factory, DavSession session, ResourceConfig config, Resource resource) throws DavException {
		super(locator, factory, session, config, resource);
		initSupportedReports();
	}

	/**
	* Create a new {@link org.apache.jackrabbit.webdav.DavResource}.
	*
	* @param locator
	* @param factory
	* @param session
	* @param config 
	* @param item 
	* @throws DavException
	*/
	public VersionControlledResourceImpl(DavResourceLocator locator, DavResourceFactory factory, DavSession session, ResourceConfig config) throws DavException {
		super(locator, factory, session, config);
		initSupportedReports();
	}
	
	/**
	* Create a new {@link org.apache.jackrabbit.webdav.DavResource}.
	*
	* @param locator
	* @param factory
	* @param session
	* @param config 
	* @param isCollection 
	* @throws DavException 
	*/
	public VersionControlledResourceImpl(DavResourceLocator locator, DavResourceFactory factory, DavSession session, ResourceConfig config, boolean isCollection) throws DavException {
		super(locator, factory, session, config, isCollection);
		initSupportedReports();
	}

	//--------------------------------------------------------< DavResource >---
	/**
	* Return a comma separated string listing the supported method names.
	*
	* @return the supported method names.
	* @see org.apache.jackrabbit.webdav.DavResource#getSupportedMethods()
	*/
	public String getSupportedMethods() {
		StringBuffer sb = new StringBuffer(super.getSupportedMethods());
		// Versioning support
		sb.append(", ").append(VersionableResource.METHODS);
		if (isVersionControlled()) {
			sb.append(", ").append(DavMethods.METHOD_CHECKOUT);
			sb.append(", ").append(DavMethods.METHOD_UNCHECKOUT);
		    sb.append(", ").append(DavMethods.METHOD_LABEL);
		}
		return sb.toString();
	}

	//------------------------------------------< VersionControlledResource >---
	/**
	* Adds version control to this resource. If the resource is already under
	* version control, this method has no effect. If this resource is a Collection
	* resource this method fails with {@link DavServletResponse#SC_METHOD_NOT_ALLOWED}.
	*
	* @throws org.apache.jackrabbit.webdav.DavException if this resource does not
	* exist yet, is a collection or if an error occurs while making the
	* underlying node versionable.
	* @see org.apache.jackrabbit.webdav.version.VersionableResource#addVersionControl()
	*/
	public void addVersionControl() throws DavException {
	
	}

	/**
	* Calls {@link javax.jcr.Node#checkin()} on the underlying repository node.
	*
	* @throws org.apache.jackrabbit.webdav.DavException
	* @see org.apache.jackrabbit.webdav.version.VersionControlledResource#checkin()
	*/
	public String checkin() throws DavException {
		return null;
	}

	/**
	* Calls {@link javax.jcr.Node#checkout()} on the underlying repository node.
	*
	* @throws org.apache.jackrabbit.webdav.DavException
	* @see org.apache.jackrabbit.webdav.version.VersionControlledResource#checkout()
	*/
	public void checkout() throws DavException {
		resourceService.checkout(getResource());
	}


	/**
	* @throws org.apache.jackrabbit.webdav.DavException
	* @see org.apache.jackrabbit.webdav.version.VersionControlledResource#uncheckout()
	*/
	public void uncheckout() throws DavException {
		resourceService.uncheckout(getResource());
	}

	/**
	* UPDATE feature is not (yet) supported. This method allows fails with
	* {@link DavServletResponse#SC_NOT_IMPLEMENTED}.
	*
	* @param updateInfo
	* @return
	* @throws DavException
	* @see VersionControlledResource#update(UpdateInfo)
	*/
	public MultiStatus update(UpdateInfo updateInfo) throws DavException {
		throw new DavException(DavServletResponse.SC_NOT_IMPLEMENTED);
	}

	/**
	* MERGE feature is not (yet) supported. This method allows fails with
	* {@link DavServletResponse#SC_NOT_IMPLEMENTED}.
	*
	* @param mergeInfo
	* @return
	* @throws DavException
	* @see VersionControlledResource#merge(MergeInfo)
	*/
	public MultiStatus merge(MergeInfo mergeInfo) throws DavException {
		throw new DavException(DavServletResponse.SC_NOT_IMPLEMENTED);
	}

	/**
	* Modify the labels present with the versions of this resource.
	*
	* @param labelInfo
	* @throws DavException
	* @see VersionControlledResource#label(LabelInfo)
	* @see javax.jcr.version.VersionHistory#addVersionLabel(String, String, boolean)
	* @see javax.jcr.version.VersionHistory#removeVersionLabel(String)
	*/
	public void label(LabelInfo labelInfo) throws DavException {
		if (labelInfo == null) {
		    throw new DavException(DavServletResponse.SC_BAD_REQUEST, "Valid label request body required.");
		}
		if (!exists()) {
		    throw new DavException(DavServletResponse.SC_NOT_FOUND);
		}
		
		try {
		    if (!isVersionControlled() ) {
		        throw new DavException(DavServletResponse.SC_PRECONDITION_FAILED, "A LABEL request may only be applied to a version-controlled, checked-in resource.");
		    }
		    DavResource[] resArr = this.getReferenceResources(CHECKED_IN);
		    if (resArr.length == 1 && resArr[0] instanceof VersionResource) {
		        ((VersionResource)resArr[0]).label(labelInfo);
		    } else {
		        throw new DavException(DavServletResponse.SC_INTERNAL_SERVER_ERROR, "DAV:checked-in property on '" + getHref() + "' did not point to a single VersionResource.");
		    }
		} catch (Exception e) {
		   
		}
	}

	/**
	* Returns the {@link javax.jcr.version.VersionHistory} associated with the repository node.
	* If the node is not versionable an exception is thrown.
	*
	* @return the {@link VersionHistoryResource} associated with this resource.
	* @throws org.apache.jackrabbit.webdav.DavException
	* @see org.apache.jackrabbit.webdav.version.VersionControlledResource#getVersionHistory()
	* @see javax.jcr.Node#getVersionHistory()
	*/
	@SuppressWarnings("unused")
	public VersionHistoryResource getVersionHistory() {
		
		DavResourceLocator loc = getLocatorFromResource(resource);
	
		try {
			return new VersionHistoryResourceImpl(loc, factory, session, config, resource);
		} catch (DavException e) {
			e.printStackTrace();
		}
		
		throw new RuntimeException("");
		
	}

	//--------------------------------------------------------------------------
	/**
	* Define the set of reports supported by this resource.
	*
	* @see SupportedReportSetProperty
	* @see DeltaVResourceImpl#initSupportedReports()
	*/
	protected void initSupportedReports() {
		super.initSupportedReports();
		if (exists()) {
			supportedReports.addReportType(ReportType.LOCATE_BY_HISTORY);
			supportedReports.addReportType(ReportType.VERSION_TREE);
	    }
	
}

	/**
	* Fill the property set for this resource.
	* @see DavResourceImpl#initProperties()
	*/
	protected void initProperties() {
		if (!propsInitialized) {
			super.initProperties();
		
			if(resource.isFolder())
				return;
		
			String baseVHref = getLocatorFromResource(resource).getHref(false);
			
			properties.add(new HrefProperty(VERSION_HISTORY, locator.getResourcePath(), true));

			// DAV:auto-version property: there is no auto version, explicit CHECKOUT is required.
            properties.add(new DefaultDavProperty(AUTO_VERSION, null, false));
            
			if (resource.getIsCheckedOut()){
				 properties.add(new HrefProperty(CHECKED_OUT, baseVHref, true));
				 properties.add(new HrefProperty(
						VersionResource.PREDECESSOR_SET, locator.getResourcePath(), false));
			}
			else {
				properties.add(new HrefProperty(CHECKED_IN, locator.getResourcePath(), true));
			}
		}
	}
	
	/**
	* @return true, if this resource is a non-collection resource and represents
	* an existing repository node that has the mixin nodetype 'mix:versionable' set.
	*/
	private boolean isVersionControlled() {
		return true;
	}
	
	 /**
     * Build a new {@link DavResourceLocator} from the given repository node.
     *
     * @param repositoryNode
     * @return a new locator for the specified node.
     * @see #getLocatorFromNodePath(String)
     */
    protected DavResourceLocator getLocatorFromResource(Resource resource) {
        String nodePath = null;
        try {
            nodePath = locator.getResourcePath();
        } catch (Exception e) {
        	throw new RuntimeException(e);
        }
        return getLocatorFromNodePath(nodePath);
    }

    /**
     * Create a new <code>DavResource</code> from the given locator.
     * @param loc
     * @return new <code>DavResource</code>
     */
    @SuppressWarnings("deprecation")
	protected DavResource createResourceFromLocator(DavResourceLocator loc)
            throws DavException {
        DavResource res = getFactory().createResource(loc, getSession());
        return res;
    }
}
