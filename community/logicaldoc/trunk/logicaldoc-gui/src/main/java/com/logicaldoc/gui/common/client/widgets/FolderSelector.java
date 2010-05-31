package com.logicaldoc.gui.common.client.widgets;

import com.logicaldoc.gui.common.client.data.FoldersDS;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.smartgwt.client.widgets.form.fields.FormItemIcon;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.events.IconClickEvent;
import com.smartgwt.client.widgets.form.fields.events.IconClickHandler;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.ItemClickEvent;
import com.smartgwt.client.widgets.menu.events.ItemClickHandler;

/**
 * Allows the selection of a specific folder
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class FolderSelector extends StaticTextItem {

	private Long folderId;

	private Menu menu = new Menu();

	public FolderSelector() {
		setName("folder");
		setTitle(I18N.message("folder"));
		setValue("");
		setRedrawOnChange(true);
		setValueField("id");
		setDisplayField("name");

		FormItemIcon icon = new FormItemIcon();
		icon.setSrc("[SKIN]/actions/remove.png");
		addIconClickHandler(new IconClickHandler() {
			public void onIconClick(IconClickEvent event) {
				setValue("");
				folderId = null;
				redraw();
			}
		});

		menu.setCanSelectParentItems(true);
		menu.setDataSource(new FoldersDS("folderselector"));
		menu.setWidth(130);
		menu.addItemClickHandler(new ItemClickHandler() {
			public void onItemClick(ItemClickEvent event) {
				MenuItem item = event.getItem();
				setFolder(new Long(item.getAttributeAsString("id")), item.getAttributeAsString("name"));
			}
		});

		FormItemIcon picker = new FormItemIcon();
		addIconClickHandler(new IconClickHandler() {
			public void onIconClick(IconClickEvent event) {
				menu.showContextMenu();
			}
		});

		setIcons(picker, icon);
	}

	public void setFolder(Long folderId, String name) {
		this.folderId = folderId;
		setValue(name);
		redraw();
	}

	public Long getFolderId() {
		return folderId;
	}

	public String getFolderName() {
		return (String) getValue();
	}
}