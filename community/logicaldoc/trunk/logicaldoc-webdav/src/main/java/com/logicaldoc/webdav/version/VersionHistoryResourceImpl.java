package com.logicaldoc.webdav.version;

import java.util.ArrayList;
import java.util.List;

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
import org.apache.jackrabbit.webdav.property.HrefProperty;
import org.apache.jackrabbit.webdav.property.ResourceType;
import org.apache.jackrabbit.webdav.version.VersionHistoryResource;
import org.apache.jackrabbit.webdav.version.VersionResource;

import com.logicaldoc.webdav.resource.DavResourceFactory;
import com.logicaldoc.webdav.resource.DeltaVResourceImpl;
import com.logicaldoc.webdav.resource.model.Resource;
import com.logicaldoc.webdav.session.DavSession;
import com.logicaldoc.webdav.web.ResourceConfig;

/**
 * <code>VersionHistoryResourceImpl</code> represents a JCR version history.
 *
 * @see VersionHistory
 */
public class VersionHistoryResourceImpl extends DeltaVResourceImpl implements VersionHistoryResource {

   
    public VersionHistoryResourceImpl(DavResourceLocator locator, DavResourceFactory factory, DavSession session, ResourceConfig config, Resource resource) throws DavException {
        super(locator, factory, session, config, resource);
           
       
    }

    //--------------------------------------------------------< DavResource >---
    /**
     * Show all versions of this history as members.
     *
     * @return
     * @see DavResource#getMembers()
     */
    public DavResourceIterator getMembers() {
        ArrayList list = new ArrayList();
        if (exists() && isCollection()) {
            try {
                // only display versions as members of the vh. the jcr:versionLabels
                // node is an internal structure.
                /*
                 
                while (it.hasNext()) {
                    // omit item filter here. if the version history is visible
                    // its versions should be visible as well.
                    Version v = it.nextVersion();
                    DavResourceLocator vhLocator = getLocator();
                    DavResourceLocator resourceLocator = vhLocator.getFactory().createResourceLocator(vhLocator.getPrefix(), vhLocator.getWorkspacePath(), v.getPath(), false);
                    DavResource childRes = getFactory().createResource(resourceLocator, getSession());
                    list.add(childRes);
                }
                */
            } catch(Exception e){
            	e.printStackTrace();
            }
        }
        return new DavResourceIteratorImpl(list);
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
     * Removing a version resource is achieved by calling <code>removeVersion</code>
     * on the versionhistory item this version belongs to.
     *
     * @throws DavException if the version does not exist or if an error occurs
     * while deleting.
     * @see DavResource#removeMember(org.apache.jackrabbit.webdav.DavResource)
     */
    public void removeMember(DavResource member) throws DavException {
        throw new UnsupportedOperationException();
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

    //---------------------------------------------< VersionHistoryResource >---
    /**
     * Return an array of {@link org.apache.jackrabbit.webdav.version.VersionResource}s representing all versions
     * present in the underlying JCR version history.
     *
     * @return array of {@link org.apache.jackrabbit.webdav.version.VersionResource}s representing all versions
     * present in the underlying JCR version history.
     * @throws org.apache.jackrabbit.webdav.DavException
     * @see org.apache.jackrabbit.webdav.version.VersionHistoryResource#getVersions()
     */
    public VersionResource[] getVersions() throws DavException {
    	
    	List<Resource> res = resourceService.getHistory(resource);
    	
    	List<VersionResource> versions = new ArrayList<VersionResource>();
    	
    	for (Resource resource : res) {
    		DavResourceLocator loc = locator.getFactory()
					.createResourceLocator(
							locator.getPrefix(),
							locator.getResourcePath() + "/vstore/"
									+ resource.getVersionLabel()+"/" + resource.getName());
    		versions.add(new VersionResourceImpl(loc, factory, session, config, resource));
		}

    	return versions.toArray(new VersionResource[versions.size()]);

    }

    //--------------------------------------------------------------------------
    /**
     * Fill the property set for this resource.
     */
    protected void initProperties() {
        if (!propsInitialized) {
            super.initProperties();

            // change resourcetype defined by default item collection
            properties.add(new ResourceType(new int[] {ResourceType.COLLECTION, ResourceType.VERSION_HISTORY}));
            
            // required root-version property for version-history resource
            try {
               // String rootVersionHref = getLocatorFromNode(((VersionHistory)getNode()).getRootVersion()).getHref(false);
                properties.add(new HrefProperty(VersionHistoryResource.ROOT_VERSION, "", false));
            } catch (Exception e) {
                log.error(e.getMessage());
            }
           
            // required, protected version-set property for version-history resource
            try {
              //  VersionIterator vIter = ((VersionHistory)getNode()).getAllVersions();
                //ArrayList l = new ArrayList();
                //while (vIter.hasNext()) {
                //    l.add(vIter.nextVersion());
                //}
                //properties.add(getHrefProperty(VersionHistoryResource.VERSION_SET, (Version[]) l.toArray(new Version[l.size()]), true, false));
            } catch (Exception e) {
                log.error(e.getMessage());
            }
           
        }
    }
}

