package com.logicaldoc.core.security;

/**
 * This class represents security permissions for a group in relation to a menu
 * 
 * @author Marco Meschieri - Logical Objects
 * @version 1.0
 */
public class MenuGroup {

	private int write = 0;

	private int addChild = 0;

	private int manageSecurity = 0;

	private int delete = 0;

	private int rename = 0;

	private int manageImmutability = 0;

	private int bulkImport = 0;

	private int bulkExport = 0;

	private int sign = 0;

	private int archive = 0;

	private int workflow = 0;

	private long groupId;

	public MenuGroup() {
	}

	public int getAddChild() {
		return addChild;
	}

	public void setAddChild(int addChild) {
		this.addChild = addChild;
	}

	public int getManageSecurity() {
		return manageSecurity;
	}

	public void setManageSecurity(int manageSecurity) {
		this.manageSecurity = manageSecurity;
	}

	public int getDelete() {
		return delete;
	}

	public void setDelete(int delete) {
		this.delete = delete;
	}

	public int getRename() {
		return rename;
	}

	public void setRename(int rename) {
		this.rename = rename;
	}

	public MenuGroup(long groupId) {
		this.groupId = groupId;
	}

	public long getGroupId() {
		return groupId;
	}

	public int getWrite() {
		return write;
	}

	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}

	public void setWrite(int write) {
		this.write = write;
	}

	public int getManageImmutability() {
		return manageImmutability;
	}

	public void setManageImmutability(int manageImmutability) {
		this.manageImmutability = manageImmutability;
	}

	public int getBulkImport() {
		return bulkImport;
	}

	public void setBulkImport(int bulkImport) {
		this.bulkImport = bulkImport;
	}

	public int getBulkExport() {
		return bulkExport;
	}

	public void setBulkExport(int bulkExport) {
		this.bulkExport = bulkExport;
	}

	@Override
	public MenuGroup clone() throws CloneNotSupportedException {
		MenuGroup mg = new MenuGroup(groupId);
		mg.setAddChild(addChild);
		mg.setDelete(delete);
		mg.setManageSecurity(manageSecurity);
		mg.setManageImmutability(manageImmutability);
		mg.setRename(rename);
		mg.setWrite(write);
		mg.setBulkImport(bulkImport);
		mg.setBulkExport(bulkExport);
		mg.setSign(sign);
		mg.setArchive(archive);
		mg.setWorkflow(workflow);
		return mg;
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

	public int getSign() {
		return sign;
	}

	public void setSign(int sign) {
		this.sign = sign;
	}

	public int getArchive() {
		return archive;
	}

	public void setArchive(int archive) {
		this.archive = archive;
	}

	public int getWorkflow() {
		return workflow;
	}

	public void setWorkflow(int workflow) {
		this.workflow = workflow;
	}
}