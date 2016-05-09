package com.logicaldoc.gui.frontend.client.document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.beans.GUIExtendedAttribute;
import com.logicaldoc.gui.common.client.beans.GUIUser;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.DocumentServiceAsync;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.events.ResizedEvent;
import com.smartgwt.client.widgets.events.ResizedHandler;
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
import com.smartgwt.client.widgets.grid.ListGridRecord;

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

	private GUIExtendedAttribute[] currentExtAttributes = null;

	private List<FormItem> standardItems = new ArrayList<FormItem>();

	private SelectItem templateItem = null;

	private List<FormItem> extendedItems = new ArrayList<FormItem>();

	public ExtendedPropertiesPanel(GUIDocument document, ChangedHandler changedHandler) {
		super(document, changedHandler, null);
		setWidth100();
		setHeight100();
		setMembersMargin(20);
		refresh();

		addResizedHandler(new ResizedHandler() {

			@Override
			public void onResized(ResizedEvent event) {
				adaptForms();
			}
		});
	}

	private void adaptForms() {
		if (templateItem.getValue() != null) {
			int maxExtCols = ((int) getWidth() - 500) / 160; // 160 = length of
																// an item
			int maxExtRows = (int) getHeight() / 46; // 46 = height of an item
			if (maxExtRows < 3)
				maxExtCols = 3;

			if (extendedItems != null) {
				maxExtCols = extendedItems.size() / maxExtRows;
			}

			if (maxExtCols < 2)
				maxExtCols = 2;

			form2.setNumCols(maxExtCols);
		}
	}

	private GUIExtendedAttribute getExtendedAttribute(String name) {
		if (currentExtAttributes != null)
			for (GUIExtendedAttribute extAttr : currentExtAttributes)
				if (extAttr.getName().equals(name))
					return extAttr;
		return null;
	}

	private void refresh() {
		vm.clearValues();
		vm.clearErrors(false);
		extendedItems.clear();

		if (form1 != null)
			form1.destroy();

		if (contains(form1))
			removeChild(form1);
		form1 = new DynamicForm();
		form1.setValuesManager(vm);
		form1.setTitleOrientation(TitleOrientation.TOP);
		form1.setNumCols(3);
		standardItems.clear();

		TextItem sourceItem = ItemFactory.newTextItem("source", "source", document.getSource());
		sourceItem.addChangedHandler(changedHandler);
		sourceItem.setDisabled(!updateEnabled);

		TextItem customId = ItemFactory.newTextItem("customid", "customid", document.getCustomId());
		customId.addChangedHandler(changedHandler);
		customId.setDisabled(!updateEnabled);

		TextItem sourceId = ItemFactory.newTextItem("sourceid", "sourceid", document.getSourceId());
		sourceId.addChangedHandler(changedHandler);
		sourceId.setDisabled(!updateEnabled);

		final DateItem sourceDate = ItemFactory.newDateItem("date", "date");
		sourceDate.setValue(document.getSourceDate());
		sourceDate.addChangedHandler(changedHandler);
		sourceDate.setDisabled(!updateEnabled);
		sourceDate.setUseMask(false);
		sourceDate.setShowPickerIcon(true);
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

		TextItem authorItem = ItemFactory.newTextItem("author", "author", document.getSourceAuthor());
		authorItem.addChangedHandler(changedHandler);
		authorItem.setDisabled(!updateEnabled);

		TextItem typeItem = ItemFactory.newTextItem("type", "type", document.getSourceType());
		typeItem.addChangedHandler(changedHandler);
		typeItem.setDisabled(!updateEnabled);

		TextItem recipientItem = ItemFactory.newTextItem("recipient", "recipient", document.getRecipient());
		recipientItem.addChangedHandler(changedHandler);
		recipientItem.setDisabled(!updateEnabled);

		TextItem objectItem = ItemFactory.newTextItem("object", "object", document.getObject());
		objectItem.addChangedHandler(changedHandler);
		objectItem.setDisabled(!updateEnabled);

		TextItem coverageItem = ItemFactory.newTextItem("coverage", "coverage", document.getCoverage());
		coverageItem.addChangedHandler(changedHandler);
		coverageItem.setDisabled(!updateEnabled);

		templateItem = ItemFactory.newTemplateSelector(true, null);
		templateItem.addChangedHandler(changedHandler);
		templateItem.setMultiple(false);
		templateItem.setDisabled(!updateEnabled || document.getFolder().getTemplateLocked() == 1);
		if (document.getTemplateId() != null)
			templateItem.setValue(document.getTemplateId().toString());

		templateItem.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				if (templateItem.getValue() != null && !"".equals(templateItem.getValue().toString())) {
					document.setAttributes(new GUIExtendedAttribute[0]);
					prepareExtendedAttributes(new Long(event.getValue().toString()));
				} else {
					document.setAttributes(new GUIExtendedAttribute[0]);
					prepareExtendedAttributes(null);
				}
			}
		});

		standardItems.add(customId);
		standardItems.add(sourceItem);

		if (Feature.visible(Feature.TEMPLATE)) {
			standardItems.add(templateItem);

			if (!Feature.enabled(Feature.TEMPLATE)) {
				templateItem.setDisabled(true);
				templateItem.setTooltip(I18N.message("featuredisabled"));
			}
		}

		standardItems.add(sourceId);
		standardItems.add(recipientItem);
		standardItems.add(objectItem);
		standardItems.add(typeItem);
		standardItems.add(coverageItem);
		standardItems.add(sourceDate);

		standardItems.add(authorItem);
		form1.setItems(standardItems.toArray(new FormItem[0]));
		addMember(form1);

		if (Feature.enabled(Feature.TEMPLATE))
			prepareExtendedAttributes(document.getTemplateId());
	}

	/*
	 * Prepare the second form for the extended attributes
	 */
	private void prepareExtendedAttributes(final Long templateId) {
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

		documentService.getAttributes(templateId, new AsyncCallback<GUIExtendedAttribute[]>() {
			@Override
			public void onFailure(Throwable caught) {
				Log.serverError(caught);
			}

			@Override
			public void onSuccess(GUIExtendedAttribute[] result) {
				currentExtAttributes = result;
				extendedItems.clear();
				for (GUIExtendedAttribute att : result) {
					if (att.getType() == GUIExtendedAttribute.TYPE_STRING) {
						FormItem item = ItemFactory.newStringItemForExtendedAttribute(templateId, att);
						if (document.getValue(att.getName()) != null)
							item.setValue((String) document.getValue(att.getName()));
						item.addChangedHandler(changedHandler);
						item.setDisabled(!updateEnabled);
						extendedItems.add(item);
					} else if (att.getType() == GUIExtendedAttribute.TYPE_INT) {
						IntegerItem item = ItemFactory.newIntegerItemForExtendedAttribute(att.getName(),
								att.getLabel(), null);
						if (document.getValue(att.getName()) != null)
							item.setValue((Long) document.getValue(att.getName()));
						item.setRequired(att.isMandatory());
						item.addChangedHandler(changedHandler);
						item.setDisabled(!updateEnabled);
						extendedItems.add(item);
					} else if (att.getType() == GUIExtendedAttribute.TYPE_BOOLEAN) {
						SelectItem item = ItemFactory.newBooleanSelectorForExtendedAttribute(att.getName(),
								att.getLabel(), !att.isMandatory());
						if (document.getValue(att.getName()) != null)
							item.setValue(((Boolean) document.getValue(att.getName())).booleanValue() ? "1" : "0");
						item.setRequired(att.isMandatory());
						item.addChangedHandler(changedHandler);
						item.setDisabled(!updateEnabled);
						extendedItems.add(item);
					} else if (att.getType() == GUIExtendedAttribute.TYPE_DOUBLE) {
						FloatItem item = ItemFactory.newFloatItemForExtendedAttribute(att.getName(), att.getLabel(),
								null);
						if (document.getValue(att.getName()) != null)
							item.setValue((Double) document.getValue(att.getName()));
						item.setRequired(att.isMandatory());
						item.addChangedHandler(changedHandler);
						item.setDisabled(!updateEnabled);
						extendedItems.add(item);
					} else if (att.getType() == GUIExtendedAttribute.TYPE_DATE) {
						final DateItem item = ItemFactory.newDateItemForExtendedAttribute(att.getName(), att.getLabel());
						if (document.getValue(att.getName()) != null)
							item.setValue((Date) document.getValue(att.getName()));
						item.setRequired(att.isMandatory());
						item.addChangedHandler(changedHandler);
						item.addKeyPressHandler(new KeyPressHandler() {
							@Override
							public void onKeyPress(KeyPressEvent event) {
								if ("backspace".equals(event.getKeyName().toLowerCase())
										|| "delete".equals(event.getKeyName().toLowerCase())) {
									item.clearValue();
									item.setValue((Date) null);
									changedHandler.onChanged(null);
								} else {
									changedHandler.onChanged(null);
								}
							}
						});
						item.setDisabled(!updateEnabled);
						extendedItems.add(item);
					} else if (att.getType() == GUIExtendedAttribute.TYPE_USER) {
						SelectItem item = ItemFactory.newUserSelectorForExtendedAttribute(att.getName(),
								att.getLabel(),
								(att.getOptions() != null && att.getOptions().length > 0) ? att.getOptions()[0] : null);
						if (document.getValue(att.getName()) != null)
							item.setValue((document.getValue(att.getName()).toString()));
						item.setRequired(att.isMandatory());
						item.addChangedHandler(changedHandler);
						item.setDisabled(!updateEnabled);
						extendedItems.add(item);
					}
				}
				form2.setItems(extendedItems.toArray(new FormItem[0]));
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
			document.setCustomId((String) values.get("customid"));
			document.setSourceDate((Date) values.get("date"));
			document.setSourceAuthor((String) values.get("author"));
			document.setSourceType((String) values.get("type"));
			document.setRecipient((String) values.get("recipient"));
			document.setObject((String) values.get("object"));
			document.setCoverage((String) values.get("coverage"));

			if (Feature.enabled(Feature.TEMPLATE)) {
				if (values.get("template") == null || "".equals(values.get("template").toString()))
					document.setTemplateId(null);
				else {
					document.setTemplateId(Long.parseLong(values.get("template").toString()));
				}
				for (String name : values.keySet()) {
					try {
						if (name.startsWith("_")) {
							Object val = values.get(name);
							String nm = name.substring(1).replaceAll(Constants.BLANK_PLACEHOLDER, " ");
							GUIExtendedAttribute att = getExtendedAttribute(nm);
							if (att == null)
								continue;

							if (val != null) {
								if (att.getType() == GUIExtendedAttribute.TYPE_USER) {
									SelectItem userItem = (SelectItem) form2.getItem(name);
									if (userItem.getValue() != null && !"".equals(userItem.getValue())) {
										ListGridRecord sel = userItem.getSelectedRecord();

										// Prepare a dummy user to set as
										// attribute
										// value
										GUIUser dummy = new GUIUser();
										dummy.setId(Long.parseLong(val.toString()));
										dummy.setFirstName(sel.getAttributeAsString("firstName"));
										dummy.setName(sel.getAttributeAsString("name"));
										document.setValue(nm, dummy);
									} else {
										GUIExtendedAttribute at = document.getExtendedAttribute(nm);
										at.setIntValue(null);
										at.setStringValue(null);
										at.setType(GUIExtendedAttribute.TYPE_USER);
									}
								} else if (att.getType() == GUIExtendedAttribute.TYPE_BOOLEAN) {
									if (!(val == null || "".equals(val.toString().trim())))
										document.setValue(nm, "1".equals(val.toString().trim()) ? true : false);
									else if (document.getExtendedAttribute(nm) != null) {
										GUIExtendedAttribute at = document.getExtendedAttribute(nm);
										at.setBooleanValue(null);
										at.setType(GUIExtendedAttribute.TYPE_BOOLEAN);
									}
								} else
									document.setValue(nm, val);
							} else {
								if (att != null) {
									if (att.getType() == GUIExtendedAttribute.TYPE_INT) {
										document.getExtendedAttribute(nm).setIntValue(null);
									} else if (att.getType() == GUIExtendedAttribute.TYPE_BOOLEAN) {
										document.getExtendedAttribute(nm).setBooleanValue(null);
									} else if (att.getType() == GUIExtendedAttribute.TYPE_DOUBLE) {
										document.getExtendedAttribute(nm).setDoubleValue(null);
									} else if (att.getType() == GUIExtendedAttribute.TYPE_DATE) {
										document.getExtendedAttribute(nm).setDateValue(null);
									} else if (att.getType() == GUIExtendedAttribute.TYPE_USER) {
										GUIExtendedAttribute at = document.getExtendedAttribute(nm);
										at.setIntValue(null);
										at.setStringValue(null);
										at.setType(GUIExtendedAttribute.TYPE_USER);
									} else {
										document.setValue(nm, (String)null);
									}
								}
							}
						}
					} catch (Throwable t) {
						
					}
				}
			}
		}
		return !vm.hasErrors();
	}
}