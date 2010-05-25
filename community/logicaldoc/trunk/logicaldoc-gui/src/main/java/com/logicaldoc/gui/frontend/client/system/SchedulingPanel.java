package com.logicaldoc.gui.frontend.client.system;

import java.util.LinkedHashMap;
import java.util.Map;

import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.beans.GUIScheduling;
import com.logicaldoc.gui.common.client.beans.GUITask;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.IntegerItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.validator.IntegerRangeValidator;
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

	private SelectItem maxDuration;

	private IntegerItem cpuIdle;

	private IntegerItem initialDelay;

	private IntegerItem repeatInterval;

	private TextItem seconds;

	private TextItem minutes;

	private TextItem hours;

	private TextItem dayMonth;

	private TextItem month;

	private TextItem dayWeek;

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
		form.setColWidths(190, 200, 190);
		form.setWrapItemTitles(false);
		form.setWidth(700);

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
		maxDuration = new SelectItem();
		LinkedHashMap<String, String> opts2 = new LinkedHashMap<String, String>();
		opts2.put("-1", I18N.getMessage("nolimits"));
		opts2.put(Long.toString(15 * 60L), I18N.getMessage("_15minutes"));
		opts2.put(Long.toString(60 * 60L), I18N.getMessage("_1hour"));
		opts2.put(Long.toString(5 * 60 * 60L), I18N.getMessage("_5hours"));
		maxDuration.setValueMap(opts2);
		maxDuration.setName("maxDuration");
		maxDuration.setTitle(I18N.getMessage("maxlengths"));
		maxDuration.setDefaultValue(Long.toString(task.getScheduling().getMaxLength()));
		maxDuration.addChangedHandler(changedHandler);

		// CPU Idle
		cpuIdle = new IntegerItem();
		cpuIdle.setName("cpuIdle");
		cpuIdle.setTitle(I18N.getMessage("schedulingidle"));
		cpuIdle.setDefaultValue(task.getScheduling().getMinCpuIdle());
		cpuIdle.addChangedHandler(changedHandler);
		IntegerRangeValidator cpuIdleValidator = new IntegerRangeValidator();
		cpuIdleValidator.setMin(-1);
		cpuIdle.setValidators(cpuIdleValidator);

		// Initial delay
		initialDelay = new IntegerItem();
		initialDelay.setName("initialDelay");
		initialDelay.setTitle(I18N.getMessage("initialdelay"));
		initialDelay.setDefaultValue(new Integer(Long.toString(task.getScheduling().getDelay())));
		initialDelay.setVisible(simplePolicy);
		initialDelay.addChangedHandler(changedHandler);
		initialDelay.setHint(I18N.getMessage("seconds").toLowerCase());
		initialDelay.setRequired(true);
		IntegerRangeValidator initialDelayValidator = new IntegerRangeValidator();
		initialDelayValidator.setMin(1);
		initialDelay.setValidators(initialDelayValidator);

		// Repeat interval
		repeatInterval = new IntegerItem();
		repeatInterval.setName("repeatInterval");
		repeatInterval.setTitle(I18N.getMessage("repeatinterval"));
		repeatInterval.setDefaultValue(new Integer(Long.toString(task.getScheduling().getInterval())));
		repeatInterval.setVisible(simplePolicy);
		repeatInterval.addChangedHandler(changedHandler);
		repeatInterval.setHint(I18N.getMessage("seconds").toLowerCase());
		repeatInterval.setRequired(true);
		IntegerRangeValidator repeatIntervalValidator = new IntegerRangeValidator();
		repeatIntervalValidator.setMin(1);
		initialDelay.setValidators(repeatIntervalValidator);

		// Seconds
		seconds = new TextItem();
		seconds.setName("seconds");
		seconds.setTitle(I18N.getMessage("seconds"));
		seconds.setDefaultValue(task.getScheduling().getSeconds());
		seconds.setVisible(!simplePolicy);
		seconds.addChangedHandler(changedHandler);
		seconds.setRequired(true);
		seconds.setHint(I18N.getMessage("schedulingsechint"));

		// Minutes
		minutes = new TextItem();
		minutes.setName("minutes");
		minutes.setTitle(I18N.getMessage("minutes"));
		minutes.setDefaultValue(task.getScheduling().getMinutes());
		minutes.setVisible(!simplePolicy);
		minutes.addChangedHandler(changedHandler);
		minutes.setRequired(true);
		minutes.setHint(I18N.getMessage("schedulingsechint"));

		// Hours
		hours = new TextItem();
		hours.setName("hours");
		hours.setTitle(I18N.getMessage("hours"));
		hours.setDefaultValue(task.getScheduling().getHours());
		hours.setVisible(!simplePolicy);
		hours.addChangedHandler(changedHandler);
		hours.setRequired(true);
		hours.setHint(I18N.getMessage("schedulinghourshint"));

		// Day of month
		dayMonth = new TextItem();
		dayMonth.setName("dayMonth");
		dayMonth.setTitle(I18N.getMessage("daymonth"));
		dayMonth.setDefaultValue(task.getScheduling().getDayOfMonth());
		dayMonth.setVisible(!simplePolicy);
		dayMonth.addChangedHandler(changedHandler);
		dayMonth.setRequired(true);
		dayMonth.setHint(I18N.getMessage("schedulingdaymonthhint"));

		// Month
		month = new TextItem();
		month.setName("month");
		month.setTitle(I18N.getMessage("month"));
		month.setDefaultValue(task.getScheduling().getMonth());
		month.setVisible(!simplePolicy);
		month.addChangedHandler(changedHandler);
		month.setRequired(true);
		month.setHint(I18N.getMessage("schedulingmonthhint"));

		// Day of week
		dayWeek = new TextItem();
		dayWeek.setName("dayWeek");
		dayWeek.setTitle(I18N.getMessage("dayweek"));
		dayWeek.setDefaultValue(task.getScheduling().getDayOfWeek());
		dayWeek.setVisible(!simplePolicy);
		dayWeek.addChangedHandler(changedHandler);
		dayWeek.setRequired(true);
		dayWeek.setHint(I18N.getMessage("schedulingdayweekhint"));

		form.setItems(simple, initialDelay, repeatInterval, seconds, minutes, hours, dayMonth, month, dayWeek,
				maxDuration, cpuIdle);

		IButton restoreDefaults = new IButton();
		restoreDefaults.setTitle(I18N.getMessage("restoredefaults"));
		restoreDefaults.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				final Map<String, Object> values = vm.getValues();

				if (vm.validate()) {
					SchedulingPanel.this.maxDuration.setValue("-1");
					SchedulingPanel.this.cpuIdle.setValue(-1);
					SchedulingPanel.this.initialDelay.setValue(1800);
					SchedulingPanel.this.repeatInterval.setValue(1800);
					SchedulingPanel.this.seconds.setValue("00");
					SchedulingPanel.this.minutes.setValue("00");
					SchedulingPanel.this.hours.setValue("4/4");
					SchedulingPanel.this.dayMonth.setValue("*");
					SchedulingPanel.this.month.setValue("*");
					SchedulingPanel.this.dayWeek.setValue("?");
				}
			}
		});

		setMembers(form, restoreDefaults);
		setMembersMargin(10);

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
