package com.logicaldoc.gui.frontend.client.workflow;

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIValuePair;
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

	private ValuesManager vm = new ValuesManager();

	private GUIWFState task;

	private SelectItem participantsList;

	private LinkedHashMap<String, String> participants = new LinkedHashMap<String, String>();

	private HLayout participantsLayout;

	private DynamicForm participantsForm;

	private Button removeParticipant = null;

	private DynamicForm buttonForm;

	private StateWidget widget;

	public TaskDialog(StateWidget widget) {
		this.task = widget.getWfState();
		this.widget = widget;
		participants.clear();

		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.message("editworkflowstate", I18N.message("task")));
		setWidth(350);
		setHeight(450);
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
		if (Session.get().isDemo()) {
			// In demo mode disable the remind setting because of this may send
			// massive emails
			remindTimeItem.setDisabled(true);
			remindTime.setDisabled(true);
		}
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

		HLayout usergroupSelection = new HLayout();
		usergroupSelection.setHeight(25);
		usergroupSelection.setMargin(3);

		// Prepare the combo and button for adding a new user
		final DynamicForm usergroupForm = new DynamicForm();
		final SelectItem user = ItemFactory.newUserSelector("user", "user");
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
						refreshParticipants(selectedRecord.getAttribute("username"),
								selectedRecord.getAttribute("label"), 1);
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
					// rights table
					for (String participant : participantsList.getValues()) {
						if (participant.equals("g." + selectedRecord.getAttribute("name"))) {
							return;
						}
					}

					if (participants.get("g." + selectedRecord.getAttribute("name")) == null)
						refreshParticipants("g." + selectedRecord.getAttribute("name"),
								selectedRecord.getAttribute("name"), 1);
					user.clearValue();
				}
			}
		});

		usergroupForm.setItems(user, group);
		usergroupSelection.addMember(usergroupForm);
		addItem(usergroupSelection);

		participantsLayout = new HLayout();
		participantsLayout.setHeight(70);
		participantsLayout.setMembersMargin(5);
		addItem(participantsLayout);

		// Initialize the participants list
		if (this.task.getParticipants() != null)
			for (GUIValuePair part : this.task.getParticipants()) {
				String prefix = (part.getCode().startsWith("g.") ? I18N.message("group") : I18N.message("user")) + ": ";
				participants.put(part.getCode(),
						part.getValue().startsWith(prefix) ? part.getValue() : prefix + part.getValue());
			}

		refreshParticipants(null, null, 0);
	}

	/**
	 * Refresh the task's users participants list. If <code>operation</code> is
	 * 0, no operation is made to the list. If <code>operation</code> is 1, the
	 * username will be added to the list. If <code>operation</code> is 2, the
	 * username will be removed from the list.
	 */
	private void refreshParticipants(String entityCode, String entityLabel, int operation) {
		if (participantsForm != null)
			participantsLayout.removeMember(participantsForm);
		if (removeParticipant != null)
			participantsLayout.removeMember(removeParticipant);
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

		if (entityCode != null && (operation == 1)) {
			String prefix = (entityCode.startsWith("g.") ? I18N.message("group") : I18N.message("user")) + ": ";
			participants.put(entityCode, entityLabel.startsWith(prefix) ? entityLabel : prefix + entityLabel);
		} else if (entityCode != null && (operation == 2)) {
			participants.remove(entityCode);
		}

		participantsList.setValueMap(participants);
		participantsForm.setItems(participantsList);

		removeParticipant = new Button(I18N.message("remove"));
		removeParticipant.setAutoFit(true);
		removeParticipant.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
			@Override
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				if (participantsList.getValue() != null)
					refreshParticipants(participantsList.getValue().toString(),
							participantsList.getDisplayValue(participantsList.getValue().toString()), 2);
			}
		});

		participantsLayout.setMembers(participantsForm, removeParticipant);

		buttonForm = new DynamicForm();
		ButtonItem saveItem = new ButtonItem("save", I18N.message("save"));
		saveItem.setAutoFit(true);
		saveItem.addClickHandler(new ClickHandler() {
			@SuppressWarnings("unchecked")
			public void onClick(ClickEvent event) {
				Map<String, Object> values = (Map<String, Object>) vm.getValues();

				if (vm.validate()) {
					TaskDialog.this.task.setName((String) values.get("taskName"));
					TaskDialog.this.task.setDescription((String) values.get("taskDescr"));
					TaskDialog.this.task.setDueDateNumber((Integer) values.get("duedateNumber"));
					TaskDialog.this.task.setDueDateUnit((String) values.get("duedateTime"));
					TaskDialog.this.task.setReminderNumber((Integer) values.get("remindtimeNumber"));
					TaskDialog.this.task.setReminderUnit((String) values.get("remindTime"));

					GUIValuePair[] b = new GUIValuePair[participants.size()];
					int i = 0;
					for (String key : participants.keySet())
						b[i++] = new GUIValuePair(key, participants.get(key));
					TaskDialog.this.task.setParticipants(b);

					if (TaskDialog.this.task.getParticipants() == null
							|| TaskDialog.this.task.getParticipants().length == 0) {
						SC.warn(I18N.message("workflowtaskparticipantatleast"));
						return;
					}

					widget.setContents("<b>" + task.getName() + "</b>");
					widget.getDrawingPanel().getDiagramController().update();

					destroy();
				}
			}
		});

		buttonForm.setMargin(3);
		buttonForm.setItems(saveItem);
		addItem(buttonForm);
	}
}