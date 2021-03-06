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
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.FormItemIcon;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.SpinnerItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemClickHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemIconClickEvent;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;

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

	private DynamicForm buttonForm;

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
		setAutoSize(true);
		setMargin(3);
		setWidth(400);
		centerInPage();

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
		addItem(taskForm);

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
			addItem(escalationFormItem);

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
			addItem(escalationForm);
		}

		HTMLPane spacer = new HTMLPane();
		spacer.setHeight(2);
		spacer.setMargin(2);
		spacer.setOverflow(Overflow.HIDDEN);
		addItem(spacer);

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
		addItem(participantsItemForm);

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
		addItem(usergroupSelection);

		participantsLayout = new HLayout();
		participantsLayout.setHeight(70);
		participantsLayout.setMembersMargin(5);
		addItem(participantsLayout);

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
		if (buttonForm != null) {
			removeMember(buttonForm);
			buttonForm.destroy();
		}

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
		participantsList.setWidth(300);
		participantsList.setHeight(70);
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

		buttonForm = new DynamicForm();
		ButtonItem saveItem = new ButtonItem("save", I18N.message("save"));
		saveItem.setAutoFit(true);
		saveItem.addClickHandler(new ClickHandler() {
			@SuppressWarnings("unchecked")
			public void onClick(ClickEvent event) {
				Map<String, Object> values = (Map<String, Object>) vm.getValues();

				if (vm.validate()) {
					TaskDialog.this.state.setName((String) values.get("taskName"));
					TaskDialog.this.state.setDescription((String) values.get("taskDescr"));

					if (state.getType() == GUIWFState.TYPE_TASK) {
						TaskDialog.this.state.setDueDateNumber((Integer) values.get("duedateNumber"));
						TaskDialog.this.state.setDueDateUnit((String) values.get("duedateTime"));
						TaskDialog.this.state.setReminderNumber((Integer) values.get("remindtimeNumber"));
						TaskDialog.this.state.setReminderUnit((String) values.get("remindTime"));
					}

					GUIValue[] b = new GUIValue[participants.size()];
					int i = 0;
					for (String key : participants.keySet())
						b[i++] = new GUIValue(key, participants.get(key));
					TaskDialog.this.state.setParticipants(b);

					if (state.getType() == GUIWFState.TYPE_TASK
							&& (TaskDialog.this.state.getParticipants() == null || TaskDialog.this.state
									.getParticipants().length == 0)) {
						SC.warn(I18N.message("workflowtaskparticipantatleast"));
						return;
					}

					widget.setContents("<b>" + state.getName() + "</b>");
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