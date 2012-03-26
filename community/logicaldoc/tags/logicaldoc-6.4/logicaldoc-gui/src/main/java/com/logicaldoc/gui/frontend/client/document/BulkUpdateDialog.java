package com.logicaldoc.gui.frontend.client.document;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.util.LD;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.DocumentServiceAsync;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * This popup window is used to allow the users to input the data for a bulk
 * update
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.3
 */
public class BulkUpdateDialog extends Window {
	private DocumentServiceAsync documentService = (DocumentServiceAsync) GWT.create(DocumentService.class);

	private VLayout layout = new VLayout();

	private BulkUpdatePanel bulkPanel;

	private String encoding;

	private boolean zip = false;

	public BulkUpdateDialog(final long[] ids, GUIDocument metadata) {
		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);

		addCloseClickHandler(new CloseClickHandler() {
			@Override
			public void onCloseClick(CloseClickEvent event) {
				destroy();
			}
		});

		setTitle(I18N.message("bulkupdate"));
		setWidth(650);
		setHeight(350);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();

		bulkPanel = new BulkUpdatePanel(metadata);
		bulkPanel.setWidth100();
		bulkPanel.setHeight("84%");
		bulkPanel.setShowResizeBar(false);

		HLayout savePanel = new HLayout();

		HTMLPane spacer = new HTMLPane();
		spacer.setContents("<div>&nbsp;</div>");
		spacer.setWidth("60%");
		spacer.setOverflow(Overflow.HIDDEN);

		TextItem versionComment = ItemFactory.newTextItem("versionComment", "versioncomment", null);
		versionComment.setWidth(300);

		final DynamicForm saveForm = new DynamicForm();
		saveForm.setMargin(3);
		saveForm.setTitleOrientation(TitleOrientation.LEFT);
		saveForm.setNumCols(1);
		saveForm.setHeight("16%");
		saveForm.setShowResizeBar(false);
		saveForm.setItems(versionComment);

		Button saveButton = new Button(I18N.message("save"));
		saveButton.setAutoFit(true);
		saveButton.setMargin(2);
		saveButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (!bulkPanel.validate())
					return;

				if (ids != null && ids.length > 0)
					LD.ask(I18N.message("bulkupdate"), I18N.message("bulkwarning"), new BooleanCallback() {
						@Override
						public void execute(Boolean value) {
							if (value) {
								bulkPanel.getDocument().setComment(saveForm.getValueAsString("versionComment"));
								documentService.bulkUpdate(Session.get().getSid(), ids, bulkPanel.getDocument(),
										new AsyncCallback<Void>() {

											@Override
											public void onSuccess(Void arg0) {
												Log.info(I18N.message("bulkapplied"), null);
												DocumentsPanel.get().refresh();
												destroy();
											}

											@Override
											public void onFailure(Throwable error) {
												Log.serverError(error);
											}
										});
							}
						}
					});
				else {
					bulkPanel.getDocument().setComment(saveForm.getValueAsString("versionComment"));
					documentService.addDocuments(Session.get().getSid(), encoding, zip, bulkPanel.getDocument(),
							new AsyncCallback<Void>() {

								@Override
								public void onSuccess(Void arg0) {
									DocumentsPanel.get().refresh();
									documentService.cleanUploadedFileFolder(Session.get().getSid(),
											new AsyncCallback<Void>() {

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

								@Override
								public void onFailure(Throwable error) {
									Log.serverError(error);

									// We have to refresh the documents list
									// because maybe
									// some documents have been stored.
									DocumentsPanel.get().refresh();
									documentService.cleanUploadedFileFolder(Session.get().getSid(),
											new AsyncCallback<Void>() {

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

		savePanel.addMember(saveButton);
		savePanel.addMember(saveForm);
		savePanel.addMember(spacer);
		savePanel.setHeight("16%");
		savePanel.setMembersMargin(10);
		savePanel.setWidth100();

		layout.setMembersMargin(10);
		layout.setTop(25);
		layout.setMargin(3);
		layout.setWidth100();
		layout.setHeight("99%");
		layout.setMembers(bulkPanel, savePanel);

		addChild(layout);
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public boolean isZip() {
		return zip;
	}

	public void setZip(boolean zip) {
		this.zip = zip;
	}
}