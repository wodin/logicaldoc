package com.logicaldoc.gui.frontend.client.metadata;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.data.TagsDS;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.frontend.client.services.TagService;
import com.logicaldoc.gui.frontend.client.services.TagServiceAsync;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * This panel shows the tags list with each tag count.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class TagsPreset extends VLayout {

	private ListGrid tags;

	private TagServiceAsync tagService = (TagServiceAsync) GWT.create(TagService.class);

	public TagsPreset(String tagMode) {
		setMembersMargin(3);

		final DynamicForm form1 = new DynamicForm();

		final SelectItem mode = ItemFactory.newTagInputMode("mode", "taginputmode");
		mode.setValue(tagMode);
		mode.addChangedHandler(new ChangedHandler() {

			@Override
			public void onChanged(ChangedEvent event) {
				tagService.setMode(Session.get().getSid(), mode.getValueAsString(), new AsyncCallback<Void>() {
					@Override
					public void onSuccess(Void arg) {
						// Nothing to do
					}

					@Override
					public void onFailure(Throwable t) {
						Log.serverError(t);
					}
				});
			}
		});

		form1.setItems(mode);

		addMember(form1);

		// reloadTags(admin, null);
	}

	private void reloadTags(final boolean admin, String letter) {
		if (tags != null) {
			removeMember(tags);
		}

		ListGridField index = new ListGridField("index", " ", 10);
		index.setHidden(true);
		ListGridField word = new ListGridField("word", I18N.message("tag"), 200);
		ListGridField count = new ListGridField("count", I18N.message("count"), 60);
		tags = new ListGrid();
		tags.setEmptyMessage(I18N.message("notitemstoshow"));
		tags.setWidth100();
		tags.setHeight100();
		tags.setFields(index, word, count);
		tags.setSelectionType(SelectionStyle.SINGLE);
		tags.setDataSource(new TagsDS(letter));
		addMember(tags);
	}
}