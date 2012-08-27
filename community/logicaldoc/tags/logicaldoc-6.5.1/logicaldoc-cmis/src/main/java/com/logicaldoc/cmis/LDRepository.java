package com.logicaldoc.cmis;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.Ace;
import org.apache.chemistry.opencmis.commons.data.Acl;
import org.apache.chemistry.opencmis.commons.data.AllowableActions;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.data.FailedToDeleteData;
import org.apache.chemistry.opencmis.commons.data.ObjectData;
import org.apache.chemistry.opencmis.commons.data.ObjectInFolderContainer;
import org.apache.chemistry.opencmis.commons.data.ObjectInFolderData;
import org.apache.chemistry.opencmis.commons.data.ObjectInFolderList;
import org.apache.chemistry.opencmis.commons.data.ObjectParentData;
import org.apache.chemistry.opencmis.commons.data.PermissionMapping;
import org.apache.chemistry.opencmis.commons.data.Properties;
import org.apache.chemistry.opencmis.commons.data.PropertyData;
import org.apache.chemistry.opencmis.commons.data.PropertyDateTime;
import org.apache.chemistry.opencmis.commons.data.PropertyId;
import org.apache.chemistry.opencmis.commons.data.PropertyString;
import org.apache.chemistry.opencmis.commons.data.RepositoryInfo;
import org.apache.chemistry.opencmis.commons.definitions.PermissionDefinition;
import org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinition;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinitionContainer;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinitionList;
import org.apache.chemistry.opencmis.commons.enums.AclPropagation;
import org.apache.chemistry.opencmis.commons.enums.Action;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.enums.CapabilityAcl;
import org.apache.chemistry.opencmis.commons.enums.CapabilityChanges;
import org.apache.chemistry.opencmis.commons.enums.CapabilityContentStreamUpdates;
import org.apache.chemistry.opencmis.commons.enums.CapabilityJoin;
import org.apache.chemistry.opencmis.commons.enums.CapabilityQuery;
import org.apache.chemistry.opencmis.commons.enums.CapabilityRenditions;
import org.apache.chemistry.opencmis.commons.enums.SupportedPermissions;
import org.apache.chemistry.opencmis.commons.enums.Updatability;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.exceptions.CmisBaseException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisConstraintException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisInvalidArgumentException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisNameConstraintViolationException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisStorageException;
import org.apache.chemistry.opencmis.commons.impl.Converter;
import org.apache.chemistry.opencmis.commons.impl.MimeTypes;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.AccessControlEntryImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.AccessControlListImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.AccessControlPrincipalDataImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.AclCapabilitiesDataImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.AllowableActionsImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.FailedToDeleteDataImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ObjectDataImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ObjectInFolderDataImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ObjectInFolderListImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ObjectParentDataImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PermissionDefinitionDataImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PermissionMappingDataImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertiesImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertyBooleanImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertyDateTimeImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertyDecimalImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertyHtmlImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertyIdImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertyIntegerImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertyStringImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertyUriImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.RepositoryCapabilitiesImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.RepositoryInfoImpl;
import org.apache.chemistry.opencmis.commons.impl.jaxb.CmisObjectType;
import org.apache.chemistry.opencmis.commons.impl.server.ObjectInfoImpl;
import org.apache.chemistry.opencmis.commons.server.CallContext;
import org.apache.chemistry.opencmis.commons.server.ObjectInfoHandler;
import org.apache.chemistry.opencmis.commons.spi.Holder;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.core.PersistentObject;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.DocumentManager;
import com.logicaldoc.core.document.History;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.security.Folder;
import com.logicaldoc.core.security.FolderHistory;
import com.logicaldoc.core.security.Permission;
import com.logicaldoc.core.security.SessionManager;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.UserSession;
import com.logicaldoc.core.security.dao.FolderDAO;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.core.store.Storer;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.ContextProperties;

/**
 * LogicalDOC implementation of a CMIS Repository
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.5.1
 */
public class LDRepository {

	private static final String ID_PREFIX_DOC = "doc.";

	private static final String ID_PREFIX_FLD = "fld.";

	private static final String USER_UNKNOWN = "<unknown>";

	private static final String CMIS_READ = "cmis:read";

	private static final String CMIS_WRITE = "cmis:write";

	private static final String CMIS_ALL = "cmis:all";

	private static final int BUFFER_SIZE = 4 * 1024;

	private static final Logger log = LoggerFactory.getLogger(LDRepository.class);

	/** Repository id */
	private final String id;

	private Folder root;

	/** User table */
	private final Map<String, Boolean> userMap;

	/** Repository info */
	private final RepositoryInfoImpl repositoryInfo;

	/** Types */
	private TypeManager types = new TypeManager();

	private UserDAO userDao;

	private FolderDAO folderDao;

	private DocumentDAO documentDao;

	private DocumentManager documentManager;

	private String sid;

	/**
	 * Constructor.
	 * 
	 * @param id CMIS repository id
	 * @param root root folder
	 * @param types type manager object
	 */
	public LDRepository(Folder root, String sid) {
		// check root folder
		if ((root == null)) {
			throw new IllegalArgumentException("Invalid root folder!");
		}

		userDao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
		folderDao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		documentDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		documentManager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);

		ContextProperties config = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);

		this.sid = sid;
		this.root = root;
		this.id = Long.toString(root.getId());

		// set up user table
		userMap = new HashMap<String, Boolean>();

		// compile repository info
		repositoryInfo = new RepositoryInfoImpl();

		repositoryInfo.setId(id);
		repositoryInfo.setName(root.getName());
		repositoryInfo.setDescription(root.getDescription());

		repositoryInfo.setCmisVersionSupported("1.0");

		repositoryInfo.setProductName("LogicalDOC");
		repositoryInfo.setProductVersion(config.getProperty("product.release"));
		repositoryInfo.setVendorName("Logical Objects");

		repositoryInfo.setRootFolder(getId(root));

		repositoryInfo.setThinClientUri("");

		RepositoryCapabilitiesImpl capabilities = new RepositoryCapabilitiesImpl();
		capabilities.setCapabilityAcl(CapabilityAcl.DISCOVER);
		capabilities.setAllVersionsSearchable(false);
		capabilities.setCapabilityJoin(CapabilityJoin.NONE);
		capabilities.setSupportsMultifiling(false);
		capabilities.setSupportsUnfiling(false);
		capabilities.setSupportsVersionSpecificFiling(false);
		capabilities.setIsPwcSearchable(false);
		capabilities.setIsPwcUpdatable(false);
		capabilities.setCapabilityQuery(CapabilityQuery.NONE);
		capabilities.setCapabilityChanges(CapabilityChanges.NONE);
		capabilities.setCapabilityContentStreamUpdates(CapabilityContentStreamUpdates.ANYTIME);
		capabilities.setSupportsGetDescendants(true);
		capabilities.setSupportsGetFolderTree(true);
		capabilities.setCapabilityRendition(CapabilityRenditions.NONE);

		repositoryInfo.setCapabilities(capabilities);

		AclCapabilitiesDataImpl aclCapability = new AclCapabilitiesDataImpl();
		aclCapability.setSupportedPermissions(SupportedPermissions.BASIC);
		aclCapability.setAclPropagation(AclPropagation.OBJECTONLY);

		// permissions
		List<PermissionDefinition> permissions = new ArrayList<PermissionDefinition>();
		permissions.add(createPermission(CMIS_READ, "Read"));
		permissions.add(createPermission(CMIS_WRITE, "Write"));
		permissions.add(createPermission(CMIS_ALL, "All"));
		aclCapability.setPermissionDefinitionData(permissions);

		// mapping
		List<PermissionMapping> list = new ArrayList<PermissionMapping>();
		list.add(createMapping(PermissionMapping.CAN_CREATE_DOCUMENT_FOLDER, CMIS_READ));
		list.add(createMapping(PermissionMapping.CAN_CREATE_FOLDER_FOLDER, CMIS_READ));
		list.add(createMapping(PermissionMapping.CAN_DELETE_CONTENT_DOCUMENT, CMIS_WRITE));
		list.add(createMapping(PermissionMapping.CAN_DELETE_OBJECT, CMIS_WRITE));
		list.add(createMapping(PermissionMapping.CAN_DELETE_TREE_FOLDER, CMIS_ALL));
		list.add(createMapping(PermissionMapping.CAN_GET_ACL_OBJECT, CMIS_READ));
		list.add(createMapping(PermissionMapping.CAN_GET_ALL_VERSIONS_VERSION_SERIES, CMIS_READ));
		list.add(createMapping(PermissionMapping.CAN_GET_CHILDREN_FOLDER, CMIS_READ));
		list.add(createMapping(PermissionMapping.CAN_GET_DESCENDENTS_FOLDER, CMIS_READ));
		list.add(createMapping(PermissionMapping.CAN_GET_FOLDER_PARENT_OBJECT, CMIS_READ));
		list.add(createMapping(PermissionMapping.CAN_GET_PARENTS_FOLDER, CMIS_READ));
		list.add(createMapping(PermissionMapping.CAN_GET_PROPERTIES_OBJECT, CMIS_READ));
		list.add(createMapping(PermissionMapping.CAN_MOVE_OBJECT, CMIS_WRITE));
		list.add(createMapping(PermissionMapping.CAN_MOVE_SOURCE, CMIS_READ));
		list.add(createMapping(PermissionMapping.CAN_MOVE_TARGET, CMIS_WRITE));
		list.add(createMapping(PermissionMapping.CAN_SET_CONTENT_DOCUMENT, CMIS_WRITE));
		list.add(createMapping(PermissionMapping.CAN_UPDATE_PROPERTIES_OBJECT, CMIS_WRITE));
		list.add(createMapping(PermissionMapping.CAN_VIEW_CONTENT_OBJECT, CMIS_READ));
		Map<String, PermissionMapping> map = new LinkedHashMap<String, PermissionMapping>();
		for (PermissionMapping pm : list) {
			map.put(pm.getKey(), pm);
		}
		aclCapability.setPermissionMappingData(map);

		repositoryInfo.setAclCapabilities(aclCapability);
	}

	private static PermissionDefinition createPermission(String permission, String description) {
		PermissionDefinitionDataImpl pd = new PermissionDefinitionDataImpl();
		pd.setPermission(permission);
		pd.setDescription(description);

		return pd;
	}

	private static PermissionMapping createMapping(String key, String permission) {
		PermissionMappingDataImpl pm = new PermissionMappingDataImpl();
		pm.setKey(key);
		pm.setPermissions(Collections.singletonList(permission));

		return pm;
	}

	/**
	 * Adds a user to the repository.
	 */
	public void addUser(String user, boolean readOnly) {
		if ((user == null) || (user.length() == 0)) {
			return;
		}

		userMap.put(user, readOnly);
	}

	/**
	 * CMIS getRepositoryInfo.
	 */
	public RepositoryInfo getRepositoryInfo(CallContext context) {
		debug("getRepositoryInfo");
		checkPermission(context.getRepositoryId(), context, null);

		return repositoryInfo;
	}

	/**
	 * CMIS getTypesChildren.
	 */
	public TypeDefinitionList getTypesChildren(CallContext context, String typeId, boolean includePropertyDefinitions,
			BigInteger maxItems, BigInteger skipCount) {
		debug("getTypesChildren");
		checkPermission(context.getRepositoryId(), context, null);
		return types.getTypesChildren(context, typeId, includePropertyDefinitions, maxItems, skipCount);
	}

	/**
	 * CMIS getTypeDefinition.
	 */
	public TypeDefinition getTypeDefinition(CallContext context, String typeId) {
		debug("getTypeDefinition");
		checkPermission(context.getRepositoryId(), context, null);

		return types.getTypeDefinition(context, typeId);
	}

	/**
	 * CMIS getTypesDescendants.
	 */
	public List<TypeDefinitionContainer> getTypesDescendants(CallContext context, String typeId, BigInteger depth,
			Boolean includePropertyDefinitions) {
		debug("getTypesDescendants");
		checkPermission(context.getRepositoryId(), context, null);
		return types.getTypesDescendants(context, typeId, depth, includePropertyDefinitions);
	}

	/**
	 * Create dispatch for AtomPub.
	 */
	public ObjectData create(CallContext context, Properties properties, String folderId, ContentStream contentStream,
			VersioningState versioningState, ObjectInfoHandler objectInfos) {
		debug("create");
		boolean userReadOnly = checkPermission(folderId, context, Permission.WRITE);

		String typeId = getTypeId(properties);

		TypeDefinition type = types.getType(typeId);
		if (type == null) {
			throw new CmisObjectNotFoundException("Type '" + typeId + "' is unknown!");
		}

		String objectId = null;
		if (type.getBaseTypeId() == BaseTypeId.CMIS_DOCUMENT) {
			objectId = createDocument(context, properties, folderId, contentStream, versioningState);
			return compileObjectType(context, getDocument(objectId), null, false, false, userReadOnly, objectInfos);
		} else if (type.getBaseTypeId() == BaseTypeId.CMIS_FOLDER) {
			objectId = createFolder(context, properties, folderId);
			return compileObjectType(context, getFolder(objectId), null, false, false, userReadOnly, objectInfos);
		} else {
			throw new CmisObjectNotFoundException("Cannot create object of type '" + typeId + "'!");
		}
	}

	/**
	 * CMIS createDocument.
	 */
	public String createDocument(CallContext context, Properties properties, String folderId,
			ContentStream contentStream, VersioningState versioningState) {
		debug("createDocument");
		checkPermission(folderId, context, Permission.WRITE);

		// check properties
		if ((properties == null) || (properties.getProperties() == null)) {
			throw new CmisInvalidArgumentException("Properties must be set!");
		}

		// check versioning state
		if (VersioningState.NONE != versioningState) {
			throw new CmisConstraintException("Versioning not supported!");
		}

		// check type
		String typeId = getTypeId(properties);
		TypeDefinition type = types.getType(typeId);
		if (type == null) {
			throw new CmisObjectNotFoundException("Type '" + typeId + "' is unknown!");
		}

		User user = getSessionUser();

		// compile the properties
		Properties props = compileProperties(typeId, user, millisToCalendar(System.currentTimeMillis()), user,
				properties);

		// check the name
		String name = getStringProperty(properties, PropertyIds.NAME);
		if (name == null) {
			throw new CmisNameConstraintViolationException("Name is not valid!");
		}

		String fileName = getStringProperty(properties, PropertyIds.CONTENT_STREAM_FILE_NAME);
		if (fileName == null)
			fileName = getStringProperty(properties, "Filename");
		if (fileName == null) {
			fileName = name;
			if (name.lastIndexOf('.') > 0)
				name = fileName.substring(0, name.lastIndexOf('.'));
		}
		if (!isValidName(fileName)) {
			throw new CmisNameConstraintViolationException("File name is not valid!");
		}

		// get parent Folder
		Folder parent = getFolder(folderId);
		if (parent == null) {
			throw new CmisObjectNotFoundException("Parent is not a folder!");
		}

		History transaction = new History();
		transaction.setUser(getSessionUser());
		transaction.setSessionId(sid);

		Document document = new Document();
		document.setTitle(name);
		document.setFileName(fileName);
		document.setFolder(getFolder(folderId));
		document.setLanguage(user.getLanguage());

		try {
			document = documentManager.create(new BufferedInputStream(contentStream.getStream(), BUFFER_SIZE),
					document, transaction);
		} catch (Throwable e) {
			throw new CmisStorageException("Could not create document: " + e.getMessage(), e);
		}

		// create object
		CmisObjectType object = new CmisObjectType();
		object.setProperties(Converter.convert(props));

		return getId(document);
	}

	/**
	 * CMIS createDocumentFromSource.
	 */
	public String createDocumentFromSource(CallContext context, String sourceId, Properties properties,
			String folderId, VersioningState versioningState) {

		// check versioning state
		if (VersioningState.NONE != versioningState) {
			throw new CmisConstraintException("Versioning not supported!");
		}

		// get parent File
		PersistentObject parent = getObject(folderId);
		if (!(parent instanceof Folder)) {
			throw new CmisObjectNotFoundException("Parent is not a folder!");
		}

		// get source Object
		PersistentObject source = getObject(sourceId);
		if (!(source instanceof Document)) {
			throw new CmisObjectNotFoundException("Source is not a document!");
		}

		Document doc = (Document) source;

		// file name
		String name = doc.getFileName();

		// get properties
		PropertiesImpl sourceProperties = new PropertiesImpl();
		readCustomProperties(source, sourceProperties, null, new ObjectInfoImpl());

		// get the type id
		String typeId = getIdProperty(sourceProperties, PropertyIds.OBJECT_TYPE_ID);
		if (typeId == null) {
			typeId = TypeManager.DOCUMENT_TYPE_ID;
		}

		// copy properties
		PropertiesImpl newProperties = new PropertiesImpl();
		for (PropertyData<?> prop : sourceProperties.getProperties().values()) {
			if ((prop.getId().equals(PropertyIds.OBJECT_TYPE_ID)) || (prop.getId().equals(PropertyIds.CREATED_BY))
					|| (prop.getId().equals(PropertyIds.CREATION_DATE))
					|| (prop.getId().equals(PropertyIds.LAST_MODIFIED_BY))) {
				continue;
			}

			newProperties.addProperty(prop);
		}

		// replace properties
		if (properties != null) {
			// find new name
			String newName = getStringProperty(properties, PropertyIds.NAME);
			if (newName != null) {
				if (!isValidName(newName)) {
					throw new CmisNameConstraintViolationException("Name is not valid!");
				}
				name = newName;
			}

			// TODO implement
			// get the property definitions
			// TypeDefinition type = types.getType(typeId);
			// if (type == null) {
			// throw new CmisObjectNotFoundException("Type '" + typeId +
			// "' is unknown!");
			// }

			// // replace with new values
			// for (PropertyData<?> prop : properties.getProperties().values())
			// {
			// PropertyDefinition<?> propType =
			// type.getPropertyDefinitions().get(prop.getId());
			//
			// // do we know that property?
			// if (propType == null) {
			// throw new CmisConstraintException("Property '" + prop.getId() +
			// "' is unknown!");
			// }
			//
			// // can it be set?
			// if ((propType.getUpdatability() != Updatability.READWRITE)) {
			// throw new CmisConstraintException("Property '" + prop.getId() +
			// "' cannot be updated!");
			// }
			//
			// // empty properties are invalid
			// if (isEmptyProperty(prop)) {
			// throw new CmisConstraintException("Property '" + prop.getId() +
			// "' must not be empty!");
			// }
			//
			// newProperties.addProperty(prop);
			// }
		}

		addPropertyId(newProperties, typeId, null, PropertyIds.OBJECT_TYPE_ID, typeId);
		addPropertyString(newProperties, typeId, null, PropertyIds.CREATED_BY, context.getUsername());
		addPropertyDateTime(newProperties, typeId, null, PropertyIds.CREATION_DATE,
				millisToCalendar(System.currentTimeMillis()));
		addPropertyString(newProperties, typeId, null, PropertyIds.LAST_MODIFIED_BY, context.getUsername());

		// TODO Implement
		return null;
		// // check the file
		// File newFile = new File(parent, name);
		// if (newFile.exists()) {
		// throw new
		// CmisNameConstraintViolationException("Document already exists.");
		// }
		//
		// // create the file
		// try {
		// newFile.createNewFile();
		// } catch (IOException e) {
		// throw new CmisStorageException("Could not create file: " +
		// e.getMessage(), e);
		// }
		//
		// // copy content
		// try {
		// OutputStream out = new BufferedOutputStream(new
		// FileOutputStream(newFile));
		// InputStream in = new BufferedInputStream(new
		// FileInputStream(source));
		//
		// byte[] buffer = new byte[BUFFER_SIZE];
		// int b;
		// while ((b = in.read(buffer)) > -1) {
		// out.write(buffer, 0, b);
		// }
		//
		// out.flush();
		// out.close();
		// in.close();
		// } catch (Exception e) {
		// throw new CmisStorageException("Could not roead or write content: " +
		// e.getMessage(), e);
		// }
		//
		// // write properties
		// writePropertiesFile(newFile, newProperties);
		//
		// return getId(newFile);
	}

	/**
	 * CMIS createFolder.
	 */
	public String createFolder(CallContext context, Properties properties, String folderId) {
		debug("createFolder");
		checkPermission(folderId, context, Permission.WRITE);

		// check properties
		if ((properties == null) || (properties.getProperties() == null)) {
			throw new CmisInvalidArgumentException("Properties must be set!");
		}

		// check type
		String typeId = getTypeId(properties);
		TypeDefinition type = types.getType(typeId);
		if (type == null) {
			throw new CmisObjectNotFoundException("Type '" + typeId + "' is unknown!");
		}

		User user = getSessionUser();

		// compile the properties
		Properties props = compileProperties(typeId, user, millisToCalendar(System.currentTimeMillis()), user,
				properties);

		// check the name
		String name = getStringProperty(properties, PropertyIds.NAME);
		if (!isValidName(name)) {
			throw new CmisNameConstraintViolationException("Name is not valid.");
		}

		// get parent File
		Folder parent = getFolder(folderId);
		if (parent == null) {
			throw new CmisObjectNotFoundException("Parent is not a folder!");
		}

		FolderHistory transaction = new FolderHistory();
		transaction.setUser(getSessionUser());
		transaction.setSessionId(sid);

		Folder folder = null;
		try {
			folder = folderDao.create(parent, name, transaction);
		} catch (Throwable e) {
			throw new CmisStorageException("Could not create document: " + e.getMessage(), e);
		}

		// create object
		CmisObjectType object = new CmisObjectType();
		object.setProperties(Converter.convert(props));

		return getId(folder);
	}

	/**
	 * CMIS moveObject.
	 */
	public ObjectData moveObject(CallContext context, Holder<String> objectId, String targetFolderId,
			ObjectInfoHandler objectInfos) {
		debug("moveObject");
		boolean userReadOnly = checkPermission(targetFolderId, context, Permission.WRITE)
				&& checkPermission(targetFolderId, context, Permission.DOWNLOAD);

		if (objectId == null) {
			throw new CmisInvalidArgumentException("Id is not valid!");
		}

		// TODO implement

		// // get the file and parent
		// File file = getFile(objectId.getValue());
		// File parent = getFile(targetFolderId);
		//
		// // build new path
		// File newFile = new File(parent, file.getName());
		// if (newFile.exists()) {
		// throw new CmisStorageException("Object already exists!");
		// }

		// move it
		// if (!file.renameTo(newFile)) {
		// throw new CmisStorageException("Move failed!");
		// } else {
		// // set new id
		// objectId.setValue(getId(newFile));
		//
		// // if it is a file, move properties file too
		// if (newFile.isFile()) {
		// File propFile = getPropertiesFile(file);
		// if (propFile.exists()) {
		// File newPropFile = new File(parent, propFile.getName());
		// propFile.renameTo(newPropFile);
		// }
		// }
		// }

		// return compileObjectType(context, newFile, null, false, false,
		// userReadOnly, objectInfos);

		return null;
	}

	/**
	 * CMIS setContentStream and deleteContentStream.
	 */
	public void setContentStream(CallContext context, Holder<String> objectId, Boolean overwriteFlag,
			ContentStream contentStream) {
		debug("setContentStream or deleteContentStream");

		if (objectId == null) {
			throw new CmisInvalidArgumentException("Id is not valid!");
		}

		Document doc = getDocument(objectId.getValue());

		checkPermission("" + doc.getFolder().getId(), context, Permission.WRITE);

		// TODO Implement

		// // check overwrite
		// boolean owf = (overwriteFlag == null ? true :
		// overwriteFlag.booleanValue());
		// if (!owf && file.length() > 0) {
		// throw new
		// CmisContentAlreadyExistsException("Content already exists!");
		// }
		//
		// try {
		// OutputStream out = new BufferedOutputStream(new
		// FileOutputStream(file), BUFFER_SIZE);
		//
		// if ((contentStream == null) || (contentStream.getStream() == null)) {
		// // delete content
		// out.write(new byte[0]);
		// } else {
		// // set content
		// InputStream in = new BufferedInputStream(contentStream.getStream(),
		// BUFFER_SIZE);
		//
		// byte[] buffer = new byte[BUFFER_SIZE];
		// int b;
		// while ((b = in.read(buffer)) > -1) {
		// out.write(buffer, 0, b);
		// }
		//
		// in.close();
		// }
		//
		// out.close();
		// } catch (Exception e) {
		// throw new CmisStorageException("Could not write content: " +
		// e.getMessage(), e);
		// }
	}

	private boolean delete(PersistentObject object) {
		try {
			User user = getSessionUser();
			if (object instanceof Folder) {
				Folder folder = (Folder) object;
				FolderHistory transaction = new FolderHistory();
				transaction.setUser(user);
				transaction.setEvent(FolderHistory.EVENT_FOLDER_DELETED);
				transaction.setSessionId(sid);

				if (!folderDao.delete(folder.getId(), transaction))
					throw new Exception("Unable to delete folder");
			} else {
				Document doc = (Document) object;
				History transaction = new History();
				transaction.setUser(user);
				transaction.setEvent(FolderHistory.EVENT_FOLDER_DELETED);
				transaction.setSessionId(sid);

				if (!documentDao.delete(doc.getId(), transaction))
					throw new Exception("Unable to delete document");
			}

			return true;
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			return false;
		}
	}

	/**
	 * CMIS deleteObject.
	 */
	public void deleteObject(CallContext context, String objectId) {
		debug("deleteObject");
		checkPermission(objectId, context, Permission.WRITE);

		// get the file or folder
		PersistentObject object = getObject(objectId);
		if (object == null) {
			throw new CmisObjectNotFoundException("Object not found!");
		}

		if (object instanceof Folder) {
			Folder folder = (Folder) object;
			List<Document> docs = documentDao.findByFolder(folder.getId(), 2);
			List<Folder> folders = folderDao.findByParentId(folder.getId());

			// check if it is a folder and if it is empty
			if (!docs.isEmpty() || !folders.isEmpty()) {
				throw new CmisConstraintException("Folder is not empty!");
			}
		}

		if (!delete(object))
			throw new CmisStorageException("Deletion failed!");
	}

	/**
	 * CMIS deleteTree.
	 */
	public FailedToDeleteData deleteTree(CallContext context, String folderId, Boolean continueOnFailure) {
		debug("deleteTree");
		checkPermission(folderId, context, Permission.WRITE);

		boolean cof = (continueOnFailure == null ? false : continueOnFailure.booleanValue());

		// get the document or folder
		PersistentObject object = getObject(folderId);
		if (object == null) {
			throw new CmisObjectNotFoundException("Object not found!");
		}

		FailedToDeleteDataImpl result = new FailedToDeleteDataImpl();
		result.setIds(new ArrayList<String>());

		// if it is a folder, remove it recursively
		// if (file.isDirectory()) {
		// deleteFolder(file, cof, result);
		// } else {
		// getPropertiesFile(file).delete();
		// if (!file.delete()) {
		// result.getIds().add(getId(file));
		// }
		// }

		try {
			if (object instanceof Folder) {
				Folder folder = (Folder) object;
				deleteFolder(folder, cof, result);
			} else {
				Document doc = (Document) object;
				History transaction = new History();
				// TODO implement
				// transaction.setUser(SessionUtil.getSessionUser(sid));
				// transaction.setEvent(FolderEvent.DELETED.toString());
				// transaction.setSessionId(sid);

				if (!documentDao.delete(doc.getId(), transaction))
					throw new Exception("Unable to delete document");
			}
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new CmisStorageException("Deletion failed!");
		}
		return result;
	}

	/**
	 * CMIS updateProperties.
	 */
	public ObjectData updateProperties(CallContext context, Holder<String> objectId, Properties properties,
			ObjectInfoHandler objectInfos) {
		debug("updateProperties");
		boolean userReadOnly = checkPermission(objectId.getValue(), context, Permission.WRITE);

		if (objectId == null) {
			throw new CmisInvalidArgumentException("Id is not valid!");
		}

		// TODO implement
		return null;

		// get the file or folder
		// File file = getFile(objectId.getValue());
		//
		// // get and check the new name
		// String newName = getStringProperty(properties, PropertyIds.NAME);
		// boolean isRename = (newName != null) &&
		// (!file.getName().equals(newName));
		// if (isRename && !isValidName(newName)) {
		// throw new CmisNameConstraintViolationException("Name is not valid!");
		// }
		//
		//
		// // get old properties
		// PropertiesImpl oldProperties = new PropertiesImpl();
		// // readCustomProperties(file, oldProperties, null, new
		// // ObjectInfoImpl());
		//
		// // get the type id
		// String typeId = getIdProperty(oldProperties,
		// PropertyIds.OBJECT_TYPE_ID);
		// if (typeId == null) {
		// typeId = (file.isDirectory() ? TypeManager.FOLDER_TYPE_ID :
		// TypeManager.DOCUMENT_TYPE_ID);
		// }
		//
		// // get the creator
		// String creator = getStringProperty(oldProperties,
		// PropertyIds.CREATED_BY);
		// if (creator == null) {
		// creator = context.getUsername();
		// }
		//
		// // get creation date
		// GregorianCalendar creationDate = getDateTimeProperty(oldProperties,
		// PropertyIds.CREATION_DATE);
		// if (creationDate == null) {
		// creationDate = millisToCalendar(file.lastModified());
		// }
		//
		// // compile the properties
		// Properties props = updateProperties(typeId, creator, creationDate,
		// context.getUsername(), oldProperties,
		// properties);
		//
		// // write properties
		// writePropertiesFile(file, props);

		// rename file or folder if necessary
		// File newFile = file;
		// if (isRename) {
		// File parent = file.getParentFile();
		// File propFile = getPropertiesFile(file);
		// newFile = new File(parent, newName);
		// if (!file.renameTo(newFile)) {
		// // if something went wrong, throw an exception
		// throw new CmisUpdateConflictException("Could not rename object!");
		// } else {
		// // set new id
		// objectId.setValue(getId(newFile));
		//
		// // if it is a file, rename properties file too
		// if (newFile.isFile()) {
		// if (propFile.exists()) {
		// File newPropFile = new File(parent, newName + SHADOW_EXT);
		// propFile.renameTo(newPropFile);
		// }
		// }
		// }
		// }
		// return compileObjectType(context, newFile, null, false, false,
		// userReadOnly, objectInfos);
	}

	/**
	 * CMIS getObject.
	 */
	public ObjectData getObject(CallContext context, String objectId, String versionServicesId, String filter,
			Boolean includeAllowableActions, Boolean includeAcl, ObjectInfoHandler objectInfos) {
		debug("getObject");
		boolean userReadOnly = checkPermission(objectId, context, null);

		// check id
		if ((objectId == null) && (versionServicesId == null)) {
			throw new CmisInvalidArgumentException("Object Id must be set.");
		}

		if (objectId == null) {
			// this works only because there are no versions in a file system
			// and the object id and version series id are the same
			objectId = versionServicesId;
		}

		PersistentObject obj = getObject(objectId);

		// set defaults if values not set
		boolean iaa = (includeAllowableActions == null ? false : includeAllowableActions.booleanValue());
		boolean iacl = (includeAcl == null ? false : includeAcl.booleanValue());

		// split filter
		Set<String> filterCollection = splitFilter(filter);

		// gather properties
		return compileObjectType(context, obj, filterCollection, iaa, iacl, userReadOnly, objectInfos);
	}

	/**
	 * CMIS getAllowableActions.
	 */
	public AllowableActions getAllowableActions(CallContext context, String objectId) {
		debug("getAllowableActions");
		return compileAllowableActions(getObject(objectId));
	}

	/**
	 * CMIS getACL.
	 */
	public Acl getAcl(CallContext context, String objectId) {
		debug("getAcl");
		checkPermission(objectId, context, null);

		return compileAcl(getObject(objectId));
	}

	/**
	 * CMIS getContentStream.
	 */
	public ContentStream getContentStream(CallContext context, String objectId, BigInteger offset, BigInteger length) {
		debug("getContentStream");
		checkPermission(objectId, context, null);

		if ((offset != null) || (length != null)) {
			throw new CmisInvalidArgumentException("Offset and Length are not supported!");
		}

		Document doc = getDocument(objectId);

		if (doc.getFileSize() == 0) {
			throw new CmisConstraintException("Document has no content!");
		}

		InputStream stream = null;
		try {
			Storer storer = (Storer) Context.getInstance().getBean(Storer.class);
			InputStream is = storer.getStream(doc.getId(), storer.getResourceName(doc, null, null));
			stream = new BufferedInputStream(is, BUFFER_SIZE);
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			throw new CmisObjectNotFoundException(e.getMessage(), e);
		}

		// compile data
		ContentStreamImpl result = new ContentStreamImpl();
		result.setFileName(doc.getFileName());
		result.setLength(BigInteger.valueOf(doc.getFileSize()));
		result.setMimeType(MimeTypes.getMIMEType(doc.getFileName()));
		result.setStream(stream);

		return result;
	}

	/**
	 * CMIS getChildren.
	 */
	public ObjectInFolderList getChildren(CallContext context, String folderId, String filter,
			Boolean includeAllowableActions, Boolean includePathSegment, BigInteger maxItems, BigInteger skipCount,
			ObjectInfoHandler objectInfos) {
		debug("getChildren");
		boolean userReadOnly = checkPermission(folderId, context, null);

		// split filter
		Set<String> filterCollection = splitFilter(filter);

		// set defaults if values not set
		boolean iaa = (includeAllowableActions == null ? false : includeAllowableActions.booleanValue());
		boolean ips = (includePathSegment == null ? false : includePathSegment.booleanValue());

		// skip and max
		int skip = (skipCount == null ? 0 : skipCount.intValue());
		if (skip < 0) {
			skip = 0;
		}

		int max = (maxItems == null ? Integer.MAX_VALUE : maxItems.intValue());
		if (max < 0) {
			max = Integer.MAX_VALUE;
		}

		// get the folder
		Folder folder = getFolder(folderId);

		// set object info of the the folder
		if (context.isObjectInfoRequired()) {
			compileObjectType(context, folder, null, false, false, userReadOnly, objectInfos);
		}

		// prepare result
		ObjectInFolderListImpl result = new ObjectInFolderListImpl();
		result.setObjects(new ArrayList<ObjectInFolderData>());
		result.setHasMoreItems(false);
		int count = 0;

		// iterate through children folders
		for (Folder child : folderDao.findChildren(folder.getId(), null)) {
			count++;

			if (skip > 0) {
				skip--;
				continue;
			}

			if (result.getObjects().size() >= max) {
				result.setHasMoreItems(true);
				continue;
			}

			// build and add child object
			ObjectInFolderDataImpl objectInFolder = new ObjectInFolderDataImpl();
			objectInFolder.setObject(compileObjectType(context, child, filterCollection, iaa, false, userReadOnly,
					objectInfos));
			if (ips) {
				objectInFolder.setPathSegment(child.getName());
			}

			result.getObjects().add(objectInFolder);
		}

		// iterate through children documents
		for (Document child : documentDao.findByFolder(folder.getId(), null)) {
			count++;

			if (skip > 0) {
				skip--;
				continue;
			}

			if (result.getObjects().size() >= max) {
				result.setHasMoreItems(true);
				continue;
			}

			// build and add child object
			ObjectInFolderDataImpl objectInFolder = new ObjectInFolderDataImpl();
			objectInFolder.setObject(compileObjectType(context, child, filterCollection, iaa, false, userReadOnly,
					objectInfos));
			if (ips) {
				objectInFolder.setPathSegment(child.getFileName());
			}

			result.getObjects().add(objectInFolder);
		}

		result.setNumItems(BigInteger.valueOf(count));

		return result;
	}

	private Folder getFolder(String folderId) {
		PersistentObject object = getObject(folderId);
		if (!(object instanceof Folder)) {
			throw new CmisObjectNotFoundException("Not a folder!");
		}
		return (Folder) object;
	}

	private Document getDocument(String documentId) {
		PersistentObject object = getObject(documentId);
		if (!(object instanceof Document)) {
			throw new CmisObjectNotFoundException("Not a document!");
		}
		return (Document) object;
	}

	/**
	 * CMIS getDescendants.
	 */
	public List<ObjectInFolderContainer> getDescendants(CallContext context, String folderId, BigInteger depth,
			String filter, Boolean includeAllowableActions, Boolean includePathSegment, ObjectInfoHandler objectInfos,
			boolean foldersOnly) {
		debug("getDescendants or getFolderTree");
		boolean userReadOnly = checkPermission(folderId, context, null);

		// check depth
		int d = (depth == null ? 2 : depth.intValue());
		if (d == 0) {
			throw new CmisInvalidArgumentException("Depth must not be 0!");
		}
		if (d < -1) {
			d = -1;
		}

		// split filter
		Set<String> filterCollection = splitFilter(filter);

		// set defaults if values not set
		boolean iaa = (includeAllowableActions == null ? false : includeAllowableActions.booleanValue());
		boolean ips = (includePathSegment == null ? false : includePathSegment.booleanValue());

		PersistentObject object = getFolder(folderId);

		Folder folder = (Folder) object;

		// set object info of the the folder
		if (context.isObjectInfoRequired()) {
			compileObjectType(context, folder, null, false, false, userReadOnly, objectInfos);
		}

		// get the tree
		List<ObjectInFolderContainer> result = new ArrayList<ObjectInFolderContainer>();
		gatherDescendants(context, folder, result, foldersOnly, d, filterCollection, iaa, ips, userReadOnly,
				objectInfos);

		return result;
	}

	/**
	 * CMIS getFolderParent.
	 */
	public ObjectData getFolderParent(CallContext context, String folderId, String filter, ObjectInfoHandler objectInfos) {
		List<ObjectParentData> parents = getObjectParents(context, folderId, filter, false, false, objectInfos);

		if (parents.size() == 0) {
			throw new CmisInvalidArgumentException("The root folder has no parent!");
		}

		return parents.get(0).getObject();
	}

	/**
	 * CMIS getObjectParents.
	 */
	public List<ObjectParentData> getObjectParents(CallContext context, String objectId, String filter,
			Boolean includeAllowableActions, Boolean includeRelativePathSegment, ObjectInfoHandler objectInfos) {
		debug("getObjectParents");
		boolean userReadOnly = checkPermission(objectId, context, null);

		// split filter
		Set<String> filterCollection = splitFilter(filter);

		// set defaults if values not set
		boolean iaa = (includeAllowableActions == null ? false : includeAllowableActions.booleanValue());
		boolean irps = (includeRelativePathSegment == null ? false : includeRelativePathSegment.booleanValue());

		// get the file or folder
		PersistentObject object = getObject(objectId);

		// don't climb above the root folder
		if (root.equals(object)) {
			return Collections.emptyList();
		}

		// set object info of the the object
		if (context.isObjectInfoRequired()) {
			compileObjectType(context, object, null, false, false, userReadOnly, objectInfos);
		}

		Folder parent;
		if (object instanceof Document)
			parent = ((Document) object).getFolder();
		else
			parent = folderDao.findById(((Folder) object).getParentId());

		// get parent folder
		ObjectData obj = compileObjectType(context, parent, filterCollection, iaa, false, userReadOnly, objectInfos);

		ObjectParentDataImpl result = new ObjectParentDataImpl();
		result.setObject(obj);
		if (irps) {
			if (object instanceof Document)
				result.setRelativePathSegment(((Document) object).getFileName());
			else
				result.setRelativePathSegment(((Folder) object).getName());

		}

		return Collections.singletonList((ObjectParentData) result);
	}

	/**
	 * CMIS getObjectByPath.
	 */
	public ObjectData getObjectByPath(CallContext context, String folderPath, String filter,
			boolean includeAllowableActions, boolean includeACL, ObjectInfoHandler objectInfos) {
		debug("getObjectByPath");

		// TODO implement
		// boolean userReadOnly = checkUser(context, false);

		// split filter
		Set<String> filterCollection = splitFilter(filter);

		// check path
		if ((folderPath == null) || (!folderPath.startsWith("/"))) {
			throw new CmisInvalidArgumentException("Invalid folder path!");
		}

		// TODO implement
		// // get the file or folder
		// File file = null;
		// if (folderPath.length() == 1) {
		// file = root;
		// } else {
		// String path = folderPath.replace('/',
		// File.separatorChar).substring(1);
		// file = new File(root, path);
		// }
		//
		// if (!file.exists()) {
		// throw new CmisObjectNotFoundException("Path doesn't exist.");
		// }

		// return compileObjectType(context, file, filterCollection,
		// includeAllowableActions, includeACL, userReadOnly,
		// objectInfos);

		return null;
	}

	// --- helper methods ---

	/**
	 * Gather the children of a folder.
	 */
	private void gatherDescendants(CallContext context, Folder folder, List<ObjectInFolderContainer> list,
			boolean foldersOnly, int depth, Set<String> filter, boolean includeAllowableActions,
			boolean includePathSegments, boolean userReadOnly, ObjectInfoHandler objectInfos) {

		// TODO implement

		// // iterate through children
		// for (File child : folder.listFiles()) {
		// // skip hidden and shadow files
		// if (child.isHidden() || child.getName().equals(SHADOW_FOLDER) ||
		// child.getPath().endsWith(SHADOW_EXT)) {
		// continue;
		// }
		//
		// // folders only?
		// if (foldersOnly && !child.isDirectory()) {
		// continue;
		// }
		//
		// // add to list
		// ObjectInFolderDataImpl objectInFolder = new ObjectInFolderDataImpl();
		// objectInFolder.setObject(compileObjectType(context, child, filter,
		// includeAllowableActions, false,
		// userReadOnly, objectInfos));
		// if (includePathSegments) {
		// objectInFolder.setPathSegment(child.getName());
		// }
		//
		// ObjectInFolderContainerImpl container = new
		// ObjectInFolderContainerImpl();
		// container.setObject(objectInFolder);
		//
		// list.add(container);
		//
		// // move to next level
		// if ((depth != 1) && child.isDirectory()) {
		// container.setChildren(new ArrayList<ObjectInFolderContainer>());
		// gatherDescendants(context, child, container.getChildren(),
		// foldersOnly, depth - 1, filter,
		// includeAllowableActions, includePathSegments, userReadOnly,
		// objectInfos);
		// }
		// }
	}

	/**
	 * Removes a folder and its content.
	 * 
	 * @throws
	 */
	private boolean deleteFolder(Folder folder, boolean continueOnFailure, FailedToDeleteDataImpl ftd) {
		boolean success = true;
		for (Document doc : documentDao.findByFolder(folder.getId(), null)) {
			if (!delete(doc)) {
				ftd.getIds().add(getId(doc));
				if (!continueOnFailure) {
					return false;
				}
				success = false;
			}
		}

		for (Folder fld : folderDao.findChildren(folder.getId(), null)) {
			if (!deleteFolder(fld, continueOnFailure, ftd)) {
				if (!continueOnFailure) {
					return false;
				}
				success = false;
			}
		}

		if (!delete(folder)) {
			ftd.getIds().add(getId(folder));
			success = false;
		}

		return success;
	}

	/**
	 * Checks if the given name is valid for a file system.
	 * 
	 * @param name the name to check
	 * 
	 * @return <code>true</code> if the name is valid, <code>false</code>
	 *         otherwise
	 */
	private static boolean isValidName(String name) {
		if ((name == null) || (name.length() == 0) || (name.indexOf('/') != -1)) {
			return false;
		}

		return true;
	}

	/**
	 * Compiles an object type object from a document or folder.
	 */
	private ObjectData compileObjectType(CallContext context, PersistentObject object, Set<String> filter,
			boolean includeAllowableActions, boolean includeAcl, boolean userReadOnly, ObjectInfoHandler objectInfos) {
		ObjectDataImpl result = new ObjectDataImpl();
		ObjectInfoImpl objectInfo = new ObjectInfoImpl();

		result.setProperties(compileProperties(object, filter, objectInfo));

		if (includeAllowableActions) {
			result.setAllowableActions(compileAllowableActions(object));
		}

		if (includeAcl) {
			result.setAcl(compileAcl(object));
			result.setIsExactAcl(true);
		}

		if (context.isObjectInfoRequired()) {
			objectInfo.setObject(result);
			objectInfos.addObjectInfo(objectInfo);
		}

		return result;
	}

	/**
	 * Gathers all base properties of a document or folder.
	 */
	private Properties compileProperties(PersistentObject object, Set<String> orgfilter, ObjectInfoImpl objectInfo) {
		if (object == null) {
			throw new IllegalArgumentException("Object must not be null!");
		}

		// copy filter
		Set<String> filter = (orgfilter == null ? null : new HashSet<String>(orgfilter));

		// find base type
		String typeId = null;

		if (object instanceof Folder) {
			typeId = TypeManager.FOLDER_TYPE_ID;
			objectInfo.setBaseType(BaseTypeId.CMIS_FOLDER);
			objectInfo.setTypeId(typeId);
			objectInfo.setContentType(null);
			objectInfo.setFileName(null);
			objectInfo.setHasAcl(true);
			objectInfo.setHasContent(false);
			objectInfo.setVersionSeriesId(null);
			objectInfo.setIsCurrentVersion(true);
			objectInfo.setRelationshipSourceIds(null);
			objectInfo.setRelationshipTargetIds(null);
			objectInfo.setRenditionInfos(null);
			objectInfo.setSupportsDescendants(true);
			objectInfo.setSupportsFolderTree(true);
			objectInfo.setSupportsPolicies(false);
			objectInfo.setSupportsRelationships(false);
			objectInfo.setWorkingCopyId(null);
			objectInfo.setWorkingCopyOriginalId(null);
		} else {
			typeId = TypeManager.DOCUMENT_TYPE_ID;
			objectInfo.setBaseType(BaseTypeId.CMIS_DOCUMENT);
			objectInfo.setTypeId(typeId);
			objectInfo.setHasAcl(true);
			objectInfo.setHasContent(true);
			objectInfo.setHasParent(true);
			objectInfo.setVersionSeriesId(null);
			objectInfo.setIsCurrentVersion(true);
			objectInfo.setRelationshipSourceIds(null);
			objectInfo.setRelationshipTargetIds(null);
			objectInfo.setRenditionInfos(null);
			objectInfo.setSupportsDescendants(false);
			objectInfo.setSupportsFolderTree(false);
			objectInfo.setSupportsPolicies(false);
			objectInfo.setSupportsRelationships(false);
			objectInfo.setWorkingCopyId(null);
			objectInfo.setWorkingCopyOriginalId(null);
		}

		// let's do it
		try {
			PropertiesImpl result = new PropertiesImpl();

			// id
			String id = getId(object);
			addPropertyId(result, typeId, filter, PropertyIds.OBJECT_ID, id);
			objectInfo.setId(id);

			// name
			String name = "";

			if (object instanceof Folder)
				name = ((Folder) object).getName();
			else
				name = ((Document) object).getFileName();

			addPropertyString(result, typeId, filter, PropertyIds.NAME, name);
			objectInfo.setName(name);

			// created and modified by
			addPropertyString(result, typeId, filter, PropertyIds.CREATED_BY, USER_UNKNOWN);
			addPropertyString(result, typeId, filter, PropertyIds.LAST_MODIFIED_BY, USER_UNKNOWN);
			objectInfo.setCreatedBy(USER_UNKNOWN);

			// creation and modification date
			GregorianCalendar lastModified = millisToCalendar(object.getLastModified().getTime());

			GregorianCalendar creation;
			if (object instanceof Folder)
				creation = millisToCalendar(((Folder) object).getCreation().getTime());
			else
				creation = millisToCalendar(((Document) object).getCreation().getTime());

			addPropertyDateTime(result, typeId, filter, PropertyIds.CREATION_DATE, creation);
			addPropertyDateTime(result, typeId, filter, PropertyIds.LAST_MODIFICATION_DATE, lastModified);
			objectInfo.setCreationDate(creation);
			objectInfo.setLastModificationDate(lastModified);

			// change token - always null
			addPropertyString(result, typeId, filter, PropertyIds.CHANGE_TOKEN, null);

			// directory or file
			if (object instanceof Folder) {
				// base type and type name
				addPropertyId(result, typeId, filter, PropertyIds.BASE_TYPE_ID, BaseTypeId.CMIS_FOLDER.value());
				addPropertyId(result, typeId, filter, PropertyIds.OBJECT_TYPE_ID, TypeManager.FOLDER_TYPE_ID);

				String path = folderDao.computePathExtended(object.getId());
				addPropertyString(result, typeId, filter, PropertyIds.PATH, (path.length() == 0 ? "/" : path));

				// folder properties
				if (!root.equals(object)) {
					addPropertyId(result, typeId, filter, PropertyIds.PARENT_ID,
							ID_PREFIX_FLD + ((Folder) object).getParentId());
					objectInfo.setHasParent(true);
				} else {
					addPropertyId(result, typeId, filter, PropertyIds.PARENT_ID, null);
					objectInfo.setHasParent(false);
				}

				addPropertyIdList(result, typeId, filter, PropertyIds.ALLOWED_CHILD_OBJECT_TYPE_IDS, null);
			} else {
				Document doc = (Document) object;

				// base type and type name
				addPropertyId(result, typeId, filter, PropertyIds.BASE_TYPE_ID, BaseTypeId.CMIS_DOCUMENT.value());
				addPropertyId(result, typeId, filter, PropertyIds.OBJECT_TYPE_ID, TypeManager.DOCUMENT_TYPE_ID);

				// file properties
				addPropertyBoolean(result, typeId, filter, PropertyIds.IS_IMMUTABLE, false);
				addPropertyBoolean(result, typeId, filter, PropertyIds.IS_LATEST_VERSION, true);
				addPropertyBoolean(result, typeId, filter, PropertyIds.IS_MAJOR_VERSION, true);
				addPropertyBoolean(result, typeId, filter, PropertyIds.IS_LATEST_MAJOR_VERSION, true);
				addPropertyString(result, typeId, filter, PropertyIds.VERSION_LABEL, doc.getTitle());
				addPropertyId(result, typeId, filter, PropertyIds.VERSION_SERIES_ID, getId(doc));
				addPropertyBoolean(result, typeId, filter, PropertyIds.IS_VERSION_SERIES_CHECKED_OUT, false);
				addPropertyString(result, typeId, filter, PropertyIds.VERSION_SERIES_CHECKED_OUT_BY, null);
				addPropertyString(result, typeId, filter, PropertyIds.VERSION_SERIES_CHECKED_OUT_ID, null);
				addPropertyString(result, typeId, filter, PropertyIds.CHECKIN_COMMENT, "");

				if (doc.getFileSize() == 0) {
					addPropertyBigInteger(result, typeId, filter, PropertyIds.CONTENT_STREAM_LENGTH, null);
					addPropertyString(result, typeId, filter, PropertyIds.CONTENT_STREAM_MIME_TYPE, null);
					addPropertyString(result, typeId, filter, PropertyIds.CONTENT_STREAM_FILE_NAME, null);

					objectInfo.setHasContent(false);
					objectInfo.setContentType(null);
					objectInfo.setFileName(null);
				} else {
					addPropertyInteger(result, typeId, filter, PropertyIds.CONTENT_STREAM_LENGTH, doc.getFileSize());
					addPropertyString(result, typeId, filter, PropertyIds.CONTENT_STREAM_MIME_TYPE,
							MimeTypes.getMIMEType(FilenameUtils.getExtension(doc.getFileName())));
					addPropertyString(result, typeId, filter, PropertyIds.CONTENT_STREAM_FILE_NAME, doc.getFileName());

					objectInfo.setHasContent(true);
					objectInfo.setContentType(FilenameUtils.getExtension(doc.getFileName()));
					objectInfo.setFileName(doc.getFileName());
				}

				addPropertyId(result, typeId, filter, PropertyIds.CONTENT_STREAM_ID, null);
			}

			// read custom properties
			readCustomProperties(object, result, filter, objectInfo);

			if (filter != null) {
				if (!filter.isEmpty()) {
					debug("Unknown filter properties: " + filter.toString(), null);
				}
			}

			return result;
		} catch (Exception e) {
			if (e instanceof CmisBaseException) {
				throw (CmisBaseException) e;
			}
			throw new CmisRuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * Reads and adds properties.
	 */
	@SuppressWarnings("unchecked")
	private void readCustomProperties(PersistentObject object, PropertiesImpl properties, Set<String> filter,
			ObjectInfoImpl objectInfo) {

		// TODO implement

		// File propFile = getPropertiesFile(file);
		//
		// // if it doesn't exists, ignore it
		// if (!propFile.exists()) {
		// return;
		// }
		//
		// // parse it
		// JAXBElement<CmisObjectType> obj = null;
		// try {
		// Unmarshaller u = JaxBHelper.createUnmarshaller();
		// obj = (JAXBElement<CmisObjectType>) u.unmarshal(propFile);
		// } catch (Exception e) {
		// warn("Unvalid CMIS properties: " + propFile.getAbsolutePath(), e);
		// }
		//
		// if ((obj == null) || (obj.getValue() == null) ||
		// (obj.getValue().getProperties() == null)) {
		// return;
		// }
		//
		// // add it to properties
		// for (CmisProperty cmisProp :
		// obj.getValue().getProperties().getProperty()) {
		// PropertyData<?> prop = Converter.convert(cmisProp);
		//
		// // overwrite object info
		// if (prop instanceof PropertyString) {
		// String firstValueStr = ((PropertyString) prop).getFirstValue();
		// if (PropertyIds.NAME.equals(prop.getId())) {
		// objectInfo.setName(firstValueStr);
		// } else if (PropertyIds.OBJECT_TYPE_ID.equals(prop.getId())) {
		// objectInfo.setTypeId(firstValueStr);
		// } else if (PropertyIds.CREATED_BY.equals(prop.getId())) {
		// objectInfo.setCreatedBy(firstValueStr);
		// } else if (PropertyIds.CONTENT_STREAM_MIME_TYPE.equals(prop.getId()))
		// {
		// objectInfo.setContentType(firstValueStr);
		// } else if (PropertyIds.CONTENT_STREAM_FILE_NAME.equals(prop.getId()))
		// {
		// objectInfo.setFileName(firstValueStr);
		// }
		// }
		//
		// if (prop instanceof PropertyDateTime) {
		// GregorianCalendar firstValueCal = ((PropertyDateTime)
		// prop).getFirstValue();
		// if (PropertyIds.CREATION_DATE.equals(prop.getId())) {
		// objectInfo.setCreationDate(firstValueCal);
		// } else if (PropertyIds.LAST_MODIFICATION_DATE.equals(prop.getId())) {
		// objectInfo.setLastModificationDate(firstValueCal);
		// }
		// }
		//
		// // check filter
		// if (filter != null) {
		// if (!filter.contains(prop.getId())) {
		// continue;
		// } else {
		// filter.remove(prop.getId());
		// }
		// }
		//
		// // don't overwrite id
		// if (PropertyIds.OBJECT_ID.equals(prop.getId())) {
		// continue;
		// }
		//
		// // don't overwrite base type
		// if (PropertyIds.BASE_TYPE_ID.equals(prop.getId())) {
		// continue;
		// }
		//
		// // add it
		// properties.addProperty(prop);
		// }
	}

	/**
	 * Checks and compiles a property set that can be stored.
	 */
	private Properties compileProperties(String typeId, User creator, GregorianCalendar creationDate, User modifier,
			Properties properties) {
		PropertiesImpl result = new PropertiesImpl();
		Set<String> addedProps = new HashSet<String>();

		if ((properties == null) || (properties.getProperties() == null)) {
			throw new CmisConstraintException("No properties!");
		}

		// get the property definitions
		TypeDefinition type = types.getType(typeId);
		if (type == null) {
			throw new CmisObjectNotFoundException("Type '" + typeId + "' is unknown!");
		}

		// check if all required properties are there
		for (PropertyData<?> prop : properties.getProperties().values()) {
			PropertyDefinition<?> propType = type.getPropertyDefinitions().get(prop.getId());

			// do we know that property?
			if (propType == null) {
				throw new CmisConstraintException("Property '" + prop.getId() + "' is unknown!");
			}

			// can it be set?
			if ((propType.getUpdatability() == Updatability.READONLY)) {
				throw new CmisConstraintException("Property '" + prop.getId() + "' is readonly!");
			}

			// empty properties are invalid
			if (isEmptyProperty(prop)) {
				throw new CmisConstraintException("Property '" + prop.getId() + "' must not be empty!");
			}

			// add it
			result.addProperty(prop);
			addedProps.add(prop.getId());
		}

		// check if required properties are missing
		for (PropertyDefinition<?> propDef : type.getPropertyDefinitions().values()) {
			if (!addedProps.contains(propDef.getId()) && (propDef.getUpdatability() != Updatability.READONLY)) {
				if (!addPropertyDefault(result, propDef) && propDef.isRequired()) {
					throw new CmisConstraintException("Property '" + propDef.getId() + "' is required!");
				}
			}
		}

		addPropertyId(result, typeId, null, PropertyIds.OBJECT_TYPE_ID, typeId);
		addPropertyString(result, typeId, null, PropertyIds.CREATED_BY, creator.getFullName());
		addPropertyDateTime(result, typeId, null, PropertyIds.CREATION_DATE, creationDate);
		addPropertyString(result, typeId, null, PropertyIds.LAST_MODIFIED_BY, modifier.getFullName());

		return result;
	}

	/**
	 * Checks and updates a property set that can be written to disc.
	 */
	private Properties updateProperties(String typeId, String creator, GregorianCalendar creationDate, String modifier,
			Properties oldProperties, Properties properties) {
		PropertiesImpl result = new PropertiesImpl();

		if (properties == null) {
			throw new CmisConstraintException("No properties!");
		}

		// TODO implement
		// // get the property definitions
		// TypeDefinition type = types.getType(typeId);
		// if (type == null) {
		// throw new CmisObjectNotFoundException("Type '" + typeId +
		// "' is unknown!");
		// }
		//
		// // copy old properties
		// for (PropertyData<?> prop : oldProperties.getProperties().values()) {
		// PropertyDefinition<?> propType =
		// type.getPropertyDefinitions().get(prop.getId());
		//
		// // do we know that property?
		// if (propType == null) {
		// throw new CmisConstraintException("Property '" + prop.getId() +
		// "' is unknown!");
		// }
		//
		// // only add read/write properties
		// if ((propType.getUpdatability() != Updatability.READWRITE)) {
		// continue;
		// }
		//
		// result.addProperty(prop);
		// }

		// update properties
		// for (PropertyData<?> prop : properties.getProperties().values()) {
		// PropertyDefinition<?> propType =
		// type.getPropertyDefinitions().get(prop.getId());
		//
		// // do we know that property?
		// if (propType == null) {
		// throw new CmisConstraintException("Property '" + prop.getId() +
		// "' is unknown!");
		// }
		//
		// // can it be set?
		// if ((propType.getUpdatability() == Updatability.READONLY)) {
		// throw new CmisConstraintException("Property '" + prop.getId() +
		// "' is readonly!");
		// }
		//
		// if ((propType.getUpdatability() == Updatability.ONCREATE)) {
		// throw new CmisConstraintException("Property '" + prop.getId() +
		// "' can only be set on create!");
		// }
		//
		// // default or value
		// if (isEmptyProperty(prop)) {
		// addPropertyDefault(result, propType);
		// } else {
		// result.addProperty(prop);
		// }
		// }
		//
		// addPropertyId(result, typeId, null, PropertyIds.OBJECT_TYPE_ID,
		// typeId);
		// addPropertyString(result, typeId, null, PropertyIds.CREATED_BY,
		// creator);
		// addPropertyDateTime(result, typeId, null, PropertyIds.CREATION_DATE,
		// creationDate);
		// addPropertyString(result, typeId, null, PropertyIds.LAST_MODIFIED_BY,
		// modifier);

		return result;
	}

	private static boolean isEmptyProperty(PropertyData<?> prop) {
		if ((prop == null) || (prop.getValues() == null)) {
			return true;
		}

		return prop.getValues().isEmpty();
	}

	private void addPropertyId(PropertiesImpl props, String typeId, Set<String> filter, String id, String value) {
		if (!checkAddProperty(props, typeId, filter, id)) {
			return;
		}

		props.addProperty(new PropertyIdImpl(id, value));
	}

	private void addPropertyIdList(PropertiesImpl props, String typeId, Set<String> filter, String id,
			List<String> value) {
		if (!checkAddProperty(props, typeId, filter, id)) {
			return;
		}

		props.addProperty(new PropertyIdImpl(id, value));
	}

	private void addPropertyString(PropertiesImpl props, String typeId, Set<String> filter, String id, String value) {
		if (!checkAddProperty(props, typeId, filter, id)) {
			return;
		}

		props.addProperty(new PropertyStringImpl(id, value));
	}

	private void addPropertyInteger(PropertiesImpl props, String typeId, Set<String> filter, String id, long value) {
		addPropertyBigInteger(props, typeId, filter, id, BigInteger.valueOf(value));
	}

	private void addPropertyBigInteger(PropertiesImpl props, String typeId, Set<String> filter, String id,
			BigInteger value) {
		if (!checkAddProperty(props, typeId, filter, id)) {
			return;
		}

		props.addProperty(new PropertyIntegerImpl(id, value));
	}

	private void addPropertyBoolean(PropertiesImpl props, String typeId, Set<String> filter, String id, boolean value) {
		if (!checkAddProperty(props, typeId, filter, id)) {
			return;
		}

		props.addProperty(new PropertyBooleanImpl(id, value));
	}

	private void addPropertyDateTime(PropertiesImpl props, String typeId, Set<String> filter, String id,
			GregorianCalendar value) {
		if (!checkAddProperty(props, typeId, filter, id)) {
			return;
		}

		props.addProperty(new PropertyDateTimeImpl(id, value));
	}

	private boolean checkAddProperty(Properties properties, String typeId, Set<String> filter, String id) {
		if ((properties == null) || (properties.getProperties() == null)) {
			throw new IllegalArgumentException("Properties must not be null!");
		}

		if (id == null) {
			throw new IllegalArgumentException("Id must not be null!");
		}

		// TODO implement
		// TypeDefinition type = types.getType(typeId);
		// if (type == null) {
		// throw new IllegalArgumentException("Unknown type: " + typeId);
		// }
		// if (!type.getPropertyDefinitions().containsKey(id)) {
		// throw new IllegalArgumentException("Unknown property: " + id);
		// }
		//
		// String queryName =
		// type.getPropertyDefinitions().get(id).getQueryName();
		//
		// if ((queryName != null) && (filter != null)) {
		// if (!filter.contains(queryName)) {
		// return false;
		// } else {
		// filter.remove(queryName);
		// }
		// }

		return true;
	}

	/**
	 * Adds the default value of property if defined.
	 */
	@SuppressWarnings("unchecked")
	private static boolean addPropertyDefault(PropertiesImpl props, PropertyDefinition<?> propDef) {
		if ((props == null) || (props.getProperties() == null)) {
			throw new IllegalArgumentException("Props must not be null!");
		}

		if (propDef == null) {
			return false;
		}

		List<?> defaultValue = propDef.getDefaultValue();
		if ((defaultValue != null) && (!defaultValue.isEmpty())) {
			switch (propDef.getPropertyType()) {
			case BOOLEAN:
				props.addProperty(new PropertyBooleanImpl(propDef.getId(), (List<Boolean>) defaultValue));
				break;
			case DATETIME:
				props.addProperty(new PropertyDateTimeImpl(propDef.getId(), (List<GregorianCalendar>) defaultValue));
				break;
			case DECIMAL:
				props.addProperty(new PropertyDecimalImpl(propDef.getId(), (List<BigDecimal>) defaultValue));
				break;
			case HTML:
				props.addProperty(new PropertyHtmlImpl(propDef.getId(), (List<String>) defaultValue));
				break;
			case ID:
				props.addProperty(new PropertyIdImpl(propDef.getId(), (List<String>) defaultValue));
				break;
			case INTEGER:
				props.addProperty(new PropertyIntegerImpl(propDef.getId(), (List<BigInteger>) defaultValue));
				break;
			case STRING:
				props.addProperty(new PropertyStringImpl(propDef.getId(), (List<String>) defaultValue));
				break;
			case URI:
				props.addProperty(new PropertyUriImpl(propDef.getId(), (List<String>) defaultValue));
				break;
			default:
				throw new RuntimeException("Unknown datatype! Spec change?");
			}

			return true;
		}

		return false;
	}

	/**
	 * Compiles the allowable actions for a file or folder.
	 */
	private AllowableActions compileAllowableActions(PersistentObject object) {
		if (object == null) {
			throw new IllegalArgumentException("Object must not be null!");
		}

		boolean write = checkPermission(object, null, Permission.WRITE);
		boolean download = checkPermission(object, null, Permission.DOWNLOAD);

		boolean isFolder = object instanceof Folder;
		boolean isRoot = root.equals(object);

		Set<Action> aas = new HashSet<Action>();

		addAction(aas, Action.CAN_GET_OBJECT_PARENTS, !isRoot);
		addAction(aas, Action.CAN_GET_PROPERTIES, true);
		addAction(aas, Action.CAN_UPDATE_PROPERTIES, write);
		addAction(aas, Action.CAN_MOVE_OBJECT, write && download);
		addAction(aas, Action.CAN_DELETE_OBJECT, write);
		addAction(aas, Action.CAN_GET_ACL, true);

		if (isFolder) {
			addAction(aas, Action.CAN_GET_DESCENDANTS, true);
			addAction(aas, Action.CAN_GET_CHILDREN, true);
			addAction(aas, Action.CAN_GET_FOLDER_PARENT, !isRoot);
			addAction(aas, Action.CAN_GET_FOLDER_TREE, true);
			addAction(aas, Action.CAN_CREATE_DOCUMENT, write);
			addAction(aas, Action.CAN_CREATE_FOLDER, write);
			addAction(aas, Action.CAN_DELETE_TREE, write);
		} else {
			addAction(aas, Action.CAN_GET_CONTENT_STREAM, true);
			addAction(aas, Action.CAN_SET_CONTENT_STREAM, write);
			addAction(aas, Action.CAN_DELETE_CONTENT_STREAM, write);
			addAction(aas, Action.CAN_GET_ALL_VERSIONS, true);
		}

		AllowableActionsImpl result = new AllowableActionsImpl();
		result.setAllowableActions(aas);

		return result;
	}

	private static void addAction(Set<Action> aas, Action action, boolean condition) {
		if (condition) {
			aas.add(action);
		}
	}

	/**
	 * Compiles the ACL for a file or folder.
	 */
	private Acl compileAcl(PersistentObject object) {
		AccessControlListImpl result = new AccessControlListImpl();
		result.setAces(new ArrayList<Ace>());

		for (Map.Entry<String, Boolean> ue : userMap.entrySet()) {
			// create principal
			AccessControlPrincipalDataImpl principal = new AccessControlPrincipalDataImpl();
			principal.setPrincipalId(ue.getKey());

			// create ACE
			AccessControlEntryImpl entry = new AccessControlEntryImpl();
			entry.setPrincipal(principal);
			entry.setPermissions(new ArrayList<String>());
			entry.getPermissions().add(CMIS_READ);

			if (!ue.getValue().booleanValue() && checkPermission(object, null, Permission.WRITE)) {
				entry.getPermissions().add(CMIS_WRITE);
				entry.getPermissions().add(CMIS_ALL);
			}

			entry.setDirect(true);

			// add ACE
			result.getAces().add(entry);
		}

		return result;
	}

	/**
	 * Converts milliseconds into a calendar object.
	 */
	private static GregorianCalendar millisToCalendar(long millis) {
		GregorianCalendar result = new GregorianCalendar();
		result.setTimeZone(TimeZone.getTimeZone("GMT"));
		result.setTimeInMillis((long) (Math.ceil(millis / 1000) * 1000));

		return result;
	}

	/**
	 * Splits a filter statement into a collection of properties. If
	 * <code>filter</code> is <code>null</code>, empty or one of the properties
	 * is '*' , an empty collection will be returned.
	 */
	private static Set<String> splitFilter(String filter) {
		if (filter == null) {
			return null;
		}

		if (filter.trim().length() == 0) {
			return null;
		}

		Set<String> result = new HashSet<String>();
		for (String s : filter.split(",")) {
			s = s.trim();
			if (s.equals("*")) {
				return null;
			} else if (s.length() > 0) {
				result.add(s);
			}
		}

		// set a few base properties
		// query name == id (for base type properties)
		result.add(PropertyIds.OBJECT_ID);
		result.add(PropertyIds.OBJECT_TYPE_ID);
		result.add(PropertyIds.BASE_TYPE_ID);

		return result;
	}

	/**
	 * Gets the type id from a set of properties.
	 */
	private static String getTypeId(Properties properties) {
		PropertyData<?> typeProperty = properties.getProperties().get(PropertyIds.OBJECT_TYPE_ID);
		if (!(typeProperty instanceof PropertyId)) {
			throw new CmisInvalidArgumentException("Type id must be set!");
		}

		String typeId = ((PropertyId) typeProperty).getFirstValue();
		if (typeId == null) {
			throw new CmisInvalidArgumentException("Type id must be set!");
		}

		return typeId;
	}

	/**
	 * Returns the first value of an id property.
	 */
	private static String getIdProperty(Properties properties, String name) {
		PropertyData<?> property = properties.getProperties().get(name);
		if (!(property instanceof PropertyId)) {
			return null;
		}

		return ((PropertyId) property).getFirstValue();
	}

	/**
	 * Returns the first value of an string property.
	 */
	private static String getStringProperty(Properties properties, String name) {
		PropertyData<?> property = properties.getProperties().get(name);
		if (!(property instanceof PropertyString)) {
			return null;
		}

		return ((PropertyString) property).getFirstValue();
	}

	/**
	 * Returns the first value of an datetime property.
	 */
	private static GregorianCalendar getDateTimeProperty(Properties properties, String name) {
		PropertyData<?> property = properties.getProperties().get(name);
		if (!(property instanceof PropertyDateTime)) {
			return null;
		}

		return ((PropertyDateTime) property).getFirstValue();
	}

	/**
	 * Gets the user associated to the current session.
	 */
	private User getSessionUser() {
		if (sid != null) {
			UserSession session = SessionManager.getInstance().get(sid);
			if (session.getStatus() != UserSession.STATUS_OPEN)
				return null;
			return userDao.findById(session.getUserId());
		} else {
			return null;
		}
	}

	private boolean checkPermission(PersistentObject object, CallContext context, Permission permission) {

		long id = root.getId();
		if (object != null)
			if (object instanceof Folder) {
				id = object.getId();
			} else {
				id = ((Document) object).getFolder().getId();
			}

		long userId = 0;
		if (sid != null) {
			UserSession session = SessionManager.getInstance().get(sid);
			if (session.getStatus() != UserSession.STATUS_OPEN)
				return false;
			userId = session.getUserId();
		} else {
			User user = userDao.findByUserName(context.getUsername());
			userId = user.getId();
		}

		boolean enabled = folderDao.isReadEnable(id, userId);

		if (enabled && permission != null)
			enabled = enabled && folderDao.isPermissionEnabled(permission, id, userId);

		return enabled;
	}

	/**
	 * Checks if the user in the given context is valid for this repository and
	 * if the user has the required permissions.
	 */
	private boolean checkPermission(String objectId, CallContext context, Permission permission) {
		return checkPermission(objectId != null ? getObject(objectId) : null, context, permission);
	}

	private void warn(String msg, Throwable t) {
		log.warn("<" + id + "> " + msg, t);
	}

	private void debug(String msg) {
		// System.out.println("debug - " + msg);
		debug(msg, null);
	}

	private void debug(String msg, Throwable t) {
		log.debug("<" + id + "> " + msg, t);
	}

	public String getId() {
		return id;
	}

	public TypeManager getTypes() {
		return types;
	}

	/**
	 * Calculates the ObjectID for a persistent object. For documents it is
	 * doc.<b>docId</b>.
	 */
	private String getId(PersistentObject object) {
		if (object == null) {
			throw new IllegalArgumentException("Object is not valid!");
		}

		if (object instanceof Document)
			return ID_PREFIX_DOC + object.getId();
		else
			return ID_PREFIX_FLD + object.getId();
	}

	/**
	 * Retrieves the instance by the given objectId
	 */
	private PersistentObject getObject(String objectId) {
		if (objectId.startsWith(ID_PREFIX_DOC)) {
			Long id = Long.parseLong(objectId.substring(4));
			return documentDao.findById(id);
		} else if (objectId.startsWith(ID_PREFIX_FLD)) {
			Long id = Long.parseLong(objectId.substring(4));
			return folderDao.findById(id);
		} else {
			Long id = Long.parseLong(objectId);
			return folderDao.findById(id);
		}
	}
}