package com.logicaldoc.gui.frontend.client.document;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUIArchive;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.frontend.client.services.ImpexService;
import com.logicaldoc.gui.frontend.client.services.ImpexServiceAsync;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;

/**
 * This popup window is used to send documents or folders to an archive.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class SendToArchiveDialog extends Window {

	private ImpexServiceAsync service = (ImpexServiceAsync) GWT.create(ImpexService.class);

	private DynamicForm form = new DynamicForm();

	/**
	 * Constructor
	 * 
	 * @param ids Identifiers of the elements that have to be archived
	 * @param document True if the ids refers to documents, False in case of
	 *        folders
	 */
	public SendToArchiveDialog(final long[] ids, final boolean document) {
		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);

		setTitle(I18N.message("sendtoexparchive"));
		setWidth(380);
		setHeight(100);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();

		SelectItem archive = ItemFactory.newArchiveSelector(GUIArchive.MODE_EXPORT, GUIArchive.STATUS_OPENED);
		archive.setTitle(I18N.message("selectopenarchive"));
		archive.setWrapTitle(false);
		archive.setRequired(true);

		ButtonItem send = new ButtonItem();
		send.setStartRow(false);
		send.setTitle(I18N.message("sendtoexparchive"));
		send.setAutoFit(true);
		send.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onSend(ids, document);
			}
		});

		form.setFields(archive, send);
		addItem(form);
	}

	public void onSend(long[] ids, boolean document) {
		if (!form.validate())
			return;

		if (document)
			service.addDocuments(Long.parseLong(form.getValueAsString("archive")), ids,
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
		else
			service.addFolder(Long.parseLong(form.getValueAsString("archive")), ids[0],
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