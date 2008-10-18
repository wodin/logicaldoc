package com.logicaldoc.core.security;

/**
 * This class represents menugroup.
 * 
 * @author Michael Scholz
 * @version 1.0
 */
public class MenuGroup {

	private int writeEnable = 0;

	private long groupId;

	public MenuGroup() {
	}

	public MenuGroup(long groupId) {
		this.groupId = groupId;
	}

	public long getGroupId() {
		return groupId;
	}

	public int getWriteEnable() {
		return writeEnable;
	}

	public void setGroupId(long groupId) {
		this.groupId = groupId;
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
		return this.getGroupId() == other.getGroupId();
	}

	@Override
	public int hashCode() {
		return new Long(groupId).hashCode();
	}
}