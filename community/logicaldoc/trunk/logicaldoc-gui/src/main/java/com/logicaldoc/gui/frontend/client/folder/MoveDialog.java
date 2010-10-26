package com.logicaldoc.gui.frontend.client.folder;

import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.widgets.FolderTree;
import com.smartgwt.client.types.HeaderControls;
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
		setHeight(205);
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
		folders.setHeight(150);
		folders.setWidth100();

		Button move = new Button();
		move.setTitle(I18N.message("move"));
		move.setMargin(1);
		move.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				FoldersNavigator.get().moveTo(
						Long.parseLong(folders.getSelectedRecord().getAttributeAsString("id")));
				destroy();
			}
		});

		content.setMembers(folders, move);
		addChild(content);
	}
}
