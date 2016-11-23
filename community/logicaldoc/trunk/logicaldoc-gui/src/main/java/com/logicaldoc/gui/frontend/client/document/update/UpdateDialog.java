package com.logicaldoc.gui.frontend.client.document.update;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.util.LD;
import com.logicaldoc.gui.common.client.widgets.ContactingServer;
import com.logicaldoc.gui.frontend.client.document.DocumentsPanel;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.DocumentServiceAsync;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * This popup window is used to allow the users to input the data for a bulk
 * update or other update operations
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.3
 */
public class UpdateDialog extends Window {

	public final static String CONTEXT_CHECKIN = "checkin";

	public final static String CONTEXT_UPLOAD = "addocuments";

	public final static String CONTEXT_UPDATE = "bulkupdate";

	private DocumentServiceAsync documentService = (DocumentServiceAsync) GWT.create(DocumentService.class);

	private UpdatePanel bulkPanel;

	private boolean zip = false;

	private boolean immediteIndexing = false;

	private String charset = "UTF-8";

	public UpdateDialog(final long[] ids, final GUIDocument metadata, final String context, final boolean majorVersion) {
		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);

		addCloseClickHandler(new CloseClickHandler() {
			@Override
			public void onCloseClick(CloseClickEvent event) {
				destroy();
			}
		});

		setTitle(I18N.message(context));
		setWidth(800);
		setHeight(300);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();

		/*
		 * Since the document is locked, temporarily alter the status to have the
		 * editing enabled.
		 */
		int originalStatus = 0;
		if (CONTEXT_CHECKIN.equals(context) && metadata != null) {
			originalStatus = metadata.getStatus();
			metadata.setStatus(0);
		}
		bulkPanel = new UpdatePanel(metadata, CONTEXT_UPLOAD.equals(context));
		bulkPanel.setWidth100();
		bulkPanel.setHeight("*");
		bulkPanel.setShowResizeBar(false);
		if (CONTEXT_CHECKIN.equals(context) && metadata != null)
			metadata.setStatus(originalStatus);

		HTMLPane spacer = new HTMLPane();
		spacer.setContents("<div>&nbsp;</div>");
		spacer.setWidth("60%");
		spacer.setOverflow(Overflow.HIDDEN);

		TextItem versionComment = ItemFactory.newTextItem("versionComment", "versioncomment", null);
		versionComment.setWidth(400);

		final DynamicForm saveForm = new DynamicForm();
		saveForm.setMargin(3);
		saveForm.setTitleOrientation(TitleOrientation.LEFT);
		saveForm.setNumCols(1);
		saveForm.setShowResizeBar(false);
		saveForm.setItems(versionComment);

		Button saveButton = new Button(CONTEXT_CHECKIN.equals(context) ? I18N.message("checkin") : I18N.message("save"));
		saveButton.setAutoFit(true);
		saveButton.setLayoutAlign(VerticalAlignment.CENTER);
		saveButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (!bulkPanel.validate())
					return;

				if (CONTEXT_CHECKIN.equals(context)) {
					ContactingServer.get().show();
					documentService.checkin(metadata, majorVersion, new AsyncCallback<GUIDocument>() {

						@Override
						public void onFailure(Throwable error) {
							ContactingServer.get().hide();
							Log.serverError(error);
						}

						@Override
						public void onSuccess(GUIDocument doc) {
							ContactingServer.get().hide();
							DocumentsPanel.get().onDocumentSaved(doc);
							DocumentsPanel.get().getDocumentsGrid().markSelectedAsCheckedIn();
							Session.get().getUser().setCheckedOutDocs(Session.get().getUser().getCheckedOutDocs() - 1);
							DocumentsPanel.get().selectDocument(doc.getId(), false);
							destroy();
						}
					});
				} else if (ids != null && ids.length > 0)
					LD.ask(I18N.message("bulkupdate"), I18N.message("bulkwarning"), new BooleanCallback() {
						@Override
						public void execute(Boolean value) {
							if (value) {
								bulkPanel.getDocument().setComment(saveForm.getValueAsString("versionComment"));
								ContactingServer.get().show();
								documentService.bulkUpdate(ids, bulkPanel.getDocument(), new AsyncCallback<Void>() {
									@Override
									public void onFailure(Throwable error) {
										ContactingServer.get().hide();
										Log.serverError(error);
									}

									@Override
									public void onSuccess(Void arg0) {
										ContactingServer.get().hide();
										Log.info(I18N.message("bulkapplied"), null);
										DocumentsPanel.get().refresh();
										destroy();
									}
								});
							}
						}
					});
				else {
					bulkPanel.getDocument().setComment(saveForm.getValueAsString("versionComment"));
					ContactingServer.get().show();
					documentService.addDocuments(zip, charset, immediteIndexing, bulkPanel.getDocument(),
							new AsyncCallback<GUIDocument[]>() {

								@Override
								public void onSuccess(GUIDocument[] arg0) {
									DocumentsPanel.get().refresh();
									documentService.cleanUploadedFileFolder(new AsyncCallback<Void>() {

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

								@Override
								public void onFailure(Throwable error) {
									ContactingServer.get().hide();
									Log.serverError(error);

									// We have to refresh the documents list
									// because maybe
									// some documents have been stored.
									DocumentsPanel.get().refresh();
									documentService.cleanUploadedFileFolder(new AsyncCallback<Void>() {

										@Override
										public void onFailure(Throwable caught) {
											Log.serverError(caught);
											destroy();
										}

										@Override
										public void onSuccess(Void result) {
											destroy();
										}
									});
								}
							});
				}
			}
		});

		HLayout savePanel = new HLayout();
		savePanel.addMember(saveButton);
		if (!CONTEXT_CHECKIN.equals(context))
			savePanel.addMember(saveForm);
		savePanel.addMember(spacer);
		savePanel.setWidth100();
		savePanel.setHeight(30);
		savePanel.setMargin(2);
		savePanel.setMembersMargin(10);

		VLayout content = new VLayout();
		content.setTop(10);
		content.setWidth100();
		content.setHeight100();
		content.setMembersMargin(3);

		content.setMembers(bulkPanel, savePanel);

		addItem(content);
	}

	public boolean isZip() {
		return zip;
	}

	public void setZip(boolean zip) {
		this.zip = zip;
	}

	public void setImmediateIndexing(boolean immediteIndexing) {
		this.immediteIndexing = immediteIndexing;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}
}