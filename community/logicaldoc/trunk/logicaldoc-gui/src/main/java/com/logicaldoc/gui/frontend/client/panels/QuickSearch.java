package com.logicaldoc.gui.frontend.client.panels;

import com.google.gwt.user.client.ui.Anchor;
import com.logicaldoc.gui.common.client.I18N;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
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
	public QuickSearch() {
		final DynamicForm form = new DynamicForm();
		TextItem searchBox = new TextItem();
		searchBox.setShowTitle(false);
		searchBox.setDefaultValue(I18N.getMessage("search") + "...");
		searchBox.setLength(30);
		form.setFields(new FormItem[] { searchBox });
		searchBox.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if ("enter".equals(event.getKeyName().toLowerCase()))
					form.submit();
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

		Anchor options = new Anchor();
		options.setText(I18N.getMessage("options"));
		options.setStyleName("searchOptions");
		options.addClickHandler(new com.google.gwt.event.dom.client.ClickHandler() {
			@Override
			public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
				SC.warn("Implement a popup with search otions");
			}
		});
		addMember(options);
	}
}