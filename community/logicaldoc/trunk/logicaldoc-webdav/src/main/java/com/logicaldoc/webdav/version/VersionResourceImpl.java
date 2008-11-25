package com.logicaldoc.webdav.version;

import java.util.Collections;
import java.util.List;

import javax.jcr.RepositoryException;
import javax.jcr.version.VersionHistory;

import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceIterator;
import org.apache.jackrabbit.webdav.DavResourceIteratorImpl;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.io.InputContext;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.jackrabbit.webdav.property.DavPropertySet;
import org.apache.jackrabbit.webdav.property.DefaultDavProperty;
import org.apache.jackrabbit.webdav.property.HrefProperty;
import org.apache.jackrabbit.webdav.version.DeltaVConstants;
import org.apache.jackrabbit.webdav.version.LabelInfo;
import org.apache.jackrabbit.webdav.version.VersionControlledResource;
import org.apache.jackrabbit.webdav.version.VersionHistoryResource;
import org.apache.jackrabbit.webdav.version.VersionResource;
import org.apache.jackrabbit.webdav.version.VersionableResource;
import org.apache.jackrabbit.webdav.version.report.ReportType;

import com.logicaldoc.webdav.resource.DavResourceFactory;
import com.logicaldoc.webdav.resource.DeltaVResourceImpl;
import com.logicaldoc.webdav.resource.model.Resource;
import com.logicaldoc.webdav.session.DavSession;
import com.logicaldoc.webdav.web.ResourceConfig;

public class VersionResourceImpl extends DeltaVResourceImpl implements VersionResource {
	
    /**
     * Create a new {@link org.apache.jackrabbit.webdav.DavResource}.
     * @param locator
     * @param factory 
     * @param session 
     * @param config 
     * @param item 
     * @throws DavException 
     *
     */
    public VersionResourceImpl(DavResourceLocator locator,
			DavResourceFactory factory, DavSession session,
			ResourceConfig config, Resource item) throws DavException {
		super(locator, factory, session, config, item);

	}

    //--------------------------------------------------------< DavResource >---
    /**
     * Since this implementation of <code>VersionResource</code> never is a
     * version belonging to a version controlled collection, this method always
     * returns <code>false</code> not respecting the configuration.
     *
     * @return always false
     */
    public boolean isCollection() {
        return false;
    }

    /**
     * @return An empty <code>DavResourceIterator</code>
     */
    public DavResourceIterator getMembers() {
        return new DavResourceIteratorImpl(Collections.EMPTY_LIST);
    }

    /**
     * The version storage is read-only -> fails with 403.
     *
     * @see DavResource#addMember(DavResource, InputContext)
     */
    public void addMember(DavResource member, InputContext inputContext) throws DavException {
        throw new DavException(DavServletResponse.SC_FORBIDDEN);
    }

    /**
     * The version storage is read-only -> fails with 403.
     *
     * @see DavResource#removeMember(DavResource)
     */
    public void removeMember(DavResource member) throws DavException {
        throw new DavException(DavServletResponse.SC_FORBIDDEN);
    }

    /**
     * Version storage is read-only -> fails with 403.
     *
     * @see DavResource#setProperty(DavProperty)
     */
    public void setProperty(DavProperty property) throws DavException {
        throw new DavException(DavServletResponse.SC_FORBIDDEN);
    }

    /**
     * Version storage is read-only -> fails with 403.
     *
     * @see DavResource#removeProperty(DavPropertyName)
     */
    public void removeProperty(DavPropertyName propertyName) throws DavException {
        throw new DavException(DavServletResponse.SC_FORBIDDEN);
    }

    /**
     * Version storage is read-only -> fails with 403.
     *
     * @see DavResource#alterProperties(DavPropertySet, DavPropertyNameSet)
     */
    public MultiStatusResponse alterProperties(DavPropertySet setProperties, DavPropertyNameSet removePropertyNames) throws DavException {
        throw new DavException(DavServletResponse.SC_FORBIDDEN);
    }

    /**
     * Version storage is read-only -> fails with 403.
     *
     * @see DavResource#alterProperties(List)
     */
    public MultiStatusResponse alterProperties(List changeList) throws DavException {
        throw new DavException(DavServletResponse.SC_FORBIDDEN);
    }

    //----------------------------------------------------< VersionResource >---
    /**
     * Modify the labels defined for the underlying repository version.
     *
     * @param labelInfo
     * @throws org.apache.jackrabbit.webdav.DavException
     * @see VersionResource#label(org.apache.jackrabbit.webdav.version.LabelInfo)
     * @see javax.jcr.version.VersionHistory#addVersionLabel(String, String, boolean)
     * @see javax.jcr.version.VersionHistory#removeVersionLabel(String)
     */
    public void label(LabelInfo labelInfo) throws DavException {
        
    }

    /**
     * Returns the {@link VersionHistory} associated with the repository version.
     * Note: in contrast to a versionable node, the version history of a version
     * item is always represented by its nearest ancestor.
     *
     * @return the {@link org.apache.jackrabbit.webdav.version.VersionHistoryResource} associated with this resource.
     * @throws org.apache.jackrabbit.webdav.DavException
     * @see org.apache.jackrabbit.webdav.version.VersionResource#getVersionHistory()
     * @see javax.jcr.Item#getParent()
     */
    public VersionHistoryResource getVersionHistory() throws DavException {
       return null;
    }

    /**
     * Return versionhistory that contains this version item
     *
     * @return versionhistory that contains this version item
     * @throws RepositoryException
     * @see javax.jcr.version.Version#getContainingHistory()
     */
    private VersionHistory getVersionHistoryItem() throws RepositoryException {
        return null;
    }

    //--------------------------------------------------------------------------
    /**
     * Define the set of reports supported by this resource.
     *
     * @see org.apache.jackrabbit.webdav.version.report.SupportedReportSetProperty
     */
    protected void initSupportedReports() {
        super.initSupportedReports();
        if (exists()) {
            supportedReports.addReportType(ReportType.VERSION_TREE);
        }
    }

    /**
     * Fill the property set for this resource.
     */
    protected void initProperties() {
        if (!propsInitialized) {
            super.initProperties();
            
            properties.add(new DefaultDavProperty(VERSION_NAME, resource
					.getVersionLabel(), true));
			properties.add(new DefaultDavProperty(DavPropertyName.CREATIONDATE,
					resource.getVersionDate()));
			properties.add(new HrefProperty(VersionResource.VERSION_HISTORY,
					locator.getResourcePath() + resource.getID(), true));
			properties.add(new DefaultDavProperty(DeltaVConstants.COMMENT,
				resource.getComment()));
        }
    }
    
    
    
    @Override
    public String getSupportedMethods() {
    	StringBuffer sb = new StringBuffer(super.getSupportedMethods());
        // Versioning support
        sb.append(", ").append(VersionableResource.METHODS);
       
	    try {
	        if ((resource.getIsCheckedOut())) {
	            sb.append(", ").append(VersionControlledResource.methods_checkedOut);
	        } else {
	            sb.append(", ").append(VersionControlledResource.methods_checkedIn);
	        }
	    } catch (Exception e) {
	        // should not occur.
	        log.error(e.getMessage());
	    }
        
        return sb.toString();
    }
}

