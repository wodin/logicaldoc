package com.logicaldoc.gui.frontend.client.search;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUISearchOptions;
import com.logicaldoc.gui.common.client.data.TagsDS;
import com.logicaldoc.gui.frontend.client.Log;
import com.logicaldoc.gui.frontend.client.services.SearchService;
import com.logicaldoc.gui.frontend.client.services.SearchServiceAsync;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * This panel shows the saved searches of the user
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class TagsForm extends VLayout {

	private ListGrid tags;

	private SearchServiceAsync service = (SearchServiceAsync) GWT.create(SearchService.class);

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
		TextItem otherChar = new TextItem("otherchar", I18N.getMessage("otherchar"));
		otherChar.setRequired(true);
		otherChar.setLength(1);
		otherChar.setWidth(50);
		otherChar.setEndRow(false);
		ButtonItem search = new ButtonItem("search", I18N.getMessage("search"));
		search.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (!form2.validate())
					return;
				onLetterSelect(form2.getValueAsString("otherchar"));
			}
		});
		form2.setItems(otherChar, search);

		vocabulary.addMember(form1);
		vocabulary.addMember(form2);

		addMember(vocabulary);

		ListGridField name = new ListGridField("name", I18N.getMessage("name"),200);
		ListGridField count = new ListGridField("count", I18N.getMessage("count"), 50);
		tags = new ListGrid();
		tags.setWidth100();
		tags.setHeight100();
		tags.setAutoFetchData(true);
		tags.setFields(name, count);
		addMember(tags);

		tags.addCellDoubleClickHandler(new CellDoubleClickHandler() {
			@Override
			public void onCellDoubleClick(CellDoubleClickEvent event) {
				ListGridRecord record = event.getRecord();
				service.load(Session.get().getSid(), record.getAttributeAsString("name"),
						new AsyncCallback<GUISearchOptions>() {

							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught);
							}

							@Override
							public void onSuccess(GUISearchOptions options) {
								Search.get().setOptions(options);
								Search.get().search();
							}
						});
			}
		});
	}

	private void onLetterSelect(String letter) {
		tags.setDataSource(new TagsDS(letter));
		tags.fetchData();
	}
}