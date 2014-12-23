package com.logicaldoc.gui.frontend.client.folder;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.widgets.FolderTree;
import com.logicaldoc.gui.frontend.client.services.FolderService;
import com.logicaldoc.gui.frontend.client.services.FolderServiceAsync;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Dialog;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeNode;

/**
 * This is the form used to apply a folder template
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 7.1
 */
public class ApplyTemplateDialog extends Dialog {
	private FolderServiceAsync service = (FolderServiceAsync) GWT.create(FolderService.class);

	public ApplyTemplateDialog() {
		super();
		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.message("applytemplate"));
		setWidth(400);
		setHeight(140);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();
		setPadding(2);

		VLayout content = new VLayout();
		content.setTop(10);
		content.setWidth100();
		content.setHeight100();
		content.setMembersMargin(3);

		final TreeGrid folders = new FolderTree();
		folders.setWidth100();
		folders.setHeight100();

		final boolean inheritOptionEnabled = "true".equals(Session.get().getInfo()
				.getConfig("gui.security.inheritoption"));

		final DynamicForm form = new DynamicForm();

		CheckboxItem inheritSecurity = new CheckboxItem();
		inheritSecurity.setName("inheritSecurity");
		inheritSecurity.setTitle(I18N.message("inheritparentsec"));
		form.setItems(inheritSecurity);

		SelectItem templateSelector = ItemFactory.newFolderTemplateSelector();

		if (inheritOptionEnabled)
			form.setItems(templateSelector, inheritSecurity);

		Button apply = new Button(I18N.message("apply"));
		apply.setAutoFit(true);
		apply.setMargin(1);
		apply.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (!form.validate())
					return;

				final TreeNode selectedNode = (TreeNode) Navigator.get().getSelectedRecord();
				final long folderId = Long.parseLong(selectedNode.getAttributeAsString("folderId"));
				long templateId = Long.parseLong(form.getValueAsString("foldertemplate"));

				service.applyTemplate(Session.get().getSid(), folderId, templateId,
						inheritOptionEnabled && "true".equals(form.getValueAsString("inheritSecurity")),
						new AsyncCallback<Void>() {

							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught);
							}

							@Override
							public void onSuccess(Void arg0) {
								Navigator.get().getTree().reloadChildren(selectedNode);
								Log.info(I18N.message("templateapplied"), null);
								destroy();
							}
						});
			}
		});

		content.setMembers(form, apply);
		addItem(content);
	}
}