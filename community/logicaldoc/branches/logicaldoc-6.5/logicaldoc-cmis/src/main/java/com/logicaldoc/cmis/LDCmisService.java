package com.logicaldoc.cmis;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.commons.data.ExtensionsData;
import org.apache.chemistry.opencmis.commons.data.ObjectData;
import org.apache.chemistry.opencmis.commons.data.ObjectInFolderList;
import org.apache.chemistry.opencmis.commons.data.ObjectParentData;
import org.apache.chemistry.opencmis.commons.data.RepositoryInfo;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinition;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinitionList;
import org.apache.chemistry.opencmis.commons.enums.IncludeRelationships;
import org.apache.chemistry.opencmis.commons.exceptions.CmisNotSupportedException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.impl.server.AbstractCmisService;
import org.apache.chemistry.opencmis.commons.server.CallContext;

import com.logicaldoc.core.security.Folder;
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
			repositories.put(Long.toString(workspace.getId()), new LDRepository(workspace));
		}
	}

	public CallContext getCallContext() {
		return context;
	}

	@Override
	public RepositoryInfo getRepositoryInfo(String repositoryId, ExtensionsData extension) {
		for (LDRepository repo : repositories.values()) {
			if (repo.getId().equals(repositoryId)) {
				return repo.getRepositoryInfo(getCallContext());
			}
		}

		throw new CmisObjectNotFoundException("Unknown repository '" + repositoryId + "'!");
	}

	@Override
	public List<RepositoryInfo> getRepositoryInfos(ExtensionsData extension) {
		List<RepositoryInfo> result = new ArrayList<RepositoryInfo>();

		for (LDRepository repo : repositories.values()) {
			result.add(repo.getRepositoryInfo(getCallContext()));
		}

		return result;
	}

	@Override
	public TypeDefinitionList getTypeChildren(String repositoryId, String typeId, Boolean includePropertyDefinitions,
			BigInteger maxItems, BigInteger skipCount, ExtensionsData extension) {
		throw new CmisNotSupportedException("getTypeChildren");
	}

	@Override
	public TypeDefinition getTypeDefinition(String repositoryId, String typeId, ExtensionsData extension) {
		throw new CmisNotSupportedException("getTypeDefinition");
	}

	@Override
	public ObjectInFolderList getChildren(String repositoryId, String folderId, String filter, String orderBy,
			Boolean includeAllowableActions, IncludeRelationships includeRelationships, String renditionFilter,
			Boolean includePathSegment, BigInteger maxItems, BigInteger skipCount, ExtensionsData extension) {
		throw new CmisNotSupportedException("getChildren");
	}

	@Override
	public List<ObjectParentData> getObjectParents(String repositoryId, String objectId, String filter,
			Boolean includeAllowableActions, IncludeRelationships includeRelationships, String renditionFilter,
			Boolean includeRelativePathSegment, ExtensionsData extension) {
		throw new CmisNotSupportedException("getObjectParents");
	}

	@Override
	public ObjectData getObject(String repositoryId, String objectId, String filter, Boolean includeAllowableActions,
			IncludeRelationships includeRelationships, String renditionFilter, Boolean includePolicyIds,
			Boolean includeAcl, ExtensionsData extension) {
		throw new CmisNotSupportedException("getObject");
	}

	public String getSessionId() {
		return sessionId;
	}

}