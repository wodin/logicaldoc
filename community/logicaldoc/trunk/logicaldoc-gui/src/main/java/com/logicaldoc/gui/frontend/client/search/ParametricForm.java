package com.logicaldoc.gui.frontend.client.search;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUICriterion;
import com.logicaldoc.gui.common.client.beans.GUISearchOptions;
import com.logicaldoc.gui.common.client.beans.GUITemplate;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.widgets.FolderSelector;
import com.logicaldoc.gui.frontend.client.services.TemplateService;
import com.logicaldoc.gui.frontend.client.services.TemplateServiceAsync;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.types.TopOperatorAppearance;
import com.smartgwt.client.util.JSOHelper;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.FilterBuilder;
import com.smartgwt.client.widgets.form.FormItemIfFunction;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * Shows a parametric search form
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class ParametricForm extends VLayout {
	private static final String NO_LANGUAGE = "";

	private ValuesManager vm = new ValuesManager();

	private TemplateServiceAsync service = (TemplateServiceAsync) GWT.create(TemplateService.class);

	private FolderSelector folder;

	private FilterBuilder filterBuilder;

	public ParametricForm() {
		setHeight100();
		setMargin(3);
		setMembersMargin(5);
		setAlign(Alignment.LEFT);

		filterBuilder = new FilterBuilder();
		filterBuilder.setDataSource(new DocumentFieldsDS(null));
		filterBuilder.setTopOperatorAppearance(TopOperatorAppearance.RADIO);

		addMember(filterBuilder);

		final DynamicForm form = new DynamicForm();
		form.setValuesManager(vm);
		form.setTitleOrientation(TitleOrientation.TOP);
		form.setNumCols(1);
		form.setWidth(300);

		SelectItem language = ItemFactory.newLanguageSelector("language", true, false);

		folder = new FolderSelector(null, true);

		CheckboxItem subfolders = new CheckboxItem("subfolders", I18N.message("searchinsubfolders2"));
		subfolders.setColSpan(3);
		subfolders.setShowIfCondition(new FormItemIfFunction() {
			public boolean execute(FormItem item, Object value, DynamicForm form) {
				return folder.getValue() != null && !"".equals(folder.getValue());
			}
		});

		if (Feature.visible(Feature.TEMPLATE)) {
			SelectItem template = ItemFactory.newTemplateSelector(false);

			template.addChangedHandler(new ChangedHandler() {
				@Override
				public void onChanged(ChangedEvent event) {
					if (event.getValue() != null && !"".equals(event.getValue()))
						service.getTemplate(Session.get().getSid(), new Long(event.getValue().toString()),
								new AsyncCallback<GUITemplate>() {
									@Override
									public void onFailure(Throwable caught) {
										Log.serverError(caught);
									}

									@Override
									public void onSuccess(GUITemplate result) {
										removeMember(filterBuilder);
										filterBuilder = new FilterBuilder();
										filterBuilder.setDataSource(new DocumentFieldsDS(result));
										filterBuilder.setTopOperatorAppearance(TopOperatorAppearance.RADIO);
										addMember(filterBuilder, 0);
									}
								});
				}
			});

			form.setItems(language, template, folder, subfolders);
		} else
			form.setItems(language, folder, subfolders);

		addMember(form);

		IButton search = new IButton(I18N.message("search"));
		addMember(search);

		search.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				search();
			}
		});
	}

	@SuppressWarnings("unchecked")
	private void search() {
		if (!vm.validate() || !filterBuilder.validate())
			return;

		Map<String, Object> values = vm.getValues();

		GUISearchOptions options = new GUISearchOptions();
		options.setMaxHits(40);
		options.setType(GUISearchOptions.TYPE_PARAMETRIC);

		if (NO_LANGUAGE.equals(vm.getValueAsString("language")))
			options.setLanguage(null);
		else
			options.setLanguage(vm.getValueAsString("language"));
		options.setExpressionLanguage(I18N.getLocale());

		if (values.containsKey("template") && !((String) values.get("template")).isEmpty())
			options.setTemplate(new Long((String) values.get("template")));

		options.setFolder(folder.getFolderId());
		options.setFolderName(folder.getFolderName());

		options.setSearchInSubPath(new Boolean(vm.getValueAsString("subfolders")).booleanValue());

		List<GUICriterion> list = new ArrayList<GUICriterion>();
		Criteria criteria = filterBuilder.getCriteria();
		if (criteria != null) {

			// build a map from the JSO
			Map criteriaMap = JSOHelper.convertToMap(criteria.getJsObj());

			// get the and|or|not Radio Button param
			String topOperator = (String) criteriaMap.get("operator");
			options.setTopOperator(topOperator);

			// get all the actual criteria as array of JSO objects
			Object[] criteriaObjects = JSOHelper.convertToJavaObjectArray((JavaScriptObject) criteriaMap
					.get("criteria"));
			for (int i = 0; i < criteriaObjects.length; i++) {

				// extract the fields as a map from the object
				Map criteriaFieldsMap = JSOHelper.convertToMap((JavaScriptObject) criteriaObjects[i]);
				String fieldName = (String) criteriaFieldsMap.get("fieldName");
				fieldName = fieldName.replaceAll(Constants.BLANK_PLACEHOLDER, " ");
				String fieldOperator = (String) criteriaFieldsMap.get("operator");
				Object fieldValue = (Object) criteriaFieldsMap.get("value");

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
					SC.say("" + m.get("value"));
				}

				criterion.setOperator(fieldOperator.toLowerCase());
			}
		}

		if (!NO_LANGUAGE.equals(vm.getValueAsString("language"))) {
			GUICriterion criterion = new GUICriterion();
			criterion.setField("language");
			criterion.setOperator("equals");
			criterion.setStringValue(vm.getValueAsString("language"));
			list.add(criterion);
		}

		if (values.containsKey("template") && !((String) values.get("template")).isEmpty()) {
			GUICriterion criterion = new GUICriterion();
			criterion.setField("template");
			criterion.setOperator("equals");
			criterion.setLongValue(new Long((String) values.get("template")));
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

		Search.get().setOptions(options);
		Search.get().search();
	}
}