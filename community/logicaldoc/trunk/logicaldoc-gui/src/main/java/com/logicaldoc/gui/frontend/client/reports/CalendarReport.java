package com.logicaldoc.gui.frontend.client.reports;

import java.util.Date;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUICalendarEvent;
import com.logicaldoc.gui.common.client.beans.GUIUser;
import com.logicaldoc.gui.common.client.formatters.DateCellFormatter;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.common.client.widgets.InfoPanel;
import com.logicaldoc.gui.frontend.client.services.CalendarService;
import com.logicaldoc.gui.frontend.client.services.CalendarServiceAsync;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ExpansionMode;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.DateItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.grid.CellFormatter;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HStack;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

/**
 * This panel is used to show a report of a selection of events.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.7
 */
public class CalendarReport extends VLayout {

	private CalendarServiceAsync service = (CalendarServiceAsync) GWT.create(CalendarService.class);

	private Layout search = new VLayout();

	private Layout results = new VLayout();

	private VLayout layout = new VLayout();

	private ValuesManager vm = new ValuesManager();

	private ListGrid list;

	private InfoPanel infoPanel;

	public CalendarReport() {
		setWidth100();

		HStack formsLayout = new HStack(10);

		DynamicForm form = new DynamicForm();
		form.setValuesManager(vm);
		form.setAlign(Alignment.LEFT);
		form.setTitleOrientation(TitleOrientation.LEFT);
		form.setNumCols(4);
		form.setWrapItemTitles(false);

		// From
		DateItem fromDate = ItemFactory.newDateItem("fromDate", "from");

		// To
		DateItem toDate = ItemFactory.newDateItem("toDate", "till");

		// Expire From
		DateItem expireFrom = ItemFactory.newDateItem("expireFrom", "expiresfrom");

		// Expire To
		DateItem expireTo = ItemFactory.newDateItem("expireTo", "till");

		SelectItem frequencySelector = ItemFactory.newFrequencySelector("frequency", "frequency");

		TextItem title = ItemFactory.newTextItem("title", "title", null);

		TextItem type = ItemFactory.newTextItem("type", "type", null);

		SelectItem statusSelector = ItemFactory.newEventStatusSelector("status", "status");

		// Max results
		TextItem displayMax = ItemFactory.newTextItem("displayMax", "displaymax", null);
		displayMax.setValue(100);
		displayMax.setDefaultValue(100);
		displayMax.setWidth(40);
		displayMax.setHint(I18N.message("elements"));

		ButtonItem searchButton = new ButtonItem();
		searchButton.setTitle(I18N.message("search"));
		searchButton.setAutoFit(true);
		searchButton.setEndRow(true);
		searchButton.setColSpan(2);
		searchButton.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {
			@Override
			public void onClick(com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				onSearch();
			}
		});

		form.setItems(fromDate, toDate, expireFrom, expireTo, title, type, frequencySelector, statusSelector,
				displayMax, searchButton);

		formsLayout.addMember(form);
		formsLayout.setMembersMargin(80);

		search.setMembersMargin(10);
		search.setMembers(formsLayout);
		search.setHeight("30%");
		search.setShowResizeBar(true);
		search.setWidth100();
		search.setMargin(10);

		ListGridField titleCol = new ListGridField("title", I18N.message("title"));
		titleCol.setWidth("*");
		titleCol.setCanFilter(true);

		ListGridField typeCol = new ListGridField("type", I18N.message("type"));
		typeCol.setWidth(100);
		typeCol.setCanFilter(true);

		ListGridField date = new ListGridField("date", I18N.message("date"), 110);
		date.setType(ListGridFieldType.DATE);
		date.setCellFormatter(new DateCellFormatter(false));
		date.setAlign(Alignment.CENTER);
		date.setCanFilter(false);

		ListGridField expiration = new ListGridField("expiration", I18N.message("expiration"), 110);
		expiration.setType(ListGridFieldType.DATE);
		expiration.setCellFormatter(new DateCellFormatter(false));
		expiration.setAlign(Alignment.CENTER);
		expiration.setCanFilter(false);

		ListGridField frequency = new ListGridField("frequency", I18N.message("frequency"), 90);
		frequency.setType(ListGridFieldType.INTEGER);
		frequency.setAlign(Alignment.CENTER);
		frequency.setCanFilter(false);
		frequency.setCellFormatter(new CellFormatter() {

			@Override
			public String format(Object value, ListGridRecord record, int rowNum, int colNum) {
				String v = value.toString();

				if ("1".equals(v)) {
					return I18N.message("daily");
				} else if ("7".equals(v)) {
					return I18N.message("weekly");
				} else if ("15".equals(v)) {
					return I18N.message("biweekly");
				} else if ("30".equals(v)) {
					return I18N.message("monthly");
				} else if ("180".equals(v)) {
					return I18N.message("sixmonthly");
				} else if ("365".equals(v)) {
					return I18N.message("yearly");
				}

				return null;
			}
		});

		ListGridField status = new ListGridField("status", I18N.message("status"), 90);
		status.setType(ListGridFieldType.INTEGER);
		status.setAlign(Alignment.CENTER);
		status.setCanFilter(false);
		status.setCellFormatter(new CellFormatter() {

			@Override
			public String format(Object value, ListGridRecord record, int rowNum, int colNum) {
				String v = value.toString();

				if ("1".equals(v)) {
					return I18N.message("working");
				} else if ("2".equals(v)) {
					return I18N.message("completed");
				} else if ("3".equals(v)) {
					return I18N.message("canceled");
				}

				return null;
			}
		});

		ListGridField description = new ListGridField("description", I18N.message("description"));
		description.setWidth(400);
		description.setHidden(true);

		ListGridField participants = new ListGridField("participants", I18N.message("participants"));
		participants.setWidth(300);

		list = new ListGrid();
		list.setEmptyMessage(I18N.message("notitemstoshow"));
		list.setWidth100();
		list.setHeight100();
		list.setSelectionType(SelectionStyle.SINGLE);
		list.setShowRecordComponents(true);
		list.setShowRecordComponentsByCell(true);
		list.setCanFreezeFields(true);
		list.setFilterOnKeypress(true);
		list.setAutoFetchData(true);
		list.setFields(date, expiration, titleCol, typeCol, status, frequency, participants, description);
		list.setDetailField("description");
		list.setCanExpandRecords(true);
		list.setExpansionMode(ExpansionMode.DETAIL_FIELD);

		results.addMember(list);
		layout.addMember(search, 0);

		ToolStrip toolStrip = new ToolStrip();
		toolStrip.setHeight(20);
		toolStrip.setWidth100();

		ToolStripButton print = new ToolStripButton();
		print.setIcon(ItemFactory.newImgIcon("printer.png").getSrc());
		print.setTooltip(I18N.message("print"));
		print.setAutoFit(true);
		print.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				Canvas.printComponents(new Object[] { list });
			}
		});
		toolStrip.addButton(print);

		if (Feature.visible(Feature.EXPORT_CSV)) {
			toolStrip.addSeparator();
			ToolStripButton export = new ToolStripButton();
			export.setIcon(ItemFactory.newImgIcon("table_row_insert.png").getSrc());
			export.setTooltip(I18N.message("export"));
			export.setAutoFit(true);
			toolStrip.addButton(export);
			export.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
					Util.exportCSV(list, true);
				}
			});
			if (!Feature.enabled(Feature.EXPORT_CSV)) {
				export.setDisabled(true);
				export.setTooltip(I18N.message("featuredisabled"));
			}
		}

		toolStrip.addFill();
		layout.addMember(toolStrip, 1);

		// Prepare a panel containing the events list
		infoPanel = new InfoPanel("");
		layout.addMember(infoPanel, 2);

		layout.addMember(results, 3);

		addMember(layout);
	}

	@SuppressWarnings("unchecked")
	private void onSearch() {
		list.setData(new ListGridRecord[0]);

		final Map<String, Object> values = (Map<String, Object>) vm.getValues();

		if (vm.validate()) {
			Date fromValue = null;
			if (values.get("fromDate") != null)
				fromValue = (Date) values.get("fromDate");
			Date toValue = null;
			if (values.get("toDate") != null)
				toValue = (Date) values.get("toDate");
			Date expireFrom = null;
			if (values.get("expireFrom") != null)
				expireFrom = (Date) values.get("expireFrom");
			Date expireTo = null;
			if (values.get("expireTo") != null)
				expireTo = (Date) values.get("expireTo");

			Integer frequencyValue = null;
			if (values.get("frequency") != null)
				frequencyValue = Integer.parseInt(values.get("frequency").toString());

			Integer statusValue = null;
			if (values.get("status") != null)
				statusValue = Integer.parseInt(values.get("status").toString());

			String titleValue = values.get("title") != null ? values.get("title").toString() : null;

			String typeValue = values.get("type") != null ? values.get("type").toString() : null;

			int maxRecords = 0;
			try {
				if (values.get("displayMax") != null) {
					if (values.get("displayMax") instanceof Integer)
						maxRecords = (Integer) values.get("displayMax");
					else
						maxRecords = Integer.parseInt((String) values.get("displayMax"));
				}
			} catch (Throwable t) {

			}

			service.find(Session.get().getSid(), fromValue, toValue, expireFrom, expireTo, frequencyValue, titleValue,
					typeValue, statusValue, maxRecords, new AsyncCallback<GUICalendarEvent[]>() {

						@Override
						public void onFailure(Throwable caught) {
							Log.serverError(caught);
						}

						@Override
						public void onSuccess(GUICalendarEvent[] result) {
							if (result != null && result.length > 0) {
								ListGridRecord[] records = new ListGridRecord[result.length];
								for (int i = 0; i < result.length; i++) {
									ListGridRecord record = new ListGridRecord();
									record.setAttribute("date", result[i].getStartDate());
									record.setAttribute("title", result[i].getTitle());
									record.setAttribute("type", result[i].getType());
									record.setAttribute("frequency", result[i].getFrequency());
									record.setAttribute("status", result[i].getStatus());
									record.setAttribute("description", result[i].getDescription());
									record.setAttribute("expiration", result[i].getExpirationDate());

									StringBuffer participants = new StringBuffer();
									GUIUser[] users = result[i].getParticipants();
									for (GUIUser user : users) {
										if (participants.length() > 0)
											participants.append(", ");
										participants.append(user.toString());
									}
									record.setAttribute("participants", participants.toString());

									records[i] = record;
								}
								list.setData(records);
							}
							layout.removeMember(infoPanel);
							infoPanel = new InfoPanel("");
							infoPanel.setMessage(I18N.message("showelements", Integer.toString(list.getTotalRows())));
							layout.addMember(infoPanel, 2);
						}
					});
		}
	}
}