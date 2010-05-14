package com.logicaldoc.gui.frontend.client.document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.beans.GUIExtendedAttribute;
import com.logicaldoc.gui.common.client.data.TemplatesDS;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.DocumentServiceAsync;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.DateItem;
import com.smartgwt.client.widgets.form.fields.FloatItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.IntegerItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;

/**
 * Shows document's standard properties and read-only data
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class ExtendedPropertiesPanel extends DocumentDetailTab {
	private DynamicForm form1 = new DynamicForm();

	private DynamicForm form2 = new DynamicForm();

	private DocumentServiceAsync documentService = (DocumentServiceAsync) GWT.create(DocumentService.class);

	private ValuesManager vm = new ValuesManager();

	public ExtendedPropertiesPanel(GUIDocument document, ChangedHandler changedHandler) {
		super(document, changedHandler);
		setWidth100();
		setHeight100();
		setMembersMargin(20);
		refresh();
	}

	private void refresh() {
		vm.clearValues();
		vm.clearErrors(false);

		if (form1 != null)
			form1.destroy();

		if (contains(form1))
			removeChild(form1);
		form1 = new DynamicForm();
		form1.setValuesManager(vm);
		form1.setTitleOrientation(TitleOrientation.TOP);
		List<FormItem> items = new ArrayList<FormItem>();

		TextItem sourceItem = new TextItem();
		sourceItem.setName("source");
		sourceItem.setTitle(I18N.getMessage("source"));
		sourceItem.setValue(document.getSource());
		sourceItem.addChangedHandler(changedHandler);
		sourceItem.setDisabled(!update);

		DateItem sourceDate = ItemFactory.newDateItem("date", I18N.getMessage("date"));
		sourceDate.setValue(document.getSourceDate());
		sourceDate.addChangedHandler(changedHandler);
		sourceDate.setDisabled(!update);

		SelectItem languageItem = ItemFactory.newLanguageSelector("language", false);
		languageItem.addChangedHandler(changedHandler);
		languageItem.setDisabled(!update);

		TextItem authorItem = new TextItem();
		authorItem.setName("author");
		authorItem.setTitle(I18N.getMessage("author"));
		authorItem.setValue(document.getSourceAuthor());
		authorItem.addChangedHandler(changedHandler);
		authorItem.setDisabled(!update);

		TextItem typeItem = new TextItem();
		typeItem.setName("type");
		typeItem.setTitle(I18N.getMessage("type"));
		typeItem.setValue(document.getSourceType());
		typeItem.addChangedHandler(changedHandler);
		typeItem.setDisabled(!update);

		TextItem recipientItem = new TextItem();
		recipientItem.setName("recipient");
		recipientItem.setTitle(I18N.getMessage("recipient"));
		recipientItem.setValue(document.getRecipient());
		recipientItem.addChangedHandler(changedHandler);
		recipientItem.setDisabled(!update);

		TextItem objectItem = new TextItem();
		objectItem.setName("object");
		objectItem.setTitle(I18N.getMessage("object"));
		objectItem.setValue(document.getObject());
		objectItem.addChangedHandler(changedHandler);
		objectItem.setDisabled(!update);

		TextItem coverageItem = new TextItem();
		coverageItem.setName("coverage");
		coverageItem.setTitle(I18N.getMessage("coverage"));
		coverageItem.setValue(document.getCoverage());
		coverageItem.addChangedHandler(changedHandler);
		coverageItem.setDisabled(!update);

		SelectItem templateItem = new SelectItem("template", I18N.getMessage("template"));
		templateItem.setDisplayField("name");
		templateItem.setValueField("id");
		templateItem.setPickListWidth(250);
		templateItem.setOptionDataSource(TemplatesDS.getInstanceWithEmpty());
		templateItem.addChangedHandler(changedHandler);
		templateItem.setDisabled(!update);

		if (document.getTemplateId() != null)
			templateItem.setValue(document.getTemplateId().toString());
		templateItem.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				if (event.getValue() != null && !"".equals(event.getValue())) {
					document.setAttributes(new GUIExtendedAttribute[0]);
					prepareExtendedAttributes(new Long(event.getValue().toString()));
				}
			}
		});

		items.add(sourceItem);
		items.add(recipientItem);
		items.add(objectItem);
		items.add(typeItem);
		items.add(coverageItem);
		items.add(sourceDate);
		items.add(authorItem);
		items.add(languageItem);
		items.add(templateItem);

		form1.setItems(items.toArray(new FormItem[0]));
		addMember(form1);

		prepareExtendedAttributes(document.getTemplateId());
	}

	/*
	 * Prepare the second form for the extended attributes
	 */
	private void prepareExtendedAttributes(Long templateId) {
		if (form2 != null)
			form2.destroy();
		if (contains(form2))
			removeChild(form2);
		form2 = new DynamicForm();
		form2.setValuesManager(vm);
		form2.setTitleOrientation(TitleOrientation.TOP);
		addMember(form2);

		if (templateId == null)
			return;

		documentService.getAttributes(Session.get().getSid(), templateId, new AsyncCallback<GUIExtendedAttribute[]>() {
			@Override
			public void onFailure(Throwable caught) {
				Log.serverError(caught);
			}

			@Override
			public void onSuccess(GUIExtendedAttribute[] result) {
				List<FormItem> items = new ArrayList<FormItem>();
				for (GUIExtendedAttribute att : result) {
					// We cannot use spaces in items name
					String itemName = "_" + att.getName().replaceAll(" ", "___");
					if (att.getType() == GUIExtendedAttribute.TYPE_STRING) {
						TextItem item = new TextItem(itemName, att.getName());
						if (document.getValue(att.getName()) != null)
							item.setValue((String) document.getValue(att.getName()));
						item.setRequired(att.isMandatory());
						item.addChangedHandler(changedHandler);
						item.setDisabled(!update);
						items.add(item);
					} else if (att.getType() == GUIExtendedAttribute.TYPE_INT) {
						IntegerItem item = new IntegerItem();
						item.setName(itemName);
						item.setTitle(att.getName());
						if (document.getValue(att.getName()) != null)
							item.setValue((Long) document.getValue(att.getName()));
						item.setRequired(att.isMandatory());
						item.addChangedHandler(changedHandler);
						item.setDisabled(!update);
						items.add(item);
					} else if (att.getType() == GUIExtendedAttribute.TYPE_DOUBLE) {
						FloatItem item = new FloatItem();
						item.setName(itemName);
						item.setTitle(att.getName());
						if (document.getValue(att.getName()) != null)
							item.setValue((Double) document.getValue(att.getName()));
						item.setRequired(att.isMandatory());
						item.addChangedHandler(changedHandler);
						item.setDisabled(!update);
						items.add(item);
					} else if (att.getType() == GUIExtendedAttribute.TYPE_DATE) {
						DateItem item = ItemFactory.newDateItem(itemName, att.getName());
						if (document.getValue(att.getName()) != null)
							item.setValue((Date) document.getValue(att.getName()));
						item.setRequired(att.isMandatory());
						item.addChangedHandler(changedHandler);
						item.setDisabled(!update);
						items.add(item);
					}
				}
				form2.setItems(items.toArray(new FormItem[0]));
			}
		});
	}

	@SuppressWarnings("unchecked")
	boolean validate() {
		Map<String, Object> values = (Map<String, Object>) vm.getValues();
		vm.validate();
		if (!vm.hasErrors()) {
			document.setSource((String) values.get("source"));
			document.setSourceDate((Date) values.get("sourceDate"));
			document.setLanguage((String) values.get("language"));
			document.setSourceAuthor((String) values.get("author"));
			document.setSourceType((String) values.get("type"));
			document.setRecipient((String) values.get("recipient"));
			document.setObject((String) values.get("object"));
			document.setCoverage((String) values.get("coverage"));
			if (values.get("template") == null || "".equals((String) values.get("template")))
				document.setTemplateId(null);
			else
				document.setTemplateId(Long.parseLong((String) values.get("template")));
			for (String name : values.keySet()) {
				if (name.startsWith("_")) {
					Object val = values.get(name);
					String nm = name.substring(1).replaceAll("___", " ");
					document.setValue(nm, val);
				}
			}
		}
		return !vm.hasErrors();
	}
}