package com.logicaldoc.gui.frontend.client.system;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIHistory;
import com.logicaldoc.gui.common.client.formatters.DateCellFormatter;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.frontend.client.services.SystemService;
import com.logicaldoc.gui.frontend.client.services.SystemServiceAsync;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.events.DoubleClickEvent;
import com.smartgwt.client.widgets.events.DoubleClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.DateItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HStack;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * This panel is used to show the last changes events.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class LastChangesPanel extends VLayout {

	private SystemServiceAsync service = (SystemServiceAsync) GWT.create(SystemService.class);

	private Layout search = new VLayout();

	private Layout results = new VLayout();

	private VLayout lastchanges = new VLayout();

	private ValuesManager vm = new ValuesManager();

	private ListGrid histories;

	public LastChangesPanel() {
		setWidth100();

		HStack formsLayout = new HStack(10);

		DynamicForm form = new DynamicForm();
		form.setValuesManager(vm);
		form.setAlign(Alignment.LEFT);
		form.setTitleOrientation(TitleOrientation.LEFT);
		form.setNumCols(4);
		form.setWrapItemTitles(false);

		// Username
		TextItem user = ItemFactory.newTextItem("user", "user", null);
		user.setColSpan(4);

		// From
		DateItem fromDate = ItemFactory.newDateItem("fromDate", "from");

		// To
		DateItem tillDate = ItemFactory.newDateItem("tillDate", "till");

		// Session ID
		TextItem sessionId = ItemFactory.newTextItem("sid", "sid", null);
		sessionId.setColSpan(4);

		// Max results
		TextItem displayMax = ItemFactory.newTextItem("displayMax", "displaymax", null);
		displayMax.setDefaultValue(100);
		displayMax.setWidth(40);
		displayMax.setHint(I18N.message("elements"));

		ButtonItem searchButton = new ButtonItem();
		searchButton.setTitle(I18N.message("search"));
		searchButton.setEndRow(false);
		searchButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onSearch();
			}
		});

		ButtonItem resetButton = new ButtonItem();
		resetButton.setTitle(I18N.message("reset"));
		resetButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				vm.clearValues();
			}
		});

		form.setItems(user, sessionId, fromDate, tillDate, displayMax, searchButton, resetButton);

		DynamicForm eventForm = new DynamicForm();
		eventForm.setValuesManager(vm);
		eventForm.setAlign(Alignment.LEFT);
		eventForm.setTitleOrientation(TitleOrientation.LEFT);
		eventForm.setNumCols(2);
		eventForm.setColWidths(1, "*");

		// Event
		SelectItem event = ItemFactory.newEventsSelector("event", I18N.message("event"));
		event.setColSpan(2);
		event.setEndRow(true);

		eventForm.setItems(event);

		formsLayout.addMember(form);
		formsLayout.addMember(eventForm);
		formsLayout.setMembersMargin(80);

		search.setMembersMargin(10);
		search.setMembers(formsLayout);
		search.setHeight("30%");
		search.setShowResizeBar(true);
		search.setWidth100();
		search.setMargin(10);

		ListGridField eventField = new ListGridField("event", I18N.message("event"), 200);
		eventField.setCanFilter(true);

		ListGridField date = new ListGridField("date", I18N.message("date"), 110);
		date.setType(ListGridFieldType.DATE);
		date.setCellFormatter(new DateCellFormatter());
		date.setAlign(Alignment.CENTER);
		date.setCanFilter(false);

		ListGridField userField = new ListGridField("user", I18N.message("user"), 100);
		userField.setCanFilter(true);
		userField.setAlign(Alignment.CENTER);

		ListGridField name = new ListGridField("name", I18N.message("name"), 100);
		name.setCanFilter(true);

		ListGridField folder = new ListGridField("folder", I18N.message("folder"), 100);
		folder.setCanFilter(true);

		ListGridField sid = new ListGridField("sid", I18N.message("sid"), 250);
		sid.setCanFilter(true);
		sid.setAlign(Alignment.CENTER);

		histories = new ListGrid();
		histories.setWidth100();
		histories.setHeight100();
		histories.setFields(eventField, date, userField, name, folder, sid);
		histories.setSelectionType(SelectionStyle.SINGLE);
		histories.setShowRecordComponents(true);
		histories.setShowRecordComponentsByCell(true);
		histories.setCanFreezeFields(true);
		histories.setFilterOnKeypress(true);

		results.addMember(histories);

		lastchanges.addMember(search);
		lastchanges.addMember(results);

		addMember(lastchanges);

		histories.addDoubleClickHandler(new DoubleClickHandler() {
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				vm.setValue("sid", histories.getSelectedRecord().getAttributeAsString("sid"));
			}
		});
	}

	/**
	 * Gets the option items for Messageshistory events types
	 */
	public SelectItem[] getEventTypes() {
		List<SelectItem> items = new ArrayList<SelectItem>();

		return items.toArray(new SelectItem[0]);
	}

	private void onSearch() {
		histories.setData(new ListGridRecord[0]);
		final Map<String, Object> values = vm.getValues();

		if (vm.validate()) {
			String[] eventValues = new String[0];
			if (values.get("event") != null)
				eventValues = values.get("event").toString().trim().toLowerCase().split(",");
			String userValue = null;
			if ((values.get("user") != null))
				userValue = (String) values.get("user");
			Date fromValue = null;
			if (values.get("fromDate") != null)
				fromValue = (Date) values.get("fromDate");
			Date tillValue = null;
			if (values.get("tillDate") != null)
				tillValue = (Date) values.get("tillDate");

			String sid = null;
			if (values.get("sid") != null)
				sid = (String) values.get("sid");

			int displayMaxValue = 0;
			if (values.get("displayMax") instanceof Integer)
				displayMaxValue = (Integer) values.get("displayMax");
			else
				displayMaxValue = Integer.parseInt((String) values.get("displayMax"));

			service.search(Session.get().getSid(), userValue, fromValue, tillValue, displayMaxValue, sid, eventValues,
					new AsyncCallback<GUIHistory[]>() {

						@Override
						public void onFailure(Throwable caught) {
							Log.serverError(caught);
						}

						@Override
						public void onSuccess(GUIHistory[] result) {
							if (result != null && result.length > 0) {
								ListGridRecord[] records = new ListGridRecord[result.length];
								for (int i = 0; i < result.length; i++) {
									ListGridRecord record = new ListGridRecord();
									record.setAttribute("event", I18N.message(result[i].getEvent()));
									record.setAttribute("date", result[i].getDate());
									record.setAttribute("user", result[i].getUserName());
									record.setAttribute("name", result[i].getTitle());
									record.setAttribute("name", result[i].getTitle());
									record.setAttribute("folder", result[i].getPath());
									record.setAttribute("sid", result[i].getSessionId());
									records[i] = record;
								}
								histories.setData(records);
							}
						}
					});
		}
	}
}