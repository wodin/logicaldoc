package com.logicaldoc.gui.frontend.client.document;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.DocumentObserver;
import com.logicaldoc.gui.common.client.FolderObserver;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.frontend.client.folder.FolderDetailsPanel;
import com.logicaldoc.gui.frontend.client.folder.Navigator;
import com.logicaldoc.gui.frontend.client.panels.MainPanel;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.DocumentServiceAsync;
import com.logicaldoc.gui.frontend.client.services.FolderService;
import com.logicaldoc.gui.frontend.client.services.FolderServiceAsync;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.util.Offline;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * This panel implements the browser on the documents archive
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class DocumentsPanel extends HLayout implements FolderObserver, DocumentObserver {

	protected DocumentServiceAsync documentService = (DocumentServiceAsync) GWT.create(DocumentService.class);

	protected FolderServiceAsync folderService = (FolderServiceAsync) GWT.create(FolderService.class);

	protected Layout listing = new VLayout();

	protected Layout details = new VLayout();

	protected Canvas listingPanel;

	protected Canvas detailPanel;

	protected static DocumentsPanel instance;

	protected DocumentToolbar toolbar;

	protected VLayout right = new VLayout();

	protected GUIFolder folder;

	protected DocumentsMenu documentsMenu;

	public DocumentsMenu getDocumentsMenu() {
		return documentsMenu;
	}

	// The document that must be hilighted
	protected Long hiliteDocId = null;

	protected Integer max;

	protected DocumentsPanel() {
		// Register to folders events
		Session.get().addFolderObserver(this);

		// Register to documents events
		Session.get().addDocumentObserver(this);

		setWidth100();

		prepareMenu();

		// Initialize the listing panel as placeholder
		listingPanel = new Label("&nbsp;" + I18N.message("selectfolder"));
		listing.setAlign(Alignment.CENTER);
		listing.setHeight("51%");
		listing.setShowResizeBar(true);
		listing.addMember(listingPanel);

		// Add a details panel under the listing one
		detailPanel = new Label("&nbsp;" + I18N.message("selectfolderordoc"));
		details.setAlign(Alignment.CENTER);
		details.addMember(detailPanel);

		prepareToolBar();

		right.addMember(toolbar);
		right.addMember(listing);
		right.addMember(details);

		addMember(documentsMenu);
		addMember(right);

		setShowEdges(true);
	}

	protected void prepareToolBar() {
		toolbar = new DocumentToolbar();
		toolbar.setWidth100();
	}

	/**
	 * Prepare the collapsible menu
	 */
	protected void prepareMenu() {
		documentsMenu = new DocumentsMenu();
		documentsMenu.setShowResizeBar(true);
	}

	public static DocumentsPanel get() {
		if (instance == null) {
			instance = new DocumentsPanel();

			// Setup the default elements number
			String mx = "100";
			if (Offline.get(Constants.COOKIE_DOCSLIST_MAX) != null
					&& !Offline.get(Constants.COOKIE_DOCSLIST_MAX).equals(""))
				mx = (String) Offline.get(Constants.COOKIE_DOCSLIST_MAX);
			instance.setMax(Integer.parseInt(mx));
		}
		return instance;
	}

	public void onDocumentSaved(GUIDocument document) {
		if (listingPanel != null && listingPanel instanceof DocumentsListPanel)
			((DocumentsListPanel) listingPanel).getGrid().updateSelectedRecord(document);
	}

	@Override
	public void onDocumentSelected(GUIDocument document) {
		if (!MainPanel.get().isOnDocumentsTab())
			return;

		if (!(detailPanel instanceof DocumentDetailsPanel)) {
			details.removeMember(detailPanel);
			detailPanel.destroy();
			detailPanel = new DocumentDetailsPanel(DocumentsPanel.this);
			details.addMember(detailPanel);
		}

		toolbar.update(document);
		if (detailPanel instanceof DocumentDetailsPanel) {
			((DocumentDetailsPanel) detailPanel).setDocument(document);
			details.redraw();
		}
	}

	public void openInFolder(long folderId, Long docId) {
		// Save the information about the document that will be hilighted by
		// habler onFolderSelect
		if (docId != null)
			hiliteDocId = docId;

		documentsMenu.openFolder(folderId);
		documentsMenu.expandSection(0);
		MainPanel.get().selectDocumentsTab();
	}

	public void openInFolder(long docId) {
		documentService.getById(Session.get().getSid(), docId, new AsyncCallback<GUIDocument>() {
			@Override
			public void onFailure(Throwable caught) {
				/*
				 * Sometimes we can have spurious errors using Firefox.
				 */
				if (Session.get().isDevel())
					Log.serverError(caught);
			}

			@Override
			public void onSuccess(GUIDocument result) {
				GUIFolder folder = result.getFolder();
				if (folder != null) {
					openInFolder(folder.getId(), result.getId());
				}
			}
		});
	}

	/**
	 * Shows the document details of a selected document. The documents details
	 * are retrieved from the server.
	 * 
	 * @param docId Id of the documents that needs to be selected
	 * @param clearSelection true if you want to de-select all records in the
	 *        list
	 */
	public void selectDocument(long docId, final boolean clearSelection) {
		documentService.getById(Session.get().getSid(), docId, new AsyncCallback<GUIDocument>() {
			@Override
			public void onFailure(Throwable caught) {
				/*
				 * Sometimes we can have spurious errors using Firefox.
				 */
				if (Session.get().isDevel())
					Log.serverError(caught);
			}

			@Override
			public void onSuccess(GUIDocument result) {
				if (!(detailPanel instanceof DocumentDetailsPanel)) {
					details.removeMember(detailPanel);
					detailPanel.destroy();
					detailPanel = new DocumentDetailsPanel(DocumentsPanel.this);
					details.addMember(detailPanel);
				}

				toolbar.update(result);
				if (detailPanel instanceof DocumentDetailsPanel) {
					((DocumentDetailsPanel) detailPanel).setDocument(result);
					details.redraw();
				}

				if (clearSelection)
					((DocumentsListPanel) listingPanel).getGrid().deselectAllRecords();
			}
		});
	}

	/**
	 * Shows the documents list under the folder with the given folderId and
	 * highlights the document with the given docId
	 */
	public void onFolderSelect(long folderId, Long docId) {
		if (docId != null)
			hiliteDocId = docId;
		folderService.getFolder(Session.get().getSid(), folderId, false, new AsyncCallback<GUIFolder>() {

			@Override
			public void onFailure(Throwable caught) {
				Log.serverError(caught);
			}

			@Override
			public void onSuccess(GUIFolder folder) {
				updateListingPanel(folder, hiliteDocId, max);
			}
		});
	}

	@Override
	public void onFolderSelected(GUIFolder folder) {
		this.folder = folder;
		refresh();
	}

	public void refresh() {
		refresh(null);
	}

	public void refresh(Integer max) {
		if (max != null && max > 0)
			this.max = max;

		updateListingPanel(folder, hiliteDocId, this.max);

		showFolderDetails();

		hiliteDocId = null;
	}

	protected void updateListingPanel(GUIFolder folder, Long hiliteDocId, Integer max) {
		listing.removeMember(listingPanel);
		listingPanel.destroy();
		listingPanel = new DocumentsListPanel(folder, hiliteDocId, max);
		listing.addMember(listingPanel);
		listing.redraw();
	}

	/**
	 * Shows folders data in the details area.
	 */
	public void showFolderDetails() {
		if (hiliteDocId != null)
			selectDocument(hiliteDocId, false);
		else {
			detailPanel.destroy();
			detailPanel = new FolderDetailsPanel(folder, Navigator.get());
			details.addMember(detailPanel);
			details.redraw();
		}
	}

	public void toggleFilters() {
		if (listingPanel instanceof DocumentsListPanel) {
			((DocumentsListPanel) listingPanel).toggleFilters();
		}
	}

	public void printPreview() {
		if (listingPanel instanceof DocumentsListPanel) {
			Canvas.printComponents(new Object[] { ((DocumentsListPanel) listingPanel).getGrid() });
		}
	}

	public void export() {
		if (listingPanel instanceof DocumentsListPanel) {
			Util.exportCSV(((DocumentsListPanel) listingPanel).getGrid(), false);
		}
	}

	public GUIDocument getSelectedDocument() {
		if (listingPanel instanceof DocumentsListPanel)
			return ((DocumentsListPanel) listingPanel).getGrid().getSelectedDocument();
		else
			return null;
	}

	public DocumentsGrid getDocumentsGrid() {
		return ((DocumentsListPanel) listingPanel).getGrid();
	}

	@Override
	public void onFolderSaved(GUIFolder folder) {
		// Nothing to do
	}

	public Integer getMax() {
		return max;
	}

	public void setMax(Integer max) {
		this.max = max;
	}
}