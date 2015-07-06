package com.logicaldoc.gui.frontend.client.gdrive;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.widgets.ContactingServer;
import com.logicaldoc.gui.frontend.client.document.DocumentsPanel;
import com.logicaldoc.gui.frontend.client.document.grid.DocumentsGrid;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.DocumentServiceAsync;
import com.logicaldoc.gui.frontend.client.services.GDriveService;
import com.logicaldoc.gui.frontend.client.services.GDriveServiceAsync;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.ResizedEvent;
import com.smartgwt.client.widgets.events.ResizedHandler;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

/**
 * This popup window is used to show the document in Google Drive.
 * 
 * @author Marco Meschieri - LogicalDOC
 * @since 7.3
 */
public class GDriveEditor extends Window {

	protected DocumentServiceAsync documentService = (DocumentServiceAsync) GWT.create(DocumentService.class);

	protected GDriveServiceAsync gdriveService = (GDriveServiceAsync) GWT.create(GDriveService.class);

	private HTMLFlow html = new HTMLFlow();

	private VLayout layout = null;

	private GUIDocument document;

	private DocumentsGrid grid;

	public GDriveEditor(final GUIDocument document, final DocumentsGrid grid) {
		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		if (document.getId() > 0)
			setTitle(I18N.message("editdoc") + ": " + document.getTitle());
		else
			setTitle(I18N.message("createdoc") + ": " + document.getTitle());

		this.document = document;
		this.grid = grid;

		setWidth(com.google.gwt.user.client.Window.getClientWidth());
		setHeight(com.google.gwt.user.client.Window.getClientHeight());
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();
		setMargin(2);

		addCloseClickHandler(new CloseClickHandler() {
			@Override
			public void onCloseClick(CloseClickEvent event) {
				if (document.getId() != 0)
					destroy();
				else {
					// Creating a new document document, so delete the temporary
					// doc in Google Drive
					ContactingServer.get().show();
					gdriveService.delete(Session.get().getSid(), GDriveEditor.this.document.getExtResId(),
							new AsyncCallback<Void>() {
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
			}
		});

		addResizedHandler(new ResizedHandler() {
			@Override
			public void onResized(ResizedEvent event) {
				reloadBody();
			}
		});

		layout = new VLayout();
		layout.setMargin(1);
		layout.setWidth100();
		layout.setHeight100();
		addItem(layout);

		reloadBody();
	}

	/**
	 * Reloads a preview.
	 */
	private void reloadBody() {
		String url =  "https://docs.google.com/document/d/" + document.getExtResId() + "/edit?hl=" + Session.get().getUser().getLanguage();
//		if (document.getExtResId().startsWith("spreadsheet:"))
//			url = "https://spreadsheets.google.com/ccc?key="
//					+ document.getExtResId().substring("spreadsheet:".length()) + "&hl="
//					+ Session.get().getUser().getLanguage();
//		else if (document.getExtResId().startsWith("presentation:"))
//			url = "https://docs.google.com/presentation/d/"
//					+ document.getExtResId().substring("presentation:".length()) + "/edit?hl="
//					+ Session.get().getUser().getLanguage();
//		else
//			url = "https://docs.google.com/document/d/" + document.getExtResId().substring("document:".length())
//					+ "/edit?hl=" + Session.get().getUser().getLanguage();
	
		
		String iframe = "<iframe src='" + url + "' style='border: 0px solid white; width:" + (getWidth() - 18)
				+ "px; height:" + (getHeight() - 68) + "px' scrolling='no'></iframe>";
		html.setContents(iframe);

		html = new HTMLFlow();
		html.setWidth100();
		html.setHeight100();
		html.setShowEdges(false);
		html.setContents(iframe);

		ToolStrip toolStrip = new ToolStrip();
		toolStrip.setHeight(20);
		toolStrip.setWidth100();
		toolStrip.setAlign(Alignment.RIGHT);
		toolStrip.addFill();

		ToolStripButton close = new ToolStripButton();
		close.setTitle(I18N.message("close"));
		toolStrip.addButton(close);
		toolStrip.addSeparator();
		close.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				destroy();
			}
		});

		ToolStripButton cancel = new ToolStripButton();
		cancel.setTitle(I18N.message("cancel"));
		toolStrip.addButton(cancel);
		toolStrip.addSeparator();
		cancel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				documentService.unlock(Session.get().getSid(), new long[] { GDriveEditor.this.document.getId() },
						new AsyncCallback<Void>() {
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
								gdriveService.delete(Session.get().getSid(), GDriveEditor.this.document.getExtResId(),
										new AsyncCallback<Void>() {
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
		});

		ToolStripButton checkin = new ToolStripButton();
		checkin.setTitle(document.getId() != 0 ? I18N.message("checkin") : I18N.message("save"));
		toolStrip.addButton(checkin);
		checkin.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (document.getId() != 0) {
					GDriveCheckin checkin = new GDriveCheckin(document, GDriveEditor.this, grid);
					checkin.show();
				} else {
					ContactingServer.get().show();
					gdriveService.importDocuments(Session.get().getSid(), new String[] { document.getExtResId() },
							Session.get().getCurrentFolder().getId(), document.getType(), new AsyncCallback<Void>() {
								@Override
								public void onFailure(Throwable caught) {
									ContactingServer.get().hide();
									Log.serverError(caught);
									destroy();
								}

								@Override
								public void onSuccess(Void result) {
									DocumentsPanel.get().refresh();

									// Delete the temporary resource in GDrive
									gdriveService.delete(Session.get().getSid(), document.getExtResId(),
											new AsyncCallback<Void>() {

												@Override
												public void onFailure(Throwable caught) {
													ContactingServer.get().hide();
													Log.serverError(caught);
													destroy();
												}

												@Override
												public void onSuccess(Void ret) {
													ContactingServer.get().hide();
													destroy();
												}
											});
								}
							});
				}
			}
		});

		layout.setMembers(toolStrip, html);
	}
}