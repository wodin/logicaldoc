package com.logicaldoc.gui.frontend.client.search;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIAttribute;
import com.logicaldoc.gui.common.client.beans.GUICriterion;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.beans.GUISearchOptions;
import com.logicaldoc.gui.common.client.beans.GUITemplate;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.frontend.client.folder.FolderSelector;
import com.logicaldoc.gui.frontend.client.services.TemplateService;
import com.logicaldoc.gui.frontend.client.services.TemplateServiceAsync;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.util.JSOHelper;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ResizedEvent;
import com.smartgwt.client.widgets.events.ResizedHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.IntegerItem;
import com.smartgwt.client.widgets.form.fields.RadioGroupItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * Shows a parametric search form
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class ParametricForm extends VLayout {
	private static final String SEARCHINHITS = "searchinhits";

	private static final String NO_LANGUAGE = "";

	private ValuesManager vm = new ValuesManager();

	private TemplateServiceAsync service = (TemplateServiceAsync) GWT.create(TemplateService.class);

	private FolderSelector folder;

	private GUITemplate selectedTemplate = null;

	private VLayout conditionsLayout = null;

	private static ParametricForm instance;

	public static ParametricForm get() {
		if (instance == null)
			instance = new ParametricForm();
		return instance;
	}

	private ParametricForm() {
		setHeight100();
		setWidth100();
		setMargin(3);
		setMembersMargin(3);
		setAlign(Alignment.LEFT);

		setOverflow(Overflow.AUTO);

		final DynamicForm languageForm = new DynamicForm();
		languageForm.setValuesManager(vm);
		languageForm.setTitleOrientation(TitleOrientation.TOP);
		languageForm.setNumCols(2);
		SelectItem language = ItemFactory.newLanguageSelector("language", true, false);
		language.setDefaultValue("");
		languageForm.setItems(language);

		IButton search = new IButton(I18N.message("search"));
		search.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {

			@Override
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				search();
			}
		});

		HLayout topLayout = new HLayout(80);
		topLayout.setMembers(languageForm, search);
		topLayout.setTop(3);
		topLayout.setHeight(15);
		addMember(topLayout);

		final DynamicForm form = new DynamicForm();
		form.setValuesManager(vm);
		form.setTitleOrientation(TitleOrientation.TOP);
		form.setNumCols(2);

		folder = new FolderSelector(null, true);
		folder.setColSpan(2);
		folder.setEndRow(true);
		folder.setWidth(200);

		CheckboxItem casesensitive = new CheckboxItem("casesensitive", I18N.message("casesensitive"));
		casesensitive.setValue(true);
		CheckboxItem aliases = new CheckboxItem("aliases", I18N.message("retrievealiases"));

		CheckboxItem subfolders = new CheckboxItem("subfolders", I18N.message("searchinsubfolders2"));
		subfolders.setColSpan(2);
		subfolders.setEndRow(true);

		CheckboxItem searchinhits = new CheckboxItem(SEARCHINHITS, I18N.message(SEARCHINHITS));

		LinkedHashMap<String, String> matchMap = new LinkedHashMap<String, String>();
		matchMap.put("and", I18N.message("matchall"));
		matchMap.put("or", I18N.message("matchany"));
		matchMap.put("not", I18N.message("matchnone"));
		RadioGroupItem match = new RadioGroupItem("match");
		match.setDefaultValue("and");
		match.setShowTitle(false);
		match.setValueMap(matchMap);
		match.setVertical(false);
		match.setWrap(false);
		match.setWrapTitle(false);
		match.setColSpan(4);

		if (Feature.visible(Feature.TEMPLATE)) {
			SelectItem template = ItemFactory.newTemplateSelector(true, null);
			template.setMultiple(false);
			template.setEndRow(true);
			template.addChangedHandler(new ChangedHandler() {
				@Override
				public void onChanged(ChangedEvent event) {
					if (event.getValue() != null && !"".equals((String) event.getValue())) {
						service.getTemplate(new Long((String) event.getValue()), new AsyncCallback<GUITemplate>() {
							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught);
							}

							@Override
							public void onSuccess(GUITemplate result) {
								selectedTemplate = result;
							}
						});
					} else {
						selectedTemplate = null;
					}
				}
			});

			form.setItems(folder, subfolders, casesensitive, aliases, template, searchinhits, match);
		} else {
			form.setItems(folder, subfolders, casesensitive, aliases, searchinhits, match);
		}

		addMember(form);

		IButton add = new IButton(I18N.message("addcondition"));
		add.setAutoFit(true);
		add.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {

			@Override
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				addCondition();
			}
		});
		addMember(add);

		conditionsLayout = new VLayout(3);
		addMember(conditionsLayout);

		addResizedHandler(new ResizedHandler() {

			@Override
			public void onResized(ResizedEvent event) {
				if (conditionsLayout.getMembers() != null)
					for (Canvas row : conditionsLayout.getMembers()) {
						row.setWidth(ParametricForm.this.getWidth() - 10);
					}
			}
		});
	}

	public void removeCondition(ConditionRow condition) {
		conditionsLayout.removeMember(condition);
	}

	public void addCondition() {
		ConditionRow row = new ConditionRow(selectedTemplate, true);
		row.setWidth(getWidth() - 10);
		row.reload();
		conditionsLayout.addMember(row);
	}

	@SuppressWarnings("unchecked")
	private void search() {
		if (!vm.validate())
			return;

		Map<String, Object> values = vm.getValues();

		GUISearchOptions options = new GUISearchOptions();

		String hits = Session.get().getInfo().getConfig("search.hits");
		if (hits != null)
			options.setMaxHits(Integer.parseInt(hits));

		options.setRetrieveAliases(new Boolean(vm.getValueAsString("aliases")).booleanValue() ? 1 : 0);
		options.setCaseSensitive(new Boolean(vm.getValueAsString("casesensitive")).booleanValue() ? 1 : 0);

		options.setType(GUISearchOptions.TYPE_PARAMETRIC);

		if (NO_LANGUAGE.equals(vm.getValueAsString("language")))
			options.setLanguage(null);
		else
			options.setLanguage(vm.getValueAsString("language"));
		options.setExpressionLanguage(I18N.getLocale());

		if (values.containsKey("template") && values.get("template") != null
				&& !((String) values.get("template")).isEmpty())
			options.setTemplate(new Long((String) values.get("template")));

		options.setTopOperator((String) values.get("match"));

		if (folder != null) {
			options.setFolder(folder.getFolderId());
			options.setFolderName(folder.getFolderName());
		}

		options.setSearchInSubPath(new Boolean(vm.getValueAsString("subfolders")).booleanValue());

		List<GUICriterion> list = new ArrayList<GUICriterion>();
		if (conditionsLayout.getMembers() != null)
			for (Canvas canvas : conditionsLayout.getMembers()) {
				ConditionRow condition = (ConditionRow) canvas;
				String fieldName = condition.getCriteriaFieldsItem().getValueAsString();
				if (fieldName == null || fieldName.isEmpty())
					continue;

				fieldName = fieldName.replaceAll(Constants.BLANK_PLACEHOLDER, " ");
				if (fieldName.startsWith("_"))
					fieldName = fieldName.substring(1);
				String fieldOperator = condition.getOperatorsFieldsItem().getValueAsString();
				Object fieldValue = condition.getValueFieldsItem().getValue();

				// This lines are necessary to avoid error for GWT values type.
				if (condition.getValueFieldsItem() instanceof IntegerItem)
					fieldValue = Long.parseLong(fieldValue.toString());

				if (fieldName.endsWith("type:" + GUIAttribute.TYPE_INT)
						|| fieldName.endsWith("type:" + GUIAttribute.TYPE_DOUBLE))
					fieldValue = Long.parseLong(fieldValue.toString());
				else if (fieldName.endsWith("type:" + GUIAttribute.TYPE_BOOLEAN))
					fieldValue = fieldValue.toString().equals("yes") ? 1L : 0L;
				else if (fieldName.endsWith("type:" + GUIAttribute.TYPE_DATE))
					fieldValue = (Date) fieldValue;
				else if (fieldName.endsWith("type:" + GUIAttribute.TYPE_STRING_PRESET)) {
					fieldName = fieldName.replaceAll("type:" + GUIAttribute.TYPE_STRING_PRESET, "type:"
							+ GUIAttribute.TYPE_STRING);
				}

				GUICriterion criterion = new GUICriterion();
				criterion.setField(fieldName);

				if (fieldValue instanceof Date)
					criterion.setDateValue((Date) fieldValue);
				else if (fieldValue instanceof Integer)
					criterion.setLongValue(new Long((Integer) fieldValue));
				else if (fieldValue instanceof Long)
					criterion.setLongValue((Long) fieldValue);
				else if (fieldValue instanceof Float)
					criterion.setDoubleValue(new Double((Float) fieldValue));
				else if (fieldValue instanceof Double)
					criterion.setDoubleValue((Double) fieldValue);
				else if (fieldValue instanceof String)
					criterion.setStringValue((String) fieldValue);
				else if (fieldValue instanceof JavaScriptObject) {
					Map m = JSOHelper.convertToMap((JavaScriptObject) fieldValue);
				}

				criterion.setOperator(fieldOperator.toLowerCase());

				if (!fieldName.equals("tags")) {
					list.add(criterion);
				} else {
					// In case of tags, we will have to create a criterion per
					// tag
					if (fieldName.equals("tags")) {
						String[] tgs = ((SelectItem) condition.getValueFieldsItem()).getValues();
						for (String tag : tgs) {
							GUICriterion c = new GUICriterion();
							c.setField(fieldName);
							c.setOperator(fieldOperator.toLowerCase());
							c.setStringValue(tag);
							list.add(c);
						}
					}
				}
			}

		if (!NO_LANGUAGE.equals(vm.getValueAsString("language").trim())) {
			GUICriterion criterion = new GUICriterion();
			criterion.setField("language");
			criterion.setOperator("equals");
			criterion.setStringValue(vm.getValueAsString("language"));
			list.add(criterion);
		}

		if (options.getFolder() != null) {
			GUICriterion criterion = new GUICriterion();
			criterion.setField("folder");
			criterion.setOperator("in");
			criterion.setLongValue(options.getFolder());
			if (options.isSearchInSubPath())
				criterion.setOperator("inorsubfolders");
			list.add(criterion);
		}

		options.setCriteria(list.toArray(new GUICriterion[0]));

		if (new Boolean(vm.getValueAsString(SEARCHINHITS)).booleanValue()) {
			GUIDocument[] records = Search.get().getLastResult();
			Long[] ids = new Long[records.length];
			int i = 0;
			for (GUIDocument rec : records) {
				ids[i] = rec.getId();
				i++;
			}
			options.setFilterIds(ids);
		} else
			options.setFilterIds(null);

		Search.get().setOptions(options);
		Search.get().search();
	}
}