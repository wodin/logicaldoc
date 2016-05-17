package com.logicaldoc.gui.frontend.client.metadata.template;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.logicaldoc.gui.common.client.beans.GUIAttribute;
import com.logicaldoc.gui.common.client.beans.GUIAttributeSet;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.util.LD;
import com.logicaldoc.gui.frontend.client.metadata.Options;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.events.DropCompleteEvent;
import com.smartgwt.client.widgets.events.DropCompleteHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.LinkItem;
import com.smartgwt.client.widgets.form.fields.PickerIcon;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemClickHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemIconClickEvent;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellContextClickEvent;
import com.smartgwt.client.widgets.grid.events.CellContextClickHandler;
import com.smartgwt.client.widgets.grid.events.CellSavedEvent;
import com.smartgwt.client.widgets.grid.events.CellSavedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.SectionStack;
import com.smartgwt.client.widgets.layout.SectionStackSection;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;

/**
 * This panel shows the properties of an attribute set.
 * 
 * @author Marco Meschieri - LogicalDOC
 * @since 7.5
 */
public class AttributeSetPropertiesPanel extends HLayout {

	protected DynamicForm form1 = new DynamicForm();

	protected DynamicForm form2 = new DynamicForm();

	protected ValuesManager vm = new ValuesManager();

	protected GUIAttributeSet attributeSet;

	protected ChangedHandler changedHandler;

	private AttributeSetDetailsPanel detailsPanel;

	public String updatingAttributeName = "";

	private ListGrid attributesList;

	private SectionStack attributesStack;

	private SelectItem type;

	private SelectItem editor;

	private TextItem group;

	private LinkItem options;

	public AttributeSetPropertiesPanel(GUIAttributeSet attributeSet, final ChangedHandler changedHandler,
			AttributeSetDetailsPanel detailsPanel) {
		if (attributeSet == null) {
			setMembers(AttributeSetsPanel.SELECT_SET);
			return;
		}

		this.attributeSet = attributeSet;
		this.changedHandler = changedHandler;
		this.detailsPanel = detailsPanel;
		setWidth100();
		setHeight100();
		setMembersMargin(5);

		attributesList = new ListGrid();
		attributesList.setEmptyMessage(I18N.message("notitemstoshow"));
		attributesList.setWidth100();
		attributesList.setHeight100();
		attributesList.setEmptyMessage(I18N.message("norecords"));
		attributesList.setCanReorderRecords(false);
		attributesList.setCanSort(false);
		attributesList.setCanReorderRecords(true);
		attributesList.setCanAcceptDroppedRecords(true);
		attributesList.setCanFreezeFields(false);
		attributesList.setCanGroupBy(false);
		attributesList.setLeaveScrollbarGap(false);
		attributesList.setShowHeader(true);
		attributesList.setCanEdit(true);
		attributesList.setShowRowNumbers(true);
		attributesList.setSelectionType(SelectionStyle.SINGLE);

		ListGridField name = new ListGridField("name", I18N.message("name"));
		name.setWidth(150);
		name.setCanEdit(false);
		name.setCanSort(false);
		attributesList.addCellContextClickHandler(new CellContextClickHandler() {
			@Override
			public void onCellContextClick(CellContextClickEvent event) {
				if (!AttributeSetPropertiesPanel.this.attributeSet.isReadonly())
					showContextMenu();
				event.cancel();
			}
		});

		ListGridField label = new ListGridField("label", I18N.message("label"));
		label.setCanEdit(true);
		label.setCanSort(false);
		label.addCellSavedHandler(new CellSavedHandler() {

			@Override
			public void onCellSaved(CellSavedEvent event) {
				AttributeSetPropertiesPanel.this.attributeSet.getAttribute(event.getRecord().getAttribute("name"))
						.setLabel((String) event.getNewValue());
				AttributeSetPropertiesPanel.this.changedHandler.onChanged(null);
			}
		});

		attributesList.setFields(name, label);

		attributesList.addDropCompleteHandler(new DropCompleteHandler() {

			@Override
			public void onDropComplete(DropCompleteEvent event) {
				List<String> attributes = new ArrayList<String>();
				ListGridRecord[] records = attributesList.getRecords();
				for (ListGridRecord record : records) {
					attributes.add(record.getAttributeAsString("name"));
				}

				AttributeSetPropertiesPanel.this.attributeSet.reorderAttributes(attributes);
				changedHandler.onChanged(null);
			}
		});

		attributesStack = new SectionStack();
		attributesStack.setHeight100();
		attributesStack.setWidth(400);

		SectionStackSection section = new SectionStackSection("<b>" + I18N.message("attributes") + "</b>");
		section.setCanCollapse(false);
		section.setExpanded(true);

		section.setItems(attributesList);
		attributesStack.setSections(section);

		fillAttributesList();

		refresh();
	}

	protected void fillAttributesList() {
		if (attributeSet != null && attributeSet.getAttributes() != null) {
			for (GUIAttribute att : attributeSet.getAttributes()) {
				ListGridRecord record = new ListGridRecord();
				record.setAttribute("name", att.getName());
				record.setAttribute("label", att.getLabel());
				attributesList.addData(record);
			}
		}
	}

	protected void refresh() {
		vm.clearValues();
		vm.clearErrors(false);

		HLayout setInfo = new HLayout();

		if (form1 != null)
			form1.destroy();

		if (contains(form1))
			removeChild(form1);
		addMetadata();
		addMember(form1);

		attributesList.addSelectionChangedHandler(new SelectionChangedHandler() {
			@Override
			public void onSelectionChanged(SelectionEvent event) {
				Record record = attributesList.getSelectedRecord();
				onChangeSelectedAttribute(record);
			}
		});

		setInfo.setMembers(attributesStack);
		setInfo.setMembersMargin(3);

		addMember(setInfo);
		setInfo.setWidth(200);

		/*
		 * Prepare the second form for adding or updating the extended
		 * attributes
		 */
		VLayout attributesLayout = new VLayout();

		if (form2 != null)
			form2.destroy();
		if (contains(form2))
			removeChild(form2);
		form2 = new DynamicForm();
		form2.setNumCols(1);

		// Attribute Name
		final TextItem attributeName = ItemFactory.newSimpleTextItem("attributeName", "attributename", null);
		attributeName.setRequired(true);
		PickerIcon cleanPicker = new PickerIcon(PickerIcon.CLEAR, new FormItemClickHandler() {
			public void onFormItemClick(FormItemIconClickEvent event) {
				clean();
				form2.getField("label").setDisabled(false);
				form2.getField("mandatory").setDisabled(false);
				form2.getField("type").setDisabled(false);
				form2.getField("editor").setDisabled(false);
				form2.getField("group").setDisabled(true);
				refreshFieldForm();
			}
		});
		if (!attributeSet.isReadonly()) {
			cleanPicker.setNeverDisable(true);
			attributeName.setIcons(cleanPicker);
		} else
			attributeName.setDisabled(true);

		// Attribute Name
		final TextItem label = ItemFactory.newTextItem("label", "label", null);
		label.setDisabled(attributeSet.isReadonly());

		// Mandatory
		final CheckboxItem mandatory = new CheckboxItem();
		mandatory.setName("mandatory");
		mandatory.setTitle(I18N.message("mandatory"));
		mandatory.setRedrawOnChange(true);
		mandatory.setWidth(50);
		mandatory.setDefaultValue(false);
		mandatory.setDisabled(attributeSet.isReadonly());

		// Editor
		editor = new SelectItem("editor", I18N.message("inputmode"));
		editor.setEndRow(true);
		LinkedHashMap<String, String> editors = new LinkedHashMap<String, String>();
		editors.put("" + GUIAttribute.EDITOR_DEFAULT, I18N.message("free"));
		editors.put("" + GUIAttribute.EDITOR_LISTBOX, I18N.message("preset"));
		editor.setValueMap(editors);
		editor.setWrapTitle(false);
		editor.setDefaultValue("" + GUIAttribute.EDITOR_DEFAULT);
		editor.setDisabled(attributeSet.isReadonly());
		editor.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				refreshFieldForm();
			}
		});

		// Type
		type = new SelectItem("type", I18N.message("type"));
		LinkedHashMap<String, String> types = new LinkedHashMap<String, String>();
		types.put("" + GUIAttribute.TYPE_STRING, I18N.message("string"));
		types.put("" + GUIAttribute.TYPE_INT, I18N.message("integer"));
		types.put("" + GUIAttribute.TYPE_DOUBLE, I18N.message("decimal"));
		types.put("" + GUIAttribute.TYPE_DATE, I18N.message("date"));
		types.put("" + GUIAttribute.TYPE_BOOLEAN, I18N.message("boolean"));
		types.put("" + GUIAttribute.TYPE_USER, I18N.message("user"));
		type.setValueMap(types);
		type.setWrapTitle(false);
		type.setDefaultValue("" + GUIAttribute.TYPE_STRING);
		type.setDisabled(attributeSet.isReadonly());
		type.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				refreshFieldForm();
			}
		});

		// Values (for preset editor)
		group = ItemFactory.newTextItem("group", "group", null);
		group.setHint(I18N.message("groupname"));
		group.setDisabled(attributeSet.isReadonly());
		group.setStartRow(true);

		// Options (for preset editor)
		options = ItemFactory.newLinkItem("options", I18N.message("options"));
		options.setLinkTitle(I18N.message("attributeoptions"));
		options.setStartRow(true);
		options.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {
			@Override
			public void onClick(com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				if (attributeSet.getId() == 0L) {
					SC.say(I18N.message("saveattributesetfirst"));
				} else {
					Options options = new Options(attributeSet.getId(), attributeName.getValueAsString(), attributeSet
							.isReadonly());
					options.show();
				}
			}
		});

		ButtonItem addUpdate = new ButtonItem();
		addUpdate.setTitle(I18N.message("addupdate"));
		addUpdate.setAutoFit(true);
		addUpdate.setColSpan(2);
		addUpdate.setAlign(Alignment.LEFT);
		addUpdate.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {
			@Override
			public void onClick(com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				if (attributeName.getValue() != null && !((String) attributeName.getValue()).trim().isEmpty()) {
					if (updatingAttributeName.trim().isEmpty()) {
						GUIAttribute att = new GUIAttribute();
						att.setName((String) attributeName.getValue());
						att.setLabel((String) label.getValue());
						att.setMandatory((Boolean) mandatory.getValue());
						att.setType(Integer.parseInt((String) type.getValue()));
						att.setEditor(Integer.parseInt((String) editor.getValue()));

						if (att.getType() == GUIAttribute.TYPE_USER)
							att.setStringValue(group.getValueAsString());

						if (form2.validate()) {
							changedHandler.onChanged(null);
							addAttribute(att);
						}
					} else {
						GUIAttribute att = attributeSet.getAttribute(updatingAttributeName.trim());
						if (att != null) {
							changedHandler.onChanged(null);
							att.setName(attributeName.getValueAsString());
							att.setLabel((String) label.getValue());
							att.setMandatory((Boolean) mandatory.getValue());
							att.setType(Integer.parseInt(type.getValueAsString()));
							att.setEditor(Integer.parseInt((String) editor.getValueAsString()));

							if (att.getType() == GUIAttribute.TYPE_USER)
								att.setStringValue(group.getValueAsString());
							else
								att.setStringValue(null);

							updateAttribute(att, updatingAttributeName);

							clean();
							detailsPanel.getSavePanel().setVisible(true);
						}
					}
				}
			}
		});

		if (!attributeSet.isReadonly())
			form2.setItems(attributeName, label, mandatory, type, editor, group, options, addUpdate);
		else
			form2.setItems(attributeName, label, mandatory, type, editor, group, options);

		attributesLayout.setMembers(form2);
		attributesLayout.setMembersMargin(10);
		attributesLayout.setWidth(250);
		addMember(attributesLayout);

		refreshFieldForm();
	}

	protected void addMetadata() {
		form1 = new DynamicForm();
		form1.setNumCols(1);
		form1.setValuesManager(vm);
		form1.setTitleOrientation(TitleOrientation.LEFT);

		StaticTextItem id = ItemFactory.newStaticTextItem("id", "id", Long.toString(attributeSet.getId()));
		id.setDisabled(true);

		TextItem name = ItemFactory.newSimpleTextItem("name", I18N.message("name"), attributeSet.getName());
		name.setRequired(true);
		name.setDisabled(attributeSet.isReadonly());
		if (!attributeSet.isReadonly())
			name.addChangedHandler(changedHandler);

		TextAreaItem description = ItemFactory.newTextAreaItem("description", "description",
				attributeSet.getDescription());
		description.setDisabled(attributeSet.isReadonly());
		description.setWidth(name.getWidth());
		if (!attributeSet.isReadonly())
			description.addChangedHandler(changedHandler);

		form1.setItems(id, name, description);

		form1.setWidth(200);
	}

	@SuppressWarnings("unchecked")
	protected boolean validate() {
		Map<String, Object> values = (Map<String, Object>) vm.getValues();
		vm.validate();
		if (!vm.hasErrors()) {
			attributeSet.setName((String) values.get("name"));
			attributeSet.setDescription((String) values.get("description"));
		}
		return !vm.hasErrors();
	}

	private void addAttribute(GUIAttribute att) {
		ListGridRecord record = new ListGridRecord();
		record.setAttribute("name", att.getName());
		record.setAttribute("label", att.getLabel());
		attributesList.getDataAsRecordList().add(record);
		attributeSet.appendAttribute(att);
		detailsPanel.getSavePanel().setVisible(true);
		form2.clearValues();
		attributesList.deselectRecord(record);
	}

	private void updateAttribute(GUIAttribute att, String oldAttrName) {
		attributesList.removeSelectedData();

		ListGridRecord record = new ListGridRecord();
		record.setAttribute("name", att.getName());
		record.setAttribute("label", att.getLabel());
		attributesList.getDataAsRecordList().addAt(record, att.getPosition());

		detailsPanel.getSavePanel().setVisible(true);
		form2.clearValues();
		attributesList.deselectRecord(record);
	}

	private void clean() {
		form2.clearValues();
		form2.getField("attributeName").setDisabled(false);
		updatingAttributeName = "";
		detailsPanel.getSavePanel().setVisible(false);
		attributesList.deselectAllRecords();
	}

	protected void showContextMenu() {
		Menu contextMenu = new Menu();

		MenuItem delete = new MenuItem();
		delete.setTitle(I18N.message("ddelete"));
		delete.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				final ListGridRecord[] selection = attributesList.getSelectedRecords();
				if (selection == null || selection.length == 0)
					return;
				final String[] names = new String[selection.length];
				for (int i = 0; i < selection.length; i++) {
					names[i] = selection[i].getAttribute("name");
				}

				LD.ask(I18N.message("question"), I18N.message("confirmdelete"), new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						if (value) {
							for (String attrName : names)
								attributeSet.removeAttribute(attrName);
							attributesList.removeSelectedData();
							clean();
							detailsPanel.getSavePanel().setVisible(true);
						}
					}
				});
			}
		});

		contextMenu.setItems(delete);
		contextMenu.showContextMenu();
	}

	protected void onChangeSelectedAttribute(Record record) {
		if (record != null) {
			String selectedAttributeName = record.getAttributeAsString("name");
			GUIAttribute extAttr = attributeSet.getAttribute(selectedAttributeName);
			form2.setValue("attributeName", extAttr.getName());
			form2.setValue("label", extAttr.getLabel());
			form2.setValue("mandatory", extAttr.isMandatory());
			form2.setValue("type", extAttr.getType());
			form2.setValue("editor", extAttr.getEditor());
			form2.setValue("group", extAttr.getStringValue());
			updatingAttributeName = selectedAttributeName;
			refreshFieldForm();
		}
	}

	public void refreshFieldForm() {
		if (type.getValueAsString().equals("" + GUIAttribute.TYPE_STRING)) {
			editor.setVisible(true);
			group.setVisible(false);

			if (editor.getValueAsString().equals("" + GUIAttribute.EDITOR_LISTBOX)) {
				options.setVisible(true);
			} else {
				options.setVisible(false);
			}
		} else if (type.getValueAsString().equals("" + GUIAttribute.TYPE_USER)) {
			editor.setVisible(false);
			group.setVisible(true);
			options.setVisible(false);
		} else {
			editor.setVisible(false);
			group.setVisible(false);
			options.setVisible(false);
			group.setValue("");
		}

		form2.markForRedraw();
	}

	protected boolean isMandatory(int category, String attributeName) {
		return false;
	}
}