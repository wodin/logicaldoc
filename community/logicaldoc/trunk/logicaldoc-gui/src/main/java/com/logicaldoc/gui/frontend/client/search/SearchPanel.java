package com.logicaldoc.gui.frontend.client.search;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.frontend.client.Log;
import com.logicaldoc.gui.frontend.client.document.DocumentDetailsPanel;
import com.logicaldoc.gui.frontend.client.document.DocumentsListPanel;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.DocumentServiceAsync;
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
public class SearchPanel extends HLayout {

	private DocumentServiceAsync documentService = (DocumentServiceAsync) GWT.create(DocumentService.class);

	private Layout content = new VLayout();

	private Layout details = new VLayout();

	private Canvas listingPanel;

	private Canvas detailPanel;

	private static SearchPanel instance;

	private VLayout right = new VLayout();

	private SearchPanel() {
		setWidth100();

		// Prepare the collapsible menu
		SearchMenu leftMenu = new SearchMenu();
		leftMenu.setWidth(280);
		leftMenu.setShowResizeBar(true);

		// Initialize the listing panel as placeholder
		listingPanel = new Label("&nbsp;" + I18N.getMessage("selectfolder"));
		content.setAlign(Alignment.CENTER);
		content.setHeight("51%");
		content.setShowResizeBar(true);
		content.addMember(listingPanel);

		// Add a details panel under the listing one
		detailPanel = new Label("&nbsp;" + I18N.getMessage("selectadocument"));
		details.setAlign(Alignment.CENTER);
		details.addMember(detailPanel);

		right.addMember(content);
		right.addMember(details);

		addMember(leftMenu);
		addMember(right);

		setShowEdges(true);
	}

	public static SearchPanel getInstance() {
		if (instance == null)
			instance = new SearchPanel();
		return instance;
	}

	public void onSavedDocument(GUIDocument document) {
		((DocumentsListPanel) listingPanel).updateSelectedRecord(document);
	}

	public void onSelectedDocument(long docId) {
		if (!(detailPanel instanceof DocumentDetailsPanel)) {
			details.removeMember(detailPanel);
			detailPanel.destroy();
			detailPanel = new DocumentDetailsPanel();
			details.addMember(detailPanel);
		}

		documentService.getById(Session.getInstance().getSid(), docId, new AsyncCallback<GUIDocument>() {
			@Override
			public void onFailure(Throwable caught) {
				Log.serverError(caught);
			}

			@Override
			public void onSuccess(GUIDocument result) {
				((DocumentDetailsPanel) detailPanel).setDocument(result);
				details.redraw();
			}
		});
	}

	public void refresh() {
		content.removeMember(listingPanel);
		listingPanel.destroy();
		listingPanel = new HLayout();
		content.addMember(listingPanel);
		content.redraw();
	}
}