package com.logicaldoc.web.document;

import java.util.Date;

import javax.faces.context.FacesContext;

import com.logicaldoc.core.document.Version;
import com.logicaldoc.web.util.FacesUtil;

/**
 * Utility class suitable for template display
 * 
 * @author Marco Meschieri
 * @version $Id: VersionRecord.java,v 1.1 2007/07/31 16:56:55 marco Exp $
 * @since 3.0
 */
public class VersionRecord extends Version {
	private Version wrappedVersion;

	private boolean currentVersion = false;

	public VersionRecord(Version version) {
		super();
		this.wrappedVersion = version;
	}

	public boolean isCurrentVersion() {
		return currentVersion;
	}

	public void setCurrentVersion(boolean currentVersion) {
		this.currentVersion = currentVersion;
	}

	public String edit() {
		FacesContext facesContext = FacesContext.getCurrentInstance();

		// Show the proper panel
		VersionsRecordsManager manager = ((VersionsRecordsManager) FacesUtil.accessBeanFromFacesContext(
				"versionsRecordsManager", FacesContext.getCurrentInstance()));

		manager.edit();

		// Now initialize the form
		VersionEditForm versionForm = ((VersionEditForm) FacesUtil.accessBeanFromFacesContext("versionForm",
				FacesContext.getCurrentInstance()));
		versionForm.init(this);

		return null;
	}

	public int compareTo(Version other) {
		return wrappedVersion.compareTo(other);
	}

	public boolean equals(Object obj) {
		return wrappedVersion.equals(obj);
	}

	public String getComment() {
		return wrappedVersion.getComment();
	}

	public Date getDate() {
		return wrappedVersion.getDate();
	}

	public String getNewVersionName(String oldVersionName, VERSION_TYPE versionType) {
		return wrappedVersion.getNewVersionName(oldVersionName, versionType);
	}

	public String getUser() {
		return wrappedVersion.getUser();
	}

	public String getVersion() {
		return wrappedVersion.getVersion();
	}

	public int hashCode() {
		return wrappedVersion.hashCode();
	}

	public void setComment(String comment) {
		wrappedVersion.setComment(comment);
	}

	public void setDate(Date date) {
		wrappedVersion.setDate(date);
	}

	public void setUser(String user) {
		wrappedVersion.setUser(user);
	}

	public void setVersion(String version) {
		wrappedVersion.setVersion(version);
	}

	public String toString() {
		return wrappedVersion.toString();
	}
}
