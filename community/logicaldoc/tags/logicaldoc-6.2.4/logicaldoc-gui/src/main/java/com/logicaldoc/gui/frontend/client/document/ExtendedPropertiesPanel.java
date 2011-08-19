package com.logicaldoc.gui.frontend.client.document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.beans.GUIExtendedAttribute;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.DocumentServiceAsync;
import com.smartgwt.client.types.DateDisplayFormat;
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
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;

/**
 * Shows document's standard properties and read-only data
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class ExtendedPropertiesPanel extends DocumentDetailTab {
	private DynamicForm form1 = new DynamicForm();

	private DynamicForm form2 = new DynamicForm();

	private DocumentServiceAsync documentService = (DocumentServiceAsync) GWT
			.create(DocumentService.class);

	private ValuesManager vm = new ValuesManager();

	private GUIExtendedAttribute[] currentExtAttributes = null;

	public ExtendedPropertiesPanel(GUIDocument document,
			ChangedHandler changedHandler) {
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

		TextItem sourceItem = ItemFactory.newTextItem("source", "source",
				document.getSource());
		sourceItem.addChangedHandler(changedHandler);
		sourceItem.setDisabled(!update);

		TextItem sourceId = ItemFactory.newTextItem("sourceid", "sourceid",
				document.getSourceId());
		sourceId.addChangedHandler(changedHandler);
		sourceId.setDisabled(!update);

		final DateItem sourceDate = ItemFactory.newDateItem("date", "date");
		sourceDate.setValue(document.getSourceDate());
		sourceDate.addChangedHandler(changedHandler);
		sourceDate.setDisabled(!update);
		sourceDate.setUseMask(false);
		sourceDate.setShowPickerIcon(true);
		sourceDate.setDateFormatter(DateDisplayFormat.TOEUROPEANSHORTDATE);
		sourceDate.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if ("backspace".equals(event.getKeyName().toLowerCase())
						|| "delete".equals(event.getKeyName().toLowerCase())) {
					sourceDate.clearValue();
					sourceDate.setValue((Date) null);
					changedHandler.onChanged(null);
				} else {
					changedHandler.onChanged(null);
				}
			}
		});

		TextItem authorItem = ItemFactory.newTextItem("author", "author",
				document.getSourceAuthor());
		authorItem.addChangedHandler(changedHandler);
		authorItem.setDisabled(!update);

		TextItem typeItem = ItemFactory.newTextItem("type", "type",
				document.getSourceType());
		typeItem.addChangedHandler(changedHandler);
		typeItem.setDisabled(!update);

		TextItem recipientItem = ItemFactory.newTextItem("recipient",
				"recipient", document.getRecipient());
		recipientItem.addChangedHandler(changedHandler);
		recipientItem.setDisabled(!update);

		TextItem objectItem = ItemFactory.newTextItem("object", "object",
				document.getObject());
		objectItem.addChangedHandler(changedHandler);
		objectItem.setDisabled(!update);

		TextItem coverageItem = ItemFactory.newTextItem("coverage", "coverage",
				document.getCoverage());
		coverageItem.addChangedHandler(changedHandler);
		coverageItem.setDisabled(!update);

		SelectItem templateItem = ItemFactory.newTemplateSelector(false, null);
		templateItem.addChangedHandler(changedHandler);
		templateItem.setMultiple(false);
		templateItem.setDisabled(!update);
		if (document.getTemplateId() != null)
			templateItem.setValue(document.getTemplateId().toString());

		templateItem.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				if (event.getValue() != null
						&& !"".equals(event.getValue().toString())) {
					document.setAttributes(new GUIExtendedAttribute[0]);
					prepareExtendedAttributes(new Long(event.getValue()
							.toString()));
				} else {
					document.setAttributes(new GUIExtendedAttribute[0]);
					prepareExtendedAttributes(null);

				}
			}
		});

		items.add(sourceItem);
		items.add(sourceId);
		items.add(recipientItem);
		items.add(objectItem);
		items.add(typeItem);
		items.add(coverageItem);
		items.add(sourceDate);
		if (Feature.visible(Feature.TEMPLATE)) {
			items.add(templateItem);
			if (!Feature.enabled(Feature.TEMPLATE)) {
				templateItem.setDisabled(true);
				templateItem.setTooltip(I18N.message("featuredisabled"));
			}
		}

		items.add(authorItem);
		form1.setItems(items.toArray(new FormItem[0]));
		addMember(form1);

		if (Feature.enabled(Feature.TEMPLATE))
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
		form2.clearValues();
		form2.clear();
		addMember(form2);

		if (templateId == null)
			return;

		documentService.getAttributes(Session.get().getSid(), templateId,
				new AsyncCallback<GUIExtendedAttribute[]>() {
					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(GUIExtendedAttribute[] result) {
						currentExtAttributes = result;
						List<FormItem> items = new ArrayList<FormItem>();

						for (GUIExtendedAttribute att : result) {
							if (att.getType() == GUIExtendedAttribute.TYPE_STRING) {
								FormItem item = ItemFactory
										.newStringItemForExtendedAttribute(att);
								if (document.getValue(att.getName()) != null)
									item.setValue((String) document
											.getValue(att.getName()));
								item.addChangedHandler(changedHandler);
								item.setDisabled(!update);
								items.add(item);
							} else if (att.getType() == GUIExtendedAttribute.TYPE_INT) {
								IntegerItem item = ItemFactory
										.newIntegerItemForExtendedAttribute(
												att.getName(), att.getLabel(),
												null);
								if (document.getValue(att.getName()) != null)
									item.setValue((Long) document.getValue(att
											.getName()));
								item.setRequired(att.isMandatory());
								item.addChangedHandler(changedHandler);
								item.setDisabled(!update);
								items.add(item);
							} else if (att.getType() == GUIExtendedAttribute.TYPE_DOUBLE) {
								FloatItem item = ItemFactory
										.newFloatItemForExtendedAttribute(
												att.getName(), att.getLabel(),
												null);
								if (document.getValue(att.getName()) != null)
									item.setValue((Double) document
											.getValue(att.getName()));
								item.setRequired(att.isMandatory());
								item.addChangedHandler(changedHandler);
								item.setDisabled(!update);
								items.add(item);
							} else if (att.getType() == GUIExtendedAttribute.TYPE_DATE) {
								final DateItem item = ItemFactory
										.newDateItemForExtendedAttribute(
												att.getName(), att.getLabel());
								if (document.getValue(att.getName()) != null)
									item.setValue((Date) document.getValue(att
											.getName()));
								item.setRequired(att.isMandatory());
								item.addChangedHandler(changedHandler);
								item.addKeyPressHandler(new KeyPressHandler() {
									@Override
									public void onKeyPress(KeyPressEvent event) {
										if ("backspace".equals(event
												.getKeyName().toLowerCase())
												|| "delete".equals(event
														.getKeyName()
														.toLowerCase())) {
											item.clearValue();
											item.setValue((Date) null);
											changedHandler.onChanged(null);
										} else {
											changedHandler.onChanged(null);
										}
									}
								});
								item.setDisabled(!update);
								items.add(item);
							}
						}
						form2.setItems(items.toArray(new FormItem[0]));
					}
				});
	}

	@SuppressWarnings("unchecked")
	public boolean validate() {
		Map<String, Object> values = (Map<String, Object>) vm.getValues();
		vm.validate();
		if (!vm.hasErrors()) {
			document.setSource((String) values.get("source"));
			document.setSourceId((String) values.get("sourceid"));
			document.setSourceDate((Date) values.get("date"));
			document.setSourceAuthor((String) values.get("author"));
			document.setSourceType((String) values.get("type"));
			document.setRecipient((String) values.get("recipient"));
			document.setObject((String) values.get("object"));
			document.setCoverage((String) values.get("coverage"));

			if (Feature.enabled(Feature.TEMPLATE)) {
				if (values.get("template") == null
						|| "".equals(values.get("template").toString()))
					document.setTemplateId(null);
				else {
					document.setTemplateId(Long.parseLong(values
							.get("template").toString()));
				}
				for (String name : values.keySet()) {
					if (name.startsWith("_")) {
						Object val = values.get(name);
						String nm = name.substring(1).replaceAll(
								Constants.BLANK_PLACEHOLDER, " ");
						if (val != null) {
							document.setValue(nm, val);
						} else {
							for (GUIExtendedAttribute extAttr : currentExtAttributes) {
								if (extAttr.getName().equals(nm)) {
									if (extAttr.getType() == GUIExtendedAttribute.TYPE_INT) {
										document.getExtendedAttribute(nm)
												.setIntValue(null);
										break;
									} else if (extAttr.getType() == GUIExtendedAttribute.TYPE_DOUBLE) {
										document.getExtendedAttribute(nm)
												.setDoubleValue(null);
										break;
									} else if (extAttr.getType() == GUIExtendedAttribute.TYPE_DATE) {
										document.getExtendedAttribute(nm)
												.setDateValue(null);
										break;
									} else {
										document.setValue(nm, "");
										break;
									}
								}
							}
						}
					}
				}
			}
		}
		return !vm.hasErrors();
	}
}