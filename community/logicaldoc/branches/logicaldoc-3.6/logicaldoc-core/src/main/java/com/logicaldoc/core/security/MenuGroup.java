package com.logicaldoc.core.security;

/**
 * This class represents menugroup.
 * 
 * @author Michael Scholz
 * @version 1.0
 */
public class MenuGroup {

	private int writeEnable = 0;

	private String groupName;


	public MenuGroup() {
	}

	public MenuGroup(String groupName) {
		this.groupName=groupName;
	}


	public String getGroupName() {
		return groupName;
	}

	public int getWriteEnable() {
		return writeEnable;
	}


	public void setGroupName(String gname) {
		this.groupName = gname;
	}

	public void setWriteEnable(int enable) {
		if ((enable < 0) || (enable > 1)) {
			writeEnable = 0;
		} else {
			writeEnable = enable;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof MenuGroup))
			return false;
		MenuGroup other = (MenuGroup) obj;
		return this.getGroupName().equals(other.getGroupName());
	}

	@Override
	public int hashCode() {
		return groupName.hashCode();
	}
}