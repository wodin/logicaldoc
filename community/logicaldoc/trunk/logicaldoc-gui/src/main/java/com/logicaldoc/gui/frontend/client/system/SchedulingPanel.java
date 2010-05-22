package com.logicaldoc.gui.frontend.client.system;

import java.util.LinkedHashMap;
import java.util.Map;

import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.beans.GUIScheduling;
import com.logicaldoc.gui.common.client.beans.GUITask;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.IntegerItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * This panel shows the task scheduling settings.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class SchedulingPanel extends VLayout {

	private ValuesManager vm = new ValuesManager();

	private DynamicForm form;

	private ChangedHandler changedHandler;

	private GUITask task;

	private boolean simplePolicy;

	public SchedulingPanel(GUITask task, ChangedHandler changedHandler) {
		setWidth100();
		this.changedHandler = changedHandler;
		this.task = task;
		simplePolicy = task.getScheduling().isSimple();
		reloadForm();
	}

	private DynamicForm reloadForm() {
		if (form != null) {
			removeMember(form);
		}

		form = new DynamicForm();
		form.setValuesManager(vm);
		form.setTitleOrientation(TitleOrientation.TOP);
		form.setNumCols(3);
		form.setColWidths(60, 60, 60);
		form.setWrapItemTitles(false);
		form.setWidth(500);

		// Policy
		final SelectItem simple = new SelectItem();
		LinkedHashMap<String, String> opts = new LinkedHashMap<String, String>();
		opts.put("true", I18N.getMessage("simple"));
		opts.put("false", I18N.getMessage("advanced"));
		simple.setValueMap(opts);
		simple.setName("simple");
		simple.setTitle(I18N.getMessage("policy"));
		simple.setDefaultValue(Boolean.toString(task.getScheduling().isSimple()));
		simple.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				simplePolicy = !simplePolicy;
				reloadForm();
				changedHandler.onChanged(event);
			}
		});

		// Max Lengths
		SelectItem maxDuration = new SelectItem();
		LinkedHashMap<String, String> opts2 = new LinkedHashMap<String, String>();
		opts2.put("-1", I18N.getMessage("nolimits"));
		opts2.put(Long.toString(15 * 60L), I18N.getMessage("15minutes"));
		opts2.put(Long.toString(60 * 60L), I18N.getMessage("1hour"));
		opts2.put(Long.toString(5 * 60 * 60L), I18N.getMessage("5hours"));
		maxDuration.setValueMap(opts2);
		maxDuration.setName("maxDuration");
		maxDuration.setTitle(I18N.getMessage("maxlengths"));
		maxDuration.setDefaultValue(Long.toString(task.getScheduling().getMaxLength()));
		maxDuration.addChangedHandler(changedHandler);

		// CPU Idle
		IntegerItem cpuIdle = new IntegerItem();
		cpuIdle.setName("cpuIdle");
		cpuIdle.setTitle(I18N.getMessage("schedulingidle"));
		cpuIdle.setDefaultValue(task.getScheduling().getMinCpuIdle());
		cpuIdle.addChangedHandler(changedHandler);

		// Initial delay
		IntegerItem initialDelay = new IntegerItem();
		initialDelay.setName("initialDelay");
		initialDelay.setTitle(I18N.getMessage("initialdelay"));
		initialDelay.setDefaultValue(new Integer(Long.toString(task.getScheduling().getDelay())));
		initialDelay.setVisible(simplePolicy);
		initialDelay.addChangedHandler(changedHandler);
		initialDelay.setHint(I18N.getMessage("seconds"));

		// Repeat interval
		IntegerItem repeatInterval = new IntegerItem();
		repeatInterval.setName("repeatInterval");
		repeatInterval.setTitle(I18N.getMessage("repeatinterval"));
		repeatInterval.setDefaultValue(new Integer(Long.toString(task.getScheduling().getInterval())));
		repeatInterval.setVisible(simplePolicy);
		repeatInterval.addChangedHandler(changedHandler);
		repeatInterval.setHint(I18N.getMessage("seconds"));

		// Seconds
		TextItem seconds = new TextItem();
		seconds.setName("seconds");
		seconds.setTitle(I18N.getMessage("seconds"));
		seconds.setDefaultValue(task.getScheduling().getSeconds());
		seconds.setVisible(!simplePolicy);
		seconds.addChangedHandler(changedHandler);

		// Minutes
		TextItem minutes = new TextItem();
		minutes.setName("minutes");
		minutes.setTitle(I18N.getMessage("minutes"));
		minutes.setDefaultValue(task.getScheduling().getMinutes());
		minutes.setVisible(!simplePolicy);
		minutes.addChangedHandler(changedHandler);

		// Hours
		TextItem hours = new TextItem();
		hours.setName("hours");
		hours.setTitle(I18N.getMessage("hours"));
		hours.setDefaultValue(task.getScheduling().getHours());
		hours.setVisible(!simplePolicy);
		hours.addChangedHandler(changedHandler);

		// Day of month
		TextItem dayMonth = new TextItem();
		dayMonth.setName("dayMonth");
		dayMonth.setTitle(I18N.getMessage("daymonth"));
		dayMonth.setDefaultValue(task.getScheduling().getDayOfMonth());
		dayMonth.setVisible(!simplePolicy);
		dayMonth.addChangedHandler(changedHandler);

		// Month
		TextItem month = new TextItem();
		month.setName("month");
		month.setTitle(I18N.getMessage("month"));
		month.setDefaultValue(task.getScheduling().getMonth());
		month.setVisible(!simplePolicy);
		month.addChangedHandler(changedHandler);

		// Day of week
		TextItem dayWeek = new TextItem();
		dayWeek.setName("dayWeek");
		dayWeek.setTitle(I18N.getMessage("dayweek"));
		dayWeek.setDefaultValue(task.getScheduling().getDayOfWeek());
		dayWeek.setVisible(!simplePolicy);
		dayWeek.addChangedHandler(changedHandler);

		form.setItems(simple, initialDelay, repeatInterval, seconds, minutes, hours, dayMonth, month, dayWeek,
				maxDuration, cpuIdle);

		setMembers(form);

		return form;
	}

	boolean validate() {
		Map<String, Object> values = (Map<String, Object>) vm.getValues();
		vm.validate();
		if (!vm.hasErrors()) {
			GUIScheduling s = task.getScheduling();
			s.setSimple(new Boolean((String) values.get("simple")));
			s.setMaxLength(new Long((String) values.get("maxDuration")));
			s.setMinCpuIdle((Integer) values.get("cpuIdle"));

			if (s.isSimple()) {
				if (values.get("initialDelay") instanceof String)
					s.setDelay(Long.parseLong((String) values.get("initialDelay")));
				else
					s.setDelay((Integer) values.get("initialDelay"));

				if (values.get("repeatInterval") instanceof String)
					s.setInterval(Long.parseLong((String) values.get("repeatInterval")));
				else
					s.setDelay((Integer) values.get("repeatInterval"));
			} else {
				s.setSeconds((String) values.get("seconds"));
				s.setMinutes((String) values.get("minutes"));
				s.setHours((String) values.get("hours"));
				s.setDayOfMonth((String) values.get("dayMonth"));
				s.setMonth((String) values.get("month"));
				s.setDayOfWeek((String) values.get("dayWeek"));
			}
		}
		return !vm.hasErrors();
	}
}
