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
		setWidth(350);
		setHeight(130);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();
		setPadding(3);

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

		SubmitItem create = new SubmitItem();
		create.setTitle(I18N.message("create"));
		create.setAutoFit(true);
		create.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (form.validate()) {
					folder.setName(form.getValueAsString("name").trim());
					service.create(Session.get().getSid(), folder,
							"true".equals(form.getValueAsString("inheritSecurity")), new AsyncCallback<GUIFolder>() {

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

									if (newFolder.getType() == 0) {
										TreeNode selectedNode = (TreeNode) Navigator.get().getSelectedRecord();
										if (!Navigator.get().getTree().isOpen(selectedNode))
											Navigator.get().getTree().openFolder(selectedNode);
										Navigator.get().getTree().add(newNode, selectedNode);
									} else {
										Navigator.get().getTree().add(newNode, Navigator.get().getTree().getRoot());
									}
									
									destroy();
								}
							});
				}
			}
		});

		form.setItems(name, inheritSecurity, create);

		VLayout content = new VLayout();
		content.setTop(10);
		content.setWidth100();
		content.setHeight100();
		content.setMembersMargin(3);
		content.addMember(form);

		addItem(content);
	}
}