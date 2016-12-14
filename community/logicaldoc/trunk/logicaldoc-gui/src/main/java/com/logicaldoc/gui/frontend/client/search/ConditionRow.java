package com.logicaldoc.gui.frontend.client.search;

import java.util.LinkedHashMap;

import com.logicaldoc.gui.common.client.beans.GUIAttribute;
import com.logicaldoc.gui.common.client.beans.GUITemplate;
import com.logicaldoc.gui.common.client.data.TagsDS;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.events.ResizedEvent;
import com.smartgwt.client.widgets.events.ResizedHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.DateItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.layout.HLayout;

/**
 * This class represents a Criterion Row for the Parametric Search.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.1
 */
public class ConditionRow extends HLayout {

	private ImgButton removeImg = null;

	private DynamicForm form = null;

	private SelectItem attribute = null;

	private SelectItem operator = null;

	private FormItem value = null;

	private GUITemplate template = null;

	private String fieldSelected = "";

	private boolean forDocument;

	public ConditionRow(GUITemplate templ, boolean forDocument) {
		setMembersMargin(5);
		setAlign(Alignment.LEFT);
		setHeight(5);

		this.template = templ;
		this.forDocument = forDocument;
		reload();
	}

	public void reload() {
		if (removeImg != null)
			removeMember(removeImg);
		if (form != null) {
			attribute.clearValue();
			operator.clearValue();
			value.clearValue();
			removeMember(form);
		}

		removeImg = new ImgButton();
		removeImg.setShowDown(false);
		removeImg.setShowRollOver(false);
		removeImg.setLayoutAlign(Alignment.LEFT);
		removeImg.setSrc("[SKIN]/headerIcons/close.gif");
		removeImg.setHeight(18);
		removeImg.setWidth(18);
		removeImg.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {

			@Override
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				ConditionRow.this.getParentCanvas().removeChild(ConditionRow.this);
			}
		});

		form = new DynamicForm();
		form.setTitleOrientation(TitleOrientation.TOP);
		form.setNumCols(4);
		form.setWidth(300);
		form.setHeight(20);

		attribute = new SelectItem("fields", "fields");
		attribute.setShowTitle(false);
		attribute.setPickListWidth(120);
		attribute.setWidth(120);
		attribute.setMultiple(false);
		DataSource ds = null;

		if (forDocument) {
			if (template != null)
				ds = new DocumentFieldsDS(template);
			else
				ds = new DocumentFieldsDS(null);
		} else {
			if (template != null)
				ds = new FolderFieldsDS(template);
			else
				ds = new FolderFieldsDS(null);
		}

		LinkedHashMap<String, String> fieldsMap = new LinkedHashMap<String, String>();
		fieldsMap.put("", " ");
		for (DataSourceField sourceField : ds.getFields()) {
			fieldsMap.put(sourceField.getName(), I18N.message(sourceField.getTitle()));
		}
		attribute.setValueMap(fieldsMap);
		attribute.setValue(fieldSelected);
		attribute.setColSpan(1);

		operator = new SelectItem("operators", "operators");
		operator.setPickListWidth(90);
		operator.setWidth(90);
		operator.setMultiple(false);
		operator.setShowTitle(false);
		operator.setColSpan(1);

		LinkedHashMap<String, String> operatorsMap = null;

		if (fieldSelected != null && !fieldSelected.trim().isEmpty())
			operatorsMap = operatorsFor(fieldSelected);
		else
			operatorsMap = operatorsFor(null);
		operator.setValueMap(operatorsMap);
		if (!operatorsMap.isEmpty())
			operator.setValue(operatorsMap.keySet().iterator().next());

		attribute.addChangedHandler(new ChangedHandler() {

			@Override
			public void onChanged(ChangedEvent event) {
				if (event.getValue() != null) {
					fieldSelected = (String) event.getValue();
					reload();
				}
			}
		});

		if (fieldSelected != null && !fieldSelected.trim().isEmpty()) {
			value = valueItemFor(fieldSelected);
		} else {
			value = ItemFactory.newTextItem("value", "value", null);
		}
		value.setRequired(true);
		value.setEndRow(true);
		value.setShowTitle(false);
		value.setWidth(100);
		value.setColSpan(1);

		form.setItems(attribute, operator, value);

		setMembers(removeImg, form);

		addResizedHandler(new ResizedHandler() {

			@Override
			public void onResized(ResizedEvent event) {
				if (value instanceof DateItem)
					return;

				int padSize = ConditionRow.this.getWidth() - 230;
				if (padSize < 100)
					padSize = 100;
				value.setWidth(padSize);
			}
		});
	}

	private LinkedHashMap<String, String> operatorsFor(String criteriaField) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		if (criteriaField == null)
			return map;

		if (criteriaField.equals("id") || criteriaField.equals("fileSize") || criteriaField.equals("rating")
				|| criteriaField.equals("published") || criteriaField.endsWith("type:" + GUIAttribute.TYPE_INT)
				|| criteriaField.endsWith("type:" + GUIAttribute.TYPE_DOUBLE)) {
			map.put("greaterthan", I18N.message("greaterthan"));
			map.put("lessthan", I18N.message("lessthan"));
			map.put("equals", I18N.message("equals"));
			map.put("notequal", I18N.message("notequal"));
		} else if (criteriaField.equals("sourceDate") || criteriaField.equals("lastModified")
				|| criteriaField.equals("date") || criteriaField.equals("creation")
				|| criteriaField.equals("startPublishing") || criteriaField.equals("stopPublishing")
				|| criteriaField.endsWith("type:" + GUIAttribute.TYPE_DATE)) {
			map.put("greaterthan", I18N.message("greaterthan"));
			map.put("lessthan", I18N.message("lessthan"));
		} else if (criteriaField.endsWith("type:" + GUIAttribute.TYPE_BOOLEAN)) {
			map.put("equals", I18N.message("equals"));
		} else if (criteriaField.endsWith("type:" + GUIAttribute.TYPE_STRING_PRESET)) {
			map.put("equals", I18N.message("equals"));
			map.put("notequal", I18N.message("notequal"));
		} else if (criteriaField.equals("tags")) {
			map.put("contains", I18N.message("contains"));
			map.put("notcontains", I18N.message("notcontains"));
		} else {
			map.put("contains", I18N.message("contains"));
			map.put("notcontains", I18N.message("notcontains"));
			map.put("equals", I18N.message("equals"));
			map.put("notequal", I18N.message("notequal"));
		}

		return map;
	}

	private FormItem valueItemFor(String criteriaField) {
		if (criteriaField.equals("id") || criteriaField.equals("fileSize") || criteriaField.equals("rating")
				|| criteriaField.endsWith("type:" + GUIAttribute.TYPE_INT)
				|| criteriaField.endsWith("type:" + GUIAttribute.TYPE_DOUBLE)) {
			return ItemFactory.newIntegerItem("value", "integer", null);
		} else if (criteriaField.endsWith("type:" + GUIAttribute.TYPE_BOOLEAN)) {
			FormItem item = ItemFactory.newBooleanSelector("value", "boolean");
			item.setValue("yes");
			return item;
		} else if (criteriaField.endsWith("type:" + GUIAttribute.TYPE_STRING_PRESET)) {
			String attributeName = criteriaField.substring(0, criteriaField.lastIndexOf(':') - 4).replaceAll("_", "");
			FormItem item = ItemFactory.newStringItemForAttribute(template.getAttribute(attributeName));
			item.setName("value");
			return item;
		} else if (criteriaField.equals("sourceDate") || criteriaField.equals("lastModified")
				|| criteriaField.equals("date") || criteriaField.equals("creation")
				|| criteriaField.equals("startPublishing") || criteriaField.equals("stopPublishing")
				|| criteriaField.endsWith("type:" + GUIAttribute.TYPE_DATE)) {
			return ItemFactory.newDateItem("value", "date");
		} else if (criteriaField.equals("tags")) {
			return ItemFactory.newTagsMultiplePickList("value", "tags", new TagsDS(null, false, null), null);
		} else {
			return ItemFactory.newTextItem("value", "text", null);
		}
	}

	public SelectItem getCriteriaFieldsItem() {
		return attribute;
	}

	public SelectItem getOperatorsFieldsItem() {
		return operator;
	}

	public FormItem getValueFieldsItem() {
		return value;
	}
}
