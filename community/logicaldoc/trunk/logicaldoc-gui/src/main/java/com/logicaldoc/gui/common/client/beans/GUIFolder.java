package com.logicaldoc.gui.common.client.beans;

import java.io.Serializable;
import java.util.Date;

import com.logicaldoc.gui.common.client.Constants;

/**
 * Represents a folder from the GUI view
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class GUIFolder extends GUIExtensibleObject implements Serializable {

	private static final long serialVersionUID = 1L;

	private long id;

	private long parentId;

	private String description;

	private String name;

	private String[] permissions = new String[] {};

	private GUIRight[] rights = new GUIRight[] {};

	private GUIFolder[] path = null;

	private GUIFolder securityRef = null;

	private String pathExtended;

	private String creator;

	private Long creatorId;

	private Date creation;

	private int documentCount;

	private int subfolderCount;

	private int type;

	private int templateLocked = 0;

	private int position = 1;

	private Long quotaDocs = null;

	private Long quotaSize = null;

	// Total number of documents inside the folder's tree
	private long documentsTotal = 0L;

	// Total size of the folder's tree
	private long sizeTotal = 0L;

	private Long foldRef = null;

	private Integer storage = null;

	private Integer maxVersions = null;

	public GUIFolder() {

	}

	public boolean isWorkspace() {
		return type == 1;
	}

	public boolean isDefaultWorkspace() {
		return name.equals(Constants.WORKSPACE_DEFAULTNAME) && type == 1;
	}

	public GUIFolder(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String[] getPermissions() {
		return permissions;
	}

	public void setPermissions(String[] permissions) {
		this.permissions = permissions;
	}

	public boolean isWrite() {
		return hasPermission(Constants.PERMISSION_WRITE);
	}

	public boolean isDownload() {
		return hasPermission(Constants.PERMISSION_DOWNLOAD);
	}

	public boolean isDelete() {
		return hasPermission(Constants.PERMISSION_DELETE);
	}

	public boolean isRename() {
		return hasPermission(Constants.PERMISSION_RENAME);
	}

	public boolean hasPermission(String permission) {
		if (permissions == null)
			return false;
		for (String p : permissions)
			if (p.equals(permission))
				return true;
		return false;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPathExtended() {
		return pathExtended;
	}

	public void setPathExtended(String pathExtended) {
		this.pathExtended = pathExtended;
	}

	public GUIRight[] getRights() {
		return rights;
	}

	public void setRights(GUIRight[] rights) {
		this.rights = rights;
	}

	public long getParentId() {
		return parentId;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	public GUIFolder[] getPath() {
		return path;
	}

	public void setPath(GUIFolder[] path) {
		this.path = path;
		this.pathExtended = "";
		for (GUIFolder folder : path) {
			if (folder != null && folder.getId() != folder.getParentId()
					&& folder.getId() != Constants.DOCUMENTS_FOLDERID)
				pathExtended += "/" + folder.getName();
		}
		pathExtended += "/" + getName();
	}

	public GUIFolder getParent() {
		if (getPath() != null && getPath().length > 0)
			return getPath()[getPath().length - 1];
		else
			return null;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public Long getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(Long creatorId) {
		this.creatorId = creatorId;
	}

	public Date getCreation() {
		return creation;
	}

	public void setCreation(Date creation) {
		this.creation = creation;
	}

	public int getDocumentCount() {
		return documentCount;
	}

	public void setDocumentCount(int documentCount) {
		this.documentCount = documentCount;
	}

	public int getSubfolderCount() {
		return subfolderCount;
	}

	public void setSubfolderCount(int subfolderCount) {
		this.subfolderCount = subfolderCount;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getTemplateLocked() {
		return templateLocked;
	}

	public void setTemplateLocked(int templateLocked) {
		this.templateLocked = templateLocked;
	}

	public GUIFolder getSecurityRef() {
		return securityRef;
	}

	public void setSecurityRef(GUIFolder securityRef) {
		this.securityRef = securityRef;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public Long getQuotaDocs() {
		return quotaDocs;
	}

	public void setQuotaDocs(Long quotaDocs) {
		this.quotaDocs = quotaDocs;
	}

	public Long getQuotaSize() {
		return quotaSize;
	}

	public void setQuotaSize(Long quotaSize) {
		this.quotaSize = quotaSize;
	}

	public long getDocumentsTotal() {
		return documentsTotal;
	}

	public void setDocumentsTotal(long documentsTotal) {
		this.documentsTotal = documentsTotal;
	}

	public long getSizeTotal() {
		return sizeTotal;
	}

	public void setSizeTotal(long sizeTotal) {
		this.sizeTotal = sizeTotal;
	}

	public Long getFoldRef() {
		return foldRef;
	}

	public void setFoldRef(Long foldRef) {
		this.foldRef = foldRef;
	}

	public Integer getStorage() {
		return storage;
	}

	public void setStorage(Integer storage) {
		this.storage = storage;
	}

	public Integer getMaxVersions() {
		return maxVersions;
	}

	public void setMaxVersions(Integer maxVersions) {
		this.maxVersions = maxVersions;
	}
}
