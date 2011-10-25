package com.logicaldoc.core.security;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.logicaldoc.core.PersistentObject;

/**
 * This class represents the key concept of security of documents. The Folder is
 * used as an element to build hierarchies. With foldergroups you can associate
 * groups to a given folder and grant some permissions. Also setting the
 * recurityRef you can specify another reference folder that contains the
 * security policies.
 * 
 * @author Marco Meschieri - Logical Objects
 * @version 6.0
 */
public class Folder extends PersistentObject implements Comparable<Folder> {

	public static final long ROOTID = 5;

	private long id = 0;

	private String name = "";

	private long parentId = ROOTID;

	private Long securityRef;

	private String description = "";

	private int type = 0;

	private Date creation = new Date();
	
	private String creator;

	private Long creatorId;
	
	protected Set<FolderGroup> folderGroups = new HashSet<FolderGroup>();

	public Folder() {
	}

	public long getId() {
		return id;
	}

	public long getParentId() {
		return parentId;
	}

	public Set<FolderGroup> getFolderGroups() {
		return folderGroups;
	}

	public void clearFolderGroups() {
		folderGroups.clear();
		folderGroups = new HashSet<FolderGroup>();
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	public void setFolderGroups(Set<FolderGroup> fgroup) {
		folderGroups = fgroup;
	}

	public long[] getFolderGroupIds() {
		long[] idsArray = new long[folderGroups.size()];
		int i = 0;
		for (FolderGroup mg : folderGroups) {
			idsArray[i++] = mg.getGroupId();
		}
		return idsArray;
	}

	/**
	 * Adds FolderGroup object given in a String array to the ArrayList of
	 * FolderGroup.
	 * 
	 * @param groups array of group ids
	 */
	public void setFolderGroup(long[] groups) {
		folderGroups.clear();
		for (int i = 0; i < groups.length; i++) {
			FolderGroup mg = new FolderGroup();
			mg.setGroupId(groups[i]);
			mg.setWrite(1);
			mg.setAdd(1);
			mg.setSecurity(1);
			mg.setDelete(1);
			mg.setRename(1);
			folderGroups.add(mg);
		}
	}

	/**
	 * Adds a new element, substituting a precedin one with the same groupId.
	 */
	public void addFolderGroup(FolderGroup fg) {
		FolderGroup m = getFolderGroup(fg.getGroupId());
		getFolderGroups().remove(m);
		getFolderGroups().add(fg);
	}

	public FolderGroup getFolderGroup(long groupId) {
		for (FolderGroup fg : folderGroups) {
			if (fg.getGroupId() == groupId)
				return fg;
		}
		return null;
	}

	@Override
	public int compareTo(Folder o) {
		return this.name.compareTo(o.name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getSecurityRef() {
		return securityRef;
	}

	public void setSecurityRef(Long securityRef) {
		this.securityRef = securityRef;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Date getCreation() {
		return creation;
	}

	public void setCreation(Date creation) {
		this.creation = creation;
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
}