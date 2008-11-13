package com.logicaldoc.webdav.resource;


import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavMethods;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.MultiStatus;
import org.apache.jackrabbit.webdav.version.LabelInfo;
import org.apache.jackrabbit.webdav.version.MergeInfo;
import org.apache.jackrabbit.webdav.version.UpdateInfo;
import org.apache.jackrabbit.webdav.version.VersionControlledResource;
import org.apache.jackrabbit.webdav.version.VersionHistoryResource;
import org.apache.jackrabbit.webdav.version.VersionResource;
import org.apache.jackrabbit.webdav.version.VersionableResource;
import org.apache.jackrabbit.webdav.version.report.ReportType;
import org.apache.jackrabbit.webdav.version.report.SupportedReportSetProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.webdav.resource.model.Resource;
import com.logicaldoc.webdav.session.DavSession;
import com.logicaldoc.webdav.web.ResourceConfig;

public class VersionControlledResourceImpl extends DeltaVResourceImpl
implements VersionControlledResource {

private static final Logger log = LoggerFactory.getLogger(VersionControlledResourceImpl.class);

	public VersionControlledResourceImpl(DavResourceLocator locator, DavResourceFactory factory, DavSession session, ResourceConfig config, Resource resource, String holder) throws DavException {
		super(locator, factory, session, config, resource, holder);
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
		    try {
		       
		            sb.append(", ").append(DavMethods.METHOD_CHECKOUT);
		            sb.append(", ").append(DavMethods.METHOD_LABEL);
		     
		    } catch (Exception e) {
		        // should not occur.
		        log.error(e.getMessage());
		    }
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
	
	}


	/**
	* UNCHECKOUT cannot be implemented on top of JSR 170 repository.
	* Therefore this methods always throws a <code>DavException</code> with error code
	* {@link org.apache.jackrabbit.webdav.DavServletResponse#SC_NOT_IMPLEMENTED}.
	*
	* @throws org.apache.jackrabbit.webdav.DavException
	* @see org.apache.jackrabbit.webdav.version.VersionControlledResource#uncheckout()
	*/
	public void uncheckout() throws DavException {
		throw new DavException(DavServletResponse.SC_NOT_IMPLEMENTED);
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
	public VersionHistoryResource getVersionHistory() {
		return null;
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
			log.info("STR-CALL WARN: initSupportedReports");
			if (isVersionControlled()) {
				supportedReports.addReportType(ReportType.VERSION_TREE);
	    }
	}
}

	/**
	* Fill the property set for this resource.
	* @see DavResourceImpl#initProperties()
	*/
	protected void initProperties() {
		if (!propsInitialized) {
			super.initProperties();
	   
		}
	}
	
	/**
	* @return true, if this resource is a non-collection resource and represents
	* an existing repository node that has the mixin nodetype 'mix:versionable' set.
	*/
	private boolean isVersionControlled() {
	
		return false;
	
	}
}
