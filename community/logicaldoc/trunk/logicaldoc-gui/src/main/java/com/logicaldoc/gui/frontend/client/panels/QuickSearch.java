package com.logicaldoc.gui.frontend.client.panels;

import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.beans.GUISearchOptions;
import com.logicaldoc.gui.frontend.client.search.Search;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.PickerIcon;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemClickHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemIconClickEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;
import com.smartgwt.client.widgets.layout.HLayout;

/**
 * This panel shows the quick search controls
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class QuickSearch extends HLayout {
	private DynamicForm form = new DynamicForm();

	public QuickSearch() {
		PickerIcon searchPicker = new PickerIcon(PickerIcon.SEARCH, new FormItemClickHandler() {
			public void onFormItemClick(FormItemIconClickEvent event) {
				search();
			}
		});

		TextItem searchBox = new TextItem("expression");
		searchBox.setShowTitle(false);
		searchBox.setDefaultValue(I18N.getMessage("search") + "...");
		searchBox.setWidth(200);
		searchBox.setIcons(searchPicker);
		form.setItems(searchBox);
		searchBox.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (event.getKeyName() == null)
					return;
				if (Constants.KEY_ENTER.equals(event.getKeyName().toLowerCase())) {
					search();
				}
			}
		});
		searchBox.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if ((I18N.getMessage("search") + "...").equals(event.getItem().getValue())) {
					event.getItem().setValue("");
				}
			}
		});
		addMember(form);
	}

	private void search() {
		GUISearchOptions options = Search.get().getOptions();
		options.setType(GUISearchOptions.TYPE_FULLTEXT);
		options.setExpression(form.getValueAsString("expression"));
		options.setFolder(null);
		options.setTemplate(null);
		options.setMaxHits(40);
		Search.get().setOptions(options);
		Search.get().search();
	}
}