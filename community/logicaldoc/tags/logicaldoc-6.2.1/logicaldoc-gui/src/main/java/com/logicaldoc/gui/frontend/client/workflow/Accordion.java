package com.logicaldoc.gui.frontend.client.workflow;

import java.util.Map;

import com.logicaldoc.gui.common.client.beans.GUIWorkflow;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.types.VisibilityMode;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.FormItemIcon;
import com.smartgwt.client.widgets.form.fields.SelectItem;
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

	private SelectItem supervisor = null;

	private TextItem workflowName = null;

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
		workflowName = ItemFactory.newTextItem("workflowName", "workflowname", null);
		workflowName.setRequired(true);
		if (this.workflow != null) {
			workflowName.setValue(this.workflow.getName());
			workflowName.setDisabled(!(this.workflow.getName() == null || this.workflow.getName().trim().isEmpty()));
		}

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
		taskReminder.setShouldSaveValue(false);
		taskReminder.setWrapTitle(false);
		TextItem reminderSubject = ItemFactory.newTextItem("reminderSubject", "subject", null);
		if (this.workflow != null)
			reminderSubject.setValue(this.workflow.getReminderSubject());
		TextAreaItem reminderBody = ItemFactory.newTextAreaItem("reminderBody", "body", null);
		if (this.workflow != null)
			reminderBody.setValue(this.workflow.getReminderBody());

		DynamicForm separatorForm = new DynamicForm();
		separatorForm.setTitleOrientation(TitleOrientation.TOP);
		separatorForm.setHeight(15);

		supervisorForm = new DynamicForm();
		supervisorForm.setTitleOrientation(TitleOrientation.TOP);
		supervisorForm.setNumCols(1);
		supervisorForm.setValuesManager(vm);
		StaticTextItem supervisorItem = ItemFactory.newStaticTextItem("supervisorItem", "",
				"<b>" + I18N.message("supervisor") + "</b>");
		supervisorItem.setShouldSaveValue(false);
		supervisorItem.setWrapTitle(false);
		supervisor = ItemFactory.newUserSelector("supervisor", " ");
		supervisor.setShowTitle(false);
		supervisor.setDisplayField("username");
		supervisor.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				try {
					setSupervisor(supervisor.getSelectedRecord().getAttribute("id"));
				} catch (Throwable t) {
				}
			}
		});

		FormItemIcon icon = new FormItemIcon();
		icon.setSrc("[SKIN]/actions/remove.png");
		supervisor.addIconClickHandler(new IconClickHandler() {
			public void onIconClick(IconClickEvent event) {
				supervisor.setValue("");
			}
		});
		supervisor.setIcons(icon);
		if (this.workflow != null && this.workflow.getSupervisor() != null)
			supervisor.setValue(this.workflow.getSupervisor());
		supervisorForm.setItems(supervisorItem, supervisor);

		wfForm.setItems(workflowName, workflowDescr, taskAssignment, assignmentSubject, assignmentBody, taskReminder,
				reminderSubject, reminderBody);
		wfLayout.setMembers(wfForm, separatorForm, supervisorForm);
		wfLayout.redraw();

		wfSettingsSection.setItems(wfLayout);
	}

	public SectionStackSection getWfSettingsSection() {
		return wfSettingsSection;
	}

	public void setSupervisor(String id) {
		supervisor.setValue(id);
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getValues() {
		if (vm.validate()) {
			return (Map<String, Object>) vm.getValues();
		} else
			return null;
	}

	public void setWorkflowName(String name) {
		workflowName.setValue(name);
		workflow.setName(name);
	}
}