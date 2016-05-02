package com.logicaldoc.cmis;

import java.math.BigInteger;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.commons.data.Acl;
import org.apache.chemistry.opencmis.commons.data.AllowableActions;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.data.ExtensionsData;
import org.apache.chemistry.opencmis.commons.data.FailedToDeleteData;
import org.apache.chemistry.opencmis.commons.data.ObjectData;
import org.apache.chemistry.opencmis.commons.data.ObjectInFolderList;
import org.apache.chemistry.opencmis.commons.data.ObjectList;
import org.apache.chemistry.opencmis.commons.data.ObjectParentData;
import org.apache.chemistry.opencmis.commons.data.Properties;
import org.apache.chemistry.opencmis.commons.data.RenditionData;
import org.apache.chemistry.opencmis.commons.data.RepositoryInfo;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinition;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinitionContainer;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinitionList;
import org.apache.chemistry.opencmis.commons.enums.IncludeRelationships;
import org.apache.chemistry.opencmis.commons.enums.UnfileObject;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.exceptions.CmisBaseException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisPermissionDeniedException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException;
import org.apache.chemistry.opencmis.commons.impl.server.AbstractCmisService;
import org.apache.chemistry.opencmis.commons.server.CallContext;
import org.apache.chemistry.opencmis.commons.server.ObjectInfo;
import org.apache.chemistry.opencmis.commons.spi.Holder;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.core.document.DocumentEvent;
import com.logicaldoc.core.document.dao.HistoryDAO;
import com.logicaldoc.core.folder.Folder;
import com.logicaldoc.core.folder.FolderDAO;
import com.logicaldoc.core.security.Session;
import com.logicaldoc.core.security.SessionManager;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.ContextProperties;

/**
 * LogicalDOC implementation of the CMIS service
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.5.1
 */
public class LDCmisService extends AbstractCmisService {

	private static final Logger log = LoggerFactory.getLogger(LDCmisService.class);

	/**
	 * Key is the repository Id
	 */
	private final Map<String, LDRepository> repositories = new HashMap<String, LDRepository>();

	private CallContext context;

	private String sessionId = null;

	private HistoryDAO historyDao = null;

	/* To avoid refetching it several times per session. */
	protected String cachedChangeLogToken;

	/**
	 * Constructor.
	 */
	public LDCmisService(CallContext context, String sessionId) {
		this.context = context;
		this.sessionId = sessionId;

		try {
			historyDao = (HistoryDAO) Context.get().getBean(HistoryDAO.class);

			FolderDAO fdao = (FolderDAO) Context.get().getBean(FolderDAO.class);
			Session session = SessionManager.get().get(sessionId);
			Folder root = fdao.findRoot(session.getTenantId());

			repositories.put(Long.toString(root.getId()), new LDRepository(root, sessionId));
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
		}
	}

	public CallContext getCallContext() {
		return context;
	}

	@Override
	public RepositoryInfo getRepositoryInfo(String repositoryId, ExtensionsData extension) {
		log.debug("** getRepositoryInfo");

		validateSession();

		String latestChangeLogToken;
		if (cachedChangeLogToken != null) {
			latestChangeLogToken = cachedChangeLogToken;
		} else {
			latestChangeLogToken = getLatestChangeLogToken(repositoryId);
			cachedChangeLogToken = latestChangeLogToken;
		}

		for (LDRepository repo : repositories.values()) {
			if (repo.getId().equals(repositoryId)) {
				return repo.getRepositoryInfo(getCallContext(), latestChangeLogToken);
			}
		}

		throw new CmisObjectNotFoundException("Unknown repository '" + repositoryId + "'!");
	}

	@Override
	public List<RepositoryInfo> getRepositoryInfos(ExtensionsData extension) {
		log.debug("** getRepositoryInfos");

		validateSession();

		List<RepositoryInfo> result = new ArrayList<RepositoryInfo>();

		for (LDRepository repo : repositories.values()) {
			String latestChangeLogToken = getLatestChangeLogToken(repo.getId());
			result.add(repo.getRepositoryInfo(getCallContext(), latestChangeLogToken));
		}

		return result;
	}

	/**
	 * Return the most recent events regarding a document
	 * 
	 * @param repositoryId
	 * @return The getTime() of the latest date
	 */
	protected String getLatestChangeLogToken(String repositoryId) {
		try {
			ContextProperties settings = Context.get().getProperties();
			if (!"true".equals(settings.getProperty("cmis.changelog")))
				return null;

			LDRepository repo = repositories.get(repositoryId);

			StringBuffer query = new StringBuffer(
					"select max(ld_date) from ld_history where ld_deleted=0 and ld_tenantid=");
			query.append(Long.toString(repo.getRoot().getTenantId()));
			query.append(" and ld_event in ('");
			query.append(DocumentEvent.STORED);
			query.append("','");
			query.append(DocumentEvent.CHECKEDIN);
			query.append("','");
			query.append(DocumentEvent.CHANGED);
			query.append("','");
			query.append(DocumentEvent.RENAMED);
			query.append("','");
			query.append(DocumentEvent.DELETED);
			query.append("')");

			Timestamp latestDate = (Timestamp) historyDao.queryForObject(query.toString(), Timestamp.class);
			if (latestDate == null)
				return "0";
			else
				return Long.toString(latestDate.getTime());
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			throw new CmisRuntimeException(e.toString(), e);
		}
	}

	@Override
	public TypeDefinitionList getTypeChildren(String repositoryId, String typeId, Boolean includePropertyDefinitions,
			BigInteger maxItems, BigInteger skipCount, ExtensionsData extension) {
		validateSession();
		return getRepository().getTypesChildren(getCallContext(), typeId, includePropertyDefinitions, maxItems,
				skipCount);
	}

	@Override
	public TypeDefinition getTypeDefinition(String repositoryId, String typeId, ExtensionsData extension) {
		validateSession();
		return getRepository().getTypeDefinition(getCallContext(), typeId);
	}

	@Override
	public List<TypeDefinitionContainer> getTypeDescendants(String repositoryId, String typeId, BigInteger depth,
			Boolean includePropertyDefinitions, ExtensionsData extension) {
		validateSession();
		return getRepository().getTypesDescendants(getCallContext(), typeId, depth, includePropertyDefinitions);
	}

	@Override
	public ObjectInFolderList getChildren(String repositoryId, String folderId, String filter, String orderBy,
			Boolean includeAllowableActions, IncludeRelationships includeRelationships, String renditionFilter,
			Boolean includePathSegment, BigInteger maxItems, BigInteger skipCount, ExtensionsData extension) {
		validateSession();
		return getRepository().getChildren(getCallContext(), folderId, filter, includeAllowableActions,
				includePathSegment, maxItems, skipCount, this);
	}

	@Override
	public List<ObjectParentData> getObjectParents(String repositoryId, String objectId, String filter,
			Boolean includeAllowableActions, IncludeRelationships includeRelationships, String renditionFilter,
			Boolean includeRelativePathSegment, ExtensionsData extension) {
		validateSession();
		return getRepository().getObjectParents(getCallContext(), objectId, filter, includeAllowableActions,
				includeRelativePathSegment, this);
	}

	@Override
	public ObjectData getObject(String repositoryId, String objectId, String filter, Boolean includeAllowableActions,
			IncludeRelationships includeRelationships, String renditionFilter, Boolean includePolicyIds,
			Boolean includeAcl, ExtensionsData extension) {
		validateSession();
		return getRepository().getObject(getCallContext(), objectId, null, filter, includeAllowableActions, includeAcl,
				this);
	}

	@Override
	public ObjectInfo getObjectInfo(String repositoryId, String objectId) {
		validateSession();
		return getRepository().getObjectInfo(objectId, this);
	}

	@Override
	public String create(String repositoryId, Properties properties, String folderId, ContentStream contentStream,
			VersioningState versioningState, List<String> policies, ExtensionsData extension) {
		validateSession();
		ObjectData object = getRepository().create(getCallContext(), properties, folderId, contentStream,
				versioningState, this);
		return object.getId();
	}

	@Override
	public String createDocument(String repositoryId, Properties properties, String folderId,
			ContentStream contentStream, VersioningState versioningState, List<String> policies, Acl addAces,
			Acl removeAces, ExtensionsData extension) {
		validateSession();
		return getRepository().createDocument(getCallContext(), properties, folderId, contentStream, versioningState);
	}

	@Override
	public String createFolder(String repositoryId, Properties properties, String folderId, List<String> policies,
			Acl addAces, Acl removeAces, ExtensionsData extension) {
		validateSession();
		return getRepository().createFolder(getCallContext(), properties, folderId);
	}

	@Override
	public void deleteObjectOrCancelCheckOut(String repositoryId, String objectId, Boolean allVersions,
			ExtensionsData extension) {
		validateSession();
		getRepository().deleteObjectOrCancelCheckOut(getCallContext(), objectId);
	}

	@Override
	public FailedToDeleteData deleteTree(String repositoryId, String folderId, Boolean allVersions,
			UnfileObject unfileObjects, Boolean continueOnFailure, ExtensionsData extension) {
		validateSession();
		return getRepository().deleteTree(getCallContext(), folderId, continueOnFailure);
	}

	@Override
	public AllowableActions getAllowableActions(String repositoryId, String objectId, ExtensionsData extension) {
		validateSession();
		return getRepository().getAllowableActions(getCallContext(), objectId);
	}

	@Override
	public ContentStream getContentStream(String repositoryId, String objectId, String streamId, BigInteger offset,
			BigInteger length, ExtensionsData extension) {
		validateSession();
		return getRepository().getContentStream(getCallContext(), objectId, offset, length);
	}

	@Override
	public Properties getProperties(String repositoryId, String objectId, String filter, ExtensionsData extension) {
		validateSession();
		ObjectData object = getRepository().getObject(getCallContext(), objectId, null, filter, false, false, this);
		return object.getProperties();
	}

	@Override
	public List<RenditionData> getRenditions(String repositoryId, String objectId, String renditionFilter,
			BigInteger maxItems, BigInteger skipCount, ExtensionsData extension) {
		validateSession();
		return Collections.emptyList();
	}

	@Override
	public void moveObject(String repositoryId, Holder<String> objectId, String targetFolderId, String sourceFolderId,
			ExtensionsData extension) {
		validateSession();
		getRepository().moveObject(getCallContext(), objectId, targetFolderId, this);
	}

	@Override
	public void updateProperties(String repositoryId, Holder<String> objectId, Holder<String> changeToken,
			Properties properties, ExtensionsData extension) {
		validateSession();
		getRepository().updateProperties(getCallContext(), objectId, properties, this);
	}

	@Override
	public List<ObjectData> getAllVersions(String repositoryId, String objectId, String versionSeriesId, String filter,
			Boolean includeAllowableActions, ExtensionsData extension) {
		validateSession();
		return getRepository().getAllVersions(objectId);
	}

	@Override
	public ObjectData getObjectOfLatestVersion(String repositoryId, String objectId, String versionSeriesId,
			Boolean major, String filter, Boolean includeAllowableActions, IncludeRelationships includeRelationships,
			String renditionFilter, Boolean includePolicyIds, Boolean includeAcl, ExtensionsData extension) {
		validateSession();
		return getRepository().getObject(getCallContext(), objectId, versionSeriesId, filter, includeAllowableActions,
				includeAcl, this);
	}

	@Override
	public Properties getPropertiesOfLatestVersion(String repositoryId, String objectId, String versionSeriesId,
			Boolean major, String filter, ExtensionsData extension) {
		validateSession();
		ObjectData object = getRepository().getObject(getCallContext(), objectId, versionSeriesId, filter, false,
				false, null);

		return object.getProperties();
	}

	@Override
	public Acl getAcl(String repositoryId, String objectId, Boolean onlyBasicPermissions, ExtensionsData extension) {
		validateSession();
		return getRepository().getAcl(getCallContext(), objectId);
	}

	@Override
	public ObjectList query(String repositoryId, String statement, Boolean searchAllVersions,
			Boolean includeAllowableActions, IncludeRelationships includeRelationships, String renditionFilter,
			BigInteger maxItems, BigInteger skipCount, ExtensionsData extension) {
		validateSession();
		return getRepository().query(statement, maxItems != null ? maxItems.intValue() : null);
	}

	@Override
	public void cancelCheckOut(String repositoryId, String objectId, ExtensionsData extension) {
		validateSession();
		getRepository().cancelCheckOut(objectId);
	}

	@Override
	public void checkIn(String repositoryId, Holder<String> objectId, Boolean major, Properties properties,
			ContentStream contentStream, String checkinComment, List<String> policies, Acl addAces, Acl removeAces,
			ExtensionsData extension) {
		validateSession();
		getRepository().checkIn(objectId, major, contentStream, checkinComment);
	}

	@Override
	public void checkOut(String repositoryId, Holder<String> objectId, ExtensionsData extension,
			Holder<Boolean> contentCopied) {
		validateSession();
		getRepository().checkOut(objectId, contentCopied);
	}

	public String getSessionId() {
		return sessionId;
	}

	private Session validateSession() {
		if (getSessionId() == null)
			return null;

		try {
			Session session = SessionManager.get().get(getSessionId());
			if (session == null)
				throw new CmisPermissionDeniedException("Unexisting session " + getSessionId());
			if (session.getStatus() != Session.STATUS_OPEN)
				throw new CmisPermissionDeniedException("Invalid or Expired Session " + getSessionId());
			session.renew();
			return session;
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			if (t instanceof CmisBaseException)
				throw (CmisBaseException) t;
			else
				throw new CmisPermissionDeniedException("Invalid session!");
		}
	}

	public LDRepository getRepository() {
		LDRepository repo = null;
		Session session = validateSession();

		if (StringUtils.isEmpty(getCallContext().getRepositoryId())) {
			/*
			 * The information is not in the request, so fallback to the session
			 */
			repo = repositories.get(session.getDictionary().get(ServiceFactory.KEY_REPO_ID));
		} else {
			// Update the last accessed repository
			repo = repositories.get(getCallContext().getRepositoryId());
			session.getDictionary().put(ServiceFactory.KEY_REPO_ID, repo.getId());
		}

		if (repo == null)
			throw new CmisPermissionDeniedException("Repository " + getCallContext().getRepositoryId() + " not found !");

		return repo;
	}

	@Override
	public ObjectData getObjectByPath(String repositoryId, String path, String filter, Boolean includeAllowableActions,
			IncludeRelationships includeRelationships, String renditionFilter, Boolean includePolicyIds,
			Boolean includeAcl, ExtensionsData extension) {
		validateSession();
		return getRepository().getObjectByPath(getCallContext(), path, filter, includeAllowableActions,
				includeRelationships, renditionFilter, includePolicyIds, includeAcl, extension);
	}

	@Override
	public String createDocumentFromSource(String repositoryId, String sourceId, Properties properties,
			String folderId, VersioningState versioningState, List<String> policies, Acl addAces, Acl removeAces,
			ExtensionsData extension) {
		validateSession();
		return getRepository().createDocumentFromSource(getCallContext(), sourceId, folderId);
	}

	@Override
	public void setContentStream(String repositoryId, Holder<String> objectId, Boolean overwriteFlag,
			Holder<String> changeToken, ContentStream contentStream, ExtensionsData extension) {
		validateSession();
		checkOut(repositoryId, objectId, extension, new Holder(false));
		checkIn(repositoryId, objectId, false, null, contentStream, "", null, null, null, extension);
		// checkOut(repositoryId, objectId, extension, new Holder(false));
	}

	@Override
	public ObjectList getContentChanges(String repositoryId, Holder<String> changeLogToken, Boolean includeProperties,
			String filter, Boolean includePolicyIds, Boolean includeAcl, BigInteger maxItems, ExtensionsData extension) {
		log.debug("getContentChanges " + changeLogToken.getValue() + "|" + filter + " | "
				+ new Date(Long.parseLong(changeLogToken.getValue())));

		validateSession();

		try {
			ObjectList ret = getRepository().getContentChanges(changeLogToken,
					maxItems != null ? (int) maxItems.doubleValue() : 2000);
			return ret;
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw t;
		}
	}

}