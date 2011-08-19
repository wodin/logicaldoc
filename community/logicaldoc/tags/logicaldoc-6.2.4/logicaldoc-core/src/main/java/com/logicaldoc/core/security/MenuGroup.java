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

	private int ld_import = 0;

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

	public int getld_import() {
		return ld_import;
	}

	public void setld_import(int ld_import) {
		this.ld_import = ld_import;
	}

	public int getBulkExport() {
		return bulkExport;
	}

	public void setBulkExport(int bulkExport) {
		this.bulkExport = bulkExport;
	}

	@Override
	public MenuGroup clone() {
		MenuGroup mg = new MenuGroup(groupId);
		mg.setAddChild(addChild);
		mg.setDelete(delete);
		mg.setManageSecurity(manageSecurity);
		mg.setManageImmutability(manageImmutability);
		mg.setRename(rename);
		mg.setWrite(write);
		mg.setld_import(ld_import);
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

	/**
	 * Parsing each permission and creates the integer representation
	 * 
	 * @return Permissions settings as integer representation.
	 */
	public int getPermissions() {
		StringBuffer sb = new StringBuffer("1");
		sb.append(getWrite() == 1 ? "1" : "0");
		sb.append(getAddChild() == 1 ? "1" : "0");
		sb.append(getManageSecurity() == 1 ? "1" : "0");
		sb.append(getManageImmutability() == 1 ? "1" : "0");
		sb.append(getDelete() == 1 ? "1" : "0");
		sb.append(getRename() == 1 ? "1" : "0");
		sb.append(getld_import() == 1 ? "1" : "0");
		sb.append(getBulkExport() == 1 ? "1" : "0");
		sb.append(getSign() == 1 ? "1" : "0");
		sb.append(getArchive() == 1 ? "1" : "0");
		sb.append(getWorkflow() == 1 ? "1" : "0");

		return Integer.parseInt(sb.toString(), 2);
	}

	/**
	 * Set each permission evaluating the given integer representation.
	 * 
	 * @param permissions mask(the last slot is for the 'read'
	 *        permission and it is not evaluated)
	 */
	public void setPermissions(int permissions) {
		setWrite(Permission.WRITE.match(permissions) ? 1 : 0);
		setAddChild(Permission.ADD.match(permissions) ? 1 : 0);
		setManageSecurity(Permission.SECURITY.match(permissions) ? 1 : 0);
		setManageImmutability(Permission.IMMUTABLE.match(permissions) ? 1 : 0);
		setDelete(Permission.DELETE.match(permissions) ? 1 : 0);
		setRename(Permission.RENAME.match(permissions) ? 1 : 0);
		setld_import(Permission.IMPORT.match(permissions) ? 1 : 0);
		setBulkExport(Permission.EXPORT.match(permissions) ? 1 : 0);
		setSign(Permission.SIGN.match(permissions) ? 1 : 0);
		setArchive(Permission.ARCHIVE.match(permissions) ? 1 : 0);
		setWorkflow(Permission.WORKFLOW.match(permissions) ? 1 : 0);
	}
}