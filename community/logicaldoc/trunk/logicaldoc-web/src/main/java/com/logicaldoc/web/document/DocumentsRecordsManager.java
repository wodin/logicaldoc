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

import javax.faces.context.FacesContext;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.icesoft.faces.component.ext.RowSelectorEvent;
import com.logicaldoc.core.document.DocumentLink;
import com.logicaldoc.core.document.DocumentManager;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.UserDoc;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.core.security.dao.UserDocDAO;
import com.logicaldoc.core.transfer.ZipImport;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.SettingsConfig;
import com.logicaldoc.web.SessionManagement;
import com.logicaldoc.web.components.SortableList;
import com.logicaldoc.web.i18n.Messages;
import com.logicaldoc.web.navigation.PageContentBean;
import com.logicaldoc.web.upload.InputFileBean;
import com.logicaldoc.web.util.FacesUtil;

/**
 * <p>
 * The <code>DocumentsRecordsManager</code> class is responsible for
 * constructing the list of <code>DocumentRecord</code> beans which will be
 * bound to a ice:dataTable JSF component. <p/>
 * <p>
 * Large data sets could be handle by adding a ice:dataPaginator. Alternatively
 * the dataTable could also be hidden and the dataTable could be added to
 * scrollable ice:panelGroup.
 * </p>
 * 
 * @author Marco Meschieri
 * @version $Id: DocumentsRecordsManager.java,v 1.1 2007/06/29 06:28:29 marco
 *          Exp $
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
	
	// Set of selected rows
	private Set<DocumentRecord> selection = new HashSet<DocumentRecord>();

	// A clip board of selected documents, used for example in cut&paste
	// operations
	private Set<DocumentRecord> clipboard = new HashSet<DocumentRecord>();

	private boolean selectedAll;

	private int displayedRows = 10;

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
			record = new DocumentRecord(id, documents, CHILD_INDENT_STYLE_CLASS, CHILD_ROW_STYLE_CLASS);
			if (!documents.contains(record)) {
				documents.add(record);
			}
		}
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

	public void refresh() {
		selectedAll = false;
		selectDirectory(selectedDirectory);
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
	 * Deletes all selected documents
	 */
	public String deleteSelected() {
		if (SessionManagement.isValid()) {
			if (!selection.isEmpty()) {
				DocumentManager manager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
				for (DocumentRecord record : selection) {
					try {
						manager.delete(record.getDocId());
						Messages.addLocalizedInfo("msg.action.deleteitem");
					} catch (AccessControlException e) {
						Messages.addLocalizedError("document.write.nopermission");
					} catch (Exception e) {
						Messages.addLocalizedInfo("errors.action.deleteitem");
					}
				}
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
	 * Trims all selected documents
	 */
	public String trimSelected() {
		if (SessionManagement.isValid()) {
			if (!selection.isEmpty()) {
				sourceDirectory = selectedDirectory;
				clipboard.clear();

				for (DocumentRecord record : selection) {
					clipboard.add(record);
				}

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
	 * Paste previously trimmed documents into the current directory
	 */
	public String paste() {
		if (SessionManagement.isValid()) {
			if (!clipboard.isEmpty()) {
				long userId = SessionManagement.getUserId();
				MenuDAO menuDao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
				Menu selectedMenuFolder = menuDao.findById(selectedDirectory);
				DocumentManager docManager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);

				if (menuDao.isWriteEnable(selectedDirectory, userId)) {
					try {
						for (DocumentRecord record : clipboard) {
							docManager.moveToFolder(record.getDocument(), selectedMenuFolder);
						}
					} catch (AccessControlException e) {
						Messages.addLocalizedWarn("document.write.nopermission");
					} catch (Exception e) {
						Messages.addLocalizedInfo("errors.action.movedocument");
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
					Messages.addLocalizedError("document.write.nopermission");
				}

				log.debug("show the upload zip panel");
				documentNavigation.setSelectedPanel(new PageContentBean("zipUpload"));
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
				log.debug("parent.getMenuId() = " + parent.getId());
				log.debug("parent.getMenuPath() = " + parent.getPath());

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

				// Prepare the import thread
				Thread zipImporter = new Thread(new Runnable() {
					public void run() {
						ZipImport importer = new ZipImport();
						importer.setExtractKeywords(inputFile.isExtractKeywords());
						log.debug("importing: = " + destFile.getPath());
						importer.process(destFile.getPath(), zipLanguage, parent, SessionManagement.getUserId());
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
				documentNavigation.setSelectedPanel(new PageContentBean("documents"));
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
					lastdocs.add(new DocumentRecord(userdoc.getDocId(), null, GROUP_INDENT_STYLE_CLASS,
							GROUP_ROW_STYLE_CLASS));
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}

		return lastdocs;
	}

	protected void selectHighlightedDocument(long docId) {
		selection.clear();

		// Iterate of the the list of documents in selected folder, when i find
		// the one I put it on the list of those selected
		for (DocumentRecord document : documents) {
			if (document.getDocId() == docId) {
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
		return displayedRows;
	}

	public void setDisplayedRows(int displayedRows) {
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
				if (column.equals("displayDescription")) {
					return ascending ? c1.getDisplayTitle().compareTo(c2.getDisplayTitle()) : c2.getDisplayTitle()
							.compareTo(c1.getDisplayTitle());
				} else if (column.equals("date")) {
					Date d1 = c1.getSourceDate() != null ? c1.getSourceDate() : new Date(0);
					Date d2 = c2.getSourceDate() != null ? c2.getSourceDate() : new Date(0);
					return ascending ? d1.compareTo(d2) : d2.compareTo(d1);
				} else if (column.equals("size")) {
					Long s1 = new Long(c1.getDocument().getFileSize());
					Long s2 = new Long(c2.getDocument().getFileSize());
					return ascending ? s1.compareTo(s2) : s2.compareTo(s1);
				} else
					return 0;
			}
		};

		Collections.sort(documents, comparator);
	}

	public void setSelectedAll(boolean selectedAll) {
		this.selectedAll = selectedAll;
	}
}
