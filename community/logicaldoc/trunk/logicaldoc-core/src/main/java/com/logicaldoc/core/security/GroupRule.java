package com.logicaldoc.core.security;

/**
 * A simple bean used to show Access rules
 * 
 * @author Michael Scholz
 * @author Marco Meschieri - Logical Objects
 */
public class GroupRule {
	private String groupName;

	private long groupId;

	private boolean read;

	private boolean write;

	/**
	 * true, if this rule can be changed in a dialog
	 */
	private boolean enabled;

	/** Creates a new instance of GroupRules */
	public GroupRule() {
		groupId = -1;
		groupName = "";
		read = false;
		write = false;
		enabled = false;
	}

	public long getGroupId() {
		return groupId;
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
}