package com.logicaldoc.web.document;

import com.logicaldoc.core.security.MenuGroup;

/**
 * A simple bean used to show Access rules
 * 
 * @author Michael Scholz
 * @author Marco Meschieri - Logical Objects
 */
public class GroupRule {
	private String groupName = "";

	private String displayName = "";

	private long groupId = -1;

	private boolean read = false;

	private boolean write = false;

	private boolean addChild = false;

	private boolean manageSecurity = false;

	private boolean delete = false;

	private boolean rename = false;

	/**
	 * true, if this rule can be changed in a dialog
	 */
	private boolean enabled = true;

	/** Creates a new instance of GroupRules */
	public GroupRule() {
	}

	public void init(MenuGroup mg){
		write=mg.getWrite() == 1;
		addChild=mg.getAddChild() == 1;
		manageSecurity=mg.getManageSecurity() == 1;
		delete=mg.getDelete() == 1;
		rename=mg.getRename() == 1;
	}
	
	public long getGroupId() {
		return groupId;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public boolean getRead() {
		return read;
	}

	public boolean getWrite() {
		return write;
	}

	public boolean getEnabled() {
		return enabled;
	}

	public void setGroupName(String gname) {
		groupName = gname;
	}

	public void setRead(boolean r) {
		read = r;
	}

	public void setWrite(boolean w) {
		write = w;
	}

	public void setEnabled(boolean e) {
		enabled = e;
	}

	@Override
	public boolean equals(Object obj) {
		GroupRule other = (GroupRule) obj;
		return groupId == other.getGroupId();
	}

	public boolean isAddChild() {
		return addChild;
	}

	public void setAddChild(boolean addChild) {
		this.addChild = addChild;
	}

	public boolean isManageSecurity() {
		return manageSecurity;
	}

	public void setManageSecurity(boolean manageSecurity) {
		this.manageSecurity = manageSecurity;
	}

	public boolean isDelete() {
		return delete;
	}

	public void setDelete(boolean delete) {
		this.delete = delete;
	}

	public boolean isRename() {
		return rename;
	}

	public void setRename(boolean rename) {
		this.rename = rename;
	}
}