package com.logicaldoc.webdav.resource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.webdav.DavCompliance;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavLocatorFactory;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.HrefProperty;
import org.apache.jackrabbit.webdav.version.DeltaVConstants;
import org.apache.jackrabbit.webdav.version.DeltaVResource;
import org.apache.jackrabbit.webdav.version.OptionsInfo;
import org.apache.jackrabbit.webdav.version.OptionsResponse;
import org.apache.jackrabbit.webdav.version.report.Report;
import org.apache.jackrabbit.webdav.version.report.ReportInfo;
import org.apache.jackrabbit.webdav.version.report.ReportType;
import org.apache.jackrabbit.webdav.version.report.SupportedReportSetProperty;

import com.logicaldoc.webdav.resource.model.Resource;
import com.logicaldoc.webdav.session.DavSession;
import com.logicaldoc.webdav.web.ResourceConfig;

public class DeltaVResourceImpl extends DavResourceImpl implements DeltaVResource {

	protected static Log log = LogFactory.getLog(DeltaVResourceImpl.class);
	
    protected SupportedReportSetProperty supportedReports = new SupportedReportSetProperty();
        
    public DeltaVResourceImpl(DavResourceLocator locator, DavResourceFactory factory, DavSession session, ResourceConfig config, Resource resource) throws DavException {
    	super(locator, factory, session, config, resource);
        initSupportedReports();
    }
    
    public DeltaVResourceImpl(DavResourceLocator locator, DavResourceFactory factory, DavSession session, ResourceConfig config) throws DavException {
        super(locator, factory, session, config);
        initSupportedReports();
    }

    public DeltaVResourceImpl(DavResourceLocator locator, DavResourceFactory factory, DavSession session, ResourceConfig config, boolean isCollection) throws DavException {
        super(locator, factory, session, config, isCollection);
        initSupportedReports();
    }

    
    /**
     * @return DavResource#COMPLIANCE_CLASS
     * @see org.apache.jackrabbit.webdav.DavResource#getComplianceClass()
     */
    public String getComplianceClass() {
        return DavCompliance.concatComplianceClasses(new String[] {
                DavCompliance._1_,
                DavCompliance._2_,
                DavCompliance.VERSION_CONTROL,
                DavCompliance.VERSION_HISTORY,
                DavCompliance.LABEL
        });
    }

   
    /**
     * @param optionsInfo
     * @return object to be used in the OPTIONS response body or <code>null</code>
     * @see DeltaVResource#getOptionResponse(org.apache.jackrabbit.webdav.version.OptionsInfo)
     */
    public OptionsResponse getOptionResponse(OptionsInfo optionsInfo) {
        OptionsResponse oR = null;
        if (optionsInfo != null) {
            oR = new OptionsResponse();
            // currently only DAV:version-history-collection-set is supported
            if (optionsInfo.containsElement(DeltaVConstants.XML_VH_COLLECTION_SET, DeltaVConstants.NAMESPACE)) {
                String[] hrefs = new String[] {
                    getLocatorFromNodePath("/"+JcrConstants.JCR_SYSTEM+"/"+JcrConstants.JCR_VERSIONSTORAGE).getHref(true)
                };
                oR.addEntry(DeltaVConstants.XML_VH_COLLECTION_SET, DeltaVConstants.NAMESPACE, hrefs);
            }
        }
        return oR;
    }

    /**
     * @param reportInfo
     * @return the requested report
     * @throws DavException
     * @see DeltaVResource#getReport(org.apache.jackrabbit.webdav.version.report.ReportInfo)
     */
    public Report getReport(ReportInfo reportInfo) throws DavException {
        if (reportInfo == null) {
            throw new DavException(DavServletResponse.SC_BAD_REQUEST, "A REPORT request must provide a valid XML request body.");
        }
        if (!exists()) {
            throw new DavException(DavServletResponse.SC_NOT_FOUND);
        }

        if (supportedReports.isSupportedReport(reportInfo)) {
            Report report = ReportType.getType(reportInfo).createReport(this, reportInfo);
            return report;
        } else {
            throw new DavException(DavServletResponse.SC_UNPROCESSABLE_ENTITY, "Unkown report "+ reportInfo.getReportName() +"requested.");
        }
    }

    /**
     * The JCR api does not provide methods to create new workspaces. Calling
     * <code>addWorkspace</code> on this resource will always fail.
     *
     * @param workspace
     * @throws DavException Always throws.
     * @see DeltaVResource#addWorkspace(org.apache.jackrabbit.webdav.DavResource)
     */
    public void addWorkspace(DavResource workspace) throws DavException {
        throw new DavException(DavServletResponse.SC_FORBIDDEN);
    }

    /**
     * Return an array of <code>DavResource</code> objects that are referenced
     * by the property with the specified name.
     *
     * @param hrefPropertyName
     * @return array of <code>DavResource</code>s
     * @throws DavException
     * @see DeltaVResource#getReferenceResources(org.apache.jackrabbit.webdav.property.DavPropertyName)
     */
    @SuppressWarnings("unchecked")
	public DavResource[] getReferenceResources(DavPropertyName hrefPropertyName) throws DavException {
        DavProperty prop = getProperty(hrefPropertyName);
        List resources = new ArrayList();
        if (prop != null && prop instanceof HrefProperty) {
            HrefProperty hp = (HrefProperty)prop;
            // process list of hrefs 
            List hrefs = hp.getHrefs();
            for (Iterator iter = hrefs.iterator(); iter.hasNext();) {
                String href = (String)iter.next();
                DavResourceLocator locator = getLocator().getFactory().createResourceLocator(getLocator().getPrefix(), href);
                resources.add(createResourceFromLocator(locator));
            }
        } else {
            throw new DavException(DavServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return (DavResource[])resources.toArray(new DavResource[0]);
    }

    /**
     * Build a <code>DavResourceLocator</code> from the given nodePath path.
     *
     * @param nodePath
     * @return a new <code>DavResourceLocator</code>
     * @see DavLocatorFactory#createResourceLocator(String, String, String)
     */
    protected DavResourceLocator getLocatorFromNodePath(String nodePath) {
        DavResourceLocator loc = getLocator().getFactory().createResourceLocator(getLocator().getPrefix(), getLocator().getWorkspacePath(), nodePath, false);
        return loc;
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

    /**
     * Initialize the supported reports field
     */
    protected void initSupportedReports() {
        if (exists()) {
            supportedReports.addReportType(ReportType.EXPAND_PROPERTY);
            log.info("STR-CALL WARN: initSupportedReports");
            if (isCollection()) {
               supportedReports.addReportType(ReportType.LOCATE_BY_HISTORY);
            }
        }
    }

    /**
     * Fill the property set for this resource.
     */
    protected void initProperties() {
        if (!propsInitialized) {
            super.initProperties();
            if (exists()) {
                properties.add(supportedReports);
            }
        }
    }
}

