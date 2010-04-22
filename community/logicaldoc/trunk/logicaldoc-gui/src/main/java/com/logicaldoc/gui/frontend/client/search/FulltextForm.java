package com.logicaldoc.gui.frontend.client.search;

import java.util.LinkedHashMap;

import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUISearchOptions;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.SubmitItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * Shows document's standard properties and read-only data
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class FulltextForm extends VLayout {
	private DynamicForm form = new DynamicForm();

	private ValuesManager vm = new ValuesManager();

	public FulltextForm() {
		setWidth100();
		setHeight100();
		setMembersMargin(10);

		form = new DynamicForm();
		form.setValuesManager(vm);
		form.setTitleOrientation(TitleOrientation.LEFT);

		TextItem expression = new TextItem("expression");
		expression.setRequired(true);
		expression.setTitle(I18N.getMessage("expression"));
		expression.setValue(I18N.getMessage("search") + "...");
		expression.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if ("enter".equals(event.getKeyName().toLowerCase()))
					search();
			}
		});
		expression.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if ((I18N.getMessage("search") + "...").equals(event.getItem().getValue())) {
					event.getItem().setValue("");
				}
			}
		});

		SubmitItem search = new SubmitItem("search");
		search.setTitle(I18N.getMessage("search"));
		search.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				search();
			}

		});

		SelectItem language = new SelectItem();
		LinkedHashMap<String, String> langs=I18N.getSupportedLanguages();
		langs.put("", " ");
		language.setValueMap(I18N.getSupportedLanguages());
		language.setName("language");
		language.setTitle(I18N.getMessage("language"));
		language.setRequired(true);
		language.setWrapTitle(false);

		form.setItems(expression, language, search);
		addMember(form);
	}

	private void search() {
		GUISearchOptions options = Search.get().getOptions();
		options.setMaxHits(40);
		options.setType(GUISearchOptions.TYPE_FULLTEXT);
		options.setExpression(form.getValueAsString("expression"));
		options.setLanguage(form.getValueAsString("language"));
		options.setQueryLanguage(Session.get().getLanguage());
		Search.get().search();
	}
}