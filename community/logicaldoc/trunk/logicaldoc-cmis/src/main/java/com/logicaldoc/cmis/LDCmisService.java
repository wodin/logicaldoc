package com.logicaldoc.cmis;

import java.math.BigInteger;
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
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisPermissionDeniedException;
import org.apache.chemistry.opencmis.commons.impl.server.AbstractCmisService;
import org.apache.chemistry.opencmis.commons.server.CallContext;
import org.apache.chemistry.opencmis.commons.spi.Holder;

import com.logicaldoc.core.security.Folder;
import com.logicaldoc.core.security.SessionManager;
import com.logicaldoc.core.security.UserSession;
import com.logicaldoc.core.security.dao.FolderDAO;
import com.logicaldoc.util.Context;

/**
 * LogicalDOC implementation of the CMIS service
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.5.1
 */
public class LDCmisService extends AbstractCmisService {

	/**
	 * Key is the repository Id
	 */
	private final Map<String, LDRepository> repositories = new HashMap<String, LDRepository>();

	private CallContext context;

	private String sessionId = null;

	/**
	 * Constructor.
	 */
	public LDCmisService(CallContext context, String sessionId) {
		this.context = context;
		this.sessionId = sessionId;

		FolderDAO fdao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		List<Folder> workspaces = fdao.findWorkspaces();
		for (Folder workspace : workspaces) {
			repositories.put(Long.toString(workspace.getId()), new LDRepository(workspace, sessionId));
		}
	}

	public CallContext getCallContext() {
		return context;
	}

	@Override
	public RepositoryInfo getRepositoryInfo(String repositoryId, ExtensionsData extension) {
		validateSession();
		for (LDRepository repo : repositories.values()) {
			if (repo.getId().equals(repositoryId)) {
				return repo.getRepositoryInfo(getCallContext());
			}
		}

		throw new CmisObjectNotFoundException("Unknown repository '" + repositoryId + "'!");
	}

	@Override
	public List<RepositoryInfo> getRepositoryInfos(ExtensionsData extension) {
		validateSession();
		List<RepositoryInfo> result = new ArrayList<RepositoryInfo>();

		for (LDRepository repo : repositories.values()) {
			result.add(repo.getRepositoryInfo(getCallContext()));
		}

		return result;
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
	public String createDocumentFromSource(String repositoryId, String sourceId, Properties properties,
			String folderId, VersioningState versioningState, List<String> policies, Acl addAces, Acl removeAces,
			ExtensionsData extension) {
		validateSession();

		System.out.println("***createDocumentFromSource " + properties);

		return getRepository().createDocumentFromSource(getCallContext(), sourceId, properties, folderId,
				versioningState);
	}

	@Override
	public String createFolder(String repositoryId, Properties properties, String folderId, List<String> policies,
			Acl addAces, Acl removeAces, ExtensionsData extension) {
		validateSession();
		return getRepository().createFolder(getCallContext(), properties, folderId);
	}

	@Override
	public void deleteContentStream(String repositoryId, Holder<String> objectId, Holder<String> changeToken,
			ExtensionsData extension) {
		validateSession();
		getRepository().setContentStream(getCallContext(), objectId, true, null);
	}

	@Override
	public void deleteObjectOrCancelCheckOut(String repositoryId, String objectId, Boolean allVersions,
			ExtensionsData extension) {
		validateSession();
		getRepository().deleteObject(getCallContext(), objectId);
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
	public ObjectData getObjectByPath(String repositoryId, String path, String filter, Boolean includeAllowableActions,
			IncludeRelationships includeRelationships, String renditionFilter, Boolean includePolicyIds,
			Boolean includeAcl, ExtensionsData extension) {
		validateSession();
		return getRepository().getObjectByPath(getCallContext(), path, filter, includeAllowableActions, includeAcl,
				this);
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
	public void setContentStream(String repositoryId, Holder<String> objectId, Boolean overwriteFlag,
			Holder<String> changeToken, ContentStream contentStream, ExtensionsData extension) {
		validateSession();
		getRepository().setContentStream(getCallContext(), objectId, overwriteFlag, contentStream);
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
		ObjectData theVersion = getRepository().getObject(getCallContext(), objectId, versionSeriesId, filter,
				includeAllowableActions, false, this);

		return Collections.singletonList(theVersion);
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
	
	public String getSessionId() {
		return sessionId;
	}

	private UserSession validateSession() {
		if (getSessionId() == null)
			return null;

		UserSession session = SessionManager.getInstance().get(getSessionId());
		if (session == null)
			throw new CmisPermissionDeniedException("Invalid session!");
		if (session.getStatus() != UserSession.STATUS_OPEN)
			throw new CmisPermissionDeniedException("Invalid or Expired Session");
		session.renew();
		return session;
	}

	public LDRepository getRepository() {
		LDRepository repo = repositories.get(getCallContext().getRepositoryId());
		if (repo == null)
			throw new CmisPermissionDeniedException("Repository " + getCallContext().getRepositoryId() + " not found !");
		return repo;
	}
}