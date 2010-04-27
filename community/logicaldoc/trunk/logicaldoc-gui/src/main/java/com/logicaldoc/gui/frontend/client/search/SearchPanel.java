package com.logicaldoc.gui.frontend.client.search;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.frontend.client.Log;
import com.logicaldoc.gui.frontend.client.document.DocumentDetailsPanel;
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
public class SearchPanel extends HLayout implements SearchObserver {

	private DocumentServiceAsync documentService = (DocumentServiceAsync) GWT.create(DocumentService.class);

	private Layout content = new VLayout();

	private Layout details = new VLayout();

	private HitsListPanel listingPanel;

	private Canvas detailPanel;

	private static SearchPanel instance;

	private VLayout right = new VLayout();

	private SearchPanel() {
		setWidth100();

		// Prepare the collapsible menu
		SearchMenu leftMenu = SearchMenu.get();
		leftMenu.setWidth(350);
		leftMenu.setShowResizeBar(true);

		// Initialize the listing panel as placeholder
		listingPanel = new HitsListPanel();
		content.setAlign(Alignment.CENTER);
		content.setHeight("51%");
		content.setShowResizeBar(true);
		content.addMember(listingPanel);

		// Add a details panel under the listing one
		detailPanel = new Label("&nbsp;" + I18N.getMessage("selectahit"));
		details.setAlign(Alignment.CENTER);
		details.addMember(detailPanel);

		right.addMember(content);
		right.addMember(details);

		addMember(leftMenu);
		addMember(right);

		setShowEdges(true);

		Search.get().addObserver(this);
	}

	public static SearchPanel get() {
		if (instance == null)
			instance = new SearchPanel();
		return instance;
	}

	public void onSelectedHit(long docId) {
		if (details.contains(detailPanel))
			details.removeMember(detailPanel);
		detailPanel.destroy();
		if (docId > 0)
			detailPanel = new DocumentDetailsPanel(listingPanel);
		else
			detailPanel = new Label("&nbsp;" + I18N.getMessage("selectahit"));

		details.addMember(detailPanel);

		if (docId > 0 && (detailPanel instanceof DocumentDetailsPanel))
			documentService.getById(Session.get().getSid(), docId, new AsyncCallback<GUIDocument>() {
				@Override
				public void onFailure(Throwable caught) {
					Log.serverError(caught);
				}

				@Override
				public void onSuccess(GUIDocument result) {
					if (detailPanel instanceof DocumentDetailsPanel) {
						((DocumentDetailsPanel) detailPanel).setDocument(result);
						details.redraw();
					}
				}
			});
	}

	@Override
	public void onSearchArrived() {
		onSelectedHit(-1);
	}
}