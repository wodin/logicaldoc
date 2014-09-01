package com.logicaldoc.gui.frontend.client.folder;

import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.LD;
import com.logicaldoc.gui.common.client.widgets.FolderTree;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Dialog;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tree.TreeGrid;

/**
 * This is the form used to copy a folder into another path
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 7.1
 */
public class CopyDialog extends Dialog {
	public CopyDialog() {
		super();
		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.message("copy"));
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

		HLayout buttons = new HLayout();
		buttons.setWidth100();
		buttons.setHeight(30);

		Button copy = new Button(I18N.message("copy"));
		copy.setAutoFit(true);
		copy.setMargin(1);
		copy.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				LD.ask(I18N.message("copy"),
						I18N.message("copyask", new String[] {
								Navigator.get().getSelectedRecord().getAttributeAsString("name"),
								folders.getSelectedRecord().getAttributeAsString("name") }), new BooleanCallback() {

							@Override
							public void execute(Boolean value) {
								if (value) {
									
									Navigator.get()
											.copyTo(Long.parseLong(folders.getSelectedRecord().getAttributeAsString(
													"folderId")), false);
								}
								destroy();
							}
						});
			}
		});

		Button copyFolders = new Button(I18N.message("copyfoldersonly"));
		copyFolders.setAutoFit(true);
		copyFolders.setMargin(1);
		copyFolders.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				LD.ask(I18N.message("copy"),
						I18N.message("copyask", new String[] {
								Navigator.get().getSelectedRecord().getAttributeAsString("name"),
								folders.getSelectedRecord().getAttributeAsString("name") }), new BooleanCallback() {

							@Override
							public void execute(Boolean value) {
								if (value) {
									Navigator.get()
											.copyTo(Long.parseLong(folders.getSelectedRecord().getAttributeAsString(
													"folderId")), true);
								}
								destroy();
							}
						});
			}
		});

		buttons.setMembers(copy, copyFolders);

		content.setMembers(folders, buttons);
		addItem(content);
	}
}