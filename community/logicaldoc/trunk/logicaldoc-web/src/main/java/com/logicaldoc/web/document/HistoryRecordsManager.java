package com.logicaldoc.web.document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.application.FacesMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.History;
import com.logicaldoc.core.document.dao.HistoryDAO;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.i18n.Messages;

/**
 * Control that allows the user to list history events
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.0
 */
public class HistoryRecordsManager {
	protected static Log log = LogFactory.getLog(HistoryRecordsManager.class);

	private List<History> histories = new ArrayList<History>();

	private List<History> folderHistories = new ArrayList<History>();

	private Document selectedDocument;

	private Menu selectedFolder;

	/**
	 * Changes the currently selected document and updates the articles list.
	 * 
	 * @param doc
	 */
	public void selectDocument(Document doc) {
		selectedDocument = doc;

		// initiate the list
		if (histories != null) {
			histories.clear();
		} else {
			histories = new ArrayList<History>(10);
		}

		try {
			HistoryDAO historyDAO = (HistoryDAO) Context.getInstance().getBean(HistoryDAO.class);
			histories = historyDAO.findByDocId(doc.getId());
			Collections.sort(histories, new Comparator<History>() {
				@Override
				public int compare(History o1, History o2) {
					return o2.getDate().compareTo(o1.getDate());
				}
			});
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			Messages.addMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage());
		}
	}

	/**
	 * Cleans up the resources used by this class. This method could be called
	 * when a session destroyed event is called.
	 */
	public void dispose() {
		histories.clear();
		folderHistories.clear();
	}

	/**
	 * Gets the list of histories which will be used by the ice:dataTable
	 * component.
	 * 
	 * @return array list of histories
	 */
	public Collection<History> getHistories() {
		return histories;
	}

	public Document getSelectedDocument() {
		return selectedDocument;
	}

	public void setSelectedDocument(Document selectedDocument) {
		this.selectedDocument = selectedDocument;
	}

	public Menu getSelectedFolder() {
		return selectedFolder;
	}

	public void setSelectedFolder(Menu selectedFolder) {
		this.selectedFolder = selectedFolder;
	}

	/**
	 * Gets the list of the selected folder histories which will be used by the
	 * ice:dataTable component.
	 * 
	 * @return array list of folder histories
	 */
	public List<History> getFolderHistories() {
		return folderHistories;
	}

	/**
	 * Changes the currently selected folder and updates the histories list.
	 * 
	 * @param folder
	 */
	public void selectFolder(Menu folder) {
		selectedFolder = folder;

		// initiate the list
		if (folderHistories != null) {
			folderHistories.clear();
		} else {
			folderHistories = new ArrayList<History>(10);
		}

		try {
			HistoryDAO historyDAO = (HistoryDAO) Context.getInstance().getBean(HistoryDAO.class);
			folderHistories = historyDAO.findByFolderId(folder.getId());
			Collections.sort(folderHistories, new Comparator<History>() {
				@Override
				public int compare(History o1, History o2) {
					return o2.getDate().compareTo(o1.getDate());
				}
			});
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			Messages.addMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage());
		}
	}
}
