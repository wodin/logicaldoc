package com.logicaldoc.gui.frontend.client.workflow;

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.logicaldoc.gui.common.client.beans.GUIWFState;
import com.logicaldoc.gui.common.client.beans.GUIWorkflow;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.frontend.client.administration.AdminPanel;
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
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.SpinnerItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;

/**
 * This is the form used for the workflow task setting.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class TaskDialog extends Window {

	protected WorkflowServiceAsync workflowService = (WorkflowServiceAsync) GWT.create(WorkflowService.class);

	private GUIWorkflow workflow = null;

	private ValuesManager vm = new ValuesManager();

	private GUIWFState task;

	private SelectItem participantsList;

	private LinkedHashMap<String, String> participants = new LinkedHashMap<String, String>();

	private HLayout participantsLayout;

	private DynamicForm participantsForm;

	private Button removeUser = null;

	private DynamicForm buttonForm;

	private WorkflowDesigner workflowDesigner = null;

	public TaskDialog(WorkflowDesigner designer, GUIWorkflow wfl, GUIWFState wfState) {
		this.workflow = wfl;
		this.task = wfState;
		this.workflowDesigner = designer;

		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.message("editworkflowstate", I18N.message("task")));
		setWidth(340);
		setHeight(410);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();
		setPadding(5);
		setAutoSize(true);

		DynamicForm taskForm = new DynamicForm();
		taskForm.setTitleOrientation(TitleOrientation.TOP);
		taskForm.setNumCols(1);
		taskForm.setValuesManager(vm);
		TextItem taskName = ItemFactory.newTextItem("taskName", "name", this.task.getName());
		taskName.setRequired(true);
		TextAreaItem taskDescr = ItemFactory.newTextAreaItem("taskDescr", "description", this.task.getDescription());
		taskDescr.setWrapTitle(false);
		taskForm.setFields(taskName, taskDescr);
		addItem(taskForm);

		DynamicForm escalationFormItem = new DynamicForm();
		escalationFormItem.setTitleOrientation(TitleOrientation.TOP);
		StaticTextItem escalation = ItemFactory.newStaticTextItem("escalationManagement", "",
				"<b>" + I18N.message("escalationmanagement") + "</b>");
		escalation.setShouldSaveValue(false);
		escalation.setWrapTitle(false);
		escalation.setWrap(false);
		escalationFormItem.setItems(escalation);
		addItem(escalationFormItem);

		DynamicForm escalationForm = new DynamicForm();
		escalationForm.setTitleOrientation(TitleOrientation.LEFT);
		escalationForm.setNumCols(4);
		escalationForm.setColWidths("35", "35", "50", "130");
		escalationForm.setValuesManager(vm);
		SpinnerItem duedateTimeItem = new SpinnerItem("duedateNumber");
		duedateTimeItem.setTitle(I18N.message("duedate"));
		duedateTimeItem.setDefaultValue(0);
		duedateTimeItem.setMin(0);
		duedateTimeItem.setStep(1);
		duedateTimeItem.setWidth(50);
		duedateTimeItem.setValue(this.task.getDueDateNumber());
		SelectItem duedateTime = ItemFactory.newTimeSelector("duedateTime", "");
		duedateTime.setValue(this.task.getDueDateUnit());

		SpinnerItem remindTimeItem = new SpinnerItem("remindtimeNumber");
		remindTimeItem.setTitle(I18N.message("remindtime"));
		remindTimeItem.setDefaultValue(0);
		remindTimeItem.setMin(0);
		remindTimeItem.setStep(1);
		remindTimeItem.setWidth(50);
		remindTimeItem.setValue(this.task.getReminderNumber());
		SelectItem remindTime = ItemFactory.newTimeSelector("remindTime", "");
		remindTime.setValue(this.task.getReminderUnit());
		escalationForm.setFields(duedateTimeItem, duedateTime, remindTimeItem, remindTime);
		addItem(escalationForm);

		HTMLPane spacer = new HTMLPane();
		spacer.setContents("<div>&nbsp;</div>");
		spacer.setHeight(10);
		spacer.setMargin(10);
		spacer.setOverflow(Overflow.HIDDEN);
		addItem(spacer);

		DynamicForm participantsItemForm = new DynamicForm();
		participantsItemForm.setTitleOrientation(TitleOrientation.TOP);
		participantsItemForm.setNumCols(1);
		StaticTextItem participantsItem = ItemFactory.newStaticTextItem("participants", "",
				"<b>" + I18N.message("participants") + "</b>");
		participantsItem.setShouldSaveValue(false);
		participantsItem.setWrapTitle(false);
		participantsItem.setRequired(true);
		participantsItemForm.setItems(participantsItem);
		addItem(participantsItemForm);

		HLayout userSelection = new HLayout();
		userSelection.setHeight(25);
		userSelection.setMargin(3);

		// Prepare the combo and button for adding a new user
		final DynamicForm userForm = new DynamicForm();
		final SelectItem user = ItemFactory.newUserSelector("user", "user");
		userForm.setItems(user);
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

					refreshParticipants(selectedRecord.getAttribute("username"), 1);
					user.clearValue();
				}

			}
		});
		userSelection.addMember(userForm);
		addItem(userSelection);

		participantsLayout = new HLayout();
		participantsLayout.setHeight(70);
		participantsLayout.setMembersMargin(5);
		addItem(participantsLayout);

		// Initialize the participants list
		if (this.task.getParticipants() != null)
			for (String usr : this.task.getParticipants()) {
				participants.put(usr, usr);
			}

		refreshParticipants(null, 0);
	}

	/**
	 * Refresh the task's users participants list. If <code>operation</code> is
	 * 0, no operation is made to the list. If <code>operation</code> is 1, the
	 * username will be added to the list. If <code>operation</code> is 2, the
	 * username will be removed from the list.
	 */
	private void refreshParticipants(String username, int operation) {
		if (participantsForm != null)
			participantsLayout.removeMember(participantsForm);
		if (removeUser != null)
			participantsLayout.removeMember(removeUser);
		if (buttonForm != null) {
			removeMember(buttonForm);
			buttonForm.destroy();
		}

		participantsForm = new DynamicForm();
		participantsForm.setTitleOrientation(TitleOrientation.TOP);
		participantsForm.setNumCols(1);
		participantsForm.setValuesManager(vm);
		participantsList = new SelectItem();
		participantsList.setTitle("<b>" + I18N.message("participants") + "</b>");
		participantsList.setShowTitle(false);
		participantsList.setMultiple(true);
		participantsList.setMultipleAppearance(MultipleAppearance.GRID);
		participantsList.setWidth(200);
		participantsList.setHeight(70);
		participantsList.setEndRow(true);

		if (username != null && (operation == 1)) {
			participants.put(username, username);
		} else if (username != null && (operation == 2)) {
			participants.remove(username);
		}
		participantsList.setValueMap(participants);
		participantsForm.setItems(participantsList);

		removeUser = new Button(I18N.message("removeuser"));
		removeUser.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
			@Override
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				if (participantsList.getValue() != null)
					refreshParticipants(participantsList.getValue().toString(), 2);
			}
		});

		participantsLayout.setMembers(participantsForm, removeUser);

		buttonForm = new DynamicForm();
		ButtonItem saveItem = new ButtonItem();
		saveItem.setTitle(I18N.message("save"));
		saveItem.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				final Map<String, Object> values = vm.getValues();

				if (vm.validate()) {
					TaskDialog.this.task.setName((String) values.get("taskName"));
					TaskDialog.this.task.setDescription((String) values.get("taskDescr"));
					TaskDialog.this.task.setDueDateNumber((Integer) values.get("duedateNumber"));
					TaskDialog.this.task.setDueDateUnit((String) values.get("duedateTime"));
					TaskDialog.this.task.setReminderNumber((Integer) values.get("remindtimeNumber"));
					TaskDialog.this.task.setReminderUnit((String) values.get("remindTime"));
					TaskDialog.this.task.setParticipants(participants.keySet().toArray(new String[0]));

					GUIWFState[] states = new GUIWFState[workflow.getStates().length];
					int i = 0;
					for (GUIWFState state : workflow.getStates()) {
						if (!state.getId().equals(task.getId())) {
							states[i] = state;
							i++;
						} else {
							states[i] = task;
							i++;
						}
					}
					workflow.setStates(states);

					AdminPanel.get().setContent(new WorkflowDesigner(workflow, false));
					destroy();

					if (TaskDialog.this.task.getParticipants() == null
							|| TaskDialog.this.task.getParticipants().length == 0) {
						SC.warn(I18N.message("workflowtaskparticipantatleast"));
					}
				}
			}
		});

		buttonForm.setMargin(3);
		buttonForm.setItems(saveItem);
		addItem(buttonForm);
	}
}