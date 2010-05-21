package com.logicaldoc.gui.frontend.client.system;

import java.util.LinkedHashMap;

import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.beans.GUITask;
import com.logicaldoc.gui.frontend.client.document.DocumentDetailTab;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.FloatItem;
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

	private boolean simplePolicy = true;

	private DynamicForm form;

	public SchedulingPanel(GUITask task, ChangedHandler changedHandler) {
		setWidth100();

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
		final SelectItem policy = new SelectItem();
		LinkedHashMap<String, String> opts = new LinkedHashMap<String, String>();
		opts.put("simple", I18N.getMessage("simple"));
		opts.put("advanced", I18N.getMessage("advanced"));
		policy.setValueMap(opts);
		policy.setName("policy");
		policy.setTitle(I18N.getMessage("policy"));
		policy.setDefaultValue("simple");
		policy.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				if (((String) policy.getValue()).equals("simple")) {
					simplePolicy = true;
				} else if (((String) policy.getValue()).equals("advanced")) {
					simplePolicy = false;
				}

				reloadForm();
			}

		});

		// Max Lengths
		SelectItem maxLengths = new SelectItem();
		LinkedHashMap<String, String> opts2 = new LinkedHashMap<String, String>();
		opts2.put("nolimits", I18N.getMessage("nolimits"));
		opts2.put("15minutes", I18N.getMessage("15minutes"));
		opts2.put("1hour", I18N.getMessage("1hour"));
		opts2.put("5hours", I18N.getMessage("5hours"));
		maxLengths.setValueMap(opts2);
		maxLengths.setName("maxLengths");
		maxLengths.setTitle(I18N.getMessage("maxlengths"));
		maxLengths.setDefaultValue("nolimits");

		// Scheduling Idle
		IntegerItem schedulingIdle = new IntegerItem();
		schedulingIdle.setName("schedulingIdle");
		schedulingIdle.setTitle(I18N.getMessage("schedulingidle"));
		schedulingIdle.setDefaultValue(-1);

		// Initial delay
		FloatItem initialDelay = new FloatItem();
		initialDelay.setName("initialDelay");
		initialDelay.setTitle(I18N.getMessage("initialdelay"));
		initialDelay.setDefaultValue(1800);
		initialDelay.setVisible(simplePolicy);

		// Repeat interval
		FloatItem repeatInterval = new FloatItem();
		repeatInterval.setName("repeatInterval");
		repeatInterval.setTitle(I18N.getMessage("repeatinterval"));
		repeatInterval.setDefaultValue(1800);
		repeatInterval.setVisible(simplePolicy);

		// Seconds
		TextItem seconds = new TextItem();
		seconds.setName("seconds");
		seconds.setTitle(I18N.getMessage("seconds"));
		seconds.setDefaultValue("00");
		seconds.setVisible(!simplePolicy);

		// Minutes
		TextItem minutes = new TextItem();
		minutes.setName("minutes");
		minutes.setTitle(I18N.getMessage("minutes"));
		minutes.setDefaultValue("00");
		minutes.setVisible(!simplePolicy);

		// Hours
		TextItem hours = new TextItem();
		hours.setName("hours");
		hours.setTitle(I18N.getMessage("hours"));
		hours.setDefaultValue("4/4");
		hours.setVisible(!simplePolicy);

		// Day of month
		TextItem dayMonth = new TextItem();
		dayMonth.setName("dayMonth");
		dayMonth.setTitle(I18N.getMessage("daymonth"));
		dayMonth.setDefaultValue("*");
		dayMonth.setVisible(!simplePolicy);

		// Month
		TextItem month = new TextItem();
		month.setName("month");
		month.setTitle(I18N.getMessage("month"));
		month.setDefaultValue("*");
		month.setVisible(!simplePolicy);

		// Day of week
		TextItem dayWeek = new TextItem();
		dayWeek.setName("dayWeek");
		dayWeek.setTitle(I18N.getMessage("dayweek"));
		dayWeek.setDefaultValue("?");
		dayWeek.setVisible(!simplePolicy);

		form.setItems(policy, initialDelay, repeatInterval, seconds, minutes, hours, dayMonth, month, dayWeek,
				maxLengths, schedulingIdle);

		setMembers(form);

		return form;
	}
}
