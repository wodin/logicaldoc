package com.logicaldoc.gui.frontend.client.search;

import java.util.ArrayList;
import java.util.List;

import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.beans.GUISearchOptions;
import com.logicaldoc.gui.common.client.data.TagsDS;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.PickerIcon;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemClickHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemIconClickEvent;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellContextClickEvent;
import com.smartgwt.client.widgets.grid.events.CellContextClickHandler;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;

/**
 * This panel shows the saved searches of the user
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class TagsForm extends VLayout {

	private ListGrid tags;

	private static TagsForm instance;

	public static TagsForm get() {
		if (instance == null)
			instance = new TagsForm();
		return instance;
	}

	private TagsForm() {
		setMembersMargin(3);

		HLayout vocabulary = new HLayout();
		vocabulary.setMargin(5);
		vocabulary.setMembersMargin(10);

		DynamicForm form1 = new DynamicForm();
		form1.setNumCols(9);
		List<FormItem> items = new ArrayList<FormItem>();
		String str = "abcdefghijklmnopqrstuvwxyz";
		for (int i = 0; i < str.length(); i++) {
			final StaticTextItem item = new StaticTextItem();
			item.setValue(("" + str.charAt(i)).toUpperCase());
			item.setShowTitle(false);
			item.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					onLetterSelect((String) item.getValue());
				}
			});
			items.add(item);
		}
		form1.setItems(items.toArray(new FormItem[0]));

		final DynamicForm form2 = new DynamicForm();
		form2.setNumCols(3);

		PickerIcon searchPicker = new PickerIcon(PickerIcon.SEARCH, new FormItemClickHandler() {
			public void onFormItemClick(FormItemIconClickEvent event) {
				if (!form2.validate())
					return;
				onLetterSelect(form2.getValueAsString("otherchar"));
			}
		});

		TextItem otherChar = new TextItem("otherchar", I18N.getMessage("otherchar"));
		otherChar.setRequired(true);
		otherChar.setLength(1);
		otherChar.setWidth(50);
		otherChar.setEndRow(false);
		otherChar.setIcons(searchPicker);
		form2.setItems(otherChar);

		vocabulary.addMember(form1);
		vocabulary.addMember(form2);

		addMember(vocabulary);

		ListGridField word = new ListGridField("word", I18N.getMessage("tag"), 200);
		ListGridField count = new ListGridField("count", I18N.getMessage("count"), 50);
		tags = new ListGrid();
		tags.setWidth100();
		tags.setHeight100();
		tags.setFields(word, count);
		tags.setSelectionType(SelectionStyle.SINGLE);
		addMember(tags);

		tags.addCellDoubleClickHandler(new CellDoubleClickHandler() {
			@Override
			public void onCellDoubleClick(CellDoubleClickEvent event) {
				ListGridRecord record = event.getRecord();
				executeSearch(record);
			}
		});

		tags.addCellContextClickHandler(new CellContextClickHandler() {
			@Override
			public void onCellContextClick(CellContextClickEvent event) {
				showContextMenu();
				event.cancel();
			}
		});
	}

	private void executeSearch(ListGridRecord record) {
		GUISearchOptions options = new GUISearchOptions();
		options.setType(GUISearchOptions.TYPE_TAGS);
		options.setExpression(record.getAttributeAsString("word"));
		Search.get().setOptions(options);
		Search.get().search();
	}

	private void showContextMenu() {
		Menu contextMenu = new Menu();

		MenuItem execute = new MenuItem();
		execute.setTitle(I18N.getMessage("execute"));
		execute.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				ListGridRecord selection = tags.getSelectedRecord();
				executeSearch(selection);
			}
		});

		contextMenu.setItems(execute);
		contextMenu.showContextMenu();
	}

	private void onLetterSelect(String letter) {
		tags.setDataSource(new TagsDS(letter));
		tags.fetchData();
	}
}