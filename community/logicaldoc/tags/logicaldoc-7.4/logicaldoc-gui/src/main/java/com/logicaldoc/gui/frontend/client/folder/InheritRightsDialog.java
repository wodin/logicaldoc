package com.logicaldoc.gui.frontend.client.folder;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.LD;
import com.logicaldoc.gui.common.client.widgets.FolderTree;
import com.logicaldoc.gui.frontend.client.services.FolderService;
import com.logicaldoc.gui.frontend.client.services.FolderServiceAsync;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Dialog;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tree.TreeGrid;

/**
 * This is the form used to select a folder to inherit the rights from
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 7.1
 */
public class InheritRightsDialog extends Dialog {

	private FolderServiceAsync folderService = (FolderServiceAsync) GWT.create(FolderService.class);

	public InheritRightsDialog(final SecurityPanel panel) {
		super();
		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.message("inheritrights"));
		setWidth(250);
		setHeight(270);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();
		setPadding(3);

		VLayout content = new VLayout();
		content.setTop(10);
		content.setWidth100();
		content.setHeight100();
		content.setMembersMargin(3);

		final TreeGrid folders = new FolderTree();
		folders.setWidth100();
		folders.setHeight100();

		VLayout buttons = new VLayout();
		buttons.setWidth100();
		buttons.setHeight(30);

		Button inheritRights = new Button(I18N.message("inheritrights"));
		inheritRights.setAutoFit(true);
		inheritRights.setMargin(1);
		inheritRights.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				LD.ask(I18N.message("inheritrights"),
						I18N.message("inheritrightsask", new String[] {
								FolderNavigator.get().getSelectedRecord().getAttributeAsString("name"),
								folders.getSelectedRecord().getAttributeAsString("name") }), new BooleanCallback() {

							@Override
							public void execute(Boolean value) {
								if (value) {
									folderService.inheritRights(Session.get().getSid(), panel.getFolder().getId(), Long
											.parseLong(folders.getSelectedRecord().getAttributeAsString("folderId")),
											new AsyncCallback<GUIFolder>() {

												@Override
												public void onFailure(Throwable caught) {
													Log.serverError(caught);
													destroy();
												}

												@Override
												public void onSuccess(GUIFolder arg) {
													panel.refresh(arg);
													destroy();
												}
											});
								}
								destroy();
							}
						});
			}
		});

		buttons.setMembers(inheritRights);

		content.setMembers(folders, buttons);
		addItem(content);
	}
}