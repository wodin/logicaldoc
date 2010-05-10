package com.logicaldoc.gui.frontend.client.document;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.FolderObserver;
import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.frontend.client.Log;
import com.logicaldoc.gui.frontend.client.folder.FolderDetailsPanel;
import com.logicaldoc.gui.frontend.client.panels.MainPanel;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.DocumentServiceAsync;
import com.smartgwt.client.types.Alignment;
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

	private DocumentServiceAsync documentService = (DocumentServiceAsync) GWT.create(DocumentService.class);

	private Layout listing = new VLayout();

	private Layout details = new VLayout();

	private Canvas listingPanel;

	private Canvas detailPanel;

	private static DocumentsPanel instance;

	private DocumentToolbar toolbar;

	private VLayout right = new VLayout();

	private GUIFolder folder;

	private DocumentsMenu documentsMenu;

	// The document that must be hilighted
	private Long hiliteDocId = null;

	private Integer maxRows;

	private DocumentsPanel() {
		// Register to folders events
		Session.get().addFolderObserver(this);

		setWidth100();

		// Prepare the collapsible menu
		documentsMenu = new DocumentsMenu();
		documentsMenu.setWidth(280);
		documentsMenu.setShowResizeBar(true);

		// Initialize the listing panel as placeholder
		listingPanel = new Label("&nbsp;" + I18N.getMessage("selectfolder"));
		listing.setAlign(Alignment.CENTER);
		listing.setHeight("51%");
		listing.setShowResizeBar(true);
		listing.addMember(listingPanel);

		// Add a details panel under the listing one
		detailPanel = new Label("&nbsp;" + I18N.getMessage("selectfolderordoc"));
		details.setAlign(Alignment.CENTER);
		details.addMember(detailPanel);

		toolbar = new DocumentToolbar();
		toolbar.setWidth100();

		right.addMember(toolbar);
		right.addMember(listing);
		right.addMember(details);

		addMember(documentsMenu);
		addMember(right);

		setShowEdges(true);
	}

	public static DocumentsPanel get() {
		if (instance == null)
			instance = new DocumentsPanel();
		return instance;
	}

	public void onDocumentSaved(GUIDocument document) {
		if (listingPanel != null && listingPanel instanceof DocumentsListPanel)
			((DocumentsListPanel) listingPanel).updateSelectedRecord(document);
	}

	public void openInFolder(long folderId, long docId) {
		// Save the information about the document that will be hilighted by
		// habler onFolderSelect
		hiliteDocId = docId;
		documentsMenu.openFolder(folderId);
		documentsMenu.expandSection(0);
		MainPanel.get().selectDocumentsTab();
	}

	/**
	 * Shows the document details
	 * 
	 * @param docId
	 * @param clearSelection true if you want to deselect all records in the
	 *        list
	 */
	public void onSelectedDocument(long docId, final boolean clearSelection) {
		if (!(detailPanel instanceof DocumentDetailsPanel)) {
			details.removeMember(detailPanel);
			detailPanel.destroy();
			detailPanel = new DocumentDetailsPanel(this);
			details.addMember(detailPanel);
		}

		documentService.getById(Session.get().getSid(), docId, new AsyncCallback<GUIDocument>() {
			@Override
			public void onFailure(Throwable caught) {
				Log.serverError(caught);
			}

			@Override
			public void onSuccess(GUIDocument result) {
				toolbar.update(result);
				if (detailPanel instanceof DocumentDetailsPanel) {
					((DocumentDetailsPanel) detailPanel).setDocument(result);
					details.redraw();
				}
				if (clearSelection)
					((DocumentsListPanel) listingPanel).getList().deselectAllRecords();
			}
		});
	}

	@Override
	public void onFolderSelect(GUIFolder folder) {
		this.folder = folder;
		refresh();
	}

	public void refresh() {
		refresh(null);
	}

	public void refresh(Integer maxRows) {
		if (maxRows != null && maxRows > 0)
			this.maxRows = maxRows;

		listing.removeMember(listingPanel);
		listingPanel.destroy();
		listingPanel = new DocumentsListPanel(folder, hiliteDocId, this.maxRows);
		listing.addMember(listingPanel);
		listing.redraw();

		if (hiliteDocId != null)
			onSelectedDocument(hiliteDocId, false);
		else {
			detailPanel.destroy();
			detailPanel = new FolderDetailsPanel(folder);
			details.addMember(detailPanel);
			details.redraw();
		}

		hiliteDocId = null;
	}

	public void toggleFilters() {
		if (listingPanel instanceof DocumentsListPanel) {
			((DocumentsListPanel) listingPanel).toggleFilters();
		}
	}
}