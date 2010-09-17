package com.logicaldoc.gui.frontend.client.system;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUITask;
import com.logicaldoc.gui.common.client.data.TasksDS;
import com.logicaldoc.gui.common.client.formatters.DateCellFormatter;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.frontend.client.services.SystemService;
import com.logicaldoc.gui.frontend.client.services.SystemServiceAsync;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.IntegerItem;
import com.smartgwt.client.widgets.grid.CellFormatter;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellContextClickEvent;
import com.smartgwt.client.widgets.grid.events.CellContextClickHandler;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

/**
 * Panel showing the list of scheduled tasks
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class TasksPanel extends VLayout {
	private SystemServiceAsync service = (SystemServiceAsync) GWT.create(SystemService.class);

	private Layout results = new VLayout();

	private Layout details;

	private ListGrid list;

	private Timer timer;

	private Canvas detailPanel;

	public TasksPanel() {
		setWidth100();
		init();
	}

	private void showContextMenu() {
		Menu contextMenu = new Menu();

		MenuItem taskExecution = new MenuItem();
		taskExecution.setTitle(I18N.message("execute"));
		taskExecution.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				service.startTask(list.getSelectedRecord().getAttributeAsString("name"), new AsyncCallback<Boolean>() {
					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(Boolean result) {
						list.getSelectedRecord().setAttribute("status", GUITask.STATUS_RUNNING);
						list.updateData(list.getSelectedRecord());
					}
				});
			}
		});

		if (GUITask.STATUS_RUNNING == list.getSelectedRecord().getAttributeAsInt("status")
				|| !list.getSelectedRecord().getAttributeAsBoolean("eenabled"))
			taskExecution.setEnabled(false);

		MenuItem taskStop = new MenuItem();
		taskStop.setTitle(I18N.message("stop"));
		taskStop.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				service.stopTask(list.getSelectedRecord().getAttributeAsString("name"), new AsyncCallback<Boolean>() {
					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(Boolean result) {
						list.getSelectedRecord().setAttribute("status", GUITask.STATUS_IDLE);
						list.updateData(list.getSelectedRecord());
					}
				});

			}
		});

		if (GUITask.STATUS_IDLE == list.getSelectedRecord().getAttributeAsInt("status")
				|| !list.getSelectedRecord().getAttributeAsBoolean("eenabled"))
			taskStop.setEnabled(false);

		MenuItem enableTask = new MenuItem();
		enableTask.setTitle(I18N.message("enable"));
		enableTask.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				service.enableTask(Session.get().getSid(), list.getSelectedRecord().getAttributeAsString("name"),
						new AsyncCallback<Boolean>() {
							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught);
							}

							@Override
							public void onSuccess(Boolean result) {
								list.getSelectedRecord().setAttribute("enabledIcon", "bullet_green");
								list.getSelectedRecord().setAttribute("eenabled", true);
								list.updateData(list.getSelectedRecord());
							}
						});

			}
		});

		if (list.getSelectedRecord().getAttributeAsBoolean("eenabled"))
			enableTask.setEnabled(false);

		MenuItem disableTask = new MenuItem();
		disableTask.setTitle(I18N.message("disable"));
		disableTask.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				service.disableTask(Session.get().getSid(), list.getSelectedRecord().getAttributeAsString("name"),
						new AsyncCallback<Boolean>() {
							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught);
							}

							@Override
							public void onSuccess(Boolean result) {
								list.getSelectedRecord().setAttribute("enabledIcon", "bullet_red");
								list.getSelectedRecord().setAttribute("eenabled", false);
								list.updateData(list.getSelectedRecord());
							}
						});

			}
		});

		if (!list.getSelectedRecord().getAttributeAsBoolean("eenabled"))
			disableTask.setEnabled(false);

		contextMenu.setItems(taskExecution, taskStop, enableTask, disableTask);
		contextMenu.showContextMenu();
	}

	public void init() {
		results.addMember(setupToolbar());
		results.setShowResizeBar(true);
		addMember(results);

		detailPanel = new Label("&nbsp;" + I18N.message("selecttask"));
		details = new VLayout();
		details.setAlign(Alignment.CENTER);
		details.addMember(detailPanel);
		addMember(details);

		reload();

		/*
		 * Create the timer that synchronize the view
		 */
		timer = new Timer() {
			public void run() {
				reload();
			}
		};
		timer.scheduleRepeating(2 * 1000);
	}

	private void reload() {
		if (list != null) {
			results.removeMember(list);
			list.clear();
			list.invalidateCache();
			list.destroy();
		}

		list = new ListGrid();

		list.setShowRecordComponents(true);
		list.setShowRecordComponentsByCell(true);
		list.setShowAllRecords(true);
		list.setAutoFetchData(true);

		ListGridField enabled = new ListGridField("enabledIcon", " ", 30);
		enabled.setType(ListGridFieldType.IMAGE);
		enabled.setCanSort(false);
		enabled.setAlign(Alignment.CENTER);
		enabled.setImageURLPrefix(Util.imagePrefix());
		enabled.setImageURLSuffix(".png");
		enabled.setCanFilter(false);

		ListGridField label = new ListGridField("label", I18N.message("task"), 200);
		label.setCanFilter(true);
		label.setCanSort(false);

		ListGridField lastStart = new ListGridField("lastStart", I18N.message("laststart"), 110);
		lastStart.setType(ListGridFieldType.DATE);
		lastStart.setCellFormatter(new DateCellFormatter());
		lastStart.setCanFilter(false);
		lastStart.setAlign(Alignment.CENTER);
		lastStart.setCanSort(false);

		ListGridField nextStart = new ListGridField("nextStart", I18N.message("nextstart"), 110);
		nextStart.setType(ListGridFieldType.DATE);
		nextStart.setCellFormatter(new DateCellFormatter());
		nextStart.setCanFilter(false);
		nextStart.setAlign(Alignment.CENTER);
		nextStart.setCanSort(false);

		ListGridField scheduling = new ListGridField("scheduling", I18N.message("scheduling"), 150);
		scheduling.setCanFilter(false);
		scheduling.setAlign(Alignment.CENTER);
		scheduling.setCanSort(false);

		ListGridField progress = new ListGridField("progress", I18N.message("progress"), 100);
		progress.setCanFilter(false);
		progress.setAlign(Alignment.CENTER);
		progress.setCanSort(false);
		progress.setCellFormatter(new CellFormatter() {
			@Override
			public String format(Object value, ListGridRecord record, int rowNum, int colNum) {
				if (GUITask.STATUS_RUNNING == record.getAttributeAsInt("status")
						&& record.getAttributeAsBoolean("eenabled") && !record.getAttributeAsBoolean("indeterminate"))
					return value + "%";
				else if (GUITask.STATUS_RUNNING == record.getAttributeAsInt("status")
						&& record.getAttributeAsBoolean("eenabled") && record.getAttributeAsBoolean("indeterminate"))
					return I18N.message("running");
				else
					return "";
			}
		});

		list.setWidth100();
		list.setHeight100();
		list.setFields(enabled, label, lastStart, nextStart, scheduling, progress);
		list.setSelectionType(SelectionStyle.SINGLE);
		list.setShowRecordComponents(true);
		list.setShowRecordComponentsByCell(true);
		list.setCanFreezeFields(true);
		list.setFilterOnKeypress(true);
		list.setDataSource(new TasksDS());

		results.addMember(list, 1);

		list.addCellContextClickHandler(new CellContextClickHandler() {
			@Override
			public void onCellContextClick(CellContextClickEvent event) {
				showContextMenu();
				event.cancel();
			}
		});

		list.addSelectionChangedHandler(new SelectionChangedHandler() {
			@Override
			public void onSelectionChanged(SelectionEvent event) {
				ListGridRecord record = list.getSelectedRecord();
				if (record != null)
					service.getTaskByName(Session.get().getSid(), record.getAttribute("name"), I18N.getLocale(),
							new AsyncCallback<GUITask>() {
								@Override
								public void onFailure(Throwable caught) {
									Log.serverError(caught);
								}

								@Override
								public void onSuccess(GUITask task) {
									onSelectedTask(task);
								}
							});
			}
		});
	}

	@Override
	public void destroy() {
		super.destroy();
		this.timer.cancel();
	}

	/**
	 * Prepares the toolbar containing the search report and a set of buttons
	 */
	private ToolStrip setupToolbar() {
		ToolStrip toolStrip = new ToolStrip();
		toolStrip.setHeight(20);
		toolStrip.setWidth100();

		ToolStripButton refreshnow = new ToolStripButton();
		refreshnow.setTitle(I18N.message("refresh"));
		toolStrip.addButton(refreshnow);
		refreshnow.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				reload();
			}
		});
		toolStrip.addButton(refreshnow);

		toolStrip.addSeparator();
		final IntegerItem delay = ItemFactory.newValidateIntegerItem("delay", "", null, 1, null);
		delay.setHint(I18N.message("seconds").toLowerCase());
		delay.setShowTitle(false);
		delay.setDefaultValue(5);
		delay.setWidth(40);

		ToolStripButton refresh = new ToolStripButton();
		refresh.setTitle(I18N.message("refresheach"));
		toolStrip.addButton(refresh);
		toolStrip.addFormItem(delay);
		refresh.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (!delay.validate())
					return;
				timer.cancel();
				timer.scheduleRepeating(((Integer) delay.getValue()) * 1000);
			}
		});

		toolStrip.addFill();
		return toolStrip;
	}

	/**
	 * Shows the task details
	 * 
	 * @param task The task
	 */
	public void onSelectedTask(GUITask task) {
		if (!(detailPanel instanceof TaskDetailPanel)) {
			details.removeMember(detailPanel);
			detailPanel.destroy();
			detailPanel = new TaskDetailPanel(this);
			details.addMember(detailPanel);
		}
		((TaskDetailPanel) detailPanel).setTask(task);
	}

	public ListGrid getList() {
		return list;
	}

	/**
	 * Updates the selected record with the new task data
	 */
	public void updateSelectedRecord(GUITask task) {
		list.getSelectedRecord().setAttribute("scheduling", task.getSchedulingLabel());
		list.updateData(list.getSelectedRecord());
	}
}