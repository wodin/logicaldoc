package com.logicaldoc.gui.frontend.client.search;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUICriterion;
import com.logicaldoc.gui.common.client.beans.GUISearchOptions;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.widgets.FolderSelector;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.FormItemIfFunction;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.DateItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * Shows a folders search form
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.4
 */
public class FoldersForm extends VLayout {
	private static final String BEFORE = "before";

	private static final String NOLIMIT = "nolimit";

	private ValuesManager vm = new ValuesManager();

	private FolderSelector folder;

	public FoldersForm() {
		setHeight100();
		setMargin(3);
		setTop(5);
		setMembersMargin(5);
		setAlign(Alignment.LEFT);

		DynamicForm form = new DynamicForm();
		form.setValuesManager(vm);
		form.setTitleOrientation(TitleOrientation.LEFT);
		form.setNumCols(4);
		form.setWidth(300);

		TextItem name = ItemFactory.newTextItem("name", "name", null);
		name.setColSpan(2);
		name.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (event.getKeyName() == null)
					return;
				if (Constants.KEY_ENTER.equals(event.getKeyName().toLowerCase()))
					search();
			}
		});

		TextItem description = ItemFactory.newTextItem("description", "description", null);
		description.setColSpan(2);
		description.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (event.getKeyName() == null)
					return;
				if (Constants.KEY_ENTER.equals(event.getKeyName().toLowerCase()))
					search();
			}
		});

		SelectItem dateOperator = ItemFactory.newDateOperator("dateOperator", "createdon");
		DateItem creation = ItemFactory.newDateItem("creation", null);
		creation.setEndRow(true);

		folder = new FolderSelector(null, true);
		folder.setColSpan(3);

		CheckboxItem subfolders = new CheckboxItem("subfolders", I18N.message("searchinsubfolders",Session.get().getInfo().getConfig("search.depth")));
		subfolders.setColSpan(3);
		subfolders.setShowIfCondition(new FormItemIfFunction() {
			public boolean execute(FormItem item, Object value, DynamicForm form) {
				return folder.getValue() != null && !"".equals(folder.getValue());
			}
		});

		IButton search = new IButton(I18N.message("search"));
		search.setAutoFit(true);
		search.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {

			@Override
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				search();
			}
		});

		form.setItems(name, description, folder, subfolders, dateOperator, creation);
		addMember(form);

		addMember(search);
	}

	private void search() {
		if (!vm.validate())
			return;

		GUISearchOptions options = new GUISearchOptions();
		options.setType(GUISearchOptions.TYPE_FOLDERS);

		String hits = Session.get().getInfo().getConfig("search.hits");
		if (hits != null)
			options.setMaxHits(Integer.parseInt(hits));

		String depth = Session.get().getInfo().getConfig("search.depth");
		if (depth != null)
			options.setDepth(Integer.parseInt(depth));

		String operator = vm.getValueAsString("dateOperator");
		Date date = (Date) vm.getValues().get("creation");
		if (date != null && !NOLIMIT.equals(operator)) {
			if (BEFORE.equals(operator))
				options.setCreationTo(date);
			else
				options.setCreationFrom(date);
		}

		options.setFolder(folder.getFolderId());
		options.setFolderName(folder.getFolderName());
		options.setSearchInSubPath(new Boolean(vm.getValueAsString("subfolders")).booleanValue());
		options.setFilterIds(null);

		List<GUICriterion> criteria = new ArrayList<GUICriterion>();
		GUICriterion nameCriterion = new GUICriterion();
		nameCriterion.setField("name");
		nameCriterion.setStringValue(vm.getValueAsString("name"));
		criteria.add(nameCriterion);

		GUICriterion descCriterion = new GUICriterion();
		descCriterion.setField("description");
		descCriterion.setStringValue(vm.getValueAsString("description"));
		criteria.add(descCriterion);

		options.setCriteria(criteria.toArray(new GUICriterion[0]));

		Search.get().setOptions(options);
		Search.get().search();
	}
}