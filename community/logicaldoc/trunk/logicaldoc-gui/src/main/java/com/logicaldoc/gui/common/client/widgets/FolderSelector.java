package com.logicaldoc.gui.common.client.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.common.client.data.FoldersDS;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.smartgwt.client.widgets.form.fields.FormItemIcon;
import com.smartgwt.client.widgets.form.fields.PickerIcon;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.events.FormItemClickHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemIconClickEvent;
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

	private Collection<FolderChangeListener> listeners = new ArrayList<FolderChangeListener>();

	public FolderSelector(String name, boolean cleanPick) {
		if (name != null)
			setName(name);
		else
			setName("folder");
		setTitle(I18N.message("folder"));
		setWrapTitle(false);
		setWrap(true);
		setValue("  ");
		setRedrawOnChange(true);
		setValueField("id");
		setDisplayField("name");

		PickerIcon remove = new PickerIcon(PickerIcon.CLEAR, new FormItemClickHandler() {
			@Override
			public void onFormItemClick(FormItemIconClickEvent event) {
				setValue("");
				folderId = null;
			}
		});

		menu.setCanSelectParentItems(true);
		Date date = new Date();
		menu.setDataSource(new FoldersDS("folderselector" + date.getTime()));
		menu.setWidth(130);
		menu.addItemClickHandler(new ItemClickHandler() {
			public void onItemClick(ItemClickEvent event) {
				MenuItem item = event.getItem();
				setFolder(new Long(item.getAttributeAsString("id")), item.getAttributeAsString("name"));
			}
		});

		FormItemIcon search = new PickerIcon(PickerIcon.SEARCH, new FormItemClickHandler() {
			@Override
			public void onFormItemClick(FormItemIconClickEvent event) {
				menu.showContextMenu();
			}
		});

		if (cleanPick)
			setIcons(search, remove);
		else
			setIcons(search);
	}

	public void setFolder(Long folderId, String name) {
		this.folderId = folderId;

		if (name != null && !(name.endsWith(" ") || name.endsWith("&nbsp;"))) {
			// Use &nbsp; to have a better look&feel with Explorer
			setValue(name + "&nbsp;&nbsp;");
		}
		for (FolderChangeListener listener : listeners) {
			listener.onChanged(getFolder());
		}
		redraw();
	}

	public void setFolder(GUIFolder folder) {
		Long id = null;
		String name = null;

		if (folder != null) {
			id = folder.getId();
			if (id == 5)
				name = "/";
			else
				name = folder.getName();
		}

		setFolder(id, name);
	}

	public GUIFolder getFolder() {
		GUIFolder folder = new GUIFolder();
		folder.setId(getFolderId());
		folder.setName(getFolderName());
		return folder;
	}

	public Long getFolderId() {
		return folderId;
	}

	public String getFolderName() {
		return (String) getValue();
	}

	public void addFolderChangeListener(FolderChangeListener listener) {
		listeners.add(listener);
	}
}