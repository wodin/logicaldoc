package com.logicaldoc.gui.frontend.client.folder;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.frontend.client.services.FolderService;
import com.logicaldoc.gui.frontend.client.services.FolderServiceAsync;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.widgets.Dialog;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.SubmitItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tree.TreeNode;

/**
 * This is the form used to create a new Folder
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.7.1
 */
public class CreateDialog extends Dialog {
	private FolderServiceAsync service = (FolderServiceAsync) GWT.create(FolderService.class);

	private DynamicForm form;

	public CreateDialog(final GUIFolder folder) {
		super();

		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(folder.getType() == 0 ? I18N.message("newfolder") : I18N.message("newworkspace"));
		setWidth(400);
		setHeight(150);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();
		setPadding(3);

		final boolean inheritOptionEnabled = "true".equals(Session.get().getInfo()
				.getConfig("gui.security.inheritoption"));

		form = new DynamicForm();
		form.setHeight100();
		form.setWidth100();

		CheckboxItem inheritSecurity = new CheckboxItem();
		inheritSecurity.setName("inheritSecurity");
		inheritSecurity.setTitle(I18N.message("inheritparentsec"));

		TextItem name = ItemFactory.newTextItem("name", "name", folder.getType() == 0 ? I18N.message("newfolder")
				: I18N.message("newworkspace"));
		name.setWidth(250);
		name.setRequired(true);
		name.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (event.getKeyName() != null && "enter".equals(event.getKeyName().toLowerCase()))
					onCreate(folder, inheritOptionEnabled);
			}
		});

		SubmitItem create = new SubmitItem();
		create.setTitle(I18N.message("create"));
		create.setAutoFit(true);
		create.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onCreate(folder, inheritOptionEnabled);
			}
		});

		if (inheritOptionEnabled) {
			form.setItems(name, inheritSecurity, create);
			inheritSecurity.setValue(Session.get().getInfo().getConfig("gui.security.inheritoption.default"));
		} else
			form.setItems(name, create);

		VLayout content = new VLayout();
		content.setTop(10);
		content.setWidth100();
		content.setHeight100();
		content.setMembersMargin(3);
		content.addMember(form);

		addItem(content);
	}

	private void onCreate(final GUIFolder folder, boolean inheritOptionEnabled) {
		if (form.validate()) {
			folder.setName(form.getValueAsString("name").trim());
			service.create(Session.get().getSid(), folder,
					!inheritOptionEnabled || "true".equals(form.getValueAsString("inheritSecurity")),
					new AsyncCallback<GUIFolder>() {

						@Override
						public void onFailure(Throwable caught) {
							Log.serverError(caught);
						}

						@Override
						public void onSuccess(GUIFolder newFolder) {
							TreeNode newNode = new TreeNode(newFolder.getName());
							newNode.setAttribute("name", newFolder.getName());
							newNode.setAttribute("folderId", Long.toString(newFolder.getId()));
							newNode.setAttribute("type", Long.toString(newFolder.getType()));

							if (newFolder.getType() == 1) {
								FolderNavigator.get().getTree().add(newNode, FolderNavigator.get().getTree().getRoot());
							} else {
								TreeNode selectedNode = (TreeNode) FolderNavigator.get().getSelectedRecord();
								if (!FolderNavigator.get().getTree().isOpen(selectedNode))
									FolderNavigator.get().getTree().openFolder(selectedNode);
								FolderNavigator.get().getTree().add(newNode, selectedNode);
							}

							destroy();
						}
					});
		}
	}
}