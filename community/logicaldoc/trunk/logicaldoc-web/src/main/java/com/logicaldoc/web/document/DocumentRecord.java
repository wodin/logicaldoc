package com.logicaldoc.web.document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.icesoft.faces.context.effects.JavascriptContext;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.DocumentLink;
import com.logicaldoc.core.document.DownloadTicket;
import com.logicaldoc.core.document.History;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.document.dao.DocumentLinkDAO;
import com.logicaldoc.core.document.dao.DownloadTicketDAO;
import com.logicaldoc.core.document.dao.HistoryDAO;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.io.CryptUtil;
import com.logicaldoc.web.SessionManagement;
import com.logicaldoc.web.StyleBean;
import com.logicaldoc.web.i18n.Messages;
import com.logicaldoc.web.navigation.MenuBarBean;
import com.logicaldoc.web.navigation.MenuItem;
import com.logicaldoc.web.navigation.PageContentBean;
import com.logicaldoc.web.upload.InputFileBean;
import com.logicaldoc.web.util.FacesUtil;

/**
 * The <code>DocumentRecord</code> class contains the base information for an
 * entry in a data table. This class is meant to represent a model and should
 * only contain base document data <p/>
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

	private String documentPath;

	private long docId;

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
	 * @param parentDocumentsList
	 * @param indentStyleClass
	 * @param rowStyleClass
	 */
	public DocumentRecord(long docId, List<DocumentRecord> parentDocumentsList, String indentStyleClass,
			String rowStyleClass) {
		this.docId = docId;
		if (rowStyleClass != null)
			this.rowStyleClass = rowStyleClass;
	}

	public DocumentRecord() {
		super();
	}

	public Document getDocument() {
		if (document == null)
			loadDocument();
		return document;
	}

	public boolean isDocumentFound() {
		return document != null;
	}

	public long getSize() {
		return (long) ((double) getDocument().getFileSize() / (double) 1024);
	}

	public long getDocId() {
		return docId;
	}

	public String getCustomId() {
		return document.getCustomId();
	}

	public Date getCreationDate() {
		return document.getCreation();
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
		return StringUtils.abbreviate(getDocument().getTitle(), 68);
	}

	public String getDisplayFilename() {
		return StringUtils.abbreviate(getDocument().getFileName(), 68);
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
		if (getDocument() == null || arg0 == null)
			return false;
		return document.getId() == ((DocumentRecord) arg0).getDocument().getId();
	}

	public String getDocumentPath() {
		if (StringUtils.isEmpty(documentPath)) {
			try {
				Menu folder = getDocument().getFolder();
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
	@SuppressWarnings("unchecked")
	protected void createMenuItems() {
		model.clear();
		long userId = SessionManagement.getUserId();
		MenuDAO menuDAO = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);

		Menu folder = getDocument().getFolder();
		Document document = getDocument();
		StyleBean style = (StyleBean) Context.getInstance().getBean(StyleBean.class);
		if ((menuDAO.isWriteEnable(folder.getId(), userId)) && (document.getImmutable() == 0)) {

			if ((document.getStatus() == Document.DOC_CHECKED_OUT)
					&& (document.getCheckoutUserId().equals(new Long(userId)))) {
				model.add(createMenuItem(" " + Messages.getMessage("checkin"), "checkin-" + folder.getId(), null,
						"#{documentRecord.checkin}", null, style.getImagePath("checkin.png"), true, null, null));
			} else if (document.getStatus() == Document.DOC_CHECKED_IN) {
				model.add(createMenuItem(" " + Messages.getMessage("checkout"), "checkout-" + folder.getId(), null,
						"#{documentRecord.checkout}", null, style.getImagePath("checkout.png"), true, null, null));
			}

			model.add(createMenuItem(" " + Messages.getMessage("edit"), "pastelink-" + folder.getId(), null,
					"#{documentRecord.edit}", null, style.getImagePath("document_edit.png"), true, null, null));

			model.add(createMenuItem(" " + Messages.getMessage("link.pasteas"), "edit-" + folder.getId(), null,
					"#{documentRecord.pasteAsLink}", null, style.getImagePath("pastelink.png"), true, null, null));
		}

		model.add(createMenuItem(" " + Messages.getMessage("msg.jsp.versions"), "versions-" + folder.getId(), null,
				"#{documentRecord.versions}", null, style.getImagePath("versions.png"), true, "_blank", null));
		model.add(createMenuItem(" " + Messages.getMessage("msg.jsp.similardocs"), "similar-" + folder.getId(), null,
				"#{searchForm.searchSimilar}", null, style.getImagePath("similar.png"), true, "_blank", null));
		model.add(createMenuItem(" " + Messages.getMessage("links"), "linked-" + folder.getId(), null,
				"#{documentRecord.links}", null, style.getImagePath("link.png"), true, "_blank", null));
		model.add(createMenuItem(" " + Messages.getMessage("msg.jsp.discuss"), "articles-" + folder.getId(), null,
				"#{documentRecord.articles}", null, style.getImagePath("comments.png"), true, "_blank", null));
		model.add(createMenuItem(" " + Messages.getMessage("msg.jsp.sendasemail"), "sendasmail-" + folder.getId(),
				null, "#{documentRecord.sendAsEmail}", null, style.getImagePath("editmail.png"), true, "_blank", null));
		model.add(createMenuItem(" " + Messages.getMessage("msg.jsp.sendticket"), "sendticket-" + folder.getId(), null,
				"#{documentRecord.sendAsTicket}", null, style.getImagePath("ticket.png"), true, "_blank", null));
		model.add(createMenuItem(" " + Messages.getMessage("info"), "info-" + folder.getId(), null,
				"#{documentRecord.info}", null, style.getImagePath("info.png"), true, "_blank", null));
		model.add(createMenuItem(" " + Messages.getMessage("history"), "history-" + folder.getId(), null,
				"#{documentRecord.history}", null, style.getImagePath("history.png"), true, "_blank", null));
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
		return docDao.findLinkedDocuments(docId, null, null).size() > 0;
	}

	/**
	 * Executes the checkout and the related document's download
	 */
	public String checkout() {
		initCollections();
		long userId = SessionManagement.getUserId();
		DocumentDAO ddao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		Menu folder = getDocument().getFolder();

		if (SessionManagement.isValid()) {
			try {
				if (mdao.isWriteEnable(folder.getId(), userId)) {
					if (document.getStatus() == Document.DOC_CHECKED_IN) {
						document.setCheckoutUserId(userId);
						document.setStatus(Document.DOC_CHECKED_OUT);
						ddao.store(document);

						/* create historycheckout entry */
						History history = new History();
						history.setDocId(document.getId());
						history.setDate(new Date());
						history.setUserId(userId);
						history.setUserName(SessionManagement.getUser().getFullName());
						history.setEvent(History.CHECKOUT);

						HistoryDAO historyDAO = (HistoryDAO) Context.getInstance().getBean(HistoryDAO.class);
						historyDAO.store(history);

						JavascriptContext.addJavascriptCall(FacesContext.getCurrentInstance(), "alert('"
								+ Messages.getMessage("msg.checkout.alert") + "');");

						try {
							StyleBean style = (StyleBean) Context.getInstance().getBean(StyleBean.class);
							// create a new menu to replace the checkout
							MenuItem checkinMenuItem = createMenuItem(Messages.getMessage("checkin"), "checkin-"
									+ document.getId(), null, "#{documentRecord.checkin}", null, style
									.getImagePath("checkin.png"), true, null, null);
							// replacing the old menu at the same index
							model.set(0, checkinMenuItem);
						} catch (Throwable e) {
						}

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
		} else {
			return "login";
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
	 * Shows the articles for this document
	 */
	public String articles() {
		if (SessionManagement.isValid()) {
			try {
				ArticlesRecordsManager articlesManager = ((ArticlesRecordsManager) FacesUtil
						.accessBeanFromFacesContext("articlesRecordsManager", FacesContext.getCurrentInstance(), log));
				articlesManager.selectDocument(document);

				DocumentNavigation documentNavigation = ((DocumentNavigation) FacesUtil.accessBeanFromFacesContext(
						"documentNavigation", FacesContext.getCurrentInstance(), log));
				documentNavigation.setSelectedPanel(new PageContentBean("articles"));
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
		emailForm.setSelectedDocument(getDocument());
		emailForm.setAuthor(SessionManagement.getUser().getEmail());
		emailForm.setSelectedDocument(null);

		long userId = SessionManagement.getUserId();
		String temp = new Date().toString() + userId;
		String ticketid = CryptUtil.cryptString(temp);
		DownloadTicket ticket = new DownloadTicket();
		ticket.setTicketId(ticketid);
		ticket.setDocId(docId);
		ticket.setUserId(userId);

		DownloadTicketDAO ticketDao = (DownloadTicketDAO) Context.getInstance().getBean(DownloadTicketDAO.class);
		ticketDao.store(ticket);

		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
				.getRequest();
		request.getRequestURL();

		String address = "http://";
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

	/**
	 * Utility method used by document lazy loading
	 */
	protected void loadDocument() {
		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		this.document = docDao.findById(docId);
		if (this.document == null || this.document.getDeleted() == 1)
			this.document = new Document();
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
}