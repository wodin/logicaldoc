package com.logicaldoc.gui.frontend.client.workflow;

import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.types.VisibilityMode;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
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
	}
}