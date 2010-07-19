package com.logicaldoc.gui.frontend.client.workflow;

import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.types.VisibilityMode;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.SectionStack;
import com.smartgwt.client.widgets.layout.SectionStackSection;

public class Accordion extends SectionStack {

	public Accordion() {
		setVisibilityMode(VisibilityMode.MUTEX);
		setWidth(300);
		setHeight(557);
		setMargin(5);

		SectionStackSection wfsettingsSection = new SectionStackSection(I18N.message("workflowsettings"));
		wfsettingsSection.setExpanded(true);

		DynamicForm wfForm = new DynamicForm();
		wfForm.setTitleOrientation(TitleOrientation.TOP);
		wfForm.setNumCols(1);

		// TODO Separare workflow, task assignment e reminder in tre form
		// diversi da aggiugere al sectionstacksection

		TextItem workflowName = ItemFactory.newTextItem("workflowName", "workflowname", null);
		workflowName.setRequired(true);

		TextItem workflowDescr = ItemFactory.newTextItem("workflowDescr", "workflowdescr", null);
		workflowDescr.setWrapTitle(false);

		StaticTextItem taskAssignment = ItemFactory.newStaticTextItem("taskAssignment", "taskassignment", null);
		taskAssignment.setRequired(true);
		TextItem assignmentSubject = ItemFactory.newTextItem("assignmentSubject", "subject", null);
		TextItem assignmentBody = ItemFactory.newTextItem("assignmentBody", "body", null);

		StaticTextItem taskReminder = ItemFactory.newStaticTextItem("taskReminder", "reminder", null);
		taskReminder.setRequired(true);
		TextItem reminderSubject = ItemFactory.newTextItem("reminderSubject", "subject", null);
		TextItem reminderBody = ItemFactory.newTextItem("reminderBody", "body", null);

		StaticTextItem supervisor = ItemFactory.newStaticTextItem("supervisor", "supervisor", null);
		supervisor.setRequired(true);

		wfForm.setFields(workflowName, workflowDescr, taskAssignment, assignmentSubject, assignmentBody, taskReminder,
				reminderSubject, reminderBody, supervisor);

		wfsettingsSection.addItem(wfForm);
		addSection(wfsettingsSection);

		SectionStackSection wfdevelopSection = new SectionStackSection(I18N.message("development"));
		wfdevelopSection.setExpanded(false);
		addSection(wfdevelopSection);
	}
}