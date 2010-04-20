package com.logicaldoc.web.document;

import java.io.File;
import java.io.IOException;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.faces.component.UIData;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.icesoft.faces.component.ext.HtmlCommandLink;
import com.icesoft.faces.component.ext.RowSelectorEvent;
import com.icesoft.faces.context.effects.JavascriptContext;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.DocumentManager;
import com.logicaldoc.core.document.History;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.UserDoc;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.core.security.dao.UserDocDAO;
import com.logicaldoc.core.transfer.InMemoryZipImport;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.LocaleUtil;
import com.logicaldoc.util.config.SettingsConfig;
import com.logicaldoc.web.SessionManagement;
import com.logicaldoc.web.admin.GuiBean;
import com.logicaldoc.web.components.SortableList;
import com.logicaldoc.web.i18n.Messages;
import com.logicaldoc.web.navigation.PageContentBean;
import com.logicaldoc.web.upload.InputFileBean;
import com.logicaldoc.web.util.FacesUtil;

/**
 * <p>
 * The <code>DocumentsRecordsManager</code> class is responsible for
 * constructing the list of <code>DocumentRecord</code> beans which will be
 * bound to a ice:dataTable JSF component.
 * <p/>
 * <p>
 * Large data sets could be handle by adding a ice:dataPaginator. Alternatively
 * the dataTable could also be hidden and the dataTable could be added to
 * scrollable ice:panelGroup.
 * </p>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.0
 */
public class DocumentsRecordsManager extends SortableList {

	protected static Log log = LogFactory.getLog(DocumentsRecordsManager.class);

	// css style related constants
	public static final String GROUP_INDENT_STYLE_CLASS = "groupRowIndentStyle";

	public static final String GROUP_ROW_STYLE_CLASS = "groupRowStyle";

	public static final String CHILD_INDENT_STYLE_CLASS = "childRowIndentStyle";

	public static final String CHILD_ROW_STYLE_CLASS = "childRowStyle";

	// toggle for expand contract
	public static final String CONTRACT_IMAGE = "contract.png";

	public static final String EXPAND_IMAGE = "expand.png";

	private ArrayList<DocumentRecord> documents;

	private boolean multipleSelection = true;

	private long selectedDirectory;

	private long sourceDirectory;

	private String operationComment = "";

	// Set of selected rows
	private Set<DocumentRecord> selection = new HashSet<DocumentRecord>();

	// A clip board of selected documents, used for example in cut&paste
	// operations
	private Set<DocumentRecord> clipboard = new HashSet<DocumentRecord>();

	private boolean selectedAll;

	private int displayedRows;

	// The requested operation, can be: cut or copy
	private String guiRequest;

	private UIData table;

	private UIData panels;

	public DocumentsRecordsManager() {
		// We don't sort by default
		super("xxx");
		selectDirectory(Menu.MENUID_DOCUMENTS);
		selection.clear();
		clipboard.clear();
	}

	/**
	 * Changes the currently selected directory and updates the documents list.
	 * 
	 * @param directoryId
	 */
	public void selectDirectory(long directoryId) {
		this.table = null;
		this.panels = null;
		selectedDirectory = directoryId;
		selection.clear();

		// initiate the list
		if (documents != null) {
			documents.clear();
		} else {
			documents = new ArrayList<DocumentRecord>(10);
		}

		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Collection<Long> docIds = docDao.findDocIdByFolder(directoryId);

		for (Long id : docIds) {
			DocumentRecord record;
			record = new DocumentRecord(id, CHILD_INDENT_STYLE_CLASS, CHILD_ROW_STYLE_CLASS);
			if (!documents.contains(record)) {
				documents.add(record);
			}
		}
	}

	public String getOperationComment() {
		return operationComment;
	}

	public void setOperationComment(String operationComment) {
		this.operationComment = operationComment;
	}

	/**
	 * Cleans up the resources used by this class. This method could be called
	 * when a session destroyed event is called.
	 */
	public void dispose() {
		documents.clear();
	}

	/**
	 * Gets the list of DocumentRecord which will be used by the ice:dataTable
	 * component.
	 * 
	 * @return array list of parent DocumentRecord
	 */
	public List<DocumentRecord> getDocuments() {
		return documents;
	}

	public boolean isMultipleSelection() {
		return multipleSelection;
	}

	public Set<DocumentRecord> getClipboard() {
		return clipboard;
	}

	public void setMultipleSelection(boolean multiple) {
		this.multipleSelection = multiple;
	}

	public void selectRow(RowSelectorEvent e) {
		DocumentRecord record = documents.get(e.getRow());

		if (e.isSelected() || !selection.contains(record)) {
			selection.add(record);
		} else if (!e.isSelected() || selection.contains(record)) {
			selection.remove(record);
		}
	}

	public void selectRow(ActionEvent event) {
		HtmlCommandLink hcl = (HtmlCommandLink) event.getComponent();
		Long docId = (Long) hcl.getValue();
		DocumentRecord record = findDRinDocuments(docId);
		if (record != null) {
			record.setSelected(true);
			selection.add(record);
		}
	}

	public void unselectRow(ActionEvent event) {
		HtmlCommandLink hcl = (HtmlCommandLink) event.getComponent();
		Long docId = (Long) hcl.getValue();
		DocumentRecord record = findDRinDocuments(docId);
		if (record != null) {
			record.setSelected(false);
			selection.remove(record);
		}
	}

	public DocumentRecord findDRinDocuments(long docId) {
		for (DocumentRecord drI : documents) {
			if (drI.getDocId() == docId) {
				return drI;
			}
		}
		return null;
	}

	public void refresh() {
		selectedAll = false;
		selectDirectory(selectedDirectory);
	}

	public void refresh(long docId) {
		DocumentRecord oldRecord = null;
		for (DocumentRecord record : documents) {
			if (record.getDocId() == docId) {
				oldRecord = record;
			}
		}
		if (oldRecord != null) {
			DocumentRecord newRecord = new DocumentRecord(docId, CHILD_INDENT_STYLE_CLASS, CHILD_ROW_STYLE_CLASS);
			int index = documents.indexOf(oldRecord);
			documents.remove(index);
			documents.add(index, newRecord);
		}
	}

	public int getClipboardSize() {
		return clipboard.size();
	}

	public int getCount() {
		if (documents == null) {
			return 0;
		} else {
			return documents.size();
		}
	}

	/**
	 * Deletes all selected documents. Only not immutable and not locked
	 * document can be deleted.
	 */
	public String deleteSelected() {
		if (SessionManagement.isValid()) {
			if (!selection.isEmpty()) {
				DocumentDAO dao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
				boolean skippedSome = false;
				boolean deletedSome = false;
				boolean lockedSome = false;
				for (DocumentRecord record : selection) {
					try {
						// Create the document history event
						History transaction = new History();
						transaction.setSessionId(SessionManagement.getCurrentUserSessionId());
						transaction.setEvent(History.EVENT_DELETED);
						transaction.setComment("");
						transaction.setUserId(SessionManagement.getUserId());
						transaction.setUserName(SessionManagement.getUser().getFullName());

						// If it is a shortcut, we delete only the shortcut
						if (record.getShortcut() != null) {
							transaction.setEvent(History.EVENT_SHORTCUT_DELETED);
							dao.delete(record.getShortcut().getId(), transaction);
							deletedSome = true;
							continue;
						}

						// The document of the selected documentRecord must be
						// not immutable
						if (record.getDocument().getImmutable() == 1) {
							skippedSome = true;
							continue;
						}
						// The document of the selected documentRecord must be
						// not locked
						if (record.getDocument().getStatus() != Document.DOC_UNLOCKED
								|| record.getDocument().getExportStatus() != Document.EXPORT_UNLOCKED) {
							lockedSome = true;
							continue;
						}
						// Check if there are some shortcuts associated to the
						// deleting document. All the shortcuts must be deleted.
						if (dao.findShortcutIds(record.getDocId()).size() > 0)
							for (Long shortcutId : dao.findShortcutIds(record.getDocId())) {
								dao.delete(shortcutId);
							}
						dao.delete(record.getDocId(), transaction);
						deletedSome = true;
					} catch (AccessControlException e) {
						Messages.addLocalizedWarn("document.write.nopermission");
					} catch (Exception e) {
						Messages.addLocalizedError("errors.action.deleteitem");
					}
				}
				if (deletedSome)
					Messages.addLocalizedInfo("msg.action.deleteitem");
				if (skippedSome || lockedSome)
					Messages.addLocalizedWarn("document.delete.warn");
				refresh();
			} else {
				Messages.addLocalizedWarn("noselection");
			}

			return null;
		} else {
			return "login";
		}
	}

	/**
	 * Shows the document immutability form
	 */
	public String requestImmutabilityComment() {
		log.debug("start document immutability marking");

		DocumentNavigation documentNavigation = ((DocumentNavigation) FacesUtil.accessBeanFromFacesContext(
				"documentNavigation", FacesContext.getCurrentInstance(), log));
		operationComment = "";
		boolean documentNotImmutable = false;
		if (SessionManagement.isValid()) {
			if (!selection.isEmpty()) {
				try {
					for (DocumentRecord record : selection) {
						if (record.getDocument().getImmutable() == 0) {
							documentNotImmutable = true;
							continue;
						}
					}
					if (documentNotImmutable) {
						log.debug("show the immutability panel");
						documentNavigation.setSelectedPanel(new PageContentBean("immutability"));
					} else
						Messages.addLocalizedError("error");
				} catch (AccessControlException e) {
					Messages.addLocalizedWarn("document.write.nopermission");
				} catch (Exception e) {
					Messages.addLocalizedInfo("error");
				}
			} else {
				Messages.addLocalizedWarn("noselection");
			}

			return null;
		} else {
			return "login";
		}
	}

	/**
	 * Marks as immutable all selected documents
	 */
	public String markSelectionAsImmutable() {
		if (SessionManagement.isValid()) {
			try {
				DocumentManager manager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
				boolean lockedSome = false;
				boolean immutableSome = false;
				for (DocumentRecord record : selection) {
					if (record.getDocument().getImmutable() == 0) {
						// The document of the selected documentRecord must be
						// not locked
						if (record.getDocument().getStatus() != Document.DOC_UNLOCKED
								|| record.getDocument().getExportStatus() != Document.EXPORT_UNLOCKED) {
							lockedSome = true;
							continue;
						}

						// Create the document history event
						History transaction = new History();
						transaction.setSessionId(SessionManagement.getCurrentUserSessionId());
						transaction.setComment(operationComment);
						transaction.setUser(SessionManagement.getUser());

						manager.makeImmutable(record.getDocId(), transaction);
						immutableSome = true;
					}
				}
				if (immutableSome)
					Messages.addLocalizedInfo("document.immutable.message");
				if (lockedSome)
					Messages.addLocalizedWarn("document.immutable.warn");
				refresh();

				DocumentNavigation documentNavigation = ((DocumentNavigation) FacesUtil.accessBeanFromFacesContext(
						"documentNavigation", FacesContext.getCurrentInstance(), log));
				documentNavigation.refresh();
				documentNavigation.showDocuments();
			} catch (Throwable e) {
				log.error(e.getMessage(), e);
				Messages.addError(e.getMessage());
			}
		} else {
			return "login";
		}

		return null;
	}

	/**
	 * Places all selected documents into the clipboard and set the guiRequest
	 * to "cut"
	 */
	public String cutToClipboard() {
		String xxx = copyToClipboard();
		this.guiRequest = "cut";
		return xxx;
	}

	/**
	 * Places all selected documents into the clipboard and set the guiRequest
	 * to "copy"
	 */
	public String copyToClipboard() {
		if (SessionManagement.isValid()) {
			if (!selection.isEmpty()) {
				sourceDirectory = selectedDirectory;
				clipboard.clear();

				for (DocumentRecord record : selection) {
					clipboard.add(record);
				}
				this.guiRequest = "copy";
				refresh();
			} else {
				Messages.addLocalizedWarn("noselection");
			}

			return null;
		} else {
			return "login";
		}
	}

	/**
	 * Move or copy documents inside the clipboard into the current directory
	 * checking if the user have clicked on the 'cut' button or on the 'copy'
	 * button
	 */
	public void move() {
		if (this.guiRequest.equals("cut"))
			moveToFolder();
		else if (this.guiRequest.equals("copy"))
			copyToFolder();
	}

	/**
	 * Move documents inside the clipboard into the current directory
	 */
	public String moveToFolder() {
		if (SessionManagement.isValid()) {
			if (!clipboard.isEmpty()) {
				long userId = SessionManagement.getUserId();
				MenuDAO menuDao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
				Menu selectedMenuFolder = menuDao.findById(selectedDirectory);
				DocumentManager docManager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);

				if (menuDao.isWriteEnable(selectedDirectory, userId)) {
					try {
						boolean skippedSome = false;
						boolean lockedSome = false;
						for (DocumentRecord record : clipboard) {
							// Create the document history event
							History transaction = new History();
							transaction.setSessionId(SessionManagement.getCurrentUserSessionId());
							transaction.setUser(SessionManagement.getUser());

							if (record.getShortcut() != null) {
								if (record.getDocument().getFolder().getId() != selectedMenuFolder.getId()) {
									transaction.setEvent(History.EVENT_SHORTCUT_MOVED);
									docManager.moveToFolder(record.getShortcut(), selectedMenuFolder, transaction);
								} else
									Messages.addLocalizedWarn("warn.action.moveshortcut");
								continue;
							}
							// The document of the selected documentRecord
							// must
							// be not immutable
							if (record.getDocument().getImmutable() == 1) {
								skippedSome = true;
								continue;
							}
							// The document of the selected documentRecord
							// must
							// be not locked
							if (record.getDocument().getStatus() != Document.DOC_UNLOCKED
									|| record.getDocument().getExportStatus() != Document.EXPORT_UNLOCKED) {
								lockedSome = true;
								continue;
							}

							docManager.moveToFolder(record.getDocument(), selectedMenuFolder, transaction);
						}
						if (skippedSome || lockedSome)
							Messages.addLocalizedWarn("document.paste.warn");
					} catch (AccessControlException e) {
						Messages.addLocalizedWarn("document.write.nopermission");
					} catch (Exception e) {
						Messages.addLocalizedError("errors.action.movedocument");
						log.error("Exception moving document: " + e.getMessage(), e);
					}
					clipboard.clear();
				} else {
					Messages.addLocalizedWarn("document.write.nopermission");
				}
				refresh();
			}

			return null;
		} else {
			return "login";
		}
	}

	/**
	 * Copy documents inside the clipboard into the current directory
	 */
	public String copyToFolder() {
		if (SessionManagement.isValid()) {
			if (!clipboard.isEmpty()) {
				long userId = SessionManagement.getUserId();
				MenuDAO menuDao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
				Menu selectedMenuFolder = menuDao.findById(selectedDirectory);
				DocumentManager docManager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);

				if (menuDao.isWriteEnable(selectedDirectory, userId)) {
					try {
						for (DocumentRecord record : clipboard) {
							// Create the document history event
							History transaction = new History();
							transaction.setSessionId(SessionManagement.getCurrentUserSessionId());
							transaction.setEvent(History.EVENT_STORED);
							transaction.setComment("");
							transaction.setUser(SessionManagement.getUser());

							if (record.getShortcut() == null) {
								docManager.copyToFolder(record.getDocument(), selectedMenuFolder, transaction);
							} else {
								if (record.getDocument().getFolder().getId() != selectedMenuFolder.getId())
									docManager.copyToFolder(record.getShortcut(), selectedMenuFolder, transaction);
								else
									Messages.addLocalizedWarn("warn.action.moveshortcut");
							}
						}
					} catch (AccessControlException e) {
						Messages.addLocalizedWarn("document.write.nopermission");
					} catch (Exception e) {
						Messages.addLocalizedError("errors.action.copydocument");
						log.error("Exception copying document: " + e.getMessage(), e);
					}
					clipboard.clear();
				} else {
					Messages.addLocalizedWarn("document.write.nopermission");
				}
				refresh();
			}

			return null;
		} else {
			return "login";
		}
	}

	/**
	 * Shows the zip upload form
	 */
	public String startZipUpload() {
		log.debug("startZipUpload");

		DocumentNavigation documentNavigation = ((DocumentNavigation) FacesUtil.accessBeanFromFacesContext(
				"documentNavigation", FacesContext.getCurrentInstance(), log));

		if (SessionManagement.isValid()) {
			log.debug("session is valid");
			try {
				try {
					// Clean the upload directory
					InputFileBean inputFile = ((InputFileBean) FacesUtil.accessBeanFromFacesContext("inputFile",
							FacesContext.getCurrentInstance(), log));
					inputFile.reset();
				} catch (RuntimeException e) {
					log.info("catched Exception deleting old upload file: " + e.getMessage(), e);
				}

				MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
				long userId = SessionManagement.getUserId();
				Directory dir = documentNavigation.getSelectedDir();
				long folderId = dir.getMenuId();

				if (mdao.isWriteEnable(folderId, userId)) {
					log.debug("mdao.isWriteEnabled");
					documentNavigation.setSelectedPanel(new PageContentBean("zipImport"));
				} else {
					log.debug("no permission to upload");
					Messages.addLocalizedWarn("document.write.nopermission");
				}

				log.debug("show the upload zip panel");
				documentNavigation.setSelectedPanel(new PageContentBean("uploadZip"));
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				Messages.addError(e.getMessage());
			}
		} else {
			return "login";
		}

		return null;
	}

	/**
	 * Imports the zip content in the current directory
	 */
	public String uploadZip() {
		if (SessionManagement.isValid()) {
			try {
				final String username = SessionManagement.getUsername();

				final InputFileBean inputFile = ((InputFileBean) FacesUtil.accessBeanFromFacesContext("inputFile",
						FacesContext.getCurrentInstance(), log));

				File file = inputFile.getFile();
				log.debug("file = " + file);
				final String zipLanguage = inputFile.getLanguage();

				MenuDAO menuDao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
				final Menu parent = menuDao.findById(selectedDirectory);

				SettingsConfig conf = (SettingsConfig) Context.getInstance().getBean(SettingsConfig.class);
				String path = conf.getValue("userdir");

				if (!path.endsWith("_")) {
					path += "_";
				}
				path += username + "_" + File.separator;
				log.debug("path = " + path);

				FileUtils.forceMkdir(new File(path));

				// copy the file into the user folder
				final File destFile = new File(path, file.getName());
				log.debug("destFile = " + destFile);
				FileUtils.copyFile(file, destFile);

				// delete the original file from the upload directory
				inputFile.deleteUploadDir();

				final long userId = SessionManagement.getUserId();
				final String sessionId = SessionManagement.getCurrentUserSessionId();
				// Prepare the import thread
				Thread zipImporter = new Thread(new Runnable() {
					public void run() {
						InMemoryZipImport importer = new InMemoryZipImport();

						String encoding = inputFile.getEncoding();

						importer.setExtractTags(inputFile.isExtractTags());
						importer.setTags(inputFile.getTags());
						log.debug("importing: = " + destFile.getPath());
						importer.process(destFile, LocaleUtil.toLocale(zipLanguage), parent, userId, inputFile
								.getTemplate(), encoding, sessionId);
						try {
							FileUtils.forceDelete(destFile);
						} catch (IOException e) {
							log.error("Unable to delete " + destFile, e);
						}
					}
				});

				// And launch it
				zipImporter.start();

				Messages.addLocalizedInfo("msg.action.importfolder");

				DocumentNavigation documentNavigation = ((DocumentNavigation) FacesUtil.accessBeanFromFacesContext(
						"documentNavigation", FacesContext.getCurrentInstance(), log));
				documentNavigation.refresh();
				documentNavigation.showDocuments();
			} catch (Throwable e) {
				log.error(e.getMessage(), e);
				Messages.addError(e.getMessage());
			}
		} else {
			return "login";
		}

		return null;
	}

	/**
	 * Copy documents inside the clipboard into the current directory as alias
	 */
	public String pasteAsShortcut() {
		if (SessionManagement.isValid()) {
			if (!clipboard.isEmpty()) {
				long userId = SessionManagement.getUserId();
				MenuDAO menuDao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
				Menu selectedMenuFolder = menuDao.findById(selectedDirectory);
				DocumentManager docManager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);

				if (menuDao.isWriteEnable(selectedDirectory, userId)) {
					try {
						for (DocumentRecord record : clipboard) {
							// Create the document history event
							History transaction = new History();
							transaction.setSessionId(SessionManagement.getCurrentUserSessionId());
							transaction.setEvent(History.EVENT_STORED);
							transaction.setComment(Messages.getMessage("document.shortcut.comment"));
							transaction.setUser(SessionManagement.getUser());

							docManager.createShortcut(record.getDocument(), selectedMenuFolder, transaction);
						}
					} catch (AccessControlException e) {
						Messages.addLocalizedWarn("document.write.nopermission");
					} catch (Exception e) {
						Messages.addLocalizedError("errors.action.createshortcut");
						log.error("Exception creating the shortcut: " + e.getMessage(), e);
					}
					clipboard.clear();
				} else {
					Messages.addLocalizedWarn("document.write.nopermission");
				}
				refresh();
			}

			return null;
		} else {
			return "login";
		}
	}

	/**
	 * Retrieves the list of last accessed documents from the database
	 */
	public List<DocumentRecord> getLastDocs() {
		List<DocumentRecord> lastdocs = new ArrayList<DocumentRecord>();

		if (SessionManagement.isValid()) {
			try {
				long userId = SessionManagement.getUserId();
				UserDocDAO uddao = (UserDocDAO) Context.getInstance().getBean(UserDocDAO.class);
				Collection<UserDoc> userdocs = uddao.findByUserId(userId);
				Iterator<UserDoc> iter = userdocs.iterator();

				while (iter.hasNext()) {
					UserDoc userdoc = iter.next();
					lastdocs
							.add(new DocumentRecord(userdoc.getDocId(), GROUP_INDENT_STYLE_CLASS, GROUP_ROW_STYLE_CLASS));
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}

		return lastdocs;
	}

	public String createBookmarks() {
		return null;
	}

	protected void selectHighlightedDocument(long docId) {
		selection.clear();

		// Iterate of the the list of documents in selected folder, when I find
		// the one I put it on the list of those selected
		for (DocumentRecord document : documents) {
			if (document.getDocId() == docId) {
				document.setSelected(true);
				selection.add(document);
				break;
			} else if (document.getShortcut() != null && document.getShortcut().getId() == docId) {
				document.setSelected(true);
				selection.add(document);
				break;
			}
		}

		// Make the selected document the first
		if (!selection.isEmpty()) {
			DocumentRecord selectedDoc = selection.iterator().next();
			documents.remove(selectedDoc);
			documents.add(0, selectedDoc);
		}
	}

	public String selectAll() {
		for (DocumentRecord document : documents) {
			document.setSelected(true);
			selection.add(document);
		}
		selectedAll = true;

		return null;
	}

	public String unselectAll() {

		for (DocumentRecord document : documents) {
			document.setSelected(false);
		}
		if (selection != null)
			selection.clear();
		selectedAll = false;

		return null;
	}

	class DocumentRecordSelectedComparator implements Comparator<DocumentRecord> {
		public int compare(DocumentRecord arg0, DocumentRecord arg1) {
			return new Boolean(arg0.isSelected()).compareTo(new Boolean(arg1.isSelected()));
		}
	}

	public boolean isSelectedAll() {
		return selectedAll;
	}

	public int getDisplayedRows() {
		if (displayedRows == 0) {
			GuiBean guiBean = ((GuiBean) FacesUtil.accessBeanFromFacesContext("guiBean", FacesContext
					.getCurrentInstance(), log));
			displayedRows = guiBean.getPageSize();
		}
		return displayedRows;
	}

	public void setDisplayedRows(int displayedRows) {
		if (displayedRows != this.displayedRows)
			JavascriptContext.addJavascriptCall(FacesContext.getCurrentInstance(), "window.location.reload(false);");
		this.displayedRows = displayedRows;
	}

	/**
	 * Determines the sort order.
	 * 
	 * @param sortColumn to sort by.
	 * @return whether sort order is ascending or descending.
	 */
	@Override
	protected boolean isDefaultAscending(String sortColumn) {
		return true;
	}

	/**
	 * Sorts the list of DocumentRecord data.
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void sort(final String column, final boolean ascending) {
		log.debug("invoked DocumentsRecordsManager.sort()");
		log.debug("sort on column: " + column);
		log.debug("sort ascending: " + ascending);
		Comparator comparator = new Comparator() {
			public int compare(Object o1, Object o2) {

				DocumentRecord c1 = (DocumentRecord) o1;
				DocumentRecord c2 = (DocumentRecord) o2;
				if (column == null) {
					return 0;
				}
				if (column.equals("title")) {
					return ascending ? c1.getDisplayTitle().compareTo(c2.getDisplayTitle()) : c2.getDisplayTitle()
							.compareTo(c1.getDisplayTitle());
				} else if (column.equals("lastmodified")) {
					Date d1 = c1.getLastModified() != null ? c1.getLastModified() : new Date(0);
					Date d2 = c2.getLastModified() != null ? c2.getLastModified() : new Date(0);
					return ascending ? d1.compareTo(d2) : d2.compareTo(d1);
				} else if (column.equals("publishedby")) {
					String publisher1 = c1.getDocument().getPublisher() != null ? c1.getDocument().getPublisher()
							.toLowerCase() : "";
					String publisher2 = c2.getDocument().getPublisher() != null ? c2.getDocument().getPublisher()
							.toLowerCase() : "";
					return ascending ? publisher1.compareTo(publisher2) : publisher2.compareTo(publisher1);
				} else if (column.equals("size")) {
					Long s1 = new Long(c1.getDocument().getFileSize());
					Long s2 = new Long(c2.getDocument().getFileSize());
					return ascending ? s1.compareTo(s2) : s2.compareTo(s1);
				} else if (column.equals("customid")) {
					String id1 = c1.getCustomId() != null ? c1.getCustomId() : "";
					String id2 = c2.getCustomId() != null ? c2.getCustomId() : "";
					return ascending ? id1.compareTo(id2) : id2.compareTo(id1);
				} else if (column.equals("version")) {
					String version1 = c1.getDocument().getVersion() != null ? c1.getDocument().getVersion()
							.toLowerCase() : "";
					String version2 = c2.getDocument().getVersion() != null ? c2.getDocument().getVersion()
							.toLowerCase() : "";
					return ascending ? version1.compareTo(version2) : version2.compareTo(version1);
				} else
					return 0;
			}
		};

		Collections.sort(documents, comparator);
	}

	public void setSelectedAll(boolean selectedAll) {
		this.selectedAll = selectedAll;
	}

	public String back() {
		DocumentNavigation documentNavigation = ((DocumentNavigation) FacesUtil.accessBeanFromFacesContext(
				"documentNavigation", FacesContext.getCurrentInstance(), log));
		documentNavigation.showDocuments();
		return null;
	}

	public Set<DocumentRecord> getSelection() {
		return selection;
	}

	public String getGuiRequest() {
		return guiRequest;
	}

	public UIData getPanels() {
		return panels;
	}

	public void setPanels(UIData panels) {
		this.panels = panels;
	}

	public UIData getTable() {
		return table;
	}

	public void setTable(UIData table) {
		this.table = table;
	}
}