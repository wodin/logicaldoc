package com.logicaldoc.gui.frontend.client.workflow;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIValue;
import com.logicaldoc.gui.common.client.beans.GUIWFState;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.frontend.client.services.WorkflowService;
import com.logicaldoc.gui.frontend.client.services.WorkflowServiceAsync;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.types.MultipleAppearance;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.FormItemIcon;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.SpinnerItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemClickHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemIconClickEvent;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;

/**
 * This is the form used for the workflow task and end nodes settings.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class TaskDialog extends Window {

	protected WorkflowServiceAsync workflowService = (WorkflowServiceAsync) GWT.create(WorkflowService.class);

	private ValuesManager vm = new ValuesManager();

	private GUIWFState state;

	private SelectItem participantsList;

	private LinkedHashMap<String, String> participants = new LinkedHashMap<String, String>();

	private HLayout participantsLayout;

	private DynamicForm participantsForm;

	private Button removeParticipant = null;

	private StateWidget widget;

	public TaskDialog(StateWidget widget) {
		this.state = widget.getWfState();
		this.widget = widget;
		participants.clear();

		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.message("editworkflowstate", state.getType() == GUIWFState.TYPE_TASK ? I18N.message("task")
				: I18N.message("endstate")));
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		setMargin(3);
		setWidth(600);
		setHeight(560);
		centerInPage();

		Tab propertiesTab = new Tab(I18N.message("properties"));
		propertiesTab.setPane(preparePropertiesPanel());

		Tab automationTab = new Tab(I18N.message("automation"));
		automationTab.setPane(prepareAutomationPanel());

		TabSet tabSet = new TabSet();
		tabSet.setWidth100();
		tabSet.setTabs(propertiesTab, automationTab);
		addItem(tabSet);

		Button save = new Button(I18N.message("save"));
		save.setAutoFit(true);
		save.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {

			@Override
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				if (vm.validate()) {
					onSave();
					destroy();
				}
			}
		});
		save.setMargin(3);
		addItem(save);
	}

	private VLayout prepareAutomationPanel() {
		VLayout automationPanel = new VLayout();
		automationPanel.setWidth100();
		automationPanel.setHeight100();

		DynamicForm automationForm = new DynamicForm();
		automationForm.setTitleOrientation(TitleOrientation.TOP);
		automationForm.setNumCols(1);
		automationForm.setValuesManager(vm);

		TextAreaItem onCreation = ItemFactory.newTextAreaItem("onCreation", "execcodeontaskcreation",
				state.getOnCreation());
		onCreation.setWidth("*");
		onCreation.setHeight(200);
		onCreation.setWrapTitle(false);

		TextAreaItem onAssignment = ItemFactory.newTextAreaItem("onAssignment", "execcodeontaskassignment",
				state.getOnAssignment());
		onAssignment.setWidth(400);
		onAssignment.setWidth("*");
		onAssignment.setHeight(200);
		onAssignment.setWrapTitle(false);

		if (state.getType() == GUIWFState.TYPE_TASK)
			automationForm.setItems(onCreation, onAssignment);
		else {
			onCreation.setHeight(400);
			automationForm.setItems(onCreation);
		}

		automationPanel.addMember(automationForm);

		return automationPanel;
	}

	private VLayout preparePropertiesPanel() {
		VLayout propertiesPanel = new VLayout();
		propertiesPanel.setWidth100();
		propertiesPanel.setHeight100();

		DynamicForm taskForm = new DynamicForm();
		taskForm.setTitleOrientation(TitleOrientation.TOP);
		taskForm.setNumCols(1);
		taskForm.setValuesManager(vm);
		TextItem taskName = ItemFactory.newTextItem("taskName", "name", this.state.getName());
		taskName.setRequired(true);
		taskName.setWidth(250);
		TextAreaItem taskDescr = ItemFactory.newTextAreaItem("taskDescr", "description", this.state.getDescription());
		taskDescr.setWidth(350);
		taskDescr.setWrapTitle(false);
		taskForm.setFields(taskName, taskDescr);
		propertiesPanel.addMember(taskForm);

		if (state.getType() == GUIWFState.TYPE_TASK) {
			DynamicForm escalationFormItem = new DynamicForm();
			escalationFormItem.setTitleOrientation(TitleOrientation.TOP);
			StaticTextItem escalation = ItemFactory.newStaticTextItem("escalationManagement", "",
					"<b>" + I18N.message("escalationmanagement") + "</b>");
			escalation.setShouldSaveValue(false);
			escalation.setShowTitle(false);
			escalation.setWrapTitle(false);
			escalation.setWrap(false);
			escalationFormItem.setItems(escalation);
			propertiesPanel.addMember(escalationFormItem);

			DynamicForm escalationForm = new DynamicForm();
			escalationForm.setTitleOrientation(TitleOrientation.LEFT);
			escalationForm.setNumCols(4);
			escalationForm.setColWidths("35", "35", "50", "130");
			escalationForm.setValuesManager(vm);
			SpinnerItem duedateTimeItem = ItemFactory.newSpinnerItem("duedateNumber", "duedate",
					this.state.getDueDateNumber());
			duedateTimeItem.setDefaultValue(0);
			SelectItem duedateTime = ItemFactory.newDueTimeSelector("duedateTime", "");
			duedateTime.setValue(this.state.getDueDateUnit());

			SpinnerItem remindTimeItem = ItemFactory.newSpinnerItem("remindtimeNumber", "remindtime",
					this.state.getReminderNumber());
			remindTimeItem.setDefaultValue(0);
			SelectItem remindTime = ItemFactory.newDueTimeSelector("remindTime", "");
			remindTime.setValue(this.state.getReminderUnit());
			if (Session.get().isDemo()) {
				// In demo mode disable the remind setting because of this may
				// send massive emails
				remindTimeItem.setDisabled(true);
				remindTime.setDisabled(true);
			}
			escalationForm.setFields(duedateTimeItem, duedateTime, remindTimeItem, remindTime);
			propertiesPanel.addMember(escalationForm);
		}

		HTMLPane spacer = new HTMLPane();
		spacer.setHeight(2);
		spacer.setMargin(2);
		spacer.setOverflow(Overflow.HIDDEN);
		propertiesPanel.addMember(spacer);

		DynamicForm participantsItemForm = new DynamicForm();
		participantsItemForm.setTitleOrientation(TitleOrientation.TOP);
		participantsItemForm.setNumCols(1);
		StaticTextItem participantsItem = ItemFactory.newStaticTextItem("participants", "",
				"<b>" + I18N.message("participants") + "</b>");
		participantsItem.setShouldSaveValue(false);
		participantsItem.setShowTitle(false);
		participantsItem.setWrapTitle(false);
		participantsItem.setRequired(true);
		participantsItemForm.setItems(participantsItem);
		propertiesPanel.addMember(participantsItemForm);

		HLayout usergroupSelection = new HLayout();
		usergroupSelection.setHeight(25);
		usergroupSelection.setMargin(3);

		// Prepare the combo and button for adding a new user
		final DynamicForm usergroupForm = new DynamicForm();
		final SelectItem user = ItemFactory.newUserSelector("user", "user", null, true);
		user.setRequired(true);
		user.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				if (event.getValue() != null && !"".equals((String) event.getValue())) {
					final ListGridRecord selectedRecord = user.getSelectedRecord();
					if (selectedRecord == null)
						return;

					// Check if the selected user is already present in the
					// rights table
					for (String participant : participantsList.getValues()) {
						if (participant.equals(selectedRecord.getAttribute("username"))) {
							return;
						}
					}

					if (participants.get(selectedRecord.getAttribute("username")) == null)
						addParticipant(selectedRecord.getAttribute("username"), selectedRecord.getAttribute("label"));
					user.clearValue();
				}
			}
		});

		final SelectItem group = ItemFactory.newGroupSelector("group", "group");
		group.setRequired(true);
		group.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				if (event.getValue() != null && !"".equals((String) event.getValue())) {
					final ListGridRecord selectedRecord = group.getSelectedRecord();
					if (selectedRecord == null)
						return;

					// Check if the selected user is already present in the
					// participants list
					for (String participant : participantsList.getValues()) {
						if (participant.equals("g." + selectedRecord.getAttribute("name"))) {
							return;
						}
					}

					if (participants.get("g." + selectedRecord.getAttribute("name")) == null)
						addParticipant("g." + selectedRecord.getAttribute("name"), selectedRecord.getAttribute("name"));
					user.clearValue();
				}
			}
		});

		// Prepare dynamic user participant
		final TextItem attr = ItemFactory.newTextItem("attribute", "attribute", null);
		FormItemIcon addIcon = ItemFactory.newItemIcon("add.png");
		addIcon.addFormItemClickHandler(new FormItemClickHandler() {
			public void onFormItemClick(FormItemIconClickEvent event) {
				String val = attr.getValueAsString();
				if (val != null)
					val = val.trim();
				if (val == null || "".equals(val))
					return;

				// Check if the digited attribute user is already present in the
				// participants list
				for (String participant : participantsList.getValues()) {
					if (participant.equals("att." + val)) {
						return;
					}
				}

				if (participants.get("att." + val) == null)
					addParticipant("att." + val, val);
				attr.clearValue();
			}
		});
		attr.setIcons(addIcon);

		usergroupForm.setItems(user, group, attr);
		usergroupSelection.addMember(usergroupForm);
		propertiesPanel.addMember(usergroupSelection);

		participantsLayout = new HLayout();
		participantsLayout.setHeight(150);
		participantsLayout.setMembersMargin(5);
		propertiesPanel.addMember(participantsLayout);

		// Initialize the participants list
		if (this.state.getParticipants() != null)
			for (GUIValue part : this.state.getParticipants()) {
				if (part.getCode() == null || part.getValue() == null)
					continue;
				String prefix = I18N.message("user");
				if (part.getCode().startsWith("g."))
					prefix = I18N.message("group");
				else if (part.getCode().startsWith("att."))
					prefix = I18N.message("attribute");
				prefix += ": ";

				participants.put(part.getCode(),
						part.getValue().startsWith(prefix) ? part.getValue() : prefix + part.getValue());
			}

		addParticipant(null, null);

		return propertiesPanel;
	}

	/**
	 * Refresh the task's users participants list. If <code>operation</code> is
	 * 0, no operation is made to the list.
	 */
	private void addParticipant(String entityCode, String entityLabel) {
		if (participantsForm != null)
			participantsLayout.removeMember(participantsForm);
		if (removeParticipant != null)
			participantsLayout.removeMember(removeParticipant);

		participantsForm = new DynamicForm();
		participantsForm.setTitleOrientation(TitleOrientation.TOP);
		participantsForm.setNumCols(1);
		participantsForm.setValuesManager(vm);

		if (entityCode != null) {
			String prefix = I18N.message("user");
			if (entityCode.startsWith("g."))
				prefix = I18N.message("group");
			else if (entityCode.startsWith("att."))
				prefix = I18N.message("attribute");
			prefix += ": ";

			participants.put(entityCode.trim(), entityLabel.startsWith(prefix) ? entityLabel : prefix + entityLabel);
		}

		participantsList = new SelectItem();
		participantsList.setTitle("<b>" + I18N.message("participants") + "</b>");
		participantsList.setShowTitle(false);
		participantsList.setMultipleAppearance(MultipleAppearance.GRID);
		participantsList.setMultiple(true);
		participantsList.setWidth(350);
		participantsList.setHeight(130);
		participantsList.setEndRow(true);
		participantsList.setValueMap(participants);
		participantsForm.setItems(participantsList);

		removeParticipant = new Button(I18N.message("remove"));
		removeParticipant.setAutoFit(true);
		removeParticipant.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
			@Override
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				@SuppressWarnings("unchecked")
				List<String> selection = (List<String>) participantsList.getValue();
				for (String key : selection) {
					participants.remove(key);
					participantsList.setValueMap(participants);
				}
			}
		});

		participantsLayout.setMembers(participantsForm, removeParticipant);
	}

	private void onSave() {
		if (!vm.validate())
			return;

		Map<String, Object> values = (Map<String, Object>) vm.getValues();
		TaskDialog.this.state.setName((String) values.get("taskName"));
		TaskDialog.this.state.setDescription((String) values.get("taskDescr"));

		if (state.getType() == GUIWFState.TYPE_TASK) {
			TaskDialog.this.state.setDueDateNumber((Integer) values.get("duedateNumber"));
			TaskDialog.this.state.setDueDateUnit((String) values.get("duedateTime"));
			TaskDialog.this.state.setReminderNumber((Integer) values.get("remindtimeNumber"));
			TaskDialog.this.state.setReminderUnit((String) values.get("remindTime"));
			
			TaskDialog.this.state.setOnAssignment((String) values.get("onAssignment"));
		}

		GUIValue[] b = new GUIValue[participants.size()];
		int i = 0;
		for (String key : participants.keySet())
			b[i++] = new GUIValue(key, participants.get(key));
		TaskDialog.this.state.setParticipants(b);

		if (state.getType() == GUIWFState.TYPE_TASK
				&& (TaskDialog.this.state.getParticipants() == null || TaskDialog.this.state.getParticipants().length == 0)) {
			SC.warn(I18N.message("workflowtaskparticipantatleast"));
			return;
		}

		TaskDialog.this.state.setOnCreation((String) values.get("onCreation"));

		TaskDialog.this.widget.setContents("<b>" + state.getName() + "</b>");
		TaskDialog.this.widget.getDrawingPanel().getDiagramController().update();
	}
}