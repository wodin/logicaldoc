package com.logicaldoc.gui.frontend.client.metadata;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIExtendedAttribute;
import com.logicaldoc.gui.common.client.beans.GUITemplate;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.util.LD;
import com.logicaldoc.gui.frontend.client.services.TemplateService;
import com.logicaldoc.gui.frontend.client.services.TemplateServiceAsync;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.TransferImgButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.LinkItem;
import com.smartgwt.client.widgets.form.fields.PickerIcon;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
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
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.layout.VStack;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;

/**
 * This panel shows all template properties.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class TemplatePropertiesPanel extends HLayout {

	private TemplateServiceAsync templateService = (TemplateServiceAsync) GWT.create(TemplateService.class);

	protected DynamicForm form1 = new DynamicForm();

	protected DynamicForm form2 = new DynamicForm();

	protected ValuesManager vm = new ValuesManager();

	protected GUITemplate template;

	protected ChangedHandler changedHandler;

	private TemplateDetailsPanel detailsPanel;

	// Useful map to retrieve the extended attribute values
	protected Map<String, GUIExtendedAttribute> guiAttributes = new HashMap<String, GUIExtendedAttribute>();

	public String updatingAttributeName = "";

	private ListGrid attributesList;

	private SelectItem type;

	private SelectItem editor;

	private TextItem group;

	private LinkItem options;

	public TemplatePropertiesPanel() {

	}

	public TemplatePropertiesPanel(GUITemplate template, ChangedHandler changedHandler,
			TemplateDetailsPanel detailsPanel) {
		if (template == null) {
			setMembers(TemplatesPanel.SELECT_TEMPLATE);
			return;
		}

		this.template = template;
		if (!template.isReadonly())
			this.changedHandler = changedHandler;
		this.detailsPanel = detailsPanel;
		setWidth100();
		setHeight100();
		setMembersMargin(10);

		attributesList = new ListGrid() {
			final int category = TemplatePropertiesPanel.this.template.getCategory();

			@Override
			protected String getCellCSSText(ListGridRecord record, int rowNum, int colNum) {
				if (isMandatory(category, record.getAttribute("name"))) {
					return "color: #888888; font-style: italic;";
				} else {
					return super.getCellCSSText(record, rowNum, colNum);
				}

			}
		};
		attributesList.setEmptyMessage(I18N.message("notitemstoshow"));
		attributesList.setWidth(150);
		attributesList.setHeight(160);
		attributesList.setEmptyMessage(I18N.message("norecords"));
		attributesList.setCanReorderRecords(false);
		attributesList.setCanSort(false);
		attributesList.setCanFreezeFields(false);
		attributesList.setCanGroupBy(false);
		attributesList.setLeaveScrollbarGap(false);
		attributesList.setShowHeader(true);
		attributesList.setSelectionType(SelectionStyle.SINGLE);
		ListGridField name = new ListGridField("name", I18N.message("attributes"));
		attributesList.setFields(name);
		attributesList.addCellContextClickHandler(new CellContextClickHandler() {
			@Override
			public void onCellContextClick(CellContextClickEvent event) {
				if (!TemplatePropertiesPanel.this.template.isReadonly())
					onContextMenuCreation(TemplatePropertiesPanel.this.template.getCategory(), attributesList
							.getSelectedRecord().getAttributeAsString("name"));
				event.cancel();
			}
		});
		if (template.getId() != 0)
			fillAttributesList(template.getId());

		refresh();
	}

	protected void fillAttributesList(long templateId) {
		// Get the template attributes
		templateService.getTemplate(Session.get().getSid(), templateId, new AsyncCallback<GUITemplate>() {

			@Override
			public void onFailure(Throwable caught) {
				Log.serverError(caught);
			}

			@Override
			public void onSuccess(GUITemplate template) {
				if (template.getAttributes() != null && template.getAttributes().length > 0) {
					for (int i = 0; i < template.getAttributes().length; i++) {
						GUIExtendedAttribute att = template.getAttributes()[i];
						ListGridRecord record = new ListGridRecord();
						record.setAttribute("name", att.getName());
						guiAttributes.put(att.getName(), att);
						attributesList.getRecordList().add(record);
					}
				}
			}
		});
	}

	protected void refresh() {
		boolean readonly = (changedHandler == null);
		vm.clearValues();
		vm.clearErrors(false);

		HLayout templateInfo = new HLayout();

		if (form1 != null)
			form1.destroy();

		if (contains(form1))
			removeChild(form1);
		addTemplateMetadata(readonly);
		addMember(form1);

		attributesList.addSelectionChangedHandler(new SelectionChangedHandler() {
			@Override
			public void onSelectionChanged(SelectionEvent event) {
				Record record = attributesList.getSelectedRecord();
				onChangeSelectedAttribute(record);
			}
		});

		VStack modifyStack = new VStack(3);
		modifyStack.setWidth(20);
		modifyStack.setAlign(VerticalAlignment.TOP);

		TransferImgButton up = new TransferImgButton(TransferImgButton.UP);
		up.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				ListGridRecord selectedRecord = attributesList.getSelectedRecord();
				if (selectedRecord != null) {
					int idx = attributesList.getRecordIndex(selectedRecord);
					if (idx > 0) {
						RecordList rs = attributesList.getRecordList();
						rs.removeAt(idx);
						rs.addAt(selectedRecord, idx - 1);
						changedHandler.onChanged(null);
					}
				}
			}
		});

		TransferImgButton upFirst = new TransferImgButton(TransferImgButton.UP_FIRST);
		upFirst.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				ListGridRecord selectedRecord = attributesList.getSelectedRecord();
				if (selectedRecord != null) {
					int idx = attributesList.getRecordIndex(selectedRecord);
					if (idx > 0) {
						RecordList rs = attributesList.getRecordList();
						rs.removeAt(idx);
						rs.addAt(selectedRecord, 0);
						changedHandler.onChanged(null);
					}
				}
			}
		});

		TransferImgButton down = new TransferImgButton(TransferImgButton.DOWN);
		down.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				ListGridRecord selectedRecord = attributesList.getSelectedRecord();
				if (selectedRecord != null) {
					RecordList rs = attributesList.getRecordList();
					int numRecords = rs.getLength();
					int idx = attributesList.getRecordIndex(selectedRecord);
					if (idx < numRecords - 1) {
						rs.removeAt(idx);
						rs.addAt(selectedRecord, idx + 1);
						changedHandler.onChanged(null);
					}
				}
			}
		});

		TransferImgButton downLast = new TransferImgButton(TransferImgButton.DOWN_LAST);
		downLast.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				ListGridRecord selectedRecord = attributesList.getSelectedRecord();
				if (selectedRecord != null) {
					RecordList rs = attributesList.getRecordList();
					int numRecords = rs.getLength();
					int idx = attributesList.getRecordIndex(selectedRecord);
					if (idx < numRecords - 1) {
						rs.removeAt(idx);
						rs.addAt(selectedRecord, rs.getLength());
						changedHandler.onChanged(null);
					}
				}
			}
		});

		if (!template.isReadonly()) {
			modifyStack.addMember(upFirst);
			modifyStack.addMember(up);
			modifyStack.addMember(down);
			modifyStack.addMember(downLast);
		}

		templateInfo.setMembers(attributesList, modifyStack);
		templateInfo.setMembersMargin(3);

		addMember(templateInfo);
		templateInfo.setWidth(200);

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
		if (!template.isReadonly()) {
			cleanPicker.setNeverDisable(true);
			attributeName.setIcons(cleanPicker);
		} else
			attributeName.setDisabled(true);

		// Attribute Name
		final TextItem label = ItemFactory.newTextItem("label", "label", null);
		label.setDisabled(template.isReadonly());

		// Mandatory
		final CheckboxItem mandatory = new CheckboxItem();
		mandatory.setName("mandatory");
		mandatory.setTitle(I18N.message("mandatory"));
		mandatory.setRedrawOnChange(true);
		mandatory.setWidth(50);
		mandatory.setDefaultValue(false);
		mandatory.setDisabled(template.isReadonly());

		// Editor
		editor = new SelectItem("editor", I18N.message("inputmode"));
		editor.setEndRow(true);
		LinkedHashMap<String, String> editors = new LinkedHashMap<String, String>();
		editors.put("" + GUIExtendedAttribute.EDITOR_DEFAULT, I18N.message("free"));
		editors.put("" + GUIExtendedAttribute.EDITOR_LISTBOX, I18N.message("preset"));
		editor.setValueMap(editors);
		editor.setWrapTitle(false);
		editor.setDefaultValue("" + GUIExtendedAttribute.EDITOR_DEFAULT);
		editor.setDisabled(template.isReadonly());
		editor.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				refreshFieldForm();
			}
		});

		// Type
		type = new SelectItem("type", I18N.message("type"));
		LinkedHashMap<String, String> types = new LinkedHashMap<String, String>();
		types.put("" + GUIExtendedAttribute.TYPE_STRING, I18N.message("string"));
		types.put("" + GUIExtendedAttribute.TYPE_INT, I18N.message("integer"));
		types.put("" + GUIExtendedAttribute.TYPE_DOUBLE, I18N.message("decimal"));
		types.put("" + GUIExtendedAttribute.TYPE_DATE, I18N.message("date"));
		types.put("" + GUIExtendedAttribute.TYPE_BOOLEAN, I18N.message("boolean"));
		types.put("" + GUIExtendedAttribute.TYPE_USER, I18N.message("user"));
		type.setValueMap(types);
		type.setWrapTitle(false);
		type.setDefaultValue("" + GUIExtendedAttribute.TYPE_STRING);
		type.setDisabled(template.isReadonly());
		type.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				refreshFieldForm();
			}
		});

		// Values (for preset editor)
		group = ItemFactory.newTextItem("group", "group", null);
		group.setHint(I18N.message("groupname"));
		group.setDisabled(template.isReadonly());
		group.setStartRow(true);

		// Options (for preset editor)
		options = ItemFactory.newLinkItem("options", I18N.message("options"));
		options.setLinkTitle(I18N.message("attributeoptions"));
		options.setStartRow(true);
		options.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {
			@Override
			public void onClick(com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				if (template.getId() == 0L) {
					SC.say(I18N.message("savetemplatefirst"));
				} else {
					Options options = new Options(template.getId(), attributeName.getValueAsString(), template
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
						GUIExtendedAttribute att = new GUIExtendedAttribute();
						att.setName((String) attributeName.getValue());
						att.setLabel((String) label.getValue());
						att.setPosition(guiAttributes.size());
						att.setMandatory((Boolean) mandatory.getValue());
						att.setType(Integer.parseInt((String) type.getValue()));
						att.setEditor(Integer.parseInt((String) editor.getValue()));

						if (att.getType() == GUIExtendedAttribute.TYPE_USER)
							att.setStringValue(group.getValueAsString());

						if (form2.validate()) {
							changedHandler.onChanged(null);
							addAttribute(att);
						}
					} else {
						GUIExtendedAttribute att = guiAttributes.get(updatingAttributeName);
						if (att != null) {
							changedHandler.onChanged(null);
							att.setName(attributeName.getValueAsString());
							att.setLabel((String) label.getValue());
							att.setMandatory((Boolean) mandatory.getValue());
							att.setType(Integer.parseInt(type.getValueAsString()));
							att.setEditor(Integer.parseInt((String) editor.getValueAsString()));

							if (att.getType() == GUIExtendedAttribute.TYPE_USER)
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

		if (!template.isReadonly())
			form2.setItems(attributeName, label, mandatory, type, editor, group, options, addUpdate);
		else
			form2.setItems(attributeName, label, mandatory, type, editor, group, options);

		attributesLayout.setMembers(form2);
		attributesLayout.setMembersMargin(15);
		attributesLayout.setWidth(250);
		addMember(attributesLayout);

		refreshFieldForm();
	}

	protected void addTemplateMetadata(boolean readonly) {
		form1 = new DynamicForm();
		form1.setNumCols(1);
		form1.setValuesManager(vm);
		form1.setTitleOrientation(TitleOrientation.LEFT);

		StaticTextItem id = ItemFactory.newStaticTextItem("id", "id", Long.toString(template.getId()));
		id.setDisabled(true);

		TextItem name = ItemFactory.newSimpleTextItem("name", I18N.message("name"), template.getName());
		name.setRequired(true);
		name.setDisabled(readonly);
		if (!readonly)
			name.addChangedHandler(changedHandler);

		TextItem description = ItemFactory.newTextItem("description", "description", template.getDescription());
		description.setDisabled(readonly);
		if (!readonly)
			description.addChangedHandler(changedHandler);

		form1.setItems(id, name, description);

		form1.setWidth(200);
	}

	@SuppressWarnings("unchecked")
	protected boolean validate() {
		Map<String, Object> values = (Map<String, Object>) vm.getValues();
		vm.validate();
		if (!vm.hasErrors()) {
			validateFields(values);
		}
		return !vm.hasErrors();
	}

	protected void validateFields(Map<String, Object> values) {
		template.setName((String) values.get("name"));
		template.setDescription((String) values.get("description"));

		if (guiAttributes.size() > 0) {
			for (String attrName : guiAttributes.keySet()) {
				for (ListGridRecord record : attributesList.getRecords()) {
					int idx = attributesList.getRecordIndex(record);
					if (record.getAttributeAsString("name").equals(attrName)) {
						GUIExtendedAttribute extAttr = guiAttributes.get(attrName);
						extAttr.setPosition(idx);
						guiAttributes.remove(attrName);
						guiAttributes.put(attrName, extAttr);
					}
				}
			}
			template.setAttributes(guiAttributes.values().toArray(new GUIExtendedAttribute[0]));
		}
	}

	private void addAttribute(GUIExtendedAttribute att) {
		ListGridRecord record = new ListGridRecord();
		record.setAttribute("name", att.getName());
		attributesList.getDataAsRecordList().add(record);
		guiAttributes.put(att.getName(), att);
		detailsPanel.getSavePanel().setVisible(true);
		form2.clearValues();
		attributesList.deselectRecord(record);
	}

	private void updateAttribute(GUIExtendedAttribute att, String oldAttrName) {
		attributesList.removeSelectedData();
		guiAttributes.remove(oldAttrName);

		ListGridRecord record = new ListGridRecord();
		record.setAttribute("name", att.getName());
		attributesList.getDataAsRecordList().addAt(record, att.getPosition());
		guiAttributes.put(att.getName(), att);
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
							for (String attrName : names) {
								guiAttributes.remove(attrName);
							}
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
			GUIExtendedAttribute extAttr = guiAttributes.get(selectedAttributeName);
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
		if (type.getValueAsString().equals("" + GUIExtendedAttribute.TYPE_STRING)) {
			editor.setVisible(true);
			group.setVisible(false);

			if (editor.getValueAsString().equals("" + GUIExtendedAttribute.EDITOR_LISTBOX)) {
				options.setVisible(true);
			} else {
				options.setVisible(false);
			}
		} else if (type.getValueAsString().equals("" + GUIExtendedAttribute.TYPE_USER)) {
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

	protected void onContextMenuCreation(int category, String attributeName) {
		showContextMenu();
	}

	protected boolean isMandatory(int category, String attributeName) {
		return false;
	}
}
