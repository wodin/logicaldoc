package com.logicaldoc.gui.frontend.client.folder;

import java.util.LinkedHashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.frontend.client.services.AuditService;
import com.logicaldoc.gui.frontend.client.services.AuditServiceAsync;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;

/**
 * This is the form used for the workflow task setting.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class SubscriptionDialog extends Window {
	private AuditServiceAsync service = (AuditServiceAsync) GWT.create(AuditService.class);

	public SubscriptionDialog(final long folderId) {
		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.message("foldersubscription"));
		setWidth(290);
		setHeight(100);
		setMembersMargin(5);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();
		setPadding(5);
		setAutoSize(true);

		final DynamicForm form = new DynamicForm();
		form.setTitleOrientation(TitleOrientation.TOP);
		form.setNumCols(1);
		TextItem taskName = ItemFactory.newTextItem("taskName", "name", null);
		taskName.setRequired(true);

		SelectItem option = new SelectItem("option", I18N.message("subscriptionoption"));
		option.setWidth(280);
		LinkedHashMap<String, String> options = new LinkedHashMap<String, String>();
		options.put("current", I18N.message("subscribecurrent"));
		options.put("subfolders", I18N.message("subscribesubfolders"));
		option.setValueMap(options);
		option.setValue("current");

		ButtonItem subscribe = new ButtonItem();
		subscribe.setTitle(I18N.message("subscribe"));
		subscribe.setAutoFit(true);
		subscribe.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				service.subscribeFolder(Session.get().getSid(), folderId,
						form.getValueAsString("option").equals("current"), new AsyncCallback<Void>() {
							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught);
							}

							@Override
							public void onSuccess(Void ret) {
								Log.info(I18N.message("foldersubscribed"), null);
								Session.get().getUser()
										.setSubscriptions(Session.get().getUser().getSubscriptions() + 1);
							}
						});
				destroy();
			}
		});

		form.setItems(option, subscribe);
		addItem(form);
	}
}
