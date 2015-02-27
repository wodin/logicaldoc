package com.logicaldoc.gui.frontend.client.search;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.beans.GUIExtendedAttribute;
import com.logicaldoc.gui.common.client.beans.GUISearchOptions;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.frontend.client.folder.FolderSelector;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.DocumentServiceAsync;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.FormItemIfFunction;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.DateItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.IntegerItem;
import com.smartgwt.client.widgets.form.fields.MultiComboBoxItem;
import com.smartgwt.client.widgets.form.fields.PickerIcon;
import com.smartgwt.client.widgets.form.fields.SelectItem;
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
 * Shows a full-text search form
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class FulltextForm extends VLayout implements SearchObserver {
	private static final String SEARCHINHITS = "searchinhits";

	private static final String BEFORE = "before";

	private static final String DATE = "date";

	private static final String PUBLISHEDON = "publishedon";

	private static final String CREATEDON = "createdon";

	private static final String LESSTHAN = "lessthan";

	private static final String NOLIMIT = "nolimit";

	private static final String NO_LANGUAGE = "";

	private ValuesManager vm = new ValuesManager();

	private DynamicForm fieldsForm = new DynamicForm();

	private MultiComboBoxItem searchinItem = null;

	private DocumentServiceAsync documentService = (DocumentServiceAsync) GWT.create(DocumentService.class);

	private FolderSelector folder;

	public FulltextForm() {
		setHeight100();
		setWidth100();
		setOverflow(Overflow.AUTO);

		setMembersMargin(3);
		setAlign(Alignment.LEFT);

		DynamicForm form1 = new DynamicForm();
		form1.setValuesManager(vm);
		form1.setTitleOrientation(TitleOrientation.LEFT);
		form1.setNumCols(4);
		form1.setWidth(300);

		PickerIcon searchPicker = new PickerIcon(PickerIcon.SEARCH, new FormItemClickHandler() {
			public void onFormItemClick(FormItemIconClickEvent event) {
				search();
			}
		});

		TextItem expression = ItemFactory.newTextItem("expression", "expression", I18N.message("search") + "...");
		expression.setWidth(180);
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

		CheckboxItem searchinhits = new CheckboxItem("searchinhits", I18N.message("searchinhits"));
		searchinhits.setColSpan(3);

		SelectItem language = ItemFactory.newLanguageSelector("language", true, false);
		language.setDefaultValue(NO_LANGUAGE);
		language.setColSpan(4);
		language.setEndRow(true);

		SelectItem template = ItemFactory.newTemplateSelector(true, null);
		template.setMultiple(false);
		template.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				if (event.getValue() != null && !"".equals((String) event.getValue()))
					prepareFields(new Long((String) event.getValue()));
				else
					prepareFields(null);
			}
		});

		folder = new FolderSelector(null, true);
		folder.setColSpan(3);
		folder.setWidth(200);

		CheckboxItem subfolders = new CheckboxItem("subfolders", I18N.message("searchinsubfolders"));
		subfolders.setColSpan(3);
		subfolders.setShowIfCondition(new FormItemIfFunction() {
			public boolean execute(FormItem item, Object value, DynamicForm form) {
				return folder.getValue() != null && !"".equals(folder.getValue());
			}
		});
		form1.setItems(expression, language, searchinhits, folder, subfolders, template);
		addMember(form1);

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
		dateSelector.setValue(CREATEDON);
		dateSelector.setWidth(80);

		SelectItem dateOperator = ItemFactory.newDateOperator("dateOperator", null);

		DateItem date = ItemFactory.newDateItem(DATE, null);

		DynamicForm form2 = new DynamicForm();
		form2.setValuesManager(vm);
		form2.setTitleOrientation(TitleOrientation.LEFT);
		form2.setNumCols(4);
		form2.setWidth(300);
		form2.setItems(sizeOperator, size, dateSelector, dateOperator, date);
		addMember(form2);

		prepareFields(null);

		Search.get().addObserver(this);
	}

	@SuppressWarnings("unchecked")
	private void search() {
		if (!vm.validate())
			return;

		Map<String, Object> values = vm.getValues();

		GUISearchOptions options = new GUISearchOptions();

		String hits = Session.get().getConfig("search.hits");
		if (hits != null)
			options.setMaxHits(Integer.parseInt(hits));

		options.setType(GUISearchOptions.TYPE_FULLTEXT);
		options.setExpression(vm.getValueAsString("expression"));
		if (NO_LANGUAGE.equals(vm.getValueAsString("language")) || vm.getValue("language") == null) {
			options.setLanguage(null);
			options.setExpressionLanguage(I18N.getLocale());
		} else {
			options.setLanguage(vm.getValueAsString("language"));
			options.setExpressionLanguage(options.getLanguage());
		}

		Long size = vm.getValueAsString("size") != null ? new Long(vm.getValueAsString("size")) : null;
		if (size != null && !NOLIMIT.equals(vm.getValueAsString("sizeOperator"))) {
			if (LESSTHAN.equals(vm.getValueAsString("sizeOperator")))
				options.setSizeMax(size * 1024);
			else if (!NOLIMIT.equals(vm.getValueAsString("sizeOperator")))
				options.setSizeMin(size * 1024);
		}

		String operator = vm.getValueAsString("dateOperator");
		Date date = (Date) vm.getValues().get(DATE);
		if (date != null && !NOLIMIT.equals(operator)) {
			String whatDate = vm.getValueAsString("dateSelector");
			if (DATE.equals(whatDate)) {
				if (BEFORE.equals(operator))
					options.setSourceDateTo(date);
				else if (!NOLIMIT.equals(operator))
					options.setSourceDateFrom(date);
			} else if (CREATEDON.equals(whatDate)) {
				if (BEFORE.equals(operator))
					options.setCreationTo(date);
				else if (!NOLIMIT.equals(operator))
					options.setCreationFrom(date);
			} else {
				if (BEFORE.equals(operator))
					options.setDateTo(date);
				else if (!NOLIMIT.equals(operator))
					options.setDateFrom(date);
			}
		}

		if (values.containsKey("template") && !((String) values.get("template")).isEmpty())
			options.setTemplate(new Long((String) values.get("template")));

		String[] fields = searchinItem.getValues();
		options.setFields(fields);

		options.setFolder(folder.getFolderId());
		options.setFolderName(folder.getFolderName());

		options.setSearchInSubPath(new Boolean(vm.getValueAsString("subfolders")).booleanValue());

		if (new Boolean(vm.getValueAsString(SEARCHINHITS)).booleanValue()) {
			GUIDocument[] docs = Search.get().getLastResult();
			Long[] ids = new Long[docs.length];
			int i = 0;
			for (GUIDocument doc : docs) {
				ids[i] = doc.getId();
				i++;
			}
			options.setFilterIds(ids);
		} else
			options.setFilterIds(null);

		options.setType(0);
		Search.get().setOptions(options);
		Search.get().search();
	}

	/*
	 * Prepare the form for the selectable fields
	 */
	private void prepareFields(Long templateId) {
		if (fieldsForm != null && contains(fieldsForm))
			removeMember(fieldsForm);

		fieldsForm = new DynamicForm();
		fieldsForm.setTitleOrientation(TitleOrientation.TOP);
		fieldsForm.setWidth100();
		addMember(fieldsForm);

		searchinItem = ItemFactory.newMultiComboBoxItem("searchin", "searchin", null, null);
		searchinItem.setWidth(300);

		final LinkedHashMap<String, String> fieldsMap = new LinkedHashMap<String, String>();
		fieldsMap.put("title", I18N.message("title"));
		fieldsMap.put("content", I18N.message("content"));
		fieldsMap.put("tags", I18N.message("tags"));
		fieldsMap.put("customId", I18N.message("customid"));
		fieldsMap.put("coverage", I18N.message("coverage"));
		fieldsMap.put("source", I18N.message("source"));
		fieldsMap.put("sourceAuthor", I18N.message("author"));
		fieldsMap.put("sourceType", I18N.message("type"));
		fieldsMap.put("recipient", I18N.message("recipient"));
		fieldsMap.put("object", I18N.message("object"));
		fieldsMap.put("comment", I18N.message("comment"));

		searchinItem.setValueMap(fieldsMap);
		searchinItem.setValue(new String[] { "title", "content", "tags" });

		fieldsForm.setItems(searchinItem);

		if (templateId == null)
			return;

		documentService.getAttributes(Session.get().getSid(), templateId, new AsyncCallback<GUIExtendedAttribute[]>() {
			@Override
			public void onFailure(Throwable caught) {
				Log.serverError(caught);
			}

			@Override
			public void onSuccess(GUIExtendedAttribute[] result) {
				for (GUIExtendedAttribute att : result) {
					if (att.getType() == GUIExtendedAttribute.TYPE_STRING
							|| att.getType() == GUIExtendedAttribute.TYPE_USER) {
						fieldsMap.put("ext_" + att.getName(), att.getName());
					}
				}
				searchinItem.setValueMap(fieldsMap);
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