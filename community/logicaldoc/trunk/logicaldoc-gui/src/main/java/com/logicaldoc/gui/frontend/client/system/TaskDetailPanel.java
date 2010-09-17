package com.logicaldoc.gui.frontend.client.system;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUITask;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.frontend.client.services.SystemService;
import com.logicaldoc.gui.frontend.client.services.SystemServiceAsync;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;

/**
 * This panel collects all tasks details
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 * 
 */
public class TaskDetailPanel extends VLayout {

	private SystemServiceAsync service = (SystemServiceAsync) GWT.create(SystemService.class);

	TabSet tabSet = new TabSet();

	private Layout schedulingTabPanel;

	private Layout logTabPanel;

	private GUITask task;

	private SchedulingPanel schedulingPanel;

	private LogPanel logPanel;

	private HLayout savePanel;

	private TasksPanel tasksPanel;

	public TaskDetailPanel(TasksPanel tasksPanel) {
		super();
		this.tasksPanel = tasksPanel;
		setHeight100();
		setWidth100();
		setMembersMargin(10);

		savePanel = new HLayout();
		Button saveButton = new Button(I18N.message("save"));
		saveButton.setMargin(2);
		saveButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onSave();
			}
		});
		savePanel.addMember(saveButton);
		savePanel.setHeight(20);
		savePanel.setVisible(false);
		savePanel.setStyleName("warn");
		savePanel.setWidth100();
		addMember(savePanel);

		Tab schedulingTab = new Tab(I18N.message("scheduling"));
		schedulingTabPanel = new HLayout();
		schedulingTabPanel.setWidth100();
		schedulingTabPanel.setHeight100();
		schedulingTab.setPane(schedulingTabPanel);

		Tab logTab = new Tab(I18N.message("log"));
		logTabPanel = new HLayout();
		logTabPanel.setWidth100();
		logTabPanel.setHeight100();
		logTab.setPane(logTabPanel);

		tabSet.setTabs(schedulingTab, logTab);

		addMember(tabSet);
	}

	public GUITask getTask() {
		return task;
	}

	public void setTask(GUITask task) {
		this.task = task;
		refresh();
	}

	private void refresh() {
		if (savePanel != null)
			savePanel.setVisible(false);

		/*
		 * Prepare the scheduling tab
		 */
		if (schedulingPanel != null) {
			schedulingPanel.destroy();
			if (schedulingTabPanel.contains(schedulingPanel))
				schedulingTabPanel.removeMember(schedulingPanel);
		}
		ChangedHandler changeHandler = new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				onModified();
			}
		};
		schedulingPanel = new SchedulingPanel(task, changeHandler);
		schedulingTabPanel.addMember(schedulingPanel);

		/*
		 * Prepare the log tab
		 */
		if (logPanel != null) {
			logPanel.destroy();
			if (logTabPanel.contains(logPanel))
				logTabPanel.removeMember(logPanel);
		}
		logPanel = new LogPanel(task.getName() + "_WEB");
		logTabPanel.addMember(logPanel);
	}

	public void onModified() {
		savePanel.setVisible(true);
	}

	public void onSave() {
		if (schedulingPanel.validate()) {
			service.saveTask(Session.get().getSid(), task, I18N.getLocale(), new AsyncCallback<GUITask>() {
				@Override
				public void onFailure(Throwable caught) {
					Log.serverError(caught);
				}

				@Override
				public void onSuccess(GUITask task) {
					tasksPanel.updateSelectedRecord(task);
					savePanel.setVisible(false);
				}
			});
		}
	}
}
