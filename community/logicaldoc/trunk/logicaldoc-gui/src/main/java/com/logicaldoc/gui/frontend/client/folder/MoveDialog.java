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
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tree.TreeGrid;

/**
 * This is the form used to save and update the current search
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class MoveDialog extends Dialog {
	public MoveDialog() {
		super();
		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.message("move"));
		setWidth(250);
		setHeight(280);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();
		setPadding(5);

		VLayout content = new VLayout();
		content.setTop(20);
		content.setWidth100();
		content.setHeight100();
		content.setMembersMargin(3);

		final TreeGrid folders = new FolderTree();
		folders.setWidth100();
		folders.setHeight100();

		VLayout buttons = new VLayout();
		buttons.setWidth100();
		buttons.setHeight(50);

		Button move = new Button(I18N.message("move"));
		move.setAutoFit(true);
		move.setMargin(1);
		move.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				LD.ask(I18N.message("move"),
						I18N.message("moveask", new String[] {
								Navigator.get().getSelectedRecord().getAttributeAsString("name"),
								folders.getSelectedRecord().getAttributeAsString("name") }), new BooleanCallback() {

							@Override
							public void execute(Boolean value) {
								if (value) {
									Navigator.get()
											.moveTo(Long.parseLong(folders.getSelectedRecord().getAttributeAsString(
													"folderId")));
								}
								destroy();
							}
						});
			}
		});

		buttons.setMembers(move);

		content.setMembers(folders, buttons);
		addChild(content);
	}
}