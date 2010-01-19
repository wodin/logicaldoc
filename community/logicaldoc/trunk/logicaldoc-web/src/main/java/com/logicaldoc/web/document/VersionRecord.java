package com.logicaldoc.web.document;

import java.util.Date;

import javax.faces.context.FacesContext;

import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.Version;
import com.logicaldoc.core.document.dao.VersionDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.util.FacesUtil;

/**
 * Utility class suitable for template display
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.0
 */
public class VersionRecord extends Version {
	private Version wrappedVersion;

	private long wrappedVersionId;

	private boolean currentVersion = false;

	// indicates if node is selected
	private boolean selected = false;

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public VersionRecord(long versionId) {
		super();
		this.wrappedVersionId = versionId;
	}

	private void load() {
		VersionDAO versionDAO = (VersionDAO) Context.getInstance().getBean(VersionDAO.class);
		this.wrappedVersion = versionDAO.findById(wrappedVersionId);
		if(this.wrappedVersion==null)
			this.wrappedVersion=new Version();
	}

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
		if (wrappedVersion == null)
			load();

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

	public String compare() {
		if (wrappedVersion == null)
			load();

		// Show the proper panel
		VersionsRecordsManager manager = ((VersionsRecordsManager) FacesUtil.accessBeanFromFacesContext(
				"versionsRecordsManager", FacesContext.getCurrentInstance()));

		manager.compare();

		// Now initialize the form
		DiffBean diffBean = ((DiffBean) FacesUtil.accessBeanFromFacesContext("diffBean", FacesContext
				.getCurrentInstance()));

		VersionDAO vdao = (VersionDAO) Context.getInstance().getBean(VersionDAO.class);
		vdao.initialize(wrappedVersion);
		diffBean.setVersion1(wrappedVersion);
		return null;
	}

	public int compareTo(Version other) {
		if (wrappedVersion == null)
			load();
		return wrappedVersion.compareTo(other);
	}

	public boolean equals(Object obj) {
		if (wrappedVersion == null)
			load();
		return wrappedVersion.equals(obj);
	}

	public String getComment() {
		if (wrappedVersion == null)
			load();
		return wrappedVersion.getComment();
	}

	public Date getDate() {
		if (wrappedVersion == null)
			load();
		return wrappedVersion.getDate();
	}

	public String getUsername() {
		if (wrappedVersion == null)
			load();
		return wrappedVersion.getUsername();
	}

	public String getVersion() {
		if (wrappedVersion == null)
			load();
		return wrappedVersion.getVersion();
	}

	public int hashCode() {
		if (wrappedVersion == null)
			load();
		return wrappedVersion.hashCode();
	}

	public void setComment(String comment) {
		wrappedVersion.setComment(comment);
	}

	public void setDate(Date date) {
		wrappedVersion.setDate(date);
	}

	public void setUsername(String user) {
		wrappedVersion.setUsername(user);
	}

	public void setVersion(String version) {
		wrappedVersion.setVersion(version);
	}

	public String toString() {
		return wrappedVersion.toString();
	}

	public Date getVersionDate() {
		if (wrappedVersion == null)
			load();
		return wrappedVersion.getVersionDate();
	}

	public void setVersionDate(Date versionDate) {
		wrappedVersion.setVersionDate(versionDate);
	}

	public String getFileVersion() {
		if (wrappedVersion == null)
			load();
		return wrappedVersion.getFileVersion();
	}

	public String getEvent() {
		if (wrappedVersion == null)
			load();
		return wrappedVersion.getEvent();
	}

	public Version getWrappedVersion() {
		if (wrappedVersion == null)
			load();
		return wrappedVersion;
	}

	public Date getCreation() {
		if (wrappedVersion == null)
			load();
		return wrappedVersion.getCreation();
	}

	public String getCustomId() {
		if (wrappedVersion == null)
			load();
		return wrappedVersion.getCustomId();
	}

	public long getFileSize() {
		if (wrappedVersion == null)
			load();
		return wrappedVersion.getFileSize();
	}

	public String getIcon() {
		if (wrappedVersion == null)
			load();
		return wrappedVersion.getIcon();
	}

	public String getTitle() {
		if (wrappedVersion == null)
			load();
		return wrappedVersion.getTitle();
	}

	public Document getDocument() {
		if (wrappedVersion == null)
			load();
		return wrappedVersion.getDocument();
	}

	public long getId() {
		if (wrappedVersion == null)
			load();
		return wrappedVersion.getId();
	}

	public long getWrappedVersionId() {
		return wrappedVersionId;
	}
}
