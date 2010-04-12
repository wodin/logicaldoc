package com.logicaldoc.web.document;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.Version;
import com.logicaldoc.core.document.dao.VersionDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.util.FacesUtil;

/**
 * Control that allows the user to navigate document's versions
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.0
 */
public class VersionsRecordsManager {
	private List<VersionRecord> versions = new ArrayList<VersionRecord>();

	private Document selectedDocument;

	private boolean showList = true;

	private boolean showCompare = false;

	/**
	 * Changes the currently selected document and updates the versions list.
	 * 
	 * @param doc
	 */
	public void selectDocument(Document doc) {
		VersionDAO vdao = (VersionDAO) Context.getInstance().getBean(VersionDAO.class);
		selectedDocument = doc;

		showCompare = false;
		showList = true;

		// initiate the list
		if (versions != null) {
			versions.clear();
		} else {
			versions = new ArrayList<VersionRecord>(10);
		}

		List<Version> tmp = vdao.findByDocId(doc.getId());
		for (Version ver : tmp) {
			VersionRecord versionTmp = new VersionRecord(ver);
			versions.add(versionTmp);
			if (ver.getVersion().equals(doc.getVersion())) {
				versionTmp.setCurrentVersion(true);
			}
		}
	}

	/**
	 * Cleans up the resources used by this class. This method could be called
	 * when a session destroyed event is called.
	 */
	public void dispose() {
		versions.clear();
	}

	/**
	 * Gets the list of versions which will be used by the ice:dataTable
	 * component.
	 * 
	 * @return array list of versions
	 */
	public List<VersionRecord> getVersions() {
		return versions;
	}

	public List<SelectItem> getVersionItems() {
		List<SelectItem> items = new ArrayList<SelectItem>();
		for (Version version : getVersions()) {
			items.add(new SelectItem(version.getVersion()));
		}
		return items;
	}

	public VersionRecord getVersion(String version) {
		for (VersionRecord ver : getVersions()) {
			if (ver.getVersion().equals(version))
				return ver;
		}
		return null;
	}

	public String back() {
		DocumentNavigation documentNavigation = ((DocumentNavigation) FacesUtil.accessBeanFromFacesContext(
				"documentNavigation", FacesContext.getCurrentInstance()));
		documentNavigation.showDocuments();
		return null;
	}

	public String edit() {
		showList = false;
		showCompare = false;
		return null;
	}

	public String compare() {
		showList = false;
		showCompare = true;
		return null;
	}

	public String backToList() {
		showList = true;
		showCompare = false;
		return null;
	}

	public Document getSelectedDocument() {
		return selectedDocument;
	}

	public void setSelectedDocument(Document selectedDocument) {
		this.selectedDocument = selectedDocument;
	}

	public boolean isShowList() {
		return showList;
	}

	public boolean isShowCompare() {
		return showCompare;
	}
}