package com.logicaldoc.gui.frontend.client.document;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIArchive;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.frontend.client.services.ArchiveService;
import com.logicaldoc.gui.frontend.client.services.ArchiveServiceAsync;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * This popup window is used to send documents to an archive.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class SendDocsToArchiveDialog extends Window {

	private ArchiveServiceAsync service = (ArchiveServiceAsync) GWT.create(ArchiveService.class);

	private DynamicForm form = new DynamicForm();

	public SendDocsToArchiveDialog(final long[] ids) {
		VLayout layout = new VLayout();
		layout.setMargin(25);

		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);

		setTitle(I18N.message("sendtoarchive"));
		setWidth(380);
		setHeight(100);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();

		layout.addMember(form);

		SelectItem archive = ItemFactory.newArchiveSelector(GUIArchive.MODE_EXPORT, GUIArchive.STATUS_OPENED);
		archive.setTitle(I18N.message("selectopenarchive"));
		archive.setWrapTitle(false);
		archive.setRequired(true);

		ButtonItem send = new ButtonItem();
		send.setStartRow(false);
		send.setTitle(I18N.message("sendtoarchive"));
		send.setAutoFit(true);
		send.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onSend(ids);
			}
		});

		form.setFields(archive, send);
		addChild(layout);
	}

	public void onSend(long[] ids) {
		if (!form.validate())
			return;

		service.addDocuments(Session.get().getSid(), Long.parseLong(form.getValueAsString("archive")), ids,
				new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(Void result) {
						Log.info(I18N.message("documentsaddedtoarchive"), null);
						destroy();
					}
				});
	}
}