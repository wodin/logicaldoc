package com.logicaldoc.workflow;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.util.Context;

/**
 * The <code>DocumentRecord</code> class contains the base information for an
 * entry in a data table. This class is meant to represent a model and should
 * only contain base document data
 * <p/>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.0
 */
public class DocumentRecord implements Serializable {

	private static final long serialVersionUID = 1L;

	protected static Log log = LogFactory.getLog(DocumentRecord.class);

	protected Document document;

	protected Document shortcut;

	private String documentPath;

	protected long docId;

	public DocumentRecord(long docId) {
		this.docId = docId;
	}

	public DocumentRecord() {
		super();
	}

	public DocumentRecord(Document document) {
		super();
		this.document = document;
	}

	public Document getDocument() {
		if (document == null)
			loadDocument();
		return document;
	}

	public boolean isDocumentFound() {
		return document != null;
	}

	public long getDocId() {
		return getDocument().getId();
	}

	public String getCustomId() {
		return getDocument().getCustomId();
	}

	public Date getCreationDate() {
		return getDocument().getCreation();
	}

	/**
	 * Gets the description of the record
	 * 
	 * @return description of the record
	 */
	public String getTitle() {
		return getDocument().getTitle();
	}

	public String getDisplayTitle() {
		return StringUtils.abbreviate(getDocument().getTitle(), 40);
	}

	public String getDisplayFilename() {
		return StringUtils.abbreviate(getDocument().getFileName(), 60);
	}

	public boolean isCheckedOut() {
		return getDocument().getStatus() == Document.DOC_CHECKED_OUT;
	}

	public boolean isLocked() {
		return getDocument().getStatus() != Document.DOC_UNLOCKED;
	}

	public String getIcon() {
		return getDocument().getIcon();
	}

	public Date getSourceDate() {
		return getDocument().getSourceDate();
	}

	public Date getDate() {
		return getDocument().getDate();
	}

	@Override
	public boolean equals(Object arg0) {
		DocumentRecord other = (DocumentRecord) arg0;
		if (getDocument() == null || arg0 == null)
			return false;
		if (document.getId() == other.getDocument().getId() && shortcut != null) {
			// The id is the same, are we inspecting a shortcut
			return shortcut.equals(other.getShortcut());
		} else {
			return document.getId() == other.getDocument().getId();
		}
	}

	public String getDocumentPath() {
		if (StringUtils.isEmpty(documentPath)) {
			try {
				Menu folder = null;
				if (shortcut != null)
					folder = shortcut.getFolder();
				else
					folder = getDocument().getFolder();
				MenuDAO menuDao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
				List<Menu> parentColl = menuDao.findParents(folder.getId());
				parentColl.add(folder);
				ArrayList<Menu> parentList = new ArrayList<Menu>(parentColl);

				StringBuilder sb = new StringBuilder();
				boolean first = true;
				for (Menu menu : parentList) {
					if (!first)
						sb.append(" / ");

					if (menu.getId() == Menu.MENUID_HOME)
						continue;
					if (menu.getId() == Menu.MENUID_DOCUMENTS) {
						// Decoding the root of documents using the resource
						// bundle
						String menuText = "/";
						sb.append(menuText);
					} else {
						sb.append(menu.getText());
					}
					first = false;
				}
				documentPath = sb.toString();

			} catch (Throwable th) {
				log.warn("Exception getDocumentPath() " + th.getMessage(), th);
			}
		}
		return documentPath;
	}

	/**
	 * Checks if this document has links or is linked
	 */
	public boolean isLinked() {
		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		return docDao.findLinkedDocuments(getDocId(), null, null).size() > 0;
	}

	/**
	 * Utility method used by document lazy loading
	 */
	protected void loadDocument() {
		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		this.document = docDao.findById(docId);
		if (this.document == null || this.document.getDeleted() == 1)
			this.document = new Document();
		else if (this.document.getDocRef() != null) {
			// This document is a shortcut, so load the referenced doc
			this.shortcut = document;
			this.document = docDao.findById(document.getDocRef());
			if (this.document == null)
				this.document = new Document();
		}
	}

	/**
	 * Initializes document's collections
	 */
	public void initCollections() {
		if (this.document != null) {
			DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
			docDao.initialize(this.document);
		}
	}

	public Date getCreation() {
		return document.getCreation();
	}

	public Date getLastModified() {
		return document.getLastModified();
	}

	public String getExportName() {
		return document.getExportName();
	}

	public int getExportStatus() {
		return document.getExportStatus();
	}

	public Document getShortcut() {
		return shortcut;
	}

	public String getSource() {
		return getDocument().getSource();
	}
}