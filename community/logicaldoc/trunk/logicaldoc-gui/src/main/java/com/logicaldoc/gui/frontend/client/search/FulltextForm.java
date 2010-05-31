package com.logicaldoc.gui.frontend.client.search;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIExtendedAttribute;
import com.logicaldoc.gui.common.client.beans.GUISearchOptions;
import com.logicaldoc.gui.common.client.data.TemplatesDS;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.widgets.FolderSelector;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.DocumentServiceAsync;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.FormItemIfFunction;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.DateItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.IntegerItem;
import com.smartgwt.client.widgets.form.fields.PickerIcon;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemClickHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemIconClickEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * Shows document's standard properties and read-only data
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class FulltextForm extends VLayout implements SearchObserver {
	private static final String BLANK_PLACEHOLDER = "___";

	private static final String BEFORE = "before";

	private static final String DATE = "date";

	private static final String PUBLISHEDON = "publishedon";

	private static final String CREATEDON = "createdon";

	private static final String LESSTHAN = "lessthan";

	private static final String NOLIMIT = "nolimit";

	private static final String NO_LANGUAGE = "";

	private ValuesManager vm = new ValuesManager();

	private DynamicForm extForm = new DynamicForm();

	private DocumentServiceAsync documentService = (DocumentServiceAsync) GWT.create(DocumentService.class);

	private FolderSelector folder;

	public FulltextForm() {
		setHeight100();
		setMembersMargin(5);
		setAlign(Alignment.LEFT);

		DynamicForm form = new DynamicForm();
		form.setValuesManager(vm);
		form.setTitleOrientation(TitleOrientation.LEFT);
		form.setNumCols(4);
		form.setWidth(300);

		PickerIcon searchPicker = new PickerIcon(PickerIcon.SEARCH, new FormItemClickHandler() {
			public void onFormItemClick(FormItemIconClickEvent event) {
				search();
			}
		});

		TextItem expression = ItemFactory.newTextItem("expression", "expression", I18N.message("search") + "...");
		expression.setColSpan(2);
		expression.setRequired(true);
		expression.setIcons(searchPicker);
		expression.setEndRow(true);
		expression.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (event.getKeyName() == null)
					return;
				if (Constants.KEY_ENTER.equals(event.getKeyName().toLowerCase()))
					search();
			}
		});
		expression.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if ((I18N.message("search") + "...").equals(event.getItem().getValue())) {
					event.getItem().setValue("");
				}
			}
		});

		SelectItem language = ItemFactory.newLanguageSelector("language", true);
		language.setDefaultValue(NO_LANGUAGE);
		language.setColSpan(4);
		language.setEndRow(true);

		SelectItem sizeOperator = ItemFactory.newSizeOperator("sizeOperator", "size");

		IntegerItem size = ItemFactory.newIntegerItem("size", " ", null);
		size.setWidth(60);
		size.setShowTitle(false);
		size.setHint("(KB)");
		size.setEndRow(true);

		SelectItem dateSelector = new SelectItem();
		LinkedHashMap<String, String> opts = new LinkedHashMap<String, String>();
		opts.put(CREATEDON, I18N.message(CREATEDON));
		opts.put(PUBLISHEDON, I18N.message(PUBLISHEDON));
		opts.put(DATE, I18N.message(DATE));
		dateSelector.setValueMap(opts);
		dateSelector.setName("dateSelector");
		dateSelector.setTitle(I18N.message(DATE));
		dateSelector.setDefaultValue(CREATEDON);
		dateSelector.setWidth(80);

		SelectItem dateOperator = ItemFactory.newDateOperator("dateOperator", null);

		DateItem date = ItemFactory.newDateItem(DATE, null);

		StaticTextItem searchin = ItemFactory.newStaticTextItem("searchin", "searchin", null);
		searchin.setColSpan(3);
		searchin.setEndRow(true);

		SelectItem template = new SelectItem("template", I18N.message("template"));
		template.setDisplayField("name");
		template.setValueField("id");
		template.setPickListWidth(250);
		template.setOptionDataSource(TemplatesDS.getInstanceWithEmpty());
		template.setColSpan(3);
		template.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				if (event.getValue() != null && !"".equals(event.getValue()))
					prepareExtendedAttributes(new Long(event.getValue().toString()));
				else
					prepareExtendedAttributes(null);
			}
		});

		folder = new FolderSelector();
		folder.setColSpan(3);

		CheckboxItem subfolders = new CheckboxItem("subfolders", I18N.message("searchinsubfolders"));
		subfolders.setColSpan(3);
		subfolders.setShowIfCondition(new FormItemIfFunction() {
			public boolean execute(FormItem item, Object value, DynamicForm form) {
				return folder.getValue() != null && !"".equals(folder.getValue());
			}
		});

		form.setItems(expression, language, sizeOperator, size, dateSelector, dateOperator, date, template, folder,
				subfolders, searchin);
		addMember(form);

		prepareExtendedAttributes(null);
		addMember(extForm);

		Search.get().addObserver(this);
	}

	@SuppressWarnings("unchecked")
	private void search() {
		if (!vm.validate())
			return;

		Map<String, Object> values = vm.getValues();

		GUISearchOptions options = new GUISearchOptions();
		options.setMaxHits(40);
		options.setType(GUISearchOptions.TYPE_FULLTEXT);
		options.setExpression(vm.getValueAsString("expression"));
		if (NO_LANGUAGE.equals(vm.getValueAsString("language")))
			options.setLanguage(null);
		else
			options.setLanguage(vm.getValueAsString("language"));
		options.setExpressionLanguage(Session.get().getLanguage());

		Long size = vm.getValueAsString("size") != null ? new Long(vm.getValueAsString("size")) : null;
		if (size != null && !NOLIMIT.equals(vm.getValueAsString("sizeOperator"))) {
			if (LESSTHAN.equals(vm.getValueAsString("sizeOperator")))
				options.setSizeMax(size);
			else
				options.setSizeMin(size);
		}

		String operator = vm.getValueAsString("dateOperator");
		Date date = (Date) vm.getValues().get(DATE);
		if (date != null && !NOLIMIT.equals(operator)) {
			String whatDate = vm.getValueAsString("dateSelector");
			if (date.equals(whatDate)) {
				if (BEFORE.equals(operator))
					options.setSourceDateTo(date);
				else
					options.setSourceDateFrom(date);
			} else if (CREATEDON.equals(operator)) {
				if (BEFORE.equals(operator))
					options.setCreationTo(date);
				else
					options.setCreationFrom(date);
			} else {
				if (BEFORE.equals(operator))
					options.setDateTo(date);
				else
					options.setDateFrom(date);
			}
		}

		if (values.containsKey("template") && !((String) values.get("template")).isEmpty())
			options.setTemplate(new Long((String) values.get("template")));

		List<String> fields = new ArrayList<String>();
		if ("true".equals(vm.getValueAsString("titleFlag")))
			fields.add("title");
		if ("true".equals(vm.getValueAsString("contentFlag")))
			fields.add("content");
		if ("true".equals(vm.getValueAsString("tagsFlag")))
			fields.add("tags");
		if ("true".equals(vm.getValueAsString("typeFlag")))
			fields.add("type");
		if ("true".equals(vm.getValueAsString("sourceFlag")))
			fields.add("source");
		if ("true".equals(vm.getValueAsString("authorFlag")))
			fields.add("sourceAuthor");
		if ("true".equals(vm.getValueAsString("coverageFlag")))
			fields.add("coverage");
		if ("true".equals(vm.getValueAsString("customidFlag")))
			fields.add("customId");

		// Now collect all flags from the string extended attributes
		values = (Map<String, Object>) extForm.getValues();
		for (String name : values.keySet()) {
			if (((Boolean) values.get(name)).booleanValue()) {
				String tmp = name.replaceAll(BLANK_PLACEHOLDER, " ");
				fields.add("ext_" + tmp);
			}
		}
		options.setFields(fields.toArray(new String[0]));

		options.setFolder(folder.getFolderId());
		options.setFolderName(folder.getFolderName());

		options.setSearchInSubPath(new Boolean(vm.getValueAsString("subfolders")).booleanValue());

		Search.get().setOptions(options);
		Search.get().search();
	}

	/*
	 * Prepare the second form for the extended attributes
	 */
	private void prepareExtendedAttributes(Long templateId) {
		if (extForm != null && contains(extForm))
			removeMember(extForm);

		extForm = new DynamicForm();
		extForm.setVisible(false);
		extForm.setTitleOrientation(TitleOrientation.LEFT);
		extForm.setNumCols(4);
		extForm.setWidth(300);
		addMember(extForm);

		final List<FormItem> items = new ArrayList<FormItem>();

		CheckboxItem titleFlag = new CheckboxItem("titleFlag", I18N.message("title"));
		titleFlag.setValue(true);
		items.add(titleFlag);
		CheckboxItem contentFlag = new CheckboxItem("contentFlag", I18N.message("content"));
		contentFlag.setValue(true);
		items.add(contentFlag);
		CheckboxItem tagsFlag = new CheckboxItem("tagsFlag", I18N.message("tags"));
		tagsFlag.setValue(true);
		items.add(tagsFlag);
		CheckboxItem customidFlag = new CheckboxItem("customidFlag", I18N.message("customid"));
		items.add(customidFlag);
		CheckboxItem sourceFlag = new CheckboxItem("sourceFlag", I18N.message("source"));
		items.add(sourceFlag);
		CheckboxItem coverageFlag = new CheckboxItem("coverageFlag", I18N.message("coverage"));
		items.add(coverageFlag);
		CheckboxItem authorFlag = new CheckboxItem("authorFlag", I18N.message("author"));
		items.add(authorFlag);
		CheckboxItem typeFlag = new CheckboxItem("typeFlag", I18N.message("type"));
		items.add(typeFlag);

		if (templateId == null || templateId.longValue() <= 0) {
			extForm.setItems(items.toArray(new FormItem[0]));
			return;
		}

		documentService.getAttributes(Session.get().getSid(), templateId, new AsyncCallback<GUIExtendedAttribute[]>() {
			@Override
			public void onFailure(Throwable caught) {
				Log.serverError(caught);
			}

			@Override
			public void onSuccess(GUIExtendedAttribute[] result) {

				for (GUIExtendedAttribute att : result) {
					// We cannot use spaces in items name
					String itemName = "_" + att.getName().replaceAll(" ", BLANK_PLACEHOLDER);
					if (att.getType() == GUIExtendedAttribute.TYPE_STRING) {
						CheckboxItem item = new CheckboxItem(itemName, att.getName());
						items.add(item);
					}
				}
				extForm.setItems(items.toArray(new FormItem[0]));
			}
		});
	}

	@Override
	public void onSearchArrived() {

	}

	@Override
	public void onOptionsChanged(GUISearchOptions newOptions) {
		if (newOptions.getType() == GUISearchOptions.TYPE_FULLTEXT) {
			vm.setValue("expression", newOptions.getExpression());
			folder.setFolder(newOptions.getFolder(), newOptions.getFolderName());
		}
	}
}