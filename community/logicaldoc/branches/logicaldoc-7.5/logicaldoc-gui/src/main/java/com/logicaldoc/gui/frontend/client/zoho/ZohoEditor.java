package com.logicaldoc.gui.frontend.client.zoho;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.WindowUtils;
import com.logicaldoc.gui.common.client.widgets.ContactingServer;
import com.logicaldoc.gui.frontend.client.document.grid.DocumentsGrid;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.DocumentServiceAsync;
import com.logicaldoc.gui.frontend.client.services.ZohoService;
import com.logicaldoc.gui.frontend.client.services.ZohoServiceAsync;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * This popup window is used to show the document in Zoho.
 * 
 * @author Marco Meschieri - LogicalDOC
 * @since 7.5.1
 */
public class ZohoEditor extends Window {

	protected DocumentServiceAsync documentService = (DocumentServiceAsync) GWT.create(DocumentService.class);

	protected ZohoServiceAsync zohoService = (ZohoServiceAsync) GWT.create(ZohoService.class);

	private VLayout layout = null;

	private GUIDocument document;

	private DocumentsGrid grid;

	public ZohoEditor(final GUIDocument document, final DocumentsGrid grid) {
		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.message("editdoc") + ": " + document.getTitle());

		this.document = document;
		this.grid = grid;

		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		setAutoSize(true);
		centerInPage();
		setMargin(2);

		addCloseClickHandler(new CloseClickHandler() {
			@Override
			public void onCloseClick(CloseClickEvent event) {
				onCancel();
			}
		});

		layout = new VLayout();
		layout.setMargin(1);
		layout.setWidth100();
		layout.setHeight100();
		addItem(layout);

		prepareBody();
	}

	/**
	 * Reloads a preview.
	 */
	private void prepareBody() {
		Label editorUrl = new Label("<span style='text-decoration: underline'>" + I18N.message("clicktoopenzohoeditor")
				+ "</span>");
		editorUrl.setHeight(30);
		editorUrl.setWidth(300);
		editorUrl.setWrap(false);
		editorUrl.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String url = "https://docs.zoho.com/writer/open/";
				if (document.getFileName().toLowerCase().contains(".xls")
						|| document.getFileName().toLowerCase().contains(".ods"))
					url = "https://docs.zoho.com/sheet/ropen.do?rid=";
				else if (document.getFileName().toLowerCase().contains(".ppt")
						|| document.getFileName().toLowerCase().contains(".odp"))
					url = "https://docs.zoho.com/show/open/";
				url += document.getExtResId();

				WindowUtils.openUrl(url, "_blank");
			}
		});

		Label spacer20 = new Label("");
		spacer20.setHeight(20);

		Label clickHint = new Label(I18N.message("clickoncheckin"));
		clickHint.setWrap(false);
		clickHint.setWidth(300);
		clickHint.setHeight(30);

		Label spacer30 = new Label("");
		spacer30.setHeight(30);

		IButton cancel = new IButton(I18N.message("cancel"));
		cancel.setAutoFit(true);
		cancel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onCancel();
			}
		});

		IButton checkin = new IButton(I18N.message("cancel"));
		checkin.setTitle(document.getId() != 0 ? I18N.message("checkin") : I18N.message("save"));
		checkin.setAutoFit(true);
		checkin.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ZohoCheckin checkin = new ZohoCheckin(document, ZohoEditor.this, grid);
				checkin.show();
			}
		});

		Label hSpacer = new Label("");
		hSpacer.setWidth(15);

		HLayout buttonsContainer = new HLayout();
		buttonsContainer.setAutoWidth();
		buttonsContainer.setHeight(20);
		buttonsContainer.setAlign(Alignment.CENTER);

		buttonsContainer.setMembers(checkin, hSpacer, cancel);

		layout.setMembers(editorUrl, spacer20, clickHint, spacer30, buttonsContainer);
	}

	private void onCancel() {
		documentService.unlock(new long[] { ZohoEditor.this.document.getId() }, new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				Log.serverError(caught);
				destroy();
			}

			@Override
			public void onSuccess(Void result) {
				grid.markSelectedAsCheckedIn();
				Session.get().setCurrentDocument(document);
				ContactingServer.get().show();
				zohoService.delete(ZohoEditor.this.document.getExtResId(), new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						ContactingServer.get().hide();
						Log.serverError(caught);
						destroy();
					}

					@Override
					public void onSuccess(Void result) {
						ContactingServer.get().hide();
						destroy();
					}
				});
			}
		});
	}
}