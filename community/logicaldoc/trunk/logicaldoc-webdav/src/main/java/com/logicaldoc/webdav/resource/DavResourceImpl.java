package com.logicaldoc.webdav.resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.server.io.IOUtil;
import org.apache.jackrabbit.util.Text;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceIterator;
import org.apache.jackrabbit.webdav.DavResourceIteratorImpl;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.io.InputContext;
import org.apache.jackrabbit.webdav.io.OutputContext;
import org.apache.jackrabbit.webdav.lock.ActiveLock;
import org.apache.jackrabbit.webdav.lock.LockInfo;
import org.apache.jackrabbit.webdav.lock.LockManager;
import org.apache.jackrabbit.webdav.lock.Scope;
import org.apache.jackrabbit.webdav.lock.SupportedLock;
import org.apache.jackrabbit.webdav.lock.Type;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.jackrabbit.webdav.property.DavPropertySet;
import org.apache.jackrabbit.webdav.property.DefaultDavProperty;
import org.apache.jackrabbit.webdav.property.ResourceType;

import com.logicaldoc.util.Context;
import com.logicaldoc.webdav.context.ExportContext;
import com.logicaldoc.webdav.context.ExportContextImpl;
import com.logicaldoc.webdav.context.ImportContext;
import com.logicaldoc.webdav.context.ImportContextImpl;
import com.logicaldoc.webdav.exception.OperationNotSupportedException;
import com.logicaldoc.webdav.io.manager.IOManager;
import com.logicaldoc.webdav.resource.model.Resource;
import com.logicaldoc.webdav.resource.service.ResourceService;
import com.logicaldoc.webdav.resource.service.ResourceServiceImpl;
import com.logicaldoc.webdav.session.DavSession;
import com.logicaldoc.webdav.web.ResourceConfig;

/**
 * DavResourceImpl implements a DavResource.
 */
public class DavResourceImpl implements DavResource {

	/**
	 * the default logger
	 */
	protected static Log log = LogFactory.getLog(DavResourceImpl.class);

	private DavResourceFactory factory;

	private DavSession session;

	private DavResourceLocator locator;

	private Resource resource;

	protected DavPropertySet properties = new DavPropertySet();

	protected boolean propsInitialized = false;

	private boolean isCollection = true;

	private ResourceConfig config;

	//private long modificationTime = IOUtil.UNDEFINED_TIME;
	private long modificationTime = System.currentTimeMillis();

	private ResourceService resourceService;

	public DavResourceImpl(DavResourceLocator locator, DavResourceFactory factory, DavSession session,
			ResourceConfig config, Resource resource) {
		this.locator = locator;
		this.resource = resource;
		this.factory = factory;
		this.config = config;
		this.session = session;
		this.isCollection = this.resource.isFolder();
		resourceService = (ResourceService) Context.getInstance().getBean("ResourceService");
		if (this.resource != null)
			this.resource.setRequestedPerson(Long.parseLong(session.getObject("id").toString()));
	}

	/**
	 * Create a new {@link DavResource}.
	 * 
	 * @param locator
	 * @param factory
	 * @param session
	 * 
	 */
	public DavResourceImpl(DavResourceLocator locator, DavResourceFactory factory, DavSession session,
			ResourceConfig config) throws DavException {

		this.factory = factory;
		this.locator = locator;
		this.config = config;
		this.session = session;
		resourceService = (ResourceService) Context.getInstance().getBean("ResourceService");
	}

	/**
	 * Create a new {@link DavResource}.
	 * 
	 * @param locator
	 * @param factory
	 * @param session
	 * @param config
	 * @param isCollection
	 * @throws DavException
	 */
	public DavResourceImpl(DavResourceLocator locator, DavResourceFactory factory, DavSession session,
			ResourceConfig config, boolean isCollection) throws DavException {
		this(locator, factory, session, config);
		this.isCollection = isCollection;
		resourceService = (ResourceService) Context.getInstance().getBean("ResourceService");
	}

	/**
	 * @return DavResource#COMPLIANCE_CLASS
	 * @see org.apache.jackrabbit.webdav.DavResource#getComplianceClass()
	 */
	public String getComplianceClass() {
		return DavResource.COMPLIANCE_CLASS;
	}

	/**
	 * @return DavResource#METHODS
	 * @see org.apache.jackrabbit.webdav.DavResource#getSupportedMethods()
	 */
	public String getSupportedMethods() {
		return DavResource.METHODS;
	}

	/**
	 * @see DavResource#exists() )
	 */
	public boolean exists() {
		return resource != null;
	}

	/**
	 * @see DavResource#isCollection()
	 */
	public boolean isCollection() {
		return isCollection;
	}

	/**
	 * Package protected method that allows to define whether this resource
	 * represents a collection or not.
	 * 
	 * @param isCollection
	 * @deprecated Use the constructor taking a boolean flag instead.
	 */
	void setIsCollection(boolean isCollection) {
		this.isCollection = isCollection;
	}

	/**
	 * @see org.apache.jackrabbit.webdav.DavResource#getLocator()
	 */
	public DavResourceLocator getLocator() {
		return locator;
	}

	/**
	 * @see DavResource#getResourcePath()
	 */
	public String getResourcePath() {
		return locator.getResourcePath();
	}

	/**
	 * @see DavResource#getHref()
	 */
	public String getHref() {
		return locator.getHref(isCollection());
	}

	/**
	 * Returns the the last segment of the resource path.
	 * <p>
	 * Note that this must not correspond to the name of the underlying
	 * repository item for two reasons:
	 * <ul>
	 * <li>SameNameSiblings have an index appended to their item name.</li>
	 * <li>the resource path may differ from the item path.</li>
	 * </ul>
	 * Using the item name as DAV:displayname caused problems with XP built-in
	 * client in case of resources representing SameNameSibling nodes.
	 * 
	 * @see DavResource#getDisplayName()
	 */
	public String getDisplayName() {
		String resPath = getResourcePath();
		return (resPath != null) ? Text.getName(resPath) : resPath;
	}

	/**
	 * @see org.apache.jackrabbit.webdav.DavResource#getModificationTime()
	 */
	public long getModificationTime() {
		
		log.fatal("getModificationTime()");
		
		initProperties();
		return this.modificationTime;
	}
	


	/**
	 * If this resource exists and the specified context is not
	 * <code>null</code> this implementation build a new {@link ExportContext}
	 * based on the specified context and forwards the export to its
	 * <code>IOManager</code>. If the
	 * {@link IOManager#exportContent(ExportContext, DavResource)} fails, an
	 * <code>IOException</code> is thrown.
	 * 
	 * @see DavResource#spool(OutputContext)
	 * @see ResourceConfig#getIOManager()
	 * @throws IOException if the export fails.
	 */
	public void spool(OutputContext outputContext) throws IOException {
		if (exists() && outputContext != null) {
			ExportContext exportCtx = getExportContext(outputContext);
			if (!config.getIOManager().exportContent(exportCtx, this)) {
				throw new IOException("Unexpected Error while spooling resource.");
			}
		}
	}

	/**
	 * @see DavResource#getProperty(org.apache.jackrabbit.webdav.property.DavPropertyName)
	 */
	public DavProperty getProperty(DavPropertyName name) {
		initProperties();
		
		log.fatal("getProperty(..) " + name);
		
		return properties.get(name);
	}

	/**
	 * @see DavResource#getProperties()
	 */
	public DavPropertySet getProperties() {
		
		log.fatal("getProperties()");
		
		initProperties();
		return properties;
	}

	/**
	 * @see DavResource#getPropertyNames()
	 */
	public DavPropertyName[] getPropertyNames() {
		return getProperties().getPropertyNames();
	}

	/**
	 * Fill the set of properties
	 */
	protected void initProperties() {
		if (!exists() || propsInitialized) {
			return;
		}

		// set (or reset) fundamental properties
		if (getDisplayName() != null) {
			properties.add(new DefaultDavProperty(DavPropertyName.DISPLAYNAME, getDisplayName()));
		}
		if (isCollection()) {
			properties.add(new ResourceType(ResourceType.COLLECTION));
			// Windows XP support
			properties.add(new DefaultDavProperty(DavPropertyName.ISCOLLECTION, "1"));
		} else {
			properties.add(new ResourceType(ResourceType.DEFAULT_RESOURCE));
			// Windows XP support
			properties.add(new DefaultDavProperty(DavPropertyName.ISCOLLECTION, "0"));
		}

		SupportedLock supportedLock = new SupportedLock();
		supportedLock.addEntry(Type.WRITE, Scope.EXCLUSIVE);
		properties.add(supportedLock);
		properties.add(new DefaultDavProperty(DavPropertyName.GETCONTENTLENGTH, this.resource.getContentLength()));
		
		// TODO: 
		String lastModified = IOUtil.getLastModified(modificationTime);
		properties.add(new DefaultDavProperty(DavPropertyName.GETLASTMODIFIED, lastModified));
		
		propsInitialized = true;
	}

	/**
	 * @see DavResource#alterProperties(DavPropertySet, DavPropertyNameSet)
	 */
	public MultiStatusResponse alterProperties(DavPropertySet setProperties, DavPropertyNameSet removePropertyNames)
			throws DavException {
		throw new OperationNotSupportedException();
	}

	@SuppressWarnings("unchecked")
	public MultiStatusResponse alterProperties(List changeList) throws DavException {
		throw new OperationNotSupportedException();
	}

	/**
	 * @see DavResource#getCollection()
	 */
	public DavResource getCollection() {
		DavResource parent = null;
		if (getResourcePath() != null && !getResourcePath().equals("/")) {
			String parentPath = Text.getRelativeParent(getResourcePath(), 1);
			if (parentPath.equals("")) {
				parentPath = "/";
			}
			DavResourceLocator parentloc = null;
			try {
				parentloc = locator.getFactory().createResourceLocator(locator.getPrefix(), "/", parentPath);
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				parent = factory.createResource(parentloc, session);
			} catch (DavException e) {
				// should not occur
			}
		}
		return parent;
	}

	/**
	 * @see DavResource#getMembers()
	 */
	@SuppressWarnings("unchecked")
	public DavResourceIterator getMembers() {
		ArrayList list = new ArrayList();
		if (exists() && isCollection()) {
			try {
				String path = locator.getResourcePath() == null ? "/" : locator.getResourcePath() + "/";

				List<Resource> resources = resourceService.getChildResources(this.resource);
				Iterator<Resource> resourceIterator = resources.iterator();

				while (resourceIterator.hasNext()) {
					Resource resource = resourceIterator.next();

					String currentFilePath = path;
					if (currentFilePath.lastIndexOf("/") == currentFilePath.length() - 1) {
						currentFilePath = currentFilePath + resource.getName();
					} else {
						currentFilePath = currentFilePath + "/" + resource.getName();
					}

					DavResourceLocator resourceLocator = locator.getFactory().createResourceLocator(
							locator.getPrefix(), "", currentFilePath, false);

					DavResource childRes = factory.createResource(resourceLocator, session);

					list.add(childRes);
				}

			}

			catch (DavException e) {
				throw new RuntimeException(e);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return new DavResourceIteratorImpl(list);
	}

	/**
	 * Adds a new member to this resource.
	 * 
	 * @see DavResource#addMember(DavResource,
	 *      org.apache.jackrabbit.webdav.io.InputContext)
	 */
	public void addMember(DavResource member, InputContext inputContext) throws DavException {
		if (!exists()) {
			throw new DavException(DavServletResponse.SC_CONFLICT);
		}
		if (isLocked(this) || isLocked(member)) {
			throw new DavException(DavServletResponse.SC_LOCKED);
		}
		try {

			String memberName = Text.getName(member.getLocator().getResourcePath());

			ImportContext ctx = getImportContext(inputContext, memberName);
			if (!config.getIOManager().importContent(ctx, member)) {
				// any changes should have been reverted in the importer
				throw new DavException(DavServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * @see DavResource#removeMember(DavResource)
	 */
	public void removeMember(DavResource member) throws DavException {
		if (!exists() || !member.exists()) {
			throw new DavException(DavServletResponse.SC_NOT_FOUND);
		}
		if (isLocked(this) || isLocked(member)) {
			throw new DavException(DavServletResponse.SC_LOCKED);
		}

		try {
			Resource resource = resourceService.getResource(member.getLocator().getResourcePath(), this.resource
					.getRequestedPerson());
			resourceService.deleteResource(resource);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see DavResource#move(DavResource)
	 */

	public void move(DavResource destination) throws DavException {
		if (!exists()) {
			throw new DavException(DavServletResponse.SC_NOT_FOUND);
		}
		if (isLocked(this)) {
			throw new DavException(DavServletResponse.SC_LOCKED);
		}

		try {
			
			Resource res = resourceService.getResource(destination.getLocator().getResourcePath(), this.resource
					.getRequestedPerson());
			if (res != null) {
				res.setName(this.resource.getName());
				Resource parentResource = resourceService.getParentResource(res);
				resourceService.move(this.resource, parentResource);
			} else {
				String name = destination.getLocator().getResourcePath();
				name = name.substring(name.lastIndexOf("/") + 1, name.length()).replace("/default", "");

				Resource parentResource = resourceService.getParentResource(destination.getLocator().getResourcePath());
				this.resource.setName(name);

				resourceService.move(this.resource, parentResource);
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see DavResource#copy(DavResource, boolean)
	 */
	public void copy(DavResource destination, boolean shallow) throws DavException {
		if (!exists()) {
			throw new DavException(DavServletResponse.SC_NOT_FOUND);
		}
		if (isLocked(destination)) {
			throw new DavException(DavServletResponse.SC_LOCKED);
		}

		if (shallow && isCollection()) {
			// TODO: currently no support for shallow copy; however this is
			// only relevant if the source resource is a collection, because
			// otherwise it doesn't make a difference
			throw new DavException(DavServletResponse.SC_FORBIDDEN, "Unable to perform shallow copy.");
		}
		try {

			ResourceService resourceService = new ResourceServiceImpl();
			resourceService.copyResource(
					resourceService.getParentResource(destination.getLocator().getWorkspacePath()), this.resource);

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param type
	 * @param scope
	 * @return true if type is {@link Type#WRITE} and scope is
	 *         {@link Scope#EXCLUSIVE}
	 * @see DavResource#isLockable(org.apache.jackrabbit.webdav.lock.Type,
	 *      org.apache.jackrabbit.webdav.lock.Scope)
	 */
	public boolean isLockable(Type type, Scope scope) {
		throw new OperationNotSupportedException();
	}

	/**
	 * @see DavResource#hasLock(org.apache.jackrabbit.webdav.lock.Type,
	 *      org.apache.jackrabbit.webdav.lock.Scope)
	 */
	public boolean hasLock(Type type, Scope scope) {
		throw new OperationNotSupportedException();
	}

	/**
	 * @see DavResource#getLock(Type, Scope)
	 */
	public ActiveLock getLock(Type type, Scope scope) {
		return null;
	}

	/**
	 * @see org.apache.jackrabbit.webdav.DavResource#getLocks()
	 */
	public ActiveLock[] getLocks() {
		throw new OperationNotSupportedException();
	}

	/**
	 * @see DavResource#lock(LockInfo)
	 */
	public ActiveLock lock(LockInfo lockInfo) throws DavException {
		throw new OperationNotSupportedException();
	}

	/**
	 * @see DavResource#refreshLock(LockInfo, String)
	 */
	public ActiveLock refreshLock(LockInfo lockInfo, String lockToken) throws DavException {
		throw new OperationNotSupportedException();
	}

	/**
	 * @see DavResource#unlock(String)
	 */
	public void unlock(String lockToken) throws DavException {
		throw new OperationNotSupportedException();
	}

	/**
	 * @see DavResource#addLockManager(org.apache.jackrabbit.webdav.lock.LockManager)
	 */
	public void addLockManager(LockManager lockMgr) {
		throw new OperationNotSupportedException();
	}

	/**
	 * @see org.apache.jackrabbit.webdav.DavResource#getFactory()
	 * @deprecated JackRabbit usage
	 */
	public org.apache.jackrabbit.webdav.DavResourceFactory getFactory() {
		throw new OperationNotSupportedException();
	}

	/**
	 * @see org.apache.jackrabbit.webdav.DavResource#getSession()
	 */
	public org.apache.jackrabbit.webdav.DavSession getSession() {
		throw new OperationNotSupportedException();
	}

	/**
	 * 
	 * @return
	 */
	protected Resource getResource() {
		return this.resource;
	}

	/**
	 * Returns a new <code>ImportContext</code>
	 * 
	 * @param inputCtx
	 * @param systemId
	 * @return
	 * @throws IOException
	 */
	protected ImportContext getImportContext(InputContext inputCtx, String systemId) throws IOException {
		return new ImportContextImpl(resource, systemId, inputCtx, config.getMimeResolver());
	}

	/**
	 * Returns a new <code>ExportContext</code>
	 * 
	 * @param outputCtx
	 * @return
	 * @throws IOException
	 */
	protected ExportContext getExportContext(OutputContext outputCtx) throws IOException {
		return new ExportContextImpl(this.resource, outputCtx, config.getMimeResolver());
	}

	/**
	 * Return true if this resource cannot be modified due to a write lock that
	 * is not owned by the given session.
	 * 
	 * @return true if this resource cannot be modified due to a write lock
	 */
	private boolean isLocked(DavResource res) {
		return false;
	}

	@Override
	public void removeProperty(DavPropertyName arg0) throws DavException {
		throw new OperationNotSupportedException();
	}

	@Override
	public void setProperty(DavProperty arg0) throws DavException {
		throw new OperationNotSupportedException();
	}
}
