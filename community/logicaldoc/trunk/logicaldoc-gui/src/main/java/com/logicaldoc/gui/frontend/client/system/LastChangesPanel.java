package com.logicaldoc.gui.frontend.client.system;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIHistory;
import com.logicaldoc.gui.common.client.formatters.DateCellFormatter;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.frontend.client.services.SystemService;
import com.logicaldoc.gui.frontend.client.services.SystemServiceAsync;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.DateItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * This panel is used to show the last changes events.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class LastChangesPanel extends HLayout {

	private SystemServiceAsync service = (SystemServiceAsync) GWT.create(SystemService.class);

	private Layout search = new VLayout();

	private Layout results = new VLayout();

	private static LastChangesPanel instance;

	private VLayout lastchanges = new VLayout();

	private ValuesManager vm = new ValuesManager();

	private ListGrid histories;

	public LastChangesPanel() {
		setWidth100();

		HLayout formsLayout = new HLayout(10);

		DynamicForm form = new DynamicForm();
		form.setValuesManager(vm);
		form.setAlign(Alignment.LEFT);
		form.setTitleOrientation(TitleOrientation.LEFT);
		form.setNumCols(2);
		form.setColWidths(1, "*");

		// Username
		TextItem username = new TextItem();
		username.setName("username");
		username.setTitle(I18N.getMessage("username"));

		// From
		DateItem fromDate = ItemFactory.newDateItem("fromDate", I18N.getMessage("from"));

		// To
		DateItem tillDate = ItemFactory.newDateItem("tillDate", I18N.getMessage("till"));

		// Session ID
		TextItem sessionId = new TextItem();
		sessionId.setName("sessionId");
		sessionId.setTitle(I18N.getMessage("sid"));

		// Max results
		TextItem displayMax = new TextItem();
		displayMax.setName("displayMax");
		displayMax.setDefaultValue(100);
		displayMax.setWidth(40);
		displayMax.setTitle(I18N.getMessage("displaymax"));
		displayMax.setHint(I18N.getMessage("elements"));

		form.setWrapItemTitles(false);
		form.setItems(username, fromDate, tillDate, sessionId, displayMax);

		DynamicForm eventForm = new DynamicForm();
		eventForm.setValuesManager(vm);
		eventForm.setAlign(Alignment.LEFT);
		eventForm.setTitleOrientation(TitleOrientation.LEFT);
		eventForm.setNumCols(2);
		eventForm.setColWidths(1, "*");

		// Event
		SelectItem event = ItemFactory.newMultipleSelector("event", I18N.getMessage("event"));
		event.setColSpan(2);
		event.setWidth(300);
		event.setHeight(150);
		event.setEndRow(true);
		event.setValueMap(getEventsValueMap());

		eventForm.setItems(event);

		formsLayout.addMember(form);
		formsLayout.addMember(eventForm);
		formsLayout.setMembersMargin(80);

		HLayout buttons = new HLayout(10);
		buttons.setHeight(22);

		IButton searchButton = new IButton();
		searchButton.setTitle(I18N.getMessage("search"));
		searchButton.setSize("70", "25");
		searchButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				final Map<String, Object> values = vm.getValues();

				if (vm.validate()) {
					String[] eventValues = new String[0];
					if (values.get("event") != null)
						eventValues = values.get("event").toString().trim().toLowerCase().split(",");
					String usernameValue = (String) values.get("username");
					Date fromValue = (Date) values.get("fromDate");
					Date tillValue = (Date) values.get("tillDate");
					String sessionIdValue = (String) values.get("sessionId");
					int displayMaxValue = (Integer) values.get("displayMax");

					service.search(Session.get().getSid(), usernameValue, fromValue, tillValue, displayMaxValue,
							sessionIdValue, eventValues, new AsyncCallback<GUIHistory[]>() {

								@Override
								public void onFailure(Throwable caught) {
									Log.serverError(caught);
								}

								@Override
								public void onSuccess(GUIHistory[] result) {
									ListGridRecord[] records = new ListGridRecord[result.length];
									for (int i = 0; i < result.length; i++) {
										ListGridRecord record = new ListGridRecord();
										record.setAttribute("eventField", result[i].getEvent());
										record.setAttribute("dateField", result[i].getDate());
										record.setAttribute("userField", result[i].getUserName());
										record.setAttribute("nameField", result[i].getTitle());
										record.setAttribute("folderField", result[i].getPath());
										record.setAttribute("sidField", result[i].getSessionId());

										records[i] = record;
									}

									histories.setData(records);
								}

							});

				}
			}
		});

		IButton resetButton = new IButton();
		resetButton.setTitle(I18N.getMessage("reset"));
		resetButton.setSize("70", "25");
		resetButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				vm.clearValues();
			}
		});

		buttons.addMember(searchButton);
		buttons.addMember(resetButton);

		search.setMembersMargin(10);
		search.setMembers(formsLayout, buttons);
		search.setHeight("30%");
		search.setShowResizeBar(true);
		search.setWidth100();
		search.setMargin(10);

		ListGridField eventField = new ListGridField("eventField", I18N.getMessage("event"), 200);
		eventField.setCanFilter(true);

		ListGridField dateField = new ListGridField("dateField", I18N.getMessage("date"), 110);
		dateField.setType(ListGridFieldType.DATE);
		dateField.setCellFormatter(new DateCellFormatter());
		dateField.setCanFilter(false);
		dateField.setAlign(Alignment.CENTER);

		ListGridField userField = new ListGridField("userField", I18N.getMessage("user"), 100);
		userField.setCanFilter(true);
		userField.setAlign(Alignment.CENTER);

		ListGridField nameField = new ListGridField("nameField", I18N.getMessage("name"), 100);
		nameField.setCanFilter(true);

		ListGridField folderField = new ListGridField("folderField", I18N.getMessage("folder"), 100);
		folderField.setCanFilter(true);

		ListGridField sidField = new ListGridField("sidField", I18N.getMessage("sid"), 250);
		sidField.setCanFilter(true);
		sidField.setAlign(Alignment.CENTER);

		histories = new ListGrid();
		histories.setWidth100();
		histories.setHeight100();
		histories.setFields(eventField, dateField, userField, nameField, folderField, sidField);
		histories.setSelectionType(SelectionStyle.SINGLE);
		histories.setShowRecordComponents(true);
		histories.setShowRecordComponentsByCell(true);
		histories.setCanFreezeFields(true);
		histories.setAutoFetchData(true);
		histories.setFilterOnKeypress(true);

		results.addMember(histories);

		lastchanges.addMember(search);
		lastchanges.addMember(results);

		addMember(lastchanges);

		setShowEdges(true);
	}

	/**
	 * Creates the value map with all history events.
	 */
	private LinkedHashMap<String, String> getEventsValueMap() {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		// Document and folder events
		map.put("event.archived", I18N.getMessage("event.archived"));
		map.put("event.changed", I18N.getMessage("event.changed"));
		map.put("event.checkedin", I18N.getMessage("event.checkedin"));
		map.put("event.checkedout", I18N.getMessage("event.checkedout"));
		map.put("event.deleted", I18N.getMessage("event.deleted"));
		map.put("event.downloaded", I18N.getMessage("event.downloaded"));
		map.put("event.folder.created", I18N.getMessage("event.folder.created"));
		map.put("event.folder.deleted", I18N.getMessage("event.folder.deleted"));
		map.put("event.folder.permission", I18N.getMessage("event.folder.permission"));
		map.put("event.folder.renamed", I18N.getMessage("event.folder.renamed"));
		map.put("event.folder.subfolder.created", I18N.getMessage("event.folder.subfolder.created"));
		map.put("event.folder.subfolder.deleted", I18N.getMessage("event.folder.subfolder.deleted"));
		map.put("event.folder.subfolder.permission", I18N.getMessage("event.folder.subfolder.permission"));
		map.put("event.folder.subfolder.renamed", I18N.getMessage("event.folder.subfolder.renamed"));
		map.put("event.makeimmutable", I18N.getMessage("event.makeimmutable"));
		map.put("event.locked", I18N.getMessage("event.locked"));
		map.put("event.moved", I18N.getMessage("event.moved"));
		map.put("event.renamed", I18N.getMessage("event.renamed"));
		map.put("event.stored", I18N.getMessage("event.stored"));
		map.put("event.unlocked", I18N.getMessage("event.unlocked"));
		// User events
		map.put("event.user.deleted", I18N.getMessage("event.user.deleted"));
		map.put("event.user.login", I18N.getMessage("event.user.login"));
		map.put("event.user.logout", I18N.getMessage("event.user.logout"));
		map.put("event.user.passwordchanged", I18N.getMessage("event.user.passwordchanged"));
		// Workflow events
		map.put("event.workflow.start", I18N.getMessage("event.workflow.start"));
		map.put("event.workflow.end", I18N.getMessage("event.workflow.end"));
		map.put("event.workflow.task.start", I18N.getMessage("event.workflow.task.start"));
		map.put("event.workflow.task.end", I18N.getMessage("event.workflow.task.end"));
		map.put("event.workflow.task.suspended", I18N.getMessage("event.workflow.task.suspended"));
		map.put("event.workflow.task.resumed", I18N.getMessage("event.workflow.task.resumed"));
		map.put("event.workflow.task.reassigned", I18N.getMessage("event.workflow.task.reassigned"));
		map.put("event.workflow.docappended", I18N.getMessage("event.workflow.docappended"));
		return map;
	}

	/**
	 * Gets the option items for Messageshistory events types
	 */
	public SelectItem[] getEventTypes() {
		List<SelectItem> items = new ArrayList<SelectItem>();

		return items.toArray(new SelectItem[0]);
	}
}