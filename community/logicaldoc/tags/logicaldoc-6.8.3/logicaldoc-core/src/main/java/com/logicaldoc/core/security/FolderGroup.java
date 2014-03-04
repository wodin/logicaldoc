package com.logicaldoc.core.security;

/**
 * This class represents security permissions for a group in relation to a
 * folder
 * 
 * @author Marco Meschieri - Logical Objects
 * @version 6.0
 */
public class FolderGroup {

	private int read = 1;

	private int write = 0;

	private int download = 1;

	private int add = 0;

	private int security = 0;

	private int delete = 0;

	private int rename = 0;

	private int immutable = 0;

	private int _import = 0;

	private int export = 0;

	private int sign = 0;

	private int archive = 0;

	private int workflow = 0;

	private int calendar = 0;

	private long groupId;

	public FolderGroup(long groupId) {
		this.groupId = groupId;
	}

	public FolderGroup() {
	}

	@Override
	public FolderGroup clone() {
		FolderGroup mg = new FolderGroup(groupId);
		mg.setAdd(add);
		mg.setDelete(delete);
		mg.setSecurity(security);
		mg.setImmutable(immutable);
		mg.setRename(rename);
		mg.setWrite(write);
		mg.setRead(read);
		mg.setImport(_import);
		mg.setExport(export);
		mg.setSign(sign);
		mg.setArchive(archive);
		mg.setWorkflow(workflow);
		mg.setDownload(download);
		mg.setCalendar(calendar);
		return mg;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof FolderGroup))
			return false;
		FolderGroup other = (FolderGroup) obj;
		return this.getGroupId() == other.getGroupId();
	}

	@Override
	public int hashCode() {
		return new Long(groupId).hashCode();
	}

	/**
	 * Parsing each permission and creates the integer representation
	 * 
	 * @return Permissions settings as integer representation.
	 */
	public int getPermissions() {
		/**
		 * Very important, see the Permission enumeration in order u replicate
		 * the same mask order.
		 */
		StringBuffer sb = new StringBuffer();
		sb.append(getCalendar() == 1 ? "1" : "0");
		sb.append(getDownload() == 1 ? "1" : "0");
		sb.append(getWorkflow() == 1 ? "1" : "0");
		sb.append(getArchive() == 1 ? "1" : "0");
		sb.append(getSign() == 1 ? "1" : "0");
		sb.append(getExport() == 1 ? "1" : "0");
		sb.append(getImport() == 1 ? "1" : "0");
		sb.append(getRename() == 1 ? "1" : "0");
		sb.append(getDelete() == 1 ? "1" : "0");
		sb.append(getImmutable() == 1 ? "1" : "0");
		sb.append(getSecurity() == 1 ? "1" : "0");
		sb.append(getAdd() == 1 ? "1" : "0");
		sb.append(getWrite() == 1 ? "1" : "0");
		sb.append(getRead() == 1 ? "1" : "0");

		return Integer.parseInt(sb.toString(), 2);
	}

	/**
	 * Set each permission evaluating the given integer representation.
	 * 
	 * @param permissions mask(the last slot is for the 'read' permission and it
	 *        is not evaluated)
	 */
	public void setPermissions(int permissions) {
		setRead(Permission.READ.match(permissions) ? 1 : 0);
		setWrite(Permission.WRITE.match(permissions) ? 1 : 0);
		setAdd(Permission.ADD.match(permissions) ? 1 : 0);
		setSecurity(Permission.SECURITY.match(permissions) ? 1 : 0);
		setImmutable(Permission.IMMUTABLE.match(permissions) ? 1 : 0);
		setDelete(Permission.DELETE.match(permissions) ? 1 : 0);
		setRename(Permission.RENAME.match(permissions) ? 1 : 0);
		setImport(Permission.IMPORT.match(permissions) ? 1 : 0);
		setExport(Permission.EXPORT.match(permissions) ? 1 : 0);
		setSign(Permission.SIGN.match(permissions) ? 1 : 0);
		setArchive(Permission.ARCHIVE.match(permissions) ? 1 : 0);
		setWorkflow(Permission.WORKFLOW.match(permissions) ? 1 : 0);
		setDownload(Permission.DOWNLOAD.match(permissions) ? 1 : 0);
		setCalendar(Permission.CALENDAR.match(permissions) ? 1 : 0);
	}

	public int getRead() {
		return read;
	}

	public void setRead(int read) {
		this.read = read;
	}

	public int getWrite() {
		return write;
	}

	public void setWrite(int write) {
		this.write = write;
	}

	public int getAdd() {
		return add;
	}

	public void setAdd(int add) {
		this.add = add;
	}

	public int getSecurity() {
		return security;
	}

	public void setSecurity(int security) {
		this.security = security;
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

	public int getImport() {
		return _import;
	}

	public void setImport(int _import) {
		this._import = _import;
	}

	public int getExport() {
		return export;
	}

	public void setExport(int export) {
		this.export = export;
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

	public long getGroupId() {
		return groupId;
	}

	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}

	public int getImmutable() {
		return immutable;
	}

	public void setImmutable(int immutable) {
		this.immutable = immutable;
	}

	public int getDownload() {
		return download;
	}

	public void setDownload(int download) {
		this.download = download;
	}

	public int getCalendar() {
		return calendar;
	}

	public void setCalendar(int calendar) {
		this.calendar = calendar;
	}
}