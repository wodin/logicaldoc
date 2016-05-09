package com.logicaldoc.gui.frontend.client.sharefile;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.widgets.ContactingServer;
import com.logicaldoc.gui.frontend.client.document.DocumentsPanel;
import com.logicaldoc.gui.frontend.client.folder.FolderNavigator;
import com.logicaldoc.gui.frontend.client.panels.MainPanel;
import com.logicaldoc.gui.frontend.client.search.SearchPanel;
import com.logicaldoc.gui.frontend.client.services.ShareFileService;
import com.logicaldoc.gui.frontend.client.services.ShareFileServiceAsync;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Dialog;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tree.TreeGrid;

/**
 * This is the form used to select an element in ShareFile
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 7.2.1
 */
public class ShareFileDialog extends Dialog {
	private ShareFileServiceAsync service = (ShareFileServiceAsync) GWT.create(ShareFileService.class);

	private TreeGrid tree = null;

	private boolean export = false;

	public ShareFileDialog(boolean export) {
		super();
		this.export = export;
		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.message("sharefile"));
		setWidth(350);
		setHeight(300);
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

		tree = new ShareFileTree(export);
		tree.setWidth100();
		tree.setHeight100();

		VLayout buttons = new VLayout();
		buttons.setWidth100();
		buttons.setHeight(30);

		Button select = new Button(I18N.message("select"));
		select.setAutoFit(true);
		select.setMargin(1);
		select.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (ShareFileDialog.this.export)
					onExport();
				else
					onImport();
			}
		});

		buttons.setMembers(select);

		content.setMembers(tree, buttons);
		addItem(content);
	}

	private void onExport() {
		final Record selection = tree.getSelectedRecord();
		if (selection == null)
			return;

		final long[] docIds = MainPanel.get().isOnDocumentsTab() ? DocumentsPanel.get().getDocumentsGrid()
				.getSelectedIds() : SearchPanel.get().getDocumentsGrid().getSelectedIds();

		SC.ask(docIds.length == 0 ? I18N.message("exportdirtosfile", Session.get().getCurrentFolder().getName()) : I18N
				.message("exportdocstosfile"), new BooleanCallback() {

			@Override
			public void execute(Boolean choice) {
				if (choice.booleanValue()) {
					String targetId = selection.getAttributeAsString("iid");
					long[] folderIds = new long[0];
					if (docIds.length == 0 && Session.get().getCurrentFolder() != null)
						folderIds[0] = Session.get().getCurrentFolder().getId();

					ContactingServer.get().show();
					service.exportDocuments(targetId, folderIds, docIds, new AsyncCallback<Boolean>() {
						@Override
						public void onFailure(Throwable caught) {
							ContactingServer.get().hide();
							Log.serverError(caught);
						}

						@Override
						public void onSuccess(Boolean result) {
							ContactingServer.get().hide();
							if (result.booleanValue()) {
								SC.say(I18N.message("sfileexportok"));
								ShareFileDialog.this.destroy();
							} else
								SC.say(I18N.message("sfileexportko"));
						}
					});
				}
			}
		});
	}

	private void onImport() {
		final Record[] selection = tree.getSelectedRecords();
		if (selection == null)
			return;

		final String[] ids = new String[selection.length];
		for (int i = 0; i < selection.length; i++)
			ids[i] = selection[i].getAttributeAsString("iid");

		SC.ask(I18N.message("importfromsfile", Session.get().getCurrentFolder().getName()), new BooleanCallback() {

			@Override
			public void execute(Boolean choice) {
				if (choice.booleanValue()) {
					ShareFileDialog.this.destroy();
					ContactingServer.get().show();
					service.importDocuments(Session.get().getCurrentFolder().getId(), ids,
							new AsyncCallback<Integer>() {
								@Override
								public void onFailure(Throwable caught) {
									ContactingServer.get().hide();
									Log.serverError(caught);
								}

								@Override
								public void onSuccess(Integer count) {
									ContactingServer.get().hide();
									FolderNavigator.get().reload();
									SC.say(I18N.message("importeddocs2", count.toString()));
								}
							});
				}
			}
		});
	}
}