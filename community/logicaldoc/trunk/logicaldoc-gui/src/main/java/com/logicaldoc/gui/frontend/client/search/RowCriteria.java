package com.logicaldoc.gui.frontend.client.search;

import java.util.LinkedHashMap;

import com.logicaldoc.gui.common.client.beans.GUIExtendedAttribute;
import com.logicaldoc.gui.common.client.beans.GUITemplate;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.form.DynamicForm;
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
public class RowCriteria extends HLayout {

	private ImgButton removeImg = null;

	private DynamicForm criteriaForm = null;

	private SelectItem criteriaFieldsItem = null;

	private SelectItem operatorsFieldsItem = null;

	private FormItem valueFieldsItem = null;

	private GUITemplate template = null;

	private int rowPosition = 0;

	private String fieldSelected = "";

	public RowCriteria(GUITemplate templ, int position) {
		setMembersMargin(5);
		setAlign(Alignment.LEFT);
		setHeight(5);

		this.template = templ;
		this.rowPosition = position;
		reload();
	}

	public void reload() {
		if (removeImg != null)
			removeMember(removeImg);
		if (criteriaForm != null) {
			criteriaFieldsItem.clearValue();
			operatorsFieldsItem.clearValue();
			valueFieldsItem.clearValue();
			removeMember(criteriaForm);
		}

		removeImg = new ImgButton();
		removeImg.setShowDown(false);
		removeImg.setShowRollOver(false);
		removeImg.setLayoutAlign(Alignment.LEFT);
		removeImg.setSrc("[SKIN]/headerIcons/close.gif");
		removeImg.setHeight(18);
		removeImg.setWidth(18);
		removeImg.setDisabled(rowPosition == 0);
		removeImg.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {

			@Override
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				ParametricForm.get().removeCriteriaRow(RowCriteria.this);
			}
		});

		criteriaForm = new DynamicForm();
		criteriaForm.setTitleOrientation(TitleOrientation.TOP);
		criteriaForm.setNumCols(4);
		criteriaForm.setWidth(300);
		criteriaForm.setHeight(20);

		criteriaFieldsItem = new SelectItem("fields", "fields");
		criteriaFieldsItem.setShowTitle(false);
		criteriaFieldsItem.setPickListWidth(120);
		criteriaFieldsItem.setWidth(120);
		criteriaFieldsItem.setMultiple(false);
		DataSource ds = null;
		if (template != null)
			ds = new DocumentFieldsDS(template);
		else
			ds = new DocumentFieldsDS(null);

		LinkedHashMap<String, String> fieldsMap = new LinkedHashMap<String, String>();
		fieldsMap.put("", " ");
		for (DataSourceField sourceField : ds.getFields()) {
			fieldsMap.put(sourceField.getName(), I18N.message(sourceField.getTitle()));
		}
		criteriaFieldsItem.setValueMap(fieldsMap);
		criteriaFieldsItem.setValue(fieldSelected);
		criteriaFieldsItem.setColSpan(1);

		operatorsFieldsItem = new SelectItem("operators", "operators");
		operatorsFieldsItem.setPickListWidth(90);
		operatorsFieldsItem.setWidth(90);
		operatorsFieldsItem.setMultiple(false);
		operatorsFieldsItem.setShowTitle(false);
		operatorsFieldsItem.setColSpan(1);

		LinkedHashMap<String, String> operatorsMap = null;

		if (fieldSelected != null && !fieldSelected.trim().isEmpty())
			operatorsMap = operatorsFor(fieldSelected);
		else
			operatorsMap = operatorsFor(null);
		operatorsFieldsItem.setValueMap(operatorsMap);
		if (!operatorsMap.isEmpty())
			operatorsFieldsItem.setValue(operatorsMap.keySet().iterator().next());

		criteriaFieldsItem.addChangedHandler(new ChangedHandler() {

			@Override
			public void onChanged(ChangedEvent event) {
				if (event.getValue() != null) {
					fieldSelected = (String) event.getValue();
					reload();
				}
			}
		});

		if (fieldSelected != null && !fieldSelected.trim().isEmpty()) {
			valueFieldsItem = valueItemFor(fieldSelected);
		} else {
			valueFieldsItem = ItemFactory.newTextItem("value", "value", null);
		}
		valueFieldsItem.setRequired(true);
		valueFieldsItem.setEndRow(true);
		valueFieldsItem.setShowTitle(false);
		valueFieldsItem.setWidth(100);
		valueFieldsItem.setColSpan(1);

		criteriaForm.setItems(criteriaFieldsItem, operatorsFieldsItem, valueFieldsItem);

		setMembers(removeImg, criteriaForm);
	}

	private LinkedHashMap<String, String> operatorsFor(String criteriaField) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		if (criteriaField == null)
			return map;

		if (criteriaField.equals("id") || criteriaField.equals("fileSize") || criteriaField.equals("rating")
				|| criteriaField.equals("published") || criteriaField.endsWith("type:" + GUIExtendedAttribute.TYPE_INT)
				|| criteriaField.endsWith("type:" + GUIExtendedAttribute.TYPE_DOUBLE)) {
			map.put("greaterthan", I18N.message("greaterthan"));
			map.put("lessthan", I18N.message("lessthan"));
			map.put("equals", I18N.message("equals"));
			map.put("notequal", I18N.message("notequal"));
		} else if (criteriaField.equals("sourceDate") || criteriaField.equals("lastModified")
				|| criteriaField.equals("date") || criteriaField.equals("creation")
				|| criteriaField.equals("startPublishing") || criteriaField.equals("stopPublishing")
				|| criteriaField.endsWith("type:" + GUIExtendedAttribute.TYPE_DATE)) {
			map.put("greaterthan", I18N.message("greaterthan"));
			map.put("lessthan", I18N.message("lessthan"));
		} else if (criteriaField.endsWith("type:" + GUIExtendedAttribute.TYPE_BOOLEAN)) {
			map.put("equals", I18N.message("equals"));
		} else if (criteriaField.endsWith("type:" + GUIExtendedAttribute.TYPE_STRING_PRESET)) {
			map.put("equals", I18N.message("equals"));
			map.put("notequal", I18N.message("notequal"));
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
				|| criteriaField.endsWith("type:" + GUIExtendedAttribute.TYPE_INT)
				|| criteriaField.endsWith("type:" + GUIExtendedAttribute.TYPE_DOUBLE)) {
			return ItemFactory.newIntegerItem("value", "integer", null);
		} else if (criteriaField.endsWith("type:" + GUIExtendedAttribute.TYPE_BOOLEAN)) {
			FormItem item = ItemFactory.newBooleanSelector("value", "boolean");
			item.setValue("yes");
			return item;
		} else if (criteriaField.endsWith("type:" + GUIExtendedAttribute.TYPE_STRING_PRESET)) {
			String attributeName = criteriaField.substring(0, criteriaField.lastIndexOf(':') - 4).replaceAll("_", "");
			FormItem item = ItemFactory.newStringItemForPresetExtendedAttribute(template.getId(), attributeName);
			item.setName("value");
			return item;
		} else if (criteriaField.equals("sourceDate") || criteriaField.equals("lastModified")
				|| criteriaField.equals("date") || criteriaField.equals("creation")
				|| criteriaField.equals("startPublishing") || criteriaField.equals("stopPublishing")
				|| criteriaField.endsWith("type:" + GUIExtendedAttribute.TYPE_DATE)) {
			return ItemFactory.newDateItem("value", "date");
		} else {
			return ItemFactory.newTextItem("value", "text", null);
		}
	}

	public SelectItem getCriteriaFieldsItem() {
		return criteriaFieldsItem;
	}

	public SelectItem getOperatorsFieldsItem() {
		return operatorsFieldsItem;
	}

	public FormItem getValueFieldsItem() {
		return valueFieldsItem;
	}

	public int getRowPosition() {
		return rowPosition;
	}

	public void setRowPosition(int rowPosition) {
		this.rowPosition = rowPosition;
	}
}
