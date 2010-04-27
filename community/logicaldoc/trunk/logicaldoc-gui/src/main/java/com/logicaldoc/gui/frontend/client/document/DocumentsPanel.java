package com.logicaldoc.gui.frontend.client.document;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.FolderObserver;
import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.frontend.client.Log;
import com.logicaldoc.gui.frontend.client.Main;
import com.logicaldoc.gui.frontend.client.folder.FolderDetailsPanel;
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

	private Layout content = new VLayout();

	private Layout details = new VLayout();

	private Canvas listingPanel;

	private Canvas detailPanel;

	private static DocumentsPanel instance;

	private DocumentToolbar toolbar;

	private VLayout right = new VLayout();

	private GUIFolder folder;

	private DocumentsMenu leftMenu;

	private DocumentsPanel() {
		// Register to folders events
		Session.get().addFolderObserver(this);

		setWidth100();

		// Prepare the collapsible menu
		leftMenu = new DocumentsMenu();
		leftMenu.setWidth(280);
		leftMenu.setShowResizeBar(true);

		// Initialize the listing panel as placeholder
		listingPanel = new Label("&nbsp;" + I18N.getMessage("selectfolder"));
		content.setAlign(Alignment.CENTER);
		content.setHeight("51%");
		content.setShowResizeBar(true);
		content.addMember(listingPanel);

		// Add a details panel under the listing one
		detailPanel = new Label("&nbsp;" + I18N.getMessage("selectfolderordoc"));
		details.setAlign(Alignment.CENTER);
		details.addMember(detailPanel);

		toolbar = new DocumentToolbar();
		toolbar.setWidth100();

		right.addMember(toolbar);
		right.addMember(content);
		right.addMember(details);

		addMember(leftMenu);
		addMember(right);

		setShowEdges(true);
	}

	public static DocumentsPanel get() {
		if (instance == null)
			instance = new DocumentsPanel();
		return instance;
	}

	public void onDocumentSaved(GUIDocument document) {
		((DocumentsListPanel) listingPanel).updateSelectedRecord(document);
	}

	public void openInFolder(long folderId) {
		leftMenu.openFolder(folderId);
		Main.get().getMainPanel().selectDocumentsTab();
	}

	public void onSelectedDocument(long docId) {
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
				((DocumentDetailsPanel) detailPanel).setDocument(result);
				details.redraw();
			}
		});
	}

	@Override
	public void onFolderSelect(GUIFolder folder) {
		this.folder = folder;
		refresh();
	}

	public void refresh() {
		content.removeMember(listingPanel);
		listingPanel.destroy();
		listingPanel = new DocumentsListPanel(folder);
		content.addMember(listingPanel);
		content.redraw();

		detailPanel.destroy();
		detailPanel = new FolderDetailsPanel(folder);
		details.addMember(detailPanel);
		details.redraw();
	}
}