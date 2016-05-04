package com.logicaldoc.gui.frontend.client.folder;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.widgets.FolderTree;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.DocumentServiceAsync;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Dialog;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tree.TreeGrid;

/**
 * This is the form used to restore a selection of documents.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 7.2.1
 */
public class RestoreDialog extends Dialog {

	protected DocumentServiceAsync docService = (DocumentServiceAsync) GWT.create(DocumentService.class);

	protected ClickHandler handler;

	public RestoreDialog(final long[] docIds, ClickHandler handler) {
		super();
		this.handler = handler;
		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.message("restore"));
		setWidth(250);
		setHeight(270);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();
		setPadding(3);

		VLayout content = new VLayout();
		content.setTop(10);
		content.setWidth100();
		content.setHeight100();
		content.setMembersMargin(3);

		final TreeGrid folders = new FolderTree();
		folders.setWidth100();
		folders.setHeight100();

		VLayout buttons = new VLayout();
		buttons.setWidth100();
		buttons.setHeight(30);

		Button restore = new Button(I18N.message("restore"));
		restore.setAutoFit(true);
		restore.setMargin(1);
		restore.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				docService.restore(docIds,
						Long.parseLong(folders.getSelectedRecord().getAttributeAsString("folderId")),
						new AsyncCallback<Void>() {

							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught);
							}

							@Override
							public void onSuccess(Void arg0) {
								if (RestoreDialog.this.handler != null)
									RestoreDialog.this.handler.onClick(event);
								close();
							}
						});
				close();
			}
		});

		buttons.setMembers(restore);

		content.setMembers(folders, buttons);
		addItem(content);
	}
}