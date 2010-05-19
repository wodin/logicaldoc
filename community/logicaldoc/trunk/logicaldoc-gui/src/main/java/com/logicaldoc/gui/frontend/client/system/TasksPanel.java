package com.logicaldoc.gui.frontend.client.system;

import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUITask;
import com.logicaldoc.gui.common.client.formatters.DateCellFormatter;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.frontend.client.services.SystemService;
import com.logicaldoc.gui.frontend.client.services.SystemServiceAsync;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.grid.CellFormatter;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellContextClickEvent;
import com.smartgwt.client.widgets.grid.events.CellContextClickHandler;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;
import com.smartgwt.client.widgets.tab.TabSet;

public class TasksPanel extends VLayout {
	private SystemServiceAsync service = (SystemServiceAsync) GWT.create(SystemService.class);

	private TabSet tabs = new TabSet();

	private Layout results = new VLayout();

	private VLayout tasksLayout = new VLayout();

	private ValuesManager vm = new ValuesManager();

	private ListGrid list;

	private Timer timer;

	private Img image = null;

	public TasksPanel() {
		setWidth100();
		init();
	}

	private void loadTasksValues() {
		final Map<String, Object> values = vm.getValues();

		if (vm.validate()) {
			service.loadTasks(Session.get().getSid(), new AsyncCallback<GUITask[]>() {

				@Override
				public void onFailure(Throwable caught) {
					Log.serverError(caught);
				}

				@Override
				public void onSuccess(GUITask[] result) {
					ListGridRecord[] records = getData(result);

					list.setData(records);
				}
			});
		}
	}

	private ListGridRecord[] getData(GUITask[] result) {
		ListGridRecord[] records = new ListGridRecord[result.length];
		for (int i = 0; i < result.length; i++) {
			ListGridRecord record = new ListGridRecord();
			record.setAttribute("enabledImage", result[i].getScheduling().isEnabled() ? "bullet_green" : "bullet_red");
			record.setAttribute("eenabled", result[i].getScheduling().isEnabled());
			record.setAttribute("name", result[i].getName());
			record.setAttribute("lastStart", result[i].getScheduling().getPreviousFireTime());
			record.setAttribute("nextStart", result[i].getScheduling().getNextFireTime());
			record.setAttribute("scheduling", result[i].getSchedulingLabel());
			record.setAttribute("execution", result[i].getProgress());
			record.setAttribute("status", result[i].getStatus());
			records[i] = record;
		}
		return records;
	}

	private void showContextMenu() {
		Menu contextMenu = new Menu();

		MenuItem taskExecution = new MenuItem();
		taskExecution.setTitle(I18N.getMessage("execute"));
		taskExecution.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				ListGridRecord record = list.getSelectedRecord();
				// service.executeTask(Session.get().getSid(),
				// record.getAttributeAsString("name"),
				// new AsyncCallback<Void>() {
				// @Override
				// public void onFailure(Throwable caught) {
				// Log.serverError(caught);
				// }
				//
				// @Override
				// public void onSuccess(Void result) {
				//
				// }
				// });
			}
		});

		if (!list.getSelectedRecord().getAttributeAsBoolean("enabled"))
			taskExecution.setEnabled(false);

		contextMenu.setItems(taskExecution);
		contextMenu.showContextMenu();
	}

	public void init() {
		list = new ListGrid();

		list.setShowRecordComponents(true);
		list.setShowRecordComponentsByCell(true);
		list.setShowAllRecords(true);

		ListGridField enabled = new ListGridField("enabledImage", I18N.getMessage("enabled"), 50);
		enabled.setType(ListGridFieldType.IMAGE);
		enabled.setCanSort(false);
		enabled.setAlign(Alignment.CENTER);
		enabled.setImageURLPrefix(Util.imagePrefix() + "/application/");
		enabled.setImageURLSuffix(".png");
		enabled.setCanFilter(false);

		ListGridField name = new ListGridField("name", I18N.getMessage("name"), 200);
		name.setCanFilter(true);
		name.setCanSort(false);

		ListGridField lastStart = new ListGridField("lastStart", I18N.getMessage("laststart"), 110);
		lastStart.setType(ListGridFieldType.DATE);
		lastStart.setCellFormatter(new DateCellFormatter());
		lastStart.setCanFilter(false);
		lastStart.setAlign(Alignment.CENTER);
		lastStart.setCanSort(false);

		ListGridField nextStart = new ListGridField("nextStart", I18N.getMessage("nextstart"), 110);
		nextStart.setType(ListGridFieldType.DATE);
		nextStart.setCellFormatter(new DateCellFormatter());
		nextStart.setCanFilter(false);
		nextStart.setAlign(Alignment.CENTER);
		nextStart.setCanSort(false);

		ListGridField scheduling = new ListGridField("scheduling", I18N.getMessage("scheduling"), 100);
		scheduling.setCanFilter(false);
		scheduling.setAlign(Alignment.CENTER);
		scheduling.setCanSort(false);

		ListGridField execution = new ListGridField("execution", I18N.getMessage("execution"), 100);
		execution.setCanFilter(false);
		execution.setAlign(Alignment.CENTER);
		execution.setCanSort(false);
		execution.setCellFormatter(new CellFormatter(){
			@Override
			public String format(Object value, ListGridRecord record, int rowNum, int colNum) {
				return value+"%";
			}
			
		});

		list.setWidth100();
		list.setHeight100();
		list.setFields(enabled, name, lastStart, nextStart, scheduling, execution);
		list.setSelectionType(SelectionStyle.SINGLE);
		list.setShowRecordComponents(true);
		list.setShowRecordComponentsByCell(true);
		list.setCanFreezeFields(true);
		list.setFilterOnKeypress(true);

		list.addCellContextClickHandler(new CellContextClickHandler() {
			@Override
			public void onCellContextClick(CellContextClickEvent event) {
				showContextMenu();
				event.cancel();
			}
		});

		loadTasksValues();

		results.addMember(list);

		tasksLayout.addMember(results);

		tasksLayout.addMember(tabs);

		addMember(tasksLayout);

		/*
		 * Create the timer that synchronize the view
		 */
		timer = new Timer() {
			public void run() {
				service.loadTasks(Session.get().getSid(), new AsyncCallback<GUITask[]>() {

					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(GUITask[] tasks) {
						Log.debug("** task: " + tasks.length);
						// list.setRecords(getData(tasks));

						// list.redraw();
						for (int i = 0; i < tasks.length; i++) {
							list.getRecord(i).setAttribute("execution", tasks[i].getProgress());
						}
					}
				});
			}
		};
		timer.scheduleRepeating(1000);
	}

	@Override
	public void destroy() {
		super.destroy();
		this.timer.cancel();
	}
}
