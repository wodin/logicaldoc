package com.logicaldoc.gui.frontend.client.calendar;

import java.util.Date;
import java.util.LinkedHashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUICalendarEvent;
import com.logicaldoc.gui.common.client.beans.GUIValuePair;
import com.logicaldoc.gui.common.client.formatters.DateCellFormatter;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.common.client.widgets.PreviewPopup;
import com.logicaldoc.gui.frontend.client.document.DocumentsPanel;
import com.logicaldoc.gui.frontend.client.services.CalendarService;
import com.logicaldoc.gui.frontend.client.services.CalendarServiceAsync;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.DateItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.SpinnerItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.TimeItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;

/**
 * This is the form used for editing a calendar event.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.7
 */
public class CalendarEventDialog extends Window {

	protected CalendarServiceAsync service = (CalendarServiceAsync) GWT.create(CalendarService.class);

	private ValuesManager vm = new ValuesManager();

	private GUICalendarEvent calendarEvent;

	private TabSet tabs = new TabSet();

	private DynamicForm detailsForm = new DynamicForm();

	public CalendarEventDialog(GUICalendarEvent event) {
		this.calendarEvent = event;
		
		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		if (event.getId() != 0)
			setTitle(I18N.message("editevent") + " - " + event.getTitle());
		else
			setTitle(I18N.message("newevent"));
		setWidth(440);
		setHeight(400);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();
		setPadding(5);

		Tab detailsTab = prepareDetailsTab(event);
		Tab participantsTab = prepareParticipants(event);
		Tab documentsTab = prepareDocuments(event);

		tabs.setTabs(detailsTab, participantsTab, documentsTab);
		tabs.setHeight100();
		addItem(tabs);

		IButton save = new IButton();
		save.setMargin(3);
		save.setHeight(30);
		save.setTitle(I18N.message("save"));
		save.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (vm.validate()) {
					calendarEvent.setTitle(vm.getValueAsString("title"));
					calendarEvent.setDescription(vm.getValueAsString("description"));
					calendarEvent.setRemindTime(Integer.parseInt(vm.getValueAsString("remindTime")));
					calendarEvent.setRemindUnit(vm.getValueAsString("remindUnit"));

					if (vm.getValue("recurrency") != null)
						calendarEvent.setRecurrency(Integer.parseInt(vm.getValueAsString("recurrency")));

					DateTimeFormat dfDate = DateTimeFormat.getFormat("yyyy-MM-dd");
					DateTimeFormat dfTime = DateTimeFormat.getFormat("HH:mm");
					DateTimeFormat df = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm");

					String str = dfDate.format((Date) vm.getValue("startDate"));
					if (vm.getValue("startTime") != null)
						try {
							str = str + " " + dfTime.format((Date) vm.getValue("startTime"));
						} catch (Throwable t) {
						}
					calendarEvent.setStartDate(df.parse(str));

					if (vm.getValue("expirationDate") != null) {
						str = dfDate.format((Date) vm.getValue("expirationDate"));
						if (vm.getValue("expirationDate") != null)
							try {
								str = str + " " + dfTime.format((Date) vm.getValue("expirationDate"));
							} catch (Throwable t) {
							}
						calendarEvent.setExpirationDate(df.parse(str));
					}

					if (calendarEvent.getExpirationDate() != null
							&& calendarEvent.getExpirationDate().before(calendarEvent.getStartDate())) {
						SC.warn(I18N.message("endbeforestart"));
						return;
					}

					service.saveEvent(Session.get().getSid(), CalendarEventDialog.this.calendarEvent,
							new AsyncCallback<Void>() {
								@Override
								public void onFailure(Throwable caught) {
									Log.serverError(caught);
								}

								@Override
								public void onSuccess(Void arg) {
									destroy();
									CalendarDashboard.get().refresh();
								}
							});
				}
			}
		});
		addItem(save);
	}

	private Tab prepareParticipants(GUICalendarEvent event) {
		ListGridField id = new ListGridField("entityId");
		id.setHidden(true);
		ListGridField entity = new ListGridField("entity", I18N.message("name"));
		entity.setWidth("*");
		final ListGrid list = new ListGrid();
		list.setShowHeader(false);
		list.setHeight100();
		list.setWidth100();
		list.setFields(id, entity);

		ListGridRecord[] records = new ListGridRecord[event.getParticipants().length];
		for (int i = 0; i < event.getParticipants().length; i++) {
			records[i] = new ListGridRecord();
			records[i].setAttribute("entityId", event.getParticipants()[i].getCode());
			records[i].setAttribute("entity", event.getParticipants()[i].getValue());
		}
		list.setRecords(records);

		Menu contextMenu = new Menu();
		MenuItem deleteItem = new MenuItem();
		deleteItem.setTitle(I18N.message("ddelete"));
		deleteItem.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				ListGridRecord[] selection = list.getSelectedRecords();
				if (selection == null || selection.length == 0)
					return;
				for (ListGridRecord rec : selection) {
					CalendarEventDialog.this.calendarEvent.removeParticipant(rec.getAttribute("entityId"));
				}

				list.removeSelectedData();
			}
		});
		contextMenu.setItems(deleteItem);
		list.setContextMenu(contextMenu);

		DynamicForm form = new DynamicForm();
		form.setTitleOrientation(TitleOrientation.LEFT);
		final SelectItem newUser = ItemFactory.newUserSelector("user", "adduser", null);
		newUser.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				ListGridRecord selectedRecord = newUser.getSelectedRecord();
				if (selectedRecord == null)
					return;

				// Check if the selected user is already present in the list
				ListGridRecord[] records = list.getRecords();
				for (ListGridRecord test : records) {
					if (test.getAttribute("entityId").equals(selectedRecord.getAttribute("id"))) {
						newUser.clearValue();
						return;
					}
				}

				// Update the table
				ListGridRecord record = new ListGridRecord();
				String id = selectedRecord.getAttribute("id");
				String entity = selectedRecord.getAttribute("label");
				record.setAttribute("entityId", id);
				record.setAttribute("entity", entity);
				list.addData(record);
				CalendarEventDialog.this.calendarEvent.addParticipant(new GUIValuePair(id, entity));
				newUser.clearValue();
			}
		});
		form.setItems(newUser);

		Tab participantsTab = new Tab();
		participantsTab.setTitle(I18N.message("participants"));
		VLayout layout = new VLayout();
		layout.setWidth100();
		layout.setHeight100();
		layout.setMembers(list, form);
		participantsTab.setPane(layout);
		return participantsTab;
	}

	private Tab prepareDocuments(GUICalendarEvent event) {
		ListGridField title = new ListGridField("title", I18N.message("name"));
		title.setWidth("*");
		ListGridField lastModified = new ListGridField("lastModified", I18N.message("lastmodified"), 150);
		lastModified.setAlign(Alignment.CENTER);
		lastModified.setType(ListGridFieldType.DATE);
		lastModified.setCellFormatter(new DateCellFormatter(false));
		lastModified.setCanFilter(false);
		ListGridField icon = new ListGridField("icon", " ", 24);
		icon.setType(ListGridFieldType.IMAGE);
		icon.setCanSort(false);
		icon.setAlign(Alignment.CENTER);
		icon.setShowDefaultContextMenu(false);
		icon.setImageURLPrefix(Util.imagePrefix());
		icon.setImageURLSuffix(".png");
		icon.setCanFilter(false);

		final ListGrid list = new ListGrid();
		list.setEmptyMessage(I18N.message("notitemstoshow"));
		list.setWidth100();
		list.setHeight100();
		list.setCanFreezeFields(true);
		list.setAutoFetchData(true);
		list.setShowHeader(true);
		list.setCanSelectAll(false);
		list.setSelectionType(SelectionStyle.SINGLE);
		list.setFields(icon, title, lastModified);
		ListGridRecord[] records = new ListGridRecord[event.getParticipants().length];
		for (int i = 0; i < event.getDocuments().length; i++) {
			records[i] = new ListGridRecord();
			records[i].setAttribute("id", event.getDocuments()[i].getId());
			records[i].setAttribute("title", event.getDocuments()[i].getTitle());
			records[i].setAttribute("icon", event.getDocuments()[i].getIcon());
			records[i].setAttribute("version", event.getDocuments()[i].getVersion());
			records[i].setAttribute("fileVersion", event.getDocuments()[i].getFileVersion());
			records[i].setAttribute("fileName", event.getDocuments()[i].getFileName());
			records[i].setAttribute("lastModified", event.getDocuments()[i].getLastModified());
		}
		list.setRecords(records);
		
// Don't give the ability to remove a document at the moment
//		MenuItem deleteItem = new MenuItem();
//		deleteItem.setTitle(I18N.message("ddelete"));
//		deleteItem.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
//			public void onClick(MenuItemClickEvent event) {
//				ListGridRecord[] selection = list.getSelectedRecords();
//				if (selection == null || selection.length == 0)
//					return;
//				for (ListGridRecord rec : selection) {
//					CalendarEventDialog.this.calendarEvent.removeDocument(Long.parseLong(rec.getAttribute("id")));
//				}
//				list.removeSelectedData();
//			}
//		});
		MenuItem preview = new MenuItem();
		preview.setTitle(I18N.message("preview"));
		preview.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				// Detect the two selected records
				ListGridRecord selection = list.getSelectedRecord();

				long id = Long.parseLong(selection.getAttribute("id"));
				String filename = selection.getAttribute("filename");
				String fileVersion = selection.getAttribute("fileVersion");

				if (filename == null)
					filename = selection.getAttribute("title") + "." + selection.getAttribute("type");

				PreviewPopup iv = new PreviewPopup(id, fileVersion, filename, true);
				iv.show();
			}
		});
		Menu contextMenu = new Menu();
		contextMenu.setItems(preview);
		list.setContextMenu(contextMenu);

		list.addCellDoubleClickHandler(new CellDoubleClickHandler() {
			@Override
			public void onCellDoubleClick(CellDoubleClickEvent event) {
				destroy();
				Record record = event.getRecord();
				DocumentsPanel.get().openInFolder(Long.parseLong(record.getAttributeAsString("folderId")),
						Long.parseLong(record.getAttributeAsString("id")));
			}
		});

		Tab participantsTab = new Tab();
		participantsTab.setTitle(I18N.message("documents"));
		VLayout layout = new VLayout();
		layout.setWidth100();
		layout.setHeight100();
		layout.setMembers(list);
		participantsTab.setPane(layout);
		return participantsTab;
	}

	private Tab prepareDetailsTab(GUICalendarEvent event) {
		Tab details = new Tab();
		details.setTitle(I18N.message("details"));

		detailsForm.setHeight100();
		detailsForm.setTitleOrientation(TitleOrientation.TOP);
		detailsForm.setNumCols(5);
		detailsForm.setValuesManager(vm);

		TextItem title = ItemFactory.newTextItem("title", "title", event.getTitle());
		title.setRequired(true);
		title.setEndRow(true);
		title.setColSpan(5);
		title.setWidth("100%");
		title.setTitleOrientation(TitleOrientation.LEFT);
		title.setLength(255);

		DateItem startDate = ItemFactory.newDateItem("startDate", "begin");
		startDate.setRequired(true);
		startDate.setTitleOrientation(TitleOrientation.LEFT);
		startDate.setValue(event.getStartDate());
		TimeItem startTime = ItemFactory.newTimeItem("startTime", "");
		DateTimeFormat df = DateTimeFormat.getFormat("HH:mm");
		startTime.setValue(df.format(event.getStartDate()));
		startTime.setRequired(true);
		startTime.setTitleOrientation(TitleOrientation.LEFT);
		startTime.setEndRow(true);

		DateItem expirationDate = ItemFactory.newDateItem("expirationDate", "expirationdate");
		expirationDate.setRequired(false);
		expirationDate.setTitleOrientation(TitleOrientation.LEFT);
		if (event.getExpirationDate() != null)
			expirationDate.setValue(event.getExpirationDate());

		TimeItem expirationTime = ItemFactory.newTimeItem("expirationTime", "");
		expirationTime.setTitleOrientation(TitleOrientation.LEFT);
		expirationTime.setEndRow(true);
		if (event.getExpirationDate() != null)
			expirationTime.setValue(df.format(event.getExpirationDate()));

		SelectItem recurrency = ItemFactory.newRecurrencySelector("recurrency", "recurrency");
		recurrency.setEndRow(true);
		recurrency.setTitleOrientation(TitleOrientation.LEFT);
		recurrency.setValue(Integer.toString(event.getRecurrency()));
		recurrency.setColSpan(5);

		SpinnerItem remindTimeNumber = new SpinnerItem("remindTime");
		remindTimeNumber.setTitle(I18N.message("remindtime"));
		remindTimeNumber.setTitleOrientation(TitleOrientation.LEFT);
		remindTimeNumber.setDefaultValue(0);
		remindTimeNumber.setMin(0);
		remindTimeNumber.setStep(1);
		remindTimeNumber.setWidth(50);
		remindTimeNumber.setValue(event.getRemindTime());
		SelectItem remindTimeUnit = ItemFactory.newDueTimeSelector("remindUnit", "");
		remindTimeUnit.setShowTitle(false);
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("minute", I18N.message("minutes"));
		map.put("hour", I18N.message("hours"));
		map.put("day", I18N.message("ddays"));
		remindTimeUnit.setValueMap(map);
		remindTimeUnit.setValue(event.getRemindUnit());
		remindTimeUnit.setColSpan(3);
		remindTimeUnit.setAlign(Alignment.LEFT);

		TextAreaItem description = ItemFactory.newTextAreaItem("description", "description", event.getDescription());
		description.setHeight("100%");
		description.setColSpan(5);

		detailsForm.setFields(title, ItemFactory.newRowSpacer(), startDate, startTime, expirationDate, expirationTime,
				ItemFactory.newRowSpacer(), recurrency, ItemFactory.newRowSpacer(), remindTimeNumber, remindTimeUnit,
				ItemFactory.newRowSpacer(), description);
		details.setPane(detailsForm);
		return details;
	}
}