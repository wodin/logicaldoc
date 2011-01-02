package com.logicaldoc.gui.frontend.client.document;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.DocumentServiceAsync;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClientEvent;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * This popup window is used to upload documents to the server.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class ScanDialog extends Window {
	private ButtonItem send;

	private DocumentServiceAsync documentService = (DocumentServiceAsync) GWT.create(DocumentService.class);

	private DynamicForm form;

	private HTML applet = new HTML();

	public ScanDialog() {
		VLayout layout = new VLayout();
		layout.setMargin(25);

		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.message("scandocument"));
		setWidth(650);
		setHeight(500);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();

		addCloseClickHandler(new CloseClickHandler() {
			@Override
			public void onCloseClick(CloseClientEvent event) {
				documentService.cleanUploadedFileFolder(Session.get().getSid(), new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(Void result) {
						addCloseClickHandler(new CloseClickHandler() {
							@Override
							public void onCloseClick(CloseClientEvent event) {
								if (applet != null)
									applet.setHTML("");
								destroy();
							}
						});
					}
				});
			}
		});

		form = new DynamicForm();
		form.setNumCols(3);

		form.setTitleOrientation(TitleOrientation.TOP);

		SelectItem languageItem = ItemFactory.newLanguageSelector("language", false, false);
		languageItem.setRequired(true);
		languageItem.setValue(I18N.getLocale());

		SelectItem template = ItemFactory.newTemplateSelector(false, null);
		template.setMultiple(false);

		send = new ButtonItem();
		send.setStartRow(false);
		send.setTitle(I18N.message("send"));
		send.setAutoFit(true);
		send.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onSend();
			}
		});

		if (Feature.visible(Feature.TEMPLATE)) {
			form.setItems(languageItem, template, send);
			if (!Feature.enabled(Feature.LDAP)) {
				template.setDisabled(true);
				template.setTooltip(I18N.message("featuredisabled"));
			}
		} else
			form.setItems(languageItem, send);

		String tmp = "<applet name=\"ScanApplet\" archive=\""
				+ Util.contextPath()
				+ "applet/logicaldoc-scan.jar\"  code=\"com.logicaldoc.scan.applet.ScanApplet\" width=\"600\" height=\"400\">";
		tmp += "<param name=\"lang\" value=\"" + I18N.getLocale() + "\" />";
		tmp += "<param name=\"uploadUrl\" value=\"" + Util.contextPath() + "servlet.gupld?new_session=true&sid="
				+ Session.get().getSid() + "\" />";
		tmp += "</applet>";

		applet.setHTML(tmp);
		applet.setWidth("620px");
		applet.setHeight("400px");

		layout.addMember(applet);
		layout.addMember(form);
		addChild(layout);
	}

	public void onSend() {
		if (!form.validate())
			return;

		documentService.addDocuments(Session.get().getSid(), getLanguage(), Session.get().getCurrentFolder().getId(),
				"UTF-8", false, getTemplate(), new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
						SC.warn(I18N.message("scannoupload"));
					}

					@Override
					public void onSuccess(Void result) {
						DocumentsPanel.get().refresh();
						if (applet != null)
							applet.setHTML("");
						destroy();
					}
				});
	}

	public Long getTemplate() {
		if (form.getValueAsString("template") != null && !form.getValueAsString("template").trim().isEmpty())
			return new Long(form.getValueAsString("template"));
		else
			return null;
	}

	public String getLanguage() {
		return form.getValueAsString("language");
	}
}