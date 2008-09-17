package com.logicaldoc.web.document;

import java.util.Date;

import javax.faces.context.FacesContext;

import com.logicaldoc.core.document.Version;
import com.logicaldoc.core.i18n.DateBean;

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

	public Date getDate() {
		return DateBean.dateFromCompactString(getVersionDate());
	}

	public int compareTo(Version other) {
		return wrappedVersion.compareTo(other);
	}

	public boolean equals(Object obj) {
		return wrappedVersion.equals(obj);
	}

	public String getNewVersionName(String oldVersionName, VERSION_TYPE versionType) {
		return wrappedVersion.getNewVersionName(oldVersionName, versionType);
	}

	public String getVersion() {
		return wrappedVersion.getVersion();
	}

	public String getVersionComment() {
		return wrappedVersion.getVersionComment();
	}

	public String getVersionDate() {
		return wrappedVersion.getVersionDate();
	}

	public String getVersionUser() {
		return wrappedVersion.getVersionUser();
	}

	public int hashCode() {
		return wrappedVersion.hashCode();
	}

	public void setVersion(String version) {
		wrappedVersion.setVersion(version);
	}

	public void setVersionComment(String comment) {
		wrappedVersion.setVersionComment(comment);
	}

	public void setVersionDate(String date) {
		wrappedVersion.setVersionDate(date);
	}

	public void setVersionUser(String vuser) {
		wrappedVersion.setVersionUser(vuser);
	}

	public String toString() {
		return wrappedVersion.toString();
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
}
