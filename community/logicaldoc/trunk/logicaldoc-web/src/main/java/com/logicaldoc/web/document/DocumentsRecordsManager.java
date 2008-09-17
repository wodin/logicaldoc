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
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.DocumentManager;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.MenuManager;
import com.logicaldoc.core.security.UserDoc;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.core.security.dao.UserDocDAO;
import com.logicaldoc.core.transfer.ZipImport;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.SettingsConfig;

import com.icesoft.faces.component.ext.RowSelectorEvent;
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

	private int selectedDirectory;

	private int sourceDirectory;

	// Set of selected rows
	private Set<DocumentRecord> selection = new HashSet<DocumentRecord>();

	// A clip board of selected documents, used for example in cut&paste
	// operations
	private Set<DocumentRecord> clipboard = new HashSet<DocumentRecord>();

	private Comparator drsc = new DocumentRecordSelectedComparator();

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
	public void selectDirectory(int directoryId) {
		selectedDirectory = directoryId;
		selection.clear();

		// initiate the list
		if (documents != null) {
			documents.clear();
		} else {
			documents = new ArrayList<DocumentRecord>(10);
		}

		String username = SessionManagement.getUsername();
		MenuDAO menuDao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		Collection<Integer> menuIds = menuDao.findMenuIdByUserName(username, directoryId, Menu.MENUTYPE_FILE);

		for (Integer id : menuIds) {
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
				MenuManager manager = (MenuManager) Context.getInstance().getBean(MenuManager.class);

				String username = SessionManagement.getUsername();
				for (DocumentRecord record : selection) {
					try {
						manager.deleteMenu(record.getMenu(), username);
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
				String username = SessionManagement.getUsername();
				MenuDAO menuDao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);

				SettingsConfig settings = (SettingsConfig) Context.getInstance().getBean(SettingsConfig.class);

				if (menuDao.isWriteEnable(selectedDirectory, username)) {
					try {
						DocumentNavigation navigation = ((DocumentNavigation) FacesUtil.accessBeanFromFacesContext(
								"documentNavigation", FacesContext.getCurrentInstance(), log));

						for (DocumentRecord record : clipboard) {
							Menu menu = menuDao.findByPrimaryKey(record.getMenuId());

							if (!menuDao.isWriteEnable(menu.getMenuId(), username)) {
								throw new AccessControlException("");
							}

							// Get original document directory path
							Menu dir = menuDao.findByPrimaryKey(menu.getMenuParent());
							String path = settings.getValue("docdir") + "/" + menu.getMenuPath() + "/"
									+ menu.getMenuId();
							File originalDocDir = new File(path);

							menu.setMenuParent(selectedDirectory);
							dir = menuDao.findByPrimaryKey(selectedDirectory);
							menu.setMenuPath(dir.getMenuPath() + "/" + dir.getMenuId());
							menuDao.store(menu);

							// Update the FS
							path = settings.getValue("docdir") + "/" + menu.getMenuPath() + "/" + menu.getMenuId();
							File newDocDir = new File(path);

							FileUtils.copyDirectory(originalDocDir, newDocDir);
							FileUtils.forceDelete(originalDocDir);

							// Update field path on the Lucene record
							Document doc = record.getDocument();
							doc.setMenu(menu);
							DocumentManager documentManager = (DocumentManager) Context.getInstance().getBean(
									DocumentManager.class);
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
				String username = SessionManagement.getUsername();
				Directory dir = documentNavigation.getSelectedDir();
				int parentId = dir.getMenuId();

				if (mdao.isWriteEnable(parentId, username)) {
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
				final Menu parent = menuDao.findByPrimaryKey(selectedDirectory);
				log.debug("parent.getMenuId() = " + parent.getMenuId());
				log.debug("parent.getMenuPath() = " + parent.getMenuPath());

				SettingsConfig conf = (SettingsConfig) Context.getInstance().getBean(SettingsConfig.class);
				String path = conf.getValue("userdir");

				if (!path.endsWith(File.pathSeparator)) {
					path += File.pathSeparator;
				}
				path += username + File.pathSeparator + File.separator;
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
						importer.process(destFile.getPath(), zipLanguage, parent, username);
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
				String username = SessionManagement.getUsername();
				UserDocDAO uddao = (UserDocDAO) Context.getInstance().getBean(UserDocDAO.class);
				Collection userdocs = uddao.findByUserName(username);
				Iterator iter = userdocs.iterator();
				MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);

				while (iter.hasNext()) {
					UserDoc userdoc = (UserDoc) iter.next();
					Menu m = mdao.findByPrimaryKey(userdoc.getMenuId());
					lastdocs.add(new DocumentRecord(userdoc.getMenuId(), null, GROUP_INDENT_STYLE_CLASS,
							GROUP_ROW_STYLE_CLASS));
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}

		return lastdocs;
	}

	protected void selectHighlightedDocument(int menuId) {
		DocumentRecord myFatherDocument = null;
		selection.clear();

		// Iterate of the the list of documents in selected folder, when i find
		// the one I put it on the list of those selected
		for (DocumentRecord document : documents) {
			if (document.getMenu().getMenuId() == menuId) {
				document.setSelected(true);
				selection.add(document);
				break;
			}
			// iterate on the list of the children of the document
			for (DocumentRecord childDocument : document.getChildRecords()) {
				if (childDocument.getMenu().getMenuId() == menuId) {
					myFatherDocument = document;
					// imposto la selezione sul padre e sul figlio
					document.setSelected(true);
					childDocument.setSelected(true);
					selection.add(childDocument);
					break;
				}
			}
		}

		// Make the selected document the first
		if (!selection.isEmpty()) {
			DocumentRecord selectedDoc = selection.iterator().next();
			documents.remove(selectedDoc);
			documents.add(0, selectedDoc);

			// after sorting can expand the father
			if (myFatherDocument != null) {
				myFatherDocument.setSelected(false);
				myFatherDocument.setExpanded(true);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void sortDocumentsBySelected() {
		Collections.sort(documents, drsc);
		Collections.reverse(documents);
	}

	public String selectAll() {

		for (DocumentRecord document : documents) {
			document.setSelected(true);
			selection.add(document);

			// iterate on the list of the children of the document
			for (DocumentRecord childDocument : document.getChildRecords()) {
				// imposto la selezione sul padre e sul figlio
				childDocument.setSelected(true);
			}
		}
		selectedAll = true;

		return null;
	}

	public String unselectAll() {

		for (DocumentRecord document : documents) {
			document.setSelected(false);

			// iterate on the list of the children of the document
			for (DocumentRecord childDocument : document.getChildRecords()) {
				// imposto la selezione sul padre e sul figlio
				childDocument.setSelected(false);
			}
		}
		if (selection != null)
			selection.clear();
		selectedAll = false;

		return null;
	}

	class DocumentRecordSelectedComparator implements Comparator {
		public int compare(Object arg0, Object arg1) {

			DocumentRecord dr0 = (DocumentRecord) arg0;
			DocumentRecord dr1 = (DocumentRecord) arg1;

			return new Boolean(dr0.isSelected()).compareTo(new Boolean(dr1.isSelected()));
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
					return ascending ? c1.getDisplayDescription().compareTo(c2.getDisplayDescription()) : c2
							.getDisplayDescription().compareTo(c1.getDisplayDescription());
				} else if (column.equals("date")) {
					Date d1 = c1.getSourceDate() != null ? c1.getSourceDate() : new Date(0);
					Date d2 = c2.getSourceDate() != null ? c2.getSourceDate() : new Date(0);
					return ascending ? d1.compareTo(d2) : d2.compareTo(d1);
				} else if (column.equals("size")) {
					Long s1 = new Long(c1.getMenu().getMenuSize());
					Long s2 = new Long(c2.getMenu().getMenuSize());
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
