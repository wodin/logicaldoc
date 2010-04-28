package com.logicaldoc.gui.common.client.widgets;

import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.frontend.client.data.FoldersDS;
import com.smartgwt.client.widgets.form.fields.FormItemIcon;
import com.smartgwt.client.widgets.form.fields.IPickTreeItem;
import com.smartgwt.client.widgets.form.fields.events.IconClickEvent;
import com.smartgwt.client.widgets.form.fields.events.IconClickHandler;

/**
 * Allows the selection of a specific folder
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class FolderSelector extends IPickTreeItem {
	public FolderSelector() {
		setName("folder");
		FoldersDS dataSource = new FoldersDS("fulltextfolder");
		setTitle(I18N.getMessage("folder"));
		setDataSource(dataSource);
		setEmptyMenuMessage("empty");
		setCanSelectParentItems(true);
		setValue("");
		setRedrawOnChange(true);

		FormItemIcon icon = new FormItemIcon();
		icon.setSrc("[SKIN]/actions/remove.png");
		setIcons(icon);
		addIconClickHandler(new IconClickHandler() {
			public void onIconClick(IconClickEvent event) {
				setValue("");
				redraw();
			}
		});
	}
}
