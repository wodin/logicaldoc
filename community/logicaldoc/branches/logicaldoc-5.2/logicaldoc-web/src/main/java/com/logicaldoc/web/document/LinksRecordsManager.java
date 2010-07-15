package com.logicaldoc.web.document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.context.FacesContext;

import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.DocumentLink;
import com.logicaldoc.core.document.dao.DocumentLinkDAO;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.SessionManagement;
import com.logicaldoc.web.util.FacesUtil;

/**
 * Control that allows the user to navigate document's links
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.0
 */
public class LinksRecordsManager {
	private List<DocumentLink> links = new ArrayList<DocumentLink>();

	private Document selectedDocument;

	private DocumentLink selectedLink;

	private boolean showList = true;

	/**
	 * Changes the currently selected document and updates the links list.
	 * 
	 * @param doc
	 */
	public void selectDocument(Document doc) {
		selectedDocument = doc;
		reload();
	}

	private void reload() {
		// initiate the list
		if (links != null) {
			links.clear();
		} else {
			links = new ArrayList<DocumentLink>(10);
		}

		long userId = SessionManagement.getUserId();
		MenuDAO menuDao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		// Search for links to this document
		DocumentLinkDAO linkDao = (DocumentLinkDAO) Context.getInstance().getBean(DocumentLinkDAO.class);
		Collection<DocumentLink> coll = linkDao.findByDocId(selectedDocument.getId());

		// Remove not accessible documents
		for (DocumentLink link : coll) {
			if (link.getDocument1().equals(selectedDocument)
					&& menuDao.isReadEnable(link.getDocument2().getFolder().getId(), userId))
				links.add(link);
			else if (link.getDocument2().equals(selectedDocument)
					&& menuDao.isReadEnable(link.getDocument1().getFolder().getId(), userId))
				links.add(link);
		}

		// Sort by type
		Collections.sort(links, new Comparator<DocumentLink>() {
			@Override
			public int compare(DocumentLink o1, DocumentLink o2) {
				return o1.getType().compareTo(o2.getType());
			}
		});
	}

	public DocumentLink getSelectedLink() {
		return selectedLink;
	}

	public void setSelectedLink(DocumentLink selectedLink) {
		this.selectedLink = selectedLink;
	}

	/**
	 * Cleans up the resources used by this class. This method could be called
	 * when a session destroyed event is called.
	 */
	public void dispose() {
		links.clear();
	}

	/**
	 * Gets the list of links which will be used by the ice:dataTable component.
	 * 
	 * @return list of links
	 */
	public List<DocumentLink> getLinks() {
		return links;
	}

	public String back() {
		DocumentNavigation documentNavigation = ((DocumentNavigation) FacesUtil.accessBeanFromFacesContext(
				"documentNavigation", FacesContext.getCurrentInstance()));
		documentNavigation.showDocuments();
		return null;
	}

	public String edit() {
		DocumentLink link = (DocumentLink) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get(
				"link");
		setSelectedLink(link);
		showList = false;
		return null;
	}

	public String update() {
		DocumentLinkDAO dao = (DocumentLinkDAO) Context.getInstance().getBean(DocumentLinkDAO.class);
		dao.store(selectedLink);
		return backToList();
	}

	public String backToList() {
		showList = true;
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

	public String delete() {
		DocumentLink link = (DocumentLink) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get(
				"link");
		DocumentLinkDAO dao = (DocumentLinkDAO) Context.getInstance().getBean(DocumentLinkDAO.class);
		dao.delete(link.getId());
		reload();
		return null;
	}
}