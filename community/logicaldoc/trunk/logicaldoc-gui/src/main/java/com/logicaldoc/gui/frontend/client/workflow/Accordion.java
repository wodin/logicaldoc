package com.logicaldoc.gui.frontend.client.workflow;

import java.util.ArrayList;
import java.util.List;

import com.logicaldoc.gui.common.client.beans.GUIUser;
import com.logicaldoc.gui.common.client.beans.GUIWorkflow;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.types.VisibilityMode;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.FormItemIcon;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.IconClickEvent;
import com.smartgwt.client.widgets.form.fields.events.IconClickHandler;
import com.smartgwt.client.widgets.layout.SectionStack;
import com.smartgwt.client.widgets.layout.SectionStackSection;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * This panel contains all the workflow informations and it is next to its
 * drawing panel.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class Accordion extends SectionStack {
	private SectionStackSection wfSettingsSection = null;

	private GUIWorkflow workflow = new GUIWorkflow();

	private ValuesManager vm = new ValuesManager();

	private VLayout wfLayout = new VLayout();

	private DynamicForm wfForm = null;

	private DynamicForm supervisorForm = null;

	public Accordion(GUIWorkflow workflow) {
		setVisibilityMode(VisibilityMode.MUTEX);
		setWidth(250);
		setHeight(557);
		setMargin(5);

		wfSettingsSection = new SectionStackSection(I18N.message("workflowsettings"));
		wfSettingsSection.setExpanded(true);
		wfSettingsSection.setCanCollapse(true);
		addSection(wfSettingsSection);

		refresh(workflow);
	}

	public void refresh(GUIWorkflow workflow) {
		if (wfForm != null) {
			wfLayout.removeMember(wfForm);
			wfForm.destroy();
		}

		if (workflow != null) {
			this.workflow = workflow;
		}

		wfForm = new DynamicForm();
		wfForm.setTitleOrientation(TitleOrientation.TOP);
		wfForm.setNumCols(1);
		wfForm.setValuesManager(vm);
		TextItem workflowName = ItemFactory.newTextItem("workflowName", "workflowname", null);
		workflowName.setRequired(true);
		if (this.workflow != null)
			workflowName.setValue(this.workflow.getName());
		TextAreaItem workflowDescr = ItemFactory.newTextAreaItem("workflowDescr", "workflowdescr", null);
		workflowDescr.setWrapTitle(false);
		if (this.workflow != null)
			workflowDescr.setValue(this.workflow.getDescription());
		wfForm.setFields(workflowName, workflowDescr);

		StaticTextItem taskAssignment = ItemFactory.newStaticTextItem("taskAssignment", "",
				"<b>" + I18N.message("taskassignment") + "</b>");
		taskAssignment.setShouldSaveValue(false);
		taskAssignment.setWrapTitle(false);
		TextItem assignmentSubject = ItemFactory.newTextItem("assignmentSubject", "subject", null);
		if (this.workflow != null)
			assignmentSubject.setValue(this.workflow.getTaskAssignmentSubject());
		TextAreaItem assignmentBody = ItemFactory.newTextAreaItem("assignmentBody", "body", null);
		if (this.workflow != null)
			assignmentBody.setValue(this.workflow.getTaskAssignmentBody());

		StaticTextItem taskReminder = ItemFactory.newStaticTextItem("taskReminder", "",
				"<b>" + I18N.message("reminder") + "</b>");
		taskAssignment.setShouldSaveValue(false);
		taskAssignment.setWrapTitle(false);
		TextItem reminderSubject = ItemFactory.newTextItem("reminderSubject", "subject", null);
		if (this.workflow != null)
			reminderSubject.setValue(this.workflow.getReminderSubject());
		TextAreaItem reminderBody = ItemFactory.newTextAreaItem("reminderBody", "body", null);
		if (this.workflow != null)
			reminderBody.setValue(this.workflow.getReminderBody());

		DynamicForm separatorForm = new DynamicForm();
		separatorForm.setTitleOrientation(TitleOrientation.TOP);
		separatorForm.setHeight(15);

		refreshSupervisor();

		wfForm.setItems(workflowName, workflowDescr, taskAssignment, assignmentSubject, assignmentBody, taskReminder,
				reminderSubject, reminderBody);
		wfLayout.setMembers(wfForm, separatorForm, supervisorForm);
		wfLayout.redraw();

		wfSettingsSection.setItems(wfLayout);
	}

	public SectionStackSection getWfSettingsSection() {
		return wfSettingsSection;
	}

	public void addSupervisor(GUIUser user) {
		this.workflow.setSupervisor(user);
	}

	public void removeSupervisor() {
		this.workflow.setSupervisor(null);
	}

	private void refreshSupervisor() {
		if (supervisorForm != null && wfLayout.contains(supervisorForm)) {
			wfLayout.removeMember(supervisorForm);
			supervisorForm.destroy();
		}

		supervisorForm = new DynamicForm();
		supervisorForm.setColWidths(1, "*");

		final ComboBoxItem supervisor = ItemFactory.newUserSelector("supervisor", "");
		supervisor.setTitle("<b>" + I18N.message("supervisor") + "</b>");
		List<FormItem> items = new ArrayList<FormItem>();
		if (this.workflow.getSupervisor() == null) {
			supervisor.addChangedHandler(new ChangedHandler() {
				@Override
				public void onChanged(ChangedEvent event) {
					if (supervisor.getSelectedRecord() == null)
						return;
					GUIUser u = new GUIUser();
					u.setId(Long.parseLong(supervisor.getSelectedRecord().getAttribute("id")));
					u.setUserName(supervisor.getSelectedRecord().getAttribute("username"));
					addSupervisor(u);
					supervisor.clearValue();
					refreshSupervisor();
				}
			});
		}
		items.add(supervisor);

		if (this.workflow.getSupervisor() != null) {
			FormItemIcon icon = ItemFactory.newItemIcon("delete.png");

			final StaticTextItem usrItem = ItemFactory.newStaticTextItem("usr", "", this.workflow.getSupervisor()
					.getUserName());
			// usrItem.setDefaultValue(this.workflow.getSupervisor().getUserName());
			usrItem.setIcons(icon);
			usrItem.addIconClickHandler(new IconClickHandler() {
				public void onIconClick(IconClickEvent event) {
					removeSupervisor();
					refreshSupervisor();
				}
			});
			items.add(usrItem);
		}
		supervisorForm.setItems(items.toArray(new FormItem[0]));

		wfLayout.addMember(supervisorForm);
	}
}