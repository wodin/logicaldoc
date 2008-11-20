package com.logicaldoc.webdav.io.handler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.server.io.IOUtil;
import org.apache.jackrabbit.server.io.PropertyExportContext;
import org.apache.jackrabbit.server.io.PropertyImportContext;
import org.apache.jackrabbit.webdav.DavResource;

import com.logicaldoc.webdav.context.ExportContext;
import com.logicaldoc.webdav.context.ImportContext;
import com.logicaldoc.webdav.exception.WebDavStorageException;
import com.logicaldoc.webdav.io.manager.IOManager;
import com.logicaldoc.webdav.resource.model.Resource;
import com.logicaldoc.webdav.resource.service.ResourceService;
import com.logicaldoc.webdav.web.AbstractWebdavServlet;

/**
 * <code>DefaultHandler</code> implements a simple IOHandler that creates 'file'
 * and 'folder' nodes. This handler will create the following nodes:
 * <ul>
 * <li>New <b>Collection</b>: creates a new node with the {@link #getCollectionNodeType()
 * collection nodetype}. The name of the node corresponds to the systemId
 * present on the import context.</li>
 *
 * <li>New <b>Non-Collection</b>: first creates a new node with the {@link #getNodeType()
 * non-collection nodetype}. The name of the node corresponds to the systemId
 * present on the import context. Below it creates a node with name
 * {@link JcrConstants#JCR_CONTENT jcr:content} and the nodetype specified
 * by {@link #getContentNodeType()}.</li>
 * </ul>
 * <p/>
 * Import of the content:<br>
 * The content is imported to the {@link JcrConstants#JCR_DATA} property of the
 * content node. By default this handler will fail on a attempt to create/replace
 * a collection if {@link ImportContext#hasStream()} is <code>true</code>.
 * Subclasses therefore should provide their own {@link #importData(ImportContext, boolean, Node)
 * importData} method, that handles the data according their needs.
 */
public class DefaultHandler implements IOHandler {

	protected static Log log = LogFactory.getLog(AbstractWebdavServlet.class);

    private String collectionNodetype = JcrConstants.NT_FOLDER;
    private String defaultNodetype = JcrConstants.NT_FILE;
    /* IMPORTANT NOTE: for webDAV compliancy the default nodetype of the content
       node has been changed from nt:resource to nt:unstructured. */
    private String contentNodetype = JcrConstants.NT_UNSTRUCTURED;
    private ResourceService resourceService;
    
    public void setResourceService(ResourceService resourceService) {
		this.resourceService = resourceService;
	}
    
    private IOManager ioManager;

    /**
     * Creates a new <code>DefaultHandler</code> with default nodetype definitions
     * and without setting the IOManager.
     *
     * @see IOHandler#setIOManager(IOManager)
     */
    public DefaultHandler() {
    }

    /**
     * Creates a new <code>DefaultHandler</code> with default nodetype definitions:<br>
     * <ul>
     * <li>Nodetype for Collection: {@link JcrConstants#NT_FOLDER nt:folder}</li>
     * <li>Nodetype for Non-Collection: {@link JcrConstants#NT_FILE nt:file}</li>
     * <li>Nodetype for Non-Collection content: {@link JcrConstants#NT_RESOURCE nt:resource}</li>
     * </ul>
     *
     * @param ioManager
     */
    public DefaultHandler(IOManager ioManager) {
        this.ioManager = ioManager;
    }

    /**
     * Creates a new <code>DefaultHandler</code>. Please note that the specified
     * nodetypes must match the definitions of the defaults.
     *
     * @param ioManager
     * @param collectionNodetype
     * @param defaultNodetype
     * @param contentNodetype
     */
    public DefaultHandler(IOManager ioManager, String collectionNodetype, String defaultNodetype, String contentNodetype) {
        this.ioManager = ioManager;

        this.collectionNodetype = collectionNodetype;
        this.defaultNodetype = defaultNodetype;
        this.contentNodetype = contentNodetype;
    }

    /**
     * @see IOHandler#getIOManager()
     */
    public IOManager getIOManager() {
    	return ioManager;
    }

    /**
     * @see IOHandler#setIOManager(IOManager)
     */
    public void setIOManager(IOManager ioManager) {
        this.ioManager = ioManager;
    }

    /**
     * @see IOHandler#getName()
     */
    public String getName() {
        return getClass().getName();
    }

    /**
     * @see IOHandler#canImport(ImportContext, boolean)
     */
    public boolean canImport(ImportContext context, boolean isCollection) {
        if (context == null || context.isCompleted()) {
            return false;
        }
        Resource resource = context.getResource();
        return resource != null;
    }

    /**
     * @see IOHandler#canImport(ImportContext, DavResource)
     */
    public boolean canImport(ImportContext context, DavResource resource) {
        if (resource == null) {
            return false;
        }
        return canImport(context, resource.isCollection());
    }

    /**
     * @see IOHandler#importContent(ImportContext, boolean)
     */
    public boolean importContent(ImportContext context, boolean isCollection) throws IOException {
        if (!canImport(context, isCollection)) {
            throw new IOException(getName() + ": Cannot import " + context.getSystemId());
        }

        boolean success = false;
        try {
        	success = getContentNode(context, isCollection);
        }
        catch(Exception e){
        	e.printStackTrace();
        } 

        return success;
    }

    /**
     * @see IOHandler#importContent(ImportContext, DavResource)
     */
    public boolean importContent(ImportContext context, DavResource resource) throws IOException {
        if (!canImport(context, resource)) {
            throw new IOException(getName() + ": Cannot import " + context.getSystemId());
        }
        return importContent(context, resource.isCollection());
    }

    /**
     * Imports the data present on the import context to the specified content
     * node.
     *
     * @param context
     * @param isCollection
     * @param contentNode
     * @return
     * @throws IOException
     */
    protected boolean importData(ImportContext context, boolean isCollection, Resource resource) throws IOException, WebDavStorageException {
    	return true;
    }

    /**
     * Returns true if the export root is a node and if it contains a child node
     * with name {@link JcrConstants#JCR_CONTENT jcr:content} in case this
     * export is not intended for a collection.
     *
     * @return true if the export root is a node. If the specified boolean paramter
     * is false (not a collection export) the given export root must contain a
     * child node with name {@link JcrConstants#JCR_CONTENT jcr:content}.
     *
     * @see IOHandler#canExport(ExportContext, boolean)
     */
    public boolean canExport(ExportContext context, boolean isCollection) {
        if (context == null || context.isCompleted()) {
            return false;
        }
        return true;
    }

    /**
     * @see IOHandler#canExport(ExportContext, DavResource)
     */
    public boolean canExport(ExportContext context, DavResource resource) {
        if (resource == null) {
            return false;
        }
        return canExport(context, resource.isCollection());
    }

    /**
     * Retrieves the content node that will be used for exporting properties and
     * data and calls the corresponding methods.
     *
     * @param context
     * @param isCollection
     * @see #exportProperties(ExportContext, boolean, Node)
     * @see #exportData(ExportContext, boolean, Node)
     */
    public boolean exportContent(ExportContext context, boolean isCollection) throws IOException {
        if (!canExport(context, isCollection)) {
            throw new IOException(getName() + ": Cannot export " );
        }
        try {
            if (context.hasStream()) 
                exportData(context, isCollection, context.getResource());
            
            return true;
        } catch (WebDavStorageException e) {
            // should never occur, since the proper structure of the content
            // node must be asserted in the 'canExport' call.
            throw new IOException(e.getMessage());
        }
    }

    /**
     * Same as (@link IOHandler#exportContent(ExportContext, boolean)} where
     * the boolean values is defined by {@link DavResource#isCollection()}.
     *
     * @see IOHandler#exportContent(ExportContext, DavResource)
     */
    public boolean exportContent(ExportContext context, DavResource resource) throws IOException {
        if (!canExport(context, resource)) {
            throw new IOException(getName() + ": Cannot export ");
        }
        return exportContent(context, resource.isCollection());
    }

    /**
     * Checks if the given content node contains a jcr:data property
     * and spools its value to the output stream fo the export context.<br>
     * Please note, that subclasses that define a different structure of the
     * content node should create their own
     * {@link  #exportData(ExportContext, boolean, Node) exportData} method.
     *
     * @param context
     * @param isCollection
     * @param contentNode
     * @throws IOException
     */
    protected void exportData(ExportContext context, boolean isCollection,
			Resource resource) throws IOException, WebDavStorageException {
		try {
			InputStream is = resourceService.streamOut(resource);
			if (is != null)
				IOUtil.spool(is, context.getOutputStream());
		}
		catch(FileNotFoundException e){
			throw new IOException("Can´t find file " + resource.getName() + "(" + resource.getID() + ")");
		}
    }
    
    /**
     * Retrieves/creates the node that will be used to import properties and
     * data. In case of a non-collection this includes and additional content node
     * to be created beside the 'file' node.<br>
     * Please note: If the jcr:content node already exists and contains child
     * nodes, those will be removed in order to make sure, that the import
     * really replaces the existing content of the file-node.
     *
     * @param context
     * @param isCollection
     * @return
     * @throws WebDavStorageException
     */
    protected boolean getContentNode(ImportContext context, boolean isCollection) throws WebDavStorageException {
        Resource resource = context.getResource();
        String name = context.getSystemId();

        Resource res = resourceService.getChildByName(resource, name);
        
        if(res == null){
        	resourceService.createResource(resource, name, isCollection, context);
        	return true;
        }
        
		return false; 
    }
    
    /**
     * Retrieves the content node that contains the data to be exported. In case
     * isCollection is true, this corresponds to the export root. Otherwise there
     * must be a child node with name {@link JcrConstants#JCR_CONTENT jcr:content}.
     *
     * @param context
     * @param isCollection
     * @return content node used for the export
     * @throws WebDavStorageException
     */
    protected Resource getContentNode(ExportContext context, boolean isCollection) throws WebDavStorageException {
    	return context.getResource();
    }

    /**
     * Name of the nodetype to be used to create a new collection node (folder)
     *
     * @return nodetype name
     */
    protected String getCollectionNodeType() {
        return collectionNodetype;
    }

    /**
     * Name of the nodetype to be used to create a new non-collection node (file)
     *
     * @return nodetype name
     */
    protected String getNodeType() {
        return defaultNodetype;
    }

    /**
     * Name of the nodetype to be used to create the content node below
     * a new non-collection node, whose name is always {@link JcrConstants#JCR_CONTENT
     * jcr:content}.
     *
     * @return nodetype name
     */
    protected String getContentNodeType() {
        return contentNodetype;
    }

    //----------------------------------------------------< PropertyHandler >---

    public boolean canExport(PropertyExportContext context, boolean isCollection) {
        return canExport((ExportContext) context, isCollection);
    }

    public boolean exportProperties(PropertyExportContext exportContext, boolean isCollection) throws WebDavStorageException {
        if (!canExport(exportContext, isCollection)) {
            throw new WebDavStorageException("PropertyHandler " + getName() + " failed to export properties.");
        }
        
        return true;
    }

    public boolean canImport(PropertyImportContext context, boolean isCollection) {
    	return true;
    }

}