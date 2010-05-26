package com.logicaldoc.gui.frontend.client.template;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIExtendedAttribute;
import com.logicaldoc.gui.common.client.beans.GUITemplate;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.DocumentServiceAsync;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.types.MultipleAppearance;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.TransferImgButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.PickerIcon;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemClickHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemIconClickEvent;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.layout.VStack;

public class TemplatePropertiesPanel extends HLayout {

	private DocumentServiceAsync documentService = (DocumentServiceAsync) GWT.create(DocumentService.class);

	private DynamicForm form1 = new DynamicForm();

	private DynamicForm form2 = new DynamicForm();

	private ValuesManager vm = new ValuesManager();

	private GUITemplate template;

	private ChangedHandler changedHandler;

	// private LinkedHashMap<String, String> attributesMap = new
	// LinkedHashMap<String, String>();

	private SelectItem attributes = null;

	private TemplateDetailsPanel detailsPanel;

	private Map<String, GUIExtendedAttribute> guiAttributes = new HashMap<String, GUIExtendedAttribute>();

	public String updatingAttributeName = "";

	// private LinkedHashMap<String, String> lastAttributesMap = new
	// LinkedHashMap<String, String>();

	private ListGrid myList2;

	private ListGridRecord[] attributesGrid = new ListGridRecord[0];

	private ListGridRecord[] lastAttributesGrid = new ListGridRecord[0];

	public TemplatePropertiesPanel() {

	}

	public TemplatePropertiesPanel(GUITemplate template, ChangedHandler changedHandler,
			TemplateDetailsPanel detailsPanel) {
		if (template == null) {
			setMembers(TemplatesPanel.SELECT_TEMPLATE);
			return;
		}

		this.template = template;
		this.changedHandler = changedHandler;
		this.detailsPanel = detailsPanel;
		setWidth100();
		setHeight100();
		setMembersMargin(20);

		myList2 = new ListGrid();
		myList2.setWidth(130);
		myList2.setHeight(200);
		myList2.setEmptyMessage("No Records");
		myList2.setCanAcceptDroppedRecords(true);
		myList2.setCanReorderRecords(true);
		ListGridField name = new ListGridField("name", I18N.getMessage("attributes"), 110);
		myList2.setFields(name);
		if (template.getId() != 0)
			fillAttributesList(template);

		refresh();
	}

	private void fillAttributesList(GUITemplate template) {
		// Get the template attributes
		documentService.getAttributes(Session.get().getSid(), template.getId(),
				new AsyncCallback<GUIExtendedAttribute[]>() {
					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(GUIExtendedAttribute[] result) {
						attributesGrid = new ListGridRecord[result.length];
						for (int i = 0; i < result.length; i++) {
							GUIExtendedAttribute att = result[i];
							ListGridRecord record = new ListGridRecord();
							attributesGrid[i] = record;
							record.setAttribute("name", att.getName());
							// We cannot use spaces in items name
							String itemName = "_" + att.getName().replaceAll(" ", "___");
							// attributesMap.put(itemName, att.getName());
							guiAttributes.put(att.getName(), att);
						}
						// attributes.setValueMap(attributesMap);

						myList2.setRecords(attributesGrid);

						// Create the restore attribute grid
						lastAttributesGrid = new ListGridRecord[attributesGrid.length];
						for (int i = 0; i < attributesGrid.length; i++) {
							lastAttributesGrid[i] = attributesGrid[i];
						}
					}
				});
	}

	private void refresh() {
		boolean readonly = (changedHandler == null);
		vm.clearValues();
		vm.clearErrors(false);

		HLayout templateInfo = new HLayout();

		if (form1 != null)
			form1.destroy();

		if (contains(form1))
			removeChild(form1);
		form1 = new DynamicForm();
		form1.setNumCols(1);
		form1.setValuesManager(vm);
		form1.setTitleOrientation(TitleOrientation.TOP);

		TextItem id = new TextItem();
		id.setTitle(I18N.getMessage("id"));
		id.setDisabled(true);
		id.setValue(template.getId());

		TextItem name = new TextItem("name");
		name.setTitle(I18N.getMessage("name"));
		name.setValue(template.getName());
		name.setRequired(true);
		name.setDisabled(readonly || template.getId() != 0);
		if (!readonly)
			name.addChangedHandler(changedHandler);

		TextItem description = new TextItem("description");
		description.setTitle(I18N.getMessage("description"));
		description.setValue(template.getDescription());
		description.setRequired(true);
		description.setDisabled(readonly);
		if (!readonly)
			description.addChangedHandler(changedHandler);

		attributes = ItemFactory.newMultipleSelector("attributes", I18N.getMessage("attributes"));
		attributes.setDisabled(readonly);
		attributes.setWidth(130);
		attributes.setHeight(100);
		attributes.setMultipleAppearance(MultipleAppearance.GRID);
		attributes.setRowSpan(3);

		form1.setItems(id, name, description);
		addMember(form1);

		// attributes.addChangedHandler(new ChangedHandler() {
		// @Override
		// public void onChanged(ChangedEvent event) {
		// if (event.getValue() != null && !"".equals(event.getValue())) {
		// GUIExtendedAttribute extAttr =
		// guiAttributes.get(attributesMap.get(event.getValue().toString()));
		// form2.setValue("attributeName", extAttr.getName());
		// form2.setValue("mandatory", extAttr.isMandatory());
		// form2.setValue("type", extAttr.getType());
		// updatingAttributeName = extAttr.getName();
		// form2.getField("attributeName").setDisabled(true);
		// }
		// }
		// });

		myList2.addSelectionChangedHandler(new SelectionChangedHandler() {
			@Override
			public void onSelectionChanged(SelectionEvent event) {
				Record record = myList2.getSelectedRecord();
				if (record != null) {
					String selectedAttributeName = record.getAttributeAsString("name");
					GUIExtendedAttribute extAttr = guiAttributes.get(selectedAttributeName);
					form2.setValue("attributeName", extAttr.getName());
					form2.setValue("mandatory", extAttr.isMandatory());
					form2.setValue("type", extAttr.getType());
					updatingAttributeName = extAttr.getName();
					form2.getField("attributeName").setDisabled(true);
				}
			}
		});

		VStack modifyStack = new VStack(3);
		modifyStack.setWidth(20);
		modifyStack.setAlign(VerticalAlignment.CENTER);

		TransferImgButton up = new TransferImgButton(TransferImgButton.UP);
		up.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				ListGridRecord selectedRecord = myList2.getSelectedRecord();
				if (selectedRecord != null) {
					attributes.getSelectedRecord();
					int idx = myList2.getRecordIndex(selectedRecord);
					if (idx > 0) {
						RecordList rs = myList2.getRecordList();
						rs.removeAt(idx);
						rs.addAt(selectedRecord, idx - 1);
					}
				}
			}
		});

		TransferImgButton upFirst = new TransferImgButton(TransferImgButton.UP_FIRST);
		upFirst.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				ListGridRecord selectedRecord = myList2.getSelectedRecord();
				if (selectedRecord != null) {
					int idx = myList2.getRecordIndex(selectedRecord);
					if (idx > 0) {
						RecordList rs = myList2.getRecordList();
						rs.removeAt(idx);
						rs.addAt(selectedRecord, 0);
					}
				}
			}
		});

		TransferImgButton down = new TransferImgButton(TransferImgButton.DOWN);
		down.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				ListGridRecord selectedRecord = myList2.getSelectedRecord();
				if (selectedRecord != null) {
					RecordList rs = myList2.getRecordList();
					int numRecords = rs.getLength();
					int idx = myList2.getRecordIndex(selectedRecord);
					if (idx < numRecords - 1) {
						rs.removeAt(idx);
						rs.addAt(selectedRecord, idx + 1);
					}
				}
			}
		});

		TransferImgButton downLast = new TransferImgButton(TransferImgButton.DOWN_LAST);
		downLast.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				ListGridRecord selectedRecord = myList2.getSelectedRecord();
				if (selectedRecord != null) {
					RecordList rs = myList2.getRecordList();
					int numRecords = rs.getLength();
					int idx = myList2.getRecordIndex(selectedRecord);
					if (idx < numRecords - 1) {
						rs.removeAt(idx);
						rs.addAt(selectedRecord, rs.getLength() - 1);
					}
				}
			}
		});

		modifyStack.addMember(upFirst);
		modifyStack.addMember(up);
		modifyStack.addMember(down);
		modifyStack.addMember(downLast);

		templateInfo.setMembers(myList2, modifyStack);
		templateInfo.setMembersMargin(3);

		addMember(templateInfo);

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
		form2.setNumCols(2);
		form2.setTitleOrientation(TitleOrientation.LEFT);

		// Attribute Name
		final TextItem attributeName = new TextItem();
		attributeName.setName("attributeName");
		attributeName.setTitle(I18N.getMessage("attributename"));
		attributeName.setRequired(true);
		PickerIcon cleanPicker = new PickerIcon(PickerIcon.CLEAR, new FormItemClickHandler() {
			public void onFormItemClick(FormItemIconClickEvent event) {
				clean();
			}
		});
		cleanPicker.setNeverDisable(true);
		attributeName.setIcons(cleanPicker);

		// Mandatory
		final CheckboxItem mandatory = new CheckboxItem();
		mandatory.setName("mandatory");
		mandatory.setTitle(I18N.getMessage("mandatory"));
		mandatory.setRedrawOnChange(true);
		mandatory.setWidth(50);
		mandatory.setDefaultValue(false);

		// Type
		final SelectItem type = new SelectItem("type", I18N.getMessage("type"));
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("" + GUIExtendedAttribute.TYPE_STRING, I18N.getMessage("string"));
		map.put("" + GUIExtendedAttribute.TYPE_INT, I18N.getMessage("integer"));
		map.put("" + GUIExtendedAttribute.TYPE_DOUBLE, I18N.getMessage("decimal"));
		map.put("" + GUIExtendedAttribute.TYPE_DATE, I18N.getMessage("date"));
		type.setValueMap(map);
		type.setWrapTitle(false);
		type.setDefaultValue("" + GUIExtendedAttribute.TYPE_STRING);

		HLayout buttons = new HLayout();

		IButton addUpdate = new IButton();
		addUpdate.setTitle(I18N.getMessage("addupdate"));
		addUpdate.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (attributeName.getValue() != null) {
					if (updatingAttributeName.trim().isEmpty()) {
						GUIExtendedAttribute att = new GUIExtendedAttribute();
						att.setName((String) attributeName.getValue());
						att.setPosition(0);
						att.setMandatory((Boolean) mandatory.getValue());
						att.setType(Integer.parseInt((String) type.getValue()));
						addAttribute(att);
					} else {
						GUIExtendedAttribute att = guiAttributes.get(updatingAttributeName);
						att.setMandatory((Boolean) mandatory.getValue());
						if (type.getValue() instanceof String)
							att.setType(Integer.parseInt((String) type.getValue()));
						else
							att.setType((Integer) type.getValue());
						clean();
					}
				}
			}
		});

		IButton restore = new IButton();
		restore.setTitle(I18N.getMessage("restore"));
		restore.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				restore();
			}
		});

		form2.setItems(attributeName, mandatory, type);
		buttons.setMembers(addUpdate, restore);
		buttons.setMembersMargin(10);
		attributesLayout.setMembers(form2, buttons);
		attributesLayout.setMembersMargin(15);
		addMember(attributesLayout);
	}

	@SuppressWarnings("unchecked")
	boolean validate() {
		Map<String, Object> values = (Map<String, Object>) vm.getValues();
		vm.validate();
		if (!vm.hasErrors()) {
			template.setName((String) values.get("name"));
			template.setDescription((String) values.get("description"));
		}
		return !vm.hasErrors();
	}

	private void addAttribute(GUIExtendedAttribute att) {
		// this.attributesMap.put(att.getName(), att.getName());
		ListGridRecord record = new ListGridRecord();
		record.setAttribute("name", att.getName());
//		myList2.setData(newAttributesGrid);
		myList2.getDataAsRecordList().add(record);
		
//		int index = attributesGrid.length;
		attributesGrid = new ListGridRecord[myList2.getDataAsRecordList().getLength()];
		for (int i = 0; i < myList2.getDataAsRecordList().getLength(); i++) {
			attributesGrid[i] = myList2.getRecord(i);
		}

		guiAttributes.put(att.getName(), att);
		// attributes.setValueMap(attributesMap);
		detailsPanel.getSavePanel().setVisible(true);
		form2.clearValues();
	}

	private void clean() {
		form2.clearValues();
		form2.getField("attributeName").setDisabled(false);
		updatingAttributeName = "";
//		attributes.clearValue();
		detailsPanel.getSavePanel().setVisible(false);
	}

	private void restore() {
		// attributes.setValueMap(lastAttributesMap);
		myList2.setData(lastAttributesGrid);
		clean();
	}
}
