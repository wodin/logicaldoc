package com.logicaldoc.gui.frontend.client.system;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIParameter;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.common.client.widgets.ContactingServer;
import com.logicaldoc.gui.frontend.client.services.UpdateService;
import com.logicaldoc.gui.frontend.client.services.UpdateServiceAsync;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Progressbar;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;

/**
 * Updates check panel
 * 
 * @author Marco Meschieri - LogicalDOC
 * @since 7.3
 */
public class UpdatePanel extends HLayout {
	private UpdateServiceAsync service = (UpdateServiceAsync) GWT.create(UpdateService.class);

	private TabSet tabs = new TabSet();

	private Tab tab = new Tab();

	// Shows the install notes panel
	VLayout updateNotesPanel = new VLayout();

	private IButton download = new IButton(I18N.message("download"));

	public UpdatePanel() {
		setWidth100();
		setHeight100();

		setMembersMargin(10);

		tabs.setTabs(tab);
		setMembers(tabs);
		tab.setTitle(I18N.message("updates"));

		ContactingServer.get().show();

		service.checkUpdate(Session.get().getInfo().getUserNo(), Session.get().getInfo().getRelease(),
				new AsyncCallback<GUIParameter[]>() {
					@Override
					public void onFailure(Throwable caught) {
						ContactingServer.get().hide();
						Log.serverError(caught);
						onPackageUnavailable();
					}

					@Override
					public void onSuccess(GUIParameter[] parameters) {
						ContactingServer.get().hide();

						if (parameters != null) {
							DynamicForm form = new DynamicForm();
							form.setWidth(300);
							form.setTitleOrientation(TitleOrientation.LEFT);

							StaticTextItem name = ItemFactory.newStaticTextItem("name", "name",
									Util.getValue("name", parameters));
							name.setRequired(true);

							StaticTextItem date = ItemFactory.newStaticTextItem("date", "date",
									Util.getValue("date", parameters));
							date.setRequired(true);

							StaticTextItem size = ItemFactory.newStaticTextItem("size", "size",
									Util.formatSize(Long.parseLong(Util.getValue("size", parameters))));
							size.setRequired(true);

							StaticTextItem target = ItemFactory.newStaticTextItem("target", "updatesto",
									Util.getValue("target", parameters));
							target.setRequired(true);

							form.setItems(name, date, size, target);

							Label message = new Label();
							message.setContents(I18N.message("updatepackagefound"));
							message.setWrap(false);
							message.setAlign(Alignment.LEFT);
							message.setStyleName("updateavailable");
							message.setLayoutAlign(Alignment.LEFT);
							message.setLayoutAlign(VerticalAlignment.TOP);
							message.setHeight(20);

							VLayout download = prepareDownloadProgress(parameters);

							VLayout infoPanel = new VLayout();
							infoPanel.setMembersMargin(10);
							infoPanel.setMembers(form, download);

							HLayout body = new HLayout();
							body.setWidth100();
							body.setMembersMargin(50);
							updateNotesPanel.setWidth100();
							body.setMembers(infoPanel, updateNotesPanel);

							VLayout layout = new VLayout();
							layout.setMembers(message, body);

							tab.setPane(layout);
						} else {
							onPackageUnavailable();
						}
					}
				});
	}

	private void onPackageUnavailable() {
		Label message = new Label();
		message.setContents(I18N.message("updatepackagenotfound"));
		message.setWrap(false);
		message.setAlign(Alignment.LEFT);
		message.setStyleName("updateunavailable");
		message.setLayoutAlign(Alignment.LEFT);
		message.setLayoutAlign(VerticalAlignment.TOP);
		message.setHeight(20);

		tab.setPane(message);
	}

	private VLayout prepareDownloadProgress(final GUIParameter[] parameters) {

		final String updateFileName = Util.getValue("file", parameters);

		VLayout layout = new VLayout(4);
		layout.setWidth(260);

		final Label barLabel = new Label(I18N.message("downloadprogress"));
		barLabel.setHeight(16);
		layout.addMember(barLabel);

		final Progressbar bar = new Progressbar();
		bar.setHeight(24);
		bar.setVertical(false);
		layout.addMember(bar);

		final IButton confirmUpdate = new IButton(I18N.message("confirmupdate"));
		confirmUpdate.setAutoFit(true);
		confirmUpdate.setVisible(false);
		confirmUpdate.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				SC.ask(I18N.message("confirmupdate"), I18N.message("confirmupdatequestion"), new BooleanCallback() {

					@Override
					public void execute(Boolean choice) {
						if (choice.booleanValue()) {
							confirmUpdate.setVisible(false);
							download.setVisible(false);
							service.confirm(updateFileName, new AsyncCallback<String>() {

								@Override
								public void onFailure(Throwable caught) {
									Log.serverError(caught);
								}

								@Override
								public void onSuccess(String path) {
									SC.say(I18N.message("updaterunning", path.replaceAll("\\\\", "/")));
								}
							});
						}
					}
				});
			}
		});

		download = new IButton(I18N.message("download"));
		download.setAutoFit(true);
		download.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				bar.setPercentDone(0);
				download.setDisabled(true);

				service.download(Session.get().getInfo().getUserNo(), Util.getValue("id", parameters), updateFileName,
						Integer.parseInt(Util.getValue("size", parameters)), new AsyncCallback<Void>() {

							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught);
								download.setDisabled(false);
							}

							@Override
							public void onSuccess(Void arg) {
								confirmUpdate.setVisible(false);

								new Timer() {
									public void run() {
										service.checkDownloadStatus(new AsyncCallback<int[]>() {

											@Override
											public void onFailure(Throwable caught) {
												Log.serverError(caught);
											}

											@Override
											public void onSuccess(int[] status) {
												bar.setPercentDone(status[1]);

												if (status[1] == 100) {
													download.setDisabled(false);
													confirmUpdate.setVisible(true);
													displayUpdateNotes(updateFileName);
												} else
													schedule(50);
											}
										});
									}
								}.schedule(50);
							}
						});
			}
		});

		HLayout buttonCanvas = new HLayout();
		buttonCanvas.setMembersMargin(6);
		buttonCanvas.setMembers(download, confirmUpdate);

		layout.addMember(buttonCanvas);

		return layout;
	}

	private void displayUpdateNotes(String fileName) {
		service.getNotes(fileName, new AsyncCallback<String[]>() {

			@Override
			public void onFailure(Throwable caught) {
				Log.serverError(caught);
			}

			@Override
			public void onSuccess(String[] infos) {
				DynamicForm form = new DynamicForm();
				form.setTitleOrientation(TitleOrientation.TOP);
				form.setColWidths("*");
				form.setNumCols(1);

				TextAreaItem changelog = ItemFactory.newTextAreaItem("changelog", "changelog", infos[0]);
				changelog.setWidth("100%");
				changelog.setHeight(220);

				TextAreaItem updatenotes = ItemFactory.newTextAreaItem("updatenotes", "updatenotes", infos[1]);
				updatenotes.setWidth("100%");
				updatenotes.setHeight(220);

				form.setItems(updatenotes, changelog);

				updateNotesPanel.addMember(form);
			}
		});
	}
}