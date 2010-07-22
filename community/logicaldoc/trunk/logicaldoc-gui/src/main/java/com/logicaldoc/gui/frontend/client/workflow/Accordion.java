package com.logicaldoc.gui.frontend.client.workflow;

import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.types.VisibilityMode;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.SpinnerItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.SectionStack;
import com.smartgwt.client.widgets.layout.SectionStackSection;

public class Accordion extends SectionStack {
	private SectionStackSection wfSettingsSection = null;

	public SectionStackSection getWfSettingsSection() {
		return wfSettingsSection;
	}

	public SectionStackSection getTaskSettingsSection() {
		return taskSettingsSection;
	}

	private SectionStackSection taskSettingsSection = null;

	private static Accordion instance;

//	public Accordion() {
//		setVisibilityMode(VisibilityMode.MUTEX);
//		setWidth(250);
//		setHeight(557);
//		setMargin(5);
//
//		refresh(false);
//	}

	public Accordion(boolean showTask) {
		setVisibilityMode(VisibilityMode.MUTEX);
		setWidth(250);
		setHeight(557);
		setMargin(5);
	refresh(showTask);
	}

	private void refresh(boolean showTask) {
		wfSettingsSection = new SectionStackSection(I18N.message("workflowsettings"));
		wfSettingsSection.setExpanded(!showTask);
		wfSettingsSection.setCanCollapse(true);

		DynamicForm wfForm = new DynamicForm();
		wfForm.setTitleOrientation(TitleOrientation.TOP);
		wfForm.setNumCols(1);
		TextItem workflowName = ItemFactory.newTextItem("workflowName", "workflowname", null);
		workflowName.setRequired(true);
		TextAreaItem workflowDescr = ItemFactory.newTextAreaItem("workflowDescr", "workflowdescr", null);
		workflowDescr.setWrapTitle(false);
		wfForm.setFields(workflowName, workflowDescr);

		DynamicForm taskAssignmentForm = new DynamicForm();
		taskAssignmentForm.setTitleOrientation(TitleOrientation.TOP);
		taskAssignmentForm.setNumCols(1);
		StaticTextItem taskAssignment = ItemFactory.newStaticTextItem("taskAssignment", "",
				"<b>" + I18N.message("taskassignment") + "</b>");
		taskAssignment.setShouldSaveValue(false);
		taskAssignment.setWrapTitle(false);
		TextItem assignmentSubject = ItemFactory.newTextItem("assignmentSubject", "subject", null);
		TextAreaItem assignmentBody = ItemFactory.newTextAreaItem("assignmentBody", "body", null);
		taskAssignmentForm.setFields(taskAssignment, assignmentSubject, assignmentBody);

		DynamicForm taskReminderForm = new DynamicForm();
		taskReminderForm.setTitleOrientation(TitleOrientation.TOP);
		taskReminderForm.setNumCols(1);
		StaticTextItem taskReminder = ItemFactory.newStaticTextItem("taskReminder", "",
				"<b>" + I18N.message("reminder") + "</b>");
		taskAssignment.setShouldSaveValue(false);
		taskAssignment.setWrapTitle(false);
		TextItem reminderSubject = ItemFactory.newTextItem("reminderSubject", "subject", null);
		TextAreaItem reminderBody = ItemFactory.newTextAreaItem("reminderBody", "body", null);
		taskReminderForm.setFields(taskReminder, reminderSubject, reminderBody);

		DynamicForm separatorForm = new DynamicForm();
		separatorForm.setHeight(15);

		DynamicForm supervisorForm = new DynamicForm();
		supervisorForm.setTitleOrientation(TitleOrientation.TOP);
		supervisorForm.setNumCols(1);
		ComboBoxItem supervisors = ItemFactory.newUserSelector("supervisor", "");
		supervisors.setTitle("<b>" + I18N.message("supervisor") + "</b>");
		supervisors.setTitleOrientation(TitleOrientation.TOP);
		supervisorForm.setItems(supervisors);

		wfSettingsSection.setItems(wfForm, taskAssignmentForm, taskReminderForm, separatorForm, supervisorForm);
		addSection(wfSettingsSection);

		taskSettingsSection = new SectionStackSection(I18N.message("task"));
		taskSettingsSection.setExpanded(showTask);
		taskSettingsSection.setCanCollapse(true);

		DynamicForm taskForm = new DynamicForm();
		taskForm.setTitleOrientation(TitleOrientation.TOP);
		taskForm.setNumCols(1);
		TextItem taskName = ItemFactory.newTextItem("taskName", "name", null);
		taskName.setRequired(true);
		TextAreaItem taskDescr = ItemFactory.newTextAreaItem("taskDescr", "description", null);
		taskDescr.setWrapTitle(false);
		taskForm.setFields(taskName, taskDescr);

		DynamicForm escalationFormItem = new DynamicForm();
		escalationFormItem.setTitleOrientation(TitleOrientation.TOP);
		StaticTextItem escalation = ItemFactory.newStaticTextItem("escalationManagement", "",
				"<b>" + I18N.message("escalationmanagement") + "</b>");
		escalation.setShouldSaveValue(false);
		escalation.setWrapTitle(false);
		escalation.setWrap(false);
		escalationFormItem.setItems(escalation);

		DynamicForm escalationForm = new DynamicForm();
		escalationForm.setTitleOrientation(TitleOrientation.LEFT);
		escalationForm.setNumCols(4);
		escalationForm.setColWidths("35", "35", "50", "130");
		SpinnerItem duedateTimeItem = new SpinnerItem();
		duedateTimeItem.setTitle(I18N.message("duedate"));
		duedateTimeItem.setDefaultValue(0);
		duedateTimeItem.setMin(0);
		duedateTimeItem.setStep(1);
		duedateTimeItem.setWidth(50);
		SelectItem duedateTime = ItemFactory.newTimeSelector("duedateTime", "");

		SpinnerItem remindTimeItem = new SpinnerItem();
		remindTimeItem.setTitle(I18N.message("remindtime"));
		remindTimeItem.setDefaultValue(0);
		remindTimeItem.setMin(0);
		remindTimeItem.setStep(1);
		remindTimeItem.setWidth(50);
		SelectItem remindTime = ItemFactory.newTimeSelector("remindTime", "");
		escalationForm.setFields(duedateTimeItem, duedateTime, remindTimeItem, remindTime);

		DynamicForm separator1Form = new DynamicForm();
		separator1Form.setHeight(15);

		DynamicForm participantsForm = new DynamicForm();
		participantsForm.setTitleOrientation(TitleOrientation.TOP);
		participantsForm.setNumCols(1);
		ComboBoxItem participants = ItemFactory.newUserSelector("participants", "");
		participants.setTitle("<b>" + I18N.message("participants") + "</b>");
		participants.setTitleOrientation(TitleOrientation.TOP);
		participantsForm.setItems(participants);

		DynamicForm separator2Form = new DynamicForm();
		separator2Form.setHeight(15);

		DynamicForm transitionsForm = new DynamicForm();
		transitionsForm.setTitleOrientation(TitleOrientation.TOP);
		transitionsForm.setNumCols(1);
		ComboBoxItem transitions = ItemFactory.newUserSelector("transitions", "");
		transitions.setTitle("<b>" + I18N.message("transitions") + "</b>");
		transitions.setTitleOrientation(TitleOrientation.TOP);
		transitionsForm.setItems(transitions);

		taskSettingsSection.setItems(taskForm, escalationFormItem, escalationForm, separator1Form, participantsForm,
				separator2Form, transitionsForm);
//		addSection(taskSettingsSection);
	}

//	public static Accordion get() {
//		if (instance == null)
//			instance = new Accordion();
//		return instance;
//	}

	public void showTaskSection() {
		collapseSection(0);
		
		// refresh(true);
	}
}