package com.logicaldoc.gui.frontend.client.search;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.DocumentObserver;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.common.client.beans.GUISearchOptions;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.frontend.client.document.DocumentDetailsPanel;
import com.logicaldoc.gui.frontend.client.document.grid.DocumentsGrid;
import com.logicaldoc.gui.frontend.client.document.grid.DocumentsListGrid;
import com.logicaldoc.gui.frontend.client.folder.FolderDetailsPanel;
import com.logicaldoc.gui.frontend.client.panels.MainPanel;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.DocumentServiceAsync;
import com.logicaldoc.gui.frontend.client.services.FolderService;
import com.logicaldoc.gui.frontend.client.services.FolderServiceAsync;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * This panel is used to show the user a list of search results
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class SearchPanel extends HLayout implements SearchObserver, DocumentObserver {

	private DocumentServiceAsync documentService = (DocumentServiceAsync) GWT.create(DocumentService.class);

	private FolderServiceAsync folderService = (FolderServiceAsync) GWT.create(FolderService.class);

	private Layout content = new VLayout();

	private Layout details = new VLayout();

	private HitsListPanel listingPanel;

	private Canvas detailPanel;

	private static SearchPanel instance;

	private VLayout right = new VLayout();

	private SearchPanel() {
		Search.get().addObserver(this);

		Session.get().addDocumentObserver(this);

		setWidth100();

		// Prepare the collapsable menu
		SearchMenu searchMenu = SearchMenu.get();
		searchMenu.setWidth(350);
		searchMenu.setShowResizeBar(true);

		// Initialize the listing panel as placeholder
		listingPanel = new HitsListPanel();
		content.setAlign(Alignment.CENTER);
		content.setHeight("51%");
		content.setShowResizeBar(true);
		content.addMember(listingPanel);

		// Add a details panel under the listing one
		detailPanel = new Label("&nbsp;" + I18N.message("selectahit"));
		details.setAlign(Alignment.CENTER);
		details.addMember(detailPanel);

		right.addMember(content);
		right.addMember(details);

		addMember(searchMenu);
		addMember(right);

		setShowEdges(false);
	}

	public static SearchPanel get() {
		if (instance == null)
			instance = new SearchPanel();
		return instance;
	}

	public void onSelectedDocumentHit(long id) {
		if (id > 0) {
			documentService.getById(Session.get().getSid(), id, new AsyncCallback<GUIDocument>() {
				@Override
				public void onFailure(Throwable caught) {
					Log.serverError(caught);
				}

				@Override
				public void onSuccess(GUIDocument result) {
					Session.get().setCurrentDocument(result);
				}
			});
		} else
			onDocumentSelected(null);
	}

	public void onSelectedFolderHit(long id) {
		if (details.contains(detailPanel))
			details.removeMember(detailPanel);
		detailPanel.destroy();
		if (id > 0) {
			folderService.getFolder(Session.get().getSid(), id, true, new AsyncCallback<GUIFolder>() {

				@Override
				public void onFailure(Throwable caught) {
					Log.serverError(caught);
				}

				@Override
				public void onSuccess(GUIFolder fld) {
					detailPanel = new FolderDetailsPanel(fld, listingPanel);
					details.addMember(detailPanel);
				}
			});
		} else {
			detailPanel = new Label("&nbsp;" + I18N.message("selectahit"));
			details.addMember(detailPanel);
		}
	}

	@Override
	public void onSearchArrived() {
		onSelectedDocumentHit(-1);
	}

	@Override
	public void onOptionsChanged(GUISearchOptions newOptions) {

	}

	public boolean isMenuOpened() {
		return SearchMenu.get().getWidth() > 2;
	}

	public void toggleMenu() {
		if (SearchMenu.get().getWidth() > 2)
			SearchMenu.get().setWidth(0);
		else
			SearchMenu.get().setWidth(350);
	}

	public DocumentsGrid getGrid() {
		return ((HitsListPanel) listingPanel).getList();
	}

	public DocumentsGrid getDocumentsGrid() {
		DocumentsGrid grid = getGrid();
		if (grid instanceof DocumentsListGrid)
			return (DocumentsGrid) grid;
		else
			return null;
	}

	@Override
	public void onDocumentSelected(GUIDocument document) {
		if (!MainPanel.get().isOnSearchTab())
			return;

		if (document == null || document.getId() < 1 || !(detailPanel instanceof DocumentDetailsPanel)) {
			if (details.contains(detailPanel))
				details.removeMember(detailPanel);
			detailPanel.destroy();
			if (document == null || document.getId() < 1) {
				detailPanel = new Label("&nbsp;" + I18N.message("selectahit"));
				details.addMember(detailPanel);
				return;
			}
		}

		if (detailPanel instanceof DocumentDetailsPanel) {
			((DocumentDetailsPanel) detailPanel).setDocument(document);

		} else {
			detailPanel = new DocumentDetailsPanel(listingPanel);
			((DocumentDetailsPanel) detailPanel).setDocument(document);
			details.addMember(detailPanel);
		}
		details.redraw();
	}

	@Override
	public void onDocumentSaved(GUIDocument document) {
		// Nothing to do
	}
}