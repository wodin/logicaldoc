package com.logicaldoc.web.document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.java.plugin.registry.Extension;

import com.icesoft.faces.context.effects.JavascriptContext;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.DocumentLink;
import com.logicaldoc.core.document.DocumentManager;
import com.logicaldoc.core.document.DownloadTicket;
import com.logicaldoc.core.document.History;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.document.dao.DocumentLinkDAO;
import com.logicaldoc.core.document.dao.DownloadTicketDAO;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.Permission;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.PluginRegistry;
import com.logicaldoc.util.io.CryptUtil;
import com.logicaldoc.web.SessionManagement;
import com.logicaldoc.web.StyleBean;
import com.logicaldoc.web.i18n.Messages;
import com.logicaldoc.web.navigation.MenuBarBean;
import com.logicaldoc.web.navigation.PageContentBean;
import com.logicaldoc.web.upload.InputFileBean;
import com.logicaldoc.web.util.FacesUtil;

/**
 * The <code>DocumentRecord</code> class contains the base information for an
 * entry in a data table. This class is meant to represent a model and should
 * only contain base document data
 * <p/>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.0
 */
public class DocumentRecord extends MenuBarBean {

	protected static Log log = LogFactory.getLog(DocumentRecord.class);

	public static final String GROUP_ROW_STYLE_CLASS = "groupRowStyle";

	public static final String CHILD_ROW_STYLE_CLASS = "childRowStyle";

	// style for all other columns in the sales record row.
	private String rowStyleClass = CHILD_ROW_STYLE_CLASS;

	// indicates if node is selected
	private boolean selected = false;

	protected Document document;

	protected Document shortcut;

	private String documentPath;

	protected long docId;

	/**
	 * <p>
	 * Creates a new <code>DocumentRecord</code>.
	 * </p>
	 * <p/>
	 * <p>
	 * The created DocumentRecord has no image states defined.
	 * </p>
	 * 
	 * @param menuId
	 * @param indentStyleClass
	 * @param rowStyleClass
	 */
	public DocumentRecord(long docId, String indentStyleClass, String rowStyleClass) {
		this.docId = docId;
		if (rowStyleClass != null)
			this.rowStyleClass = rowStyleClass;
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
		return StringUtils.abbreviate(getDocument().getTitle(), 65);
	}

	public String getDisplayFilename() {
		return StringUtils.abbreviate(getDocument().getFileName(), 65);
	}

	/**
	 * Gets the style class name used to define all other columns in the
	 * document record row, except the first column.
	 * 
	 * @return style class as defined in css file
	 */
	public String getRowStyleClass() {
		return rowStyleClass;
	}

	public boolean isSelected() {
		return selected;
	}

	public boolean isCheckedOut() {
		return getDocument().getStatus() == Document.DOC_CHECKED_OUT;
	}

	public boolean isLocked() {
		return getDocument().getStatus() != Document.DOC_UNLOCKED;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
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
						String menuText = Messages.getMessage(menu.getText());
						sb.append(menuText);
					} else {
						sb.append(menu.getText());
					}
					first = false;
				}
				documentPath = sb.toString();

			} catch (Throwable th) {
				logger.warn("Exception getDocumentPath() " + th.getMessage(), th);
			}
		}
		return documentPath;
	}

	/**
	 * Creates the context menu associated with this record
	 * 
	 * @see com.logicaldoc.web.navigation.MenuBarBean#createMenuItems()
	 */
	protected void createMenuItems() {
		model.clear();
		long userId = SessionManagement.getUserId();
		MenuDAO menuDAO = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		// Since the document shortcut implementation, the folder is not the
		// document folder, but the current navigation folder
		DocumentNavigation documentNavigation = ((DocumentNavigation) FacesUtil.accessBeanFromFacesContext(
				"documentNavigation", FacesContext.getCurrentInstance(), log));
		Menu folder = documentNavigation.getSelectedDir().getMenu();
		Document document = getDocument();
		StyleBean style = (StyleBean) Context.getInstance().getBean(StyleBean.class);

		try {
			if ((menuDAO.isWriteEnable(folder.getId(), userId)) && (document.getImmutable() == 0)) {

				// Checkin/checkout an lock/unlock
				if (document.getStatus() == Document.DOC_UNLOCKED) {
					model.add(createMenuItem(" " + Messages.getMessage("checkout"), "checkout-" + document.getId(),
							null, "#{documentRecord.checkout}", null, style.getImagePath("checkout.png"), true, null,
							null));
					model.add(createMenuItem(" " + Messages.getMessage("lock"), "lock-" + document.getId(), null,
							"#{documentRecord.lock}", null, style.getImagePath("document_lock.png"), true, null, null));
				} else if ((document.getStatus() == Document.DOC_CHECKED_OUT)
						&& (document.getLockUserId().equals(new Long(userId)))) {
					model.add(createMenuItem(" " + Messages.getMessage("checkin"), "checkin-" + document.getId(), null,
							"#{documentRecord.checkin}", null, style.getImagePath("checkin.png"), true, null, null));
				}

				// Lock/Unlock
				if ((document.getStatus() != Document.DOC_UNLOCKED)
						&& ((document.getLockUserId().equals(new Long(userId))) || "admin".equals(SessionManagement
								.getUsername()))) {
					// The user that locked the document can unlock it, and also
					// the
					// admin user
					model.add(createMenuItem(" " + Messages.getMessage("unlock"), "unlock-" + document.getId(), null,
							"#{documentRecord.unlock}", null, style.getImagePath("lock_open.png"), true, null, null));
				}

				if (document.getStatus() == Document.DOC_UNLOCKED)
					model.add(createMenuItem(" " + Messages.getMessage("edit"), "edit-" + document.getId(), null,
							"#{documentRecord.edit}", null, style.getImagePath("document_edit.png"), true, null, null));

				model.add(createMenuItem(" " + Messages.getMessage("link.pasteas"), "pastelink-" + document.getId(),
						null, "#{documentRecord.pasteAsLink}", null, style.getImagePath("paste_link.png"), true, null,
						null));
			}

			model
					.add(createMenuItem(" " + Messages.getMessage("msg.jsp.versions"), "versions-" + document.getId(),
							null, "#{documentRecord.versions}", null, style.getImagePath("versions.png"), true,
							"_blank", null));
			model
					.add(createMenuItem(" " + Messages.getMessage("msg.jsp.similardocs"),
							"similar-" + document.getId(), null, "#{searchForm.searchSimilar}", null, style
									.getImagePath("similar.png"), true, "_blank", null));
			model.add(createMenuItem(" " + Messages.getMessage("links"), "linked-" + document.getId(), null,
					"#{documentRecord.links}", null, style.getImagePath("link.png"), true, "_blank", null));
			model.add(createMenuItem(" " + Messages.getMessage("document.discussions"), "discussions-"
					+ document.getId(), null, "#{documentRecord.discussions}", null,
					style.getImagePath("comments.png"), true, "_blank", null));
			model.add(createMenuItem(" " + Messages.getMessage("msg.jsp.sendasemail"),
					"sendasmail-" + document.getId(), null, "#{documentRecord.sendAsEmail}", null, style
							.getImagePath("email_go.png"), true, "_blank", null));
			model.add(createMenuItem(" " + Messages.getMessage("msg.jsp.sendticket"), "sendticket-" + document.getId(),
					null, "#{documentRecord.sendAsTicket}", null, style.getImagePath("ticket.png"), true, "_blank",
					null));
			model.add(createMenuItem(" " + Messages.getMessage("info"), "info-" + document.getId(), null,
					"#{documentRecord.info}", null, style.getImagePath("info.png"), true, "_blank", null));
			model.add(createMenuItem(" " + Messages.getMessage("history"), "history-" + document.getId(), null,
					"#{documentRecord.history}", null, style.getImagePath("history.png"), true, "_blank", null));

			// Add extended menues
			// Acquire the 'DocumentContextMenu' extensions of the core plugin
			PluginRegistry registry = PluginRegistry.getInstance();
			Collection<Extension> exts = registry.getSortedExtensions("logicaldoc-core", "DocumentMenu", null);

			Set<Permission> permissions = menuDAO.getEnabledPermissions(folder.getId(), userId);
			for (Extension ext : exts) {
				if (StringUtils.isNotEmpty(ext.getParameter("permission").valueAsString())) {
					Permission permission = Permission.valueOf(ext.getParameter("permission").valueAsString());
					if (!permissions.contains(permission))
						continue;
				}
				String title = Messages.getMessage(ext.getParameter("title").valueAsString());
				String id = ext.getParameter("id").valueAsString() + "-" + folder.getId();
				String action = ext.getParameter("action").valueAsString();
				String icon = ext.getParameter("icon").valueAsString();
				String readonly = ext.getParameter("readonly").valueAsString();
				// Skip if the document is locked and the menu can alter the
				// document
				if ("false".equals(readonly) && document.getStatus() != Document.DOC_UNLOCKED)
					continue;
				String target = ext.getParameter("target").valueAsString();
				model.add(createMenuItem(" " + title, id, null, action, null, style.getImagePath(icon), true, target,
						null));
			}
		} catch (Exception e) {
		}
	}

	public String noaction() {
		return null;
	}

	public String edit() {
		// Show the proper panel
		DocumentNavigation documentNavigation = ((DocumentNavigation) FacesUtil.accessBeanFromFacesContext(
				"documentNavigation", FacesContext.getCurrentInstance(), log));
		documentNavigation.setSelectedPanel(new PageContentBean("updateDocument"));

		// Now initialize the form
		DocumentEditForm docForm = ((DocumentEditForm) FacesUtil.accessBeanFromFacesContext("documentForm",
				FacesContext.getCurrentInstance(), log));
		docForm.reset();
		docForm.init(this);
		docForm.setReadOnly(false);
		return null;
	}

	/**
	 * Checks if this document has links or is linked
	 */
	public boolean isLinked() {
		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		return docDao.findLinkedDocuments(getDocId(), null, null).size() > 0;
	}

	/**
	 * Executes the checkout and the related document's download
	 */
	public String checkout() {
		initCollections();
		long userId = SessionManagement.getUserId();
		MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		Menu folder = getDocument().getFolder();

		try {
			if (mdao.isWriteEnable(folder.getId(), userId)) {
				if (document.getStatus() == Document.DOC_UNLOCKED) {
					// Create the document history event
					History transaction = new History();
					transaction.setSessionId(SessionManagement.getCurrentUserSessionId());
					transaction.setEvent(History.EVENT_CHECKEDOUT);
					transaction.setComment("");
					transaction.setUser(SessionManagement.getUser());

					DocumentManager documentManager = (DocumentManager) Context.getInstance().getBean(
							DocumentManager.class);
					documentManager.checkout(document.getId(), transaction);
					loadDocument();
					createMenuItems();

					JavascriptContext.addJavascriptCall(FacesContext.getCurrentInstance(), "alert('"
							+ Messages.getMessage("msg.checkout.alert") + "');");
				} else {
					Messages.addLocalizedError("errors.noaccess");
				}
			} else {
				Messages.addLocalizedError("errors.noaccess");
			}
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			Messages.addLocalizedError("errors.error");
		}

		return null;
	}

	/**
	 * Executes the checkin and the related document's download
	 */
	@SuppressWarnings("deprecation")
	public String checkin() {
		initCollections();
		if (SessionManagement.isValid()) {
			// Show the proper panel
			Application application = FacesContext.getCurrentInstance().getApplication();
			DocumentNavigation documentNavigation = ((DocumentNavigation) application.createValueBinding(
					"#{documentNavigation}").getValue(FacesContext.getCurrentInstance()));
			documentNavigation.setSelectedPanel(new PageContentBean("checkin"));

			// Now initialize the edit form
			DocumentEditForm docForm = ((DocumentEditForm) application.createValueBinding("#{documentForm}").getValue(
					FacesContext.getCurrentInstance()));
			docForm.reset();
			docForm.init(this);

			InputFileBean fileForm = ((InputFileBean) application.createValueBinding("#{inputFile}").getValue(
					FacesContext.getCurrentInstance()));
			fileForm.reset();
		} else {
			return "login";
		}

		return null;
	}

	/**
	 * Shows all versions of this document
	 */
	public String versions() {
		initCollections();
		long userId = SessionManagement.getUserId();
		MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		Menu folder = getDocument().getFolder();

		if (SessionManagement.isValid()) {
			try {
				if (mdao.isWriteEnable(folder.getId(), userId)) {
					VersionsRecordsManager versionsManager = ((VersionsRecordsManager) FacesUtil
							.accessBeanFromFacesContext("versionsRecordsManager", FacesContext.getCurrentInstance(),
									log));
					versionsManager.selectDocument(document);

					DocumentNavigation documentNavigation = ((DocumentNavigation) FacesUtil.accessBeanFromFacesContext(
							"documentNavigation", FacesContext.getCurrentInstance(), log));
					documentNavigation.setSelectedPanel(new PageContentBean("versions"));
				} else {
					Messages.addError(Messages.getMessage("errors.noaccess"));
				}
			} catch (Exception ex) {
				log.error(ex.getMessage(), ex);
				Messages.addError(Messages.getMessage("errors.error"));
			}
		} else {
			return "login";
		}

		return null;
	}

	/**
	 * Shows all document links
	 */
	public String links() {
		if (SessionManagement.isValid()) {
			try {
				LinksRecordsManager links = ((LinksRecordsManager) FacesUtil.accessBeanFromFacesContext(
						"linksRecordsManager", FacesContext.getCurrentInstance(), log));
				links.selectDocument(document);

				DocumentNavigation documentNavigation = ((DocumentNavigation) FacesUtil.accessBeanFromFacesContext(
						"documentNavigation", FacesContext.getCurrentInstance(), log));
				documentNavigation.setSelectedPanel(new PageContentBean("links"));
			} catch (Exception ex) {
				log.error(ex.getMessage(), ex);
				Messages.addError(Messages.getMessage("errors.error"));
			}
		} else {
			return "login";
		}

		return null;
	}

	/**
	 * Shows the discussions for this document
	 */
	public String discussions() {
		try {
			DiscussionsManager discussionsManager = ((DiscussionsManager) FacesUtil.accessBeanFromFacesContext(
					"discussionsManager", FacesContext.getCurrentInstance(), log));
			discussionsManager.selectDocument(document);

			DocumentNavigation documentNavigation = ((DocumentNavigation) FacesUtil.accessBeanFromFacesContext(
					"documentNavigation", FacesContext.getCurrentInstance(), log));
			documentNavigation.setSelectedPanel(new PageContentBean("discussions"));
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			Messages.addError(Messages.getMessage("errors.error"));
		}
		return null;
	}

	/**
	 * Shows the history of this document
	 */
	public String history() {
		if (SessionManagement.isValid()) {
			DocumentNavigation documentNavigation = ((DocumentNavigation) FacesUtil.accessBeanFromFacesContext(
					"documentNavigation", FacesContext.getCurrentInstance(), log));
			documentNavigation.setSelectedPanel(new PageContentBean("history"));

			HistoryRecordsManager manager = ((HistoryRecordsManager) FacesUtil.accessBeanFromFacesContext(
					"historyRecordsManager", FacesContext.getCurrentInstance(), log));
			manager.selectDocument(this.getDocument());
		} else {
			return "login";
		}

		return null;
	}

	public String info() {
		// All goes as edit action
		edit();

		// Now initialize the form
		DocumentEditForm docForm = ((DocumentEditForm) FacesUtil.accessBeanFromFacesContext("documentForm",
				FacesContext.getCurrentInstance(), log));
		docForm.init(this);
		docForm.setReadOnly(true);

		return null;
	}

	public String sendAsEmail() {
		// Show the proper panel
		DocumentNavigation documentNavigation = ((DocumentNavigation) FacesUtil.accessBeanFromFacesContext(
				"documentNavigation", FacesContext.getCurrentInstance(), log));
		documentNavigation.setSelectedPanel(new PageContentBean("email"));

		// Now initialize the form
		EMailForm emailForm = ((EMailForm) FacesUtil.accessBeanFromFacesContext("emailForm", FacesContext
				.getCurrentInstance(), log));
		emailForm.reset();
		emailForm.setSelectedDocument(getDocument());
		emailForm.setAuthor(SessionManagement.getUser().getEmail());

		return null;
	}

	public String sendAsTicket() {
		// Show the proper panel
		DocumentNavigation documentNavigation = ((DocumentNavigation) FacesUtil.accessBeanFromFacesContext(
				"documentNavigation", FacesContext.getCurrentInstance(), log));
		documentNavigation.setSelectedPanel(new PageContentBean("email"));

		// Now initialize the form
		EMailForm emailForm = ((EMailForm) FacesUtil.accessBeanFromFacesContext("emailForm", FacesContext
				.getCurrentInstance(), log));
		emailForm.reset();
		emailForm.setAuthor(SessionManagement.getUser().getEmail());
		emailForm.setSelectedDocument(getDocument());

		// Prepare a new download ticket
		long userId = SessionManagement.getUserId();
		String temp = new Date().toString() + userId;
		String ticketid = CryptUtil.cryptString(temp);
		DownloadTicket ticket = new DownloadTicket();
		ticket.setTicketId(ticketid);
		ticket.setDocId(getDocId());
		ticket.setUserId(userId);

		// Store the ticket
		DownloadTicketDAO ticketDao = (DownloadTicketDAO) Context.getInstance().getBean(DownloadTicketDAO.class);
		ticketDao.store(ticket);

		// Try to clean the DB from old tickets
		ticketDao.deleteOlder();

		// Prepare the download link to be shown as email body
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
				.getRequest();
		request.getRequestURL();

		String address = request.getScheme() + "://";
		address += (request.getServerName() + ":");
		address += request.getServerPort();
		address += request.getContextPath();
		address += ("/download-ticket?ticketId=" + ticketid);
		emailForm.setText("URL: " + address);

		return null;
	}

	/**
	 * Links all selected documents to this document
	 */
	public String pasteAsLink() {
		DocumentLinkDAO linkDao = (DocumentLinkDAO) Context.getInstance().getBean(DocumentLinkDAO.class);
		DocumentsRecordsManager documentsRecordsManager = ((DocumentsRecordsManager) FacesUtil
				.accessBeanFromFacesContext("documentsRecordsManager", FacesContext.getCurrentInstance(), log));

		Set<DocumentRecord> clipboard = documentsRecordsManager.getClipboard();
		if (!clipboard.isEmpty()) {
			try {
				for (DocumentRecord record : clipboard) {
					DocumentLink link = linkDao.findByDocIdsAndType(getDocId(), record.getDocId(), "default");
					if (link == null) {
						// The link doesn't exist and must be created
						link = new DocumentLink();
						link.setDocument1(getDocument());
						link.setDocument2(record.getDocument());
						link.setType("default");
						linkDao.store(link);
					}
				}
			} catch (Exception e) {
				Messages.addLocalizedInfo("link.error");
				log.error("Exception linking documents: " + e.getMessage(), e);
			}
			clipboard.clear();
		} else {
			Messages.addLocalizedWarn("clipboard.empty");
		}

		return null;
	}

	public String unlock() {
		Document document = getDocument();
		if (document.getStatus() != Document.DOC_UNLOCKED) {
			try {
				// Create the document history event
				History transaction = new History();
				transaction.setSessionId(SessionManagement.getCurrentUserSessionId());
				transaction.setUser(SessionManagement.getUser());
				
				// Unlock the document; throws an exception if something
				// goes wrong
				DocumentManager documentManager = (DocumentManager) Context.getInstance()
						.getBean(DocumentManager.class);
				documentManager.unlock(document.getId(), transaction);

				loadDocument();
				createMenuItems();

				/* create positive log message */
				Messages.addLocalizedInfo("document.action.unlocked");
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				Messages.addLocalizedError("errors.action.savedoc");
			}
		}
		return null;
	}

	public String lock() {
		Document document = getDocument();
		try {
			// Unlock the document; throws an exception if something
			// goes wrong
			DocumentManager documentManager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
			// Create the document history event
			History transaction = new History();
			transaction.setSessionId(SessionManagement.getCurrentUserSessionId());
			transaction.setEvent(History.EVENT_LOCKED);
			transaction.setUser(SessionManagement.getUser());
			documentManager.lock(document.getId(), Document.DOC_LOCKED, transaction);

			loadDocument();
			createMenuItems();

			/* create positive log message */
			Messages.addLocalizedInfo("document.action.locked");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			Messages.addLocalizedError("errors.action.savedoc");
		}
		return null;
	}

	public String getDownloadTextLink() {
		return "download?docId=" + getDocId() + "&downloadText=true";
	}

	public String getRssLink() {
		return "doc_rss?docId=" + getDocId();
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