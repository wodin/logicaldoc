package com.logicaldoc.gui.frontend.client.webcontent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.frontend.client.document.DocumentsPanel;
import com.logicaldoc.gui.frontend.client.document.grid.DocumentsGrid;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.DocumentServiceAsync;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ContentsType;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

/**
 * This popup window is used to show the HTML document in a WYSIWYG.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.8.1
 */
public class WebcontentEditor extends Window {

	protected DocumentServiceAsync documentService = (DocumentServiceAsync) GWT.create(DocumentService.class);

	private HTMLPane editorPanel = new HTMLPane();

	private VLayout layout = null;

	private GUIDocument document;

	private DocumentsGrid grid;

	public WebcontentEditor(final GUIDocument document, final DocumentsGrid grid) {
		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		if (document.getId() > 0)
			setTitle(I18N.message("editdoc") + ": " + document.getTitle());
		else
			setTitle(I18N.message("createdoc") + ": " + document.getTitle());

		this.document = document;
		this.grid = grid;

		setWidth(com.google.gwt.user.client.Window.getClientWidth());
		setHeight(com.google.gwt.user.client.Window.getClientHeight());
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();
		setMargin(2);

		layout = new VLayout(5);
		layout.setTop(20);
		layout.setMargin(5);
		layout.setWidth100();
		layout.setHeight(getHeight() - 30);

		addCloseClickHandler(new CloseClickHandler() {
			@Override
			public void onCloseClick(CloseClickEvent event) {
				unlockAndClose();
			}
		});

		addChild(layout);

		prepareBody();
	}

	/**
	 * Prepares the popup body
	 */
	private void prepareBody() {
		editorPanel = new HTMLPane();
		editorPanel.setShowEdges(false);
		editorPanel.setContentsURL(Util.webEditorUrl(document.getId(), document.getFileName(), getHeight() - 250));
		editorPanel.setContentsType(ContentsType.PAGE);

		layout.addMember(editorPanel);

		ToolStrip toolStrip = new ToolStrip();
		toolStrip.setHeight(20);
		toolStrip.setWidth100();
		toolStrip.setAlign(Alignment.RIGHT);
		toolStrip.addFill();

		ToolStripButton close = new ToolStripButton();
		close.setTitle(I18N.message("close"));
		toolStrip.addButton(close);
		toolStrip.addSeparator();
		close.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				unlockAndClose();
			}
		});

		layout.setMembers(toolStrip, editorPanel);
	}

	private void unlockAndClose() {
		if (document.getId() != 0)
			documentService.unlock(Session.get().getSid(), new long[] { WebcontentEditor.this.document.getId() },
					new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {
							Log.serverError(caught);
							destroy();
						}

						@Override
						public void onSuccess(Void result) {
							DocumentsPanel.get().refresh();
							destroy();
						}
					});
		else
			destroy();
	}
}