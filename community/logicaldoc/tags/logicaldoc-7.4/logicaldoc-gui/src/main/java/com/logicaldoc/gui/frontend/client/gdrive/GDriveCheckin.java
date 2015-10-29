package com.logicaldoc.gui.frontend.client.gdrive;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.widgets.ContactingServer;
import com.logicaldoc.gui.frontend.client.document.grid.DocumentsGrid;
import com.logicaldoc.gui.frontend.client.services.GDriveService;
import com.logicaldoc.gui.frontend.client.services.GDriveServiceAsync;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.BooleanItem;
import com.smartgwt.client.widgets.form.fields.SubmitItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;

/**
 * This popup window is used to perform the checkin of a Google Drive document
 * into LogicalDOC.
 * 
 * @author Marco Meschieri - LogicalDOC
 * @since 7.3
 */
public class GDriveCheckin extends Window {
	private SubmitItem checkin;

	private ValuesManager vm;

	protected GDriveServiceAsync gdocsService = (GDriveServiceAsync) GWT.create(GDriveService.class);

	public GDriveCheckin(final GUIDocument document, final GDriveEditor parentDialog, final DocumentsGrid grid) {
		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.message("checkin"));
		setWidth(400);
		setHeight(140);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();
		setMembersMargin(2);

		DynamicForm form = new DynamicForm();
		vm = new ValuesManager();
		form.setValuesManager(vm);

		BooleanItem versionItem = new BooleanItem();
		versionItem.setName("majorversion");
		versionItem.setTitle(I18N.message("majorversion"));

		TextItem commentItem = ItemFactory.newTextItem("comment", "comment", null);
		commentItem.setRequired(true);
		commentItem.setWidth(240);

		checkin = new SubmitItem();
		checkin.setTitle(I18N.message("checkin"));
		checkin.setAlign(Alignment.RIGHT);
		checkin.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onCheckin(document, parentDialog, grid);
			}
		});

		form.setItems(versionItem, commentItem, checkin);

		addItem(form);
	}

	public void onCheckin(GUIDocument document, final GDriveEditor parentDialog, final DocumentsGrid grid) {
		if (!vm.validate())
			return;
		ContactingServer.get().show();
		gdocsService.checkin(Session.get().getSid(), document.getId(), vm.getValueAsString("comment"),
				"true".equals(vm.getValueAsString("majorversion")), new AsyncCallback<GUIDocument>() {
					@Override
					public void onFailure(Throwable caught) {
						ContactingServer.get().hide();
						Log.serverError(caught);
						destroy();
					}

					@Override
					public void onSuccess(GUIDocument result) {
						ContactingServer.get().hide();
						destroy();
						parentDialog.destroy();
						grid.updateDocument(result);
						Session.get().setCurrentDocument(result);
					}
				});
	}
}