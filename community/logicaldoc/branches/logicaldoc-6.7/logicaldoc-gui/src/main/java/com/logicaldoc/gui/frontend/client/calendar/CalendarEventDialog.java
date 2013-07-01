package com.logicaldoc.gui.frontend.client.calendar;

import java.util.Date;
import java.util.LinkedHashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUICalendarEvent;
import com.logicaldoc.gui.common.client.beans.GUIUser;
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
 * @author Marco Meschieri - Logical Objects
 * @since 6.7
 */
public class CalendarEventDialog extends Window {

	protected CalendarServiceAsync service = (CalendarServiceAsync) GWT.create(CalendarService.class);

	private ValuesManager vm = new ValuesManager();

	private GUICalendarEvent calendarEvent;

	private TabSet tabs = new TabSet();

	private DynamicForm detailsForm = new DynamicForm();

	private boolean readOnly = false;

	public CalendarEventDialog(GUICalendarEvent calEvent) {
		this.calendarEvent = calEvent;
		readOnly = Session.get().getUser().getId() != calEvent.getCreatorId()
				&& !Session.get().getUser().isMemberOf("admin");

		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		if (calEvent.getId() != 0)
			setTitle(I18N.message("editevent") + " - " + calEvent.getTitle());
		else
			setTitle(I18N.message("newevent"));
		setWidth(510);
		setHeight(450);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();
		setPadding(5);

		Tab detailsTab = prepareDetailsTab(calEvent);
		Tab participantsTab = prepareParticipants(calEvent);
		Tab documentsTab = prepareDocuments(calEvent);

		tabs.setTabs(detailsTab, participantsTab, documentsTab);
		tabs.setHeight100();
		addItem(tabs);

		IButton save = new IButton();
		save.setMargin(3);
		save.setHeight(30);
		save.setTitle(I18N.message("save"));
		save.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				onSave();
			}
		});
		if (!readOnly)
			addItem(save);
	}

	private Tab prepareParticipants(GUICalendarEvent event) {
		ListGridField id = new ListGridField("id");
		id.setHidden(true);
		ListGridField name = new ListGridField("name", I18N.message("name"));
		name.setWidth("*");
		ListGridField username = new ListGridField("username", I18N.message("username"));
		username.setWidth(110);

		final ListGrid list = new ListGrid();
		list.setHeight100();
		list.setWidth100();
		list.setFields(id, username, name);

		ListGridRecord[] records = new ListGridRecord[event.getParticipants().length];
		for (int i = 0; i < event.getParticipants().length; i++) {
			records[i] = new ListGridRecord();
			records[i].setAttribute("id", event.getParticipants()[i].getId());
			records[i].setAttribute("name", event.getParticipants()[i].getFullName());
			records[i].setAttribute("username", event.getParticipants()[i].getUserName());
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
					CalendarEventDialog.this.calendarEvent.removeParticipant(rec.getAttributeAsLong("id"));
				}

				list.removeSelectedData();
			}
		});
		if (!readOnly) {
			contextMenu.setItems(deleteItem);
			list.setContextMenu(contextMenu);
		}

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
					if (test.getAttribute("id").equals(selectedRecord.getAttribute("id"))) {
						newUser.clearValue();
						return;
					}
				}

				// Update the table
				ListGridRecord record = new ListGridRecord();
				String id = selectedRecord.getAttribute("id");
				String label = selectedRecord.getAttribute("label");
				String username = selectedRecord.getAttribute("username");
				record.setAttribute("id", id);
				record.setAttribute("name", label);
				record.setAttribute("username", username);
				list.addData(record);

				GUIUser user = new GUIUser();
				user.setId(Long.parseLong(id));
				user.setUserName(username);
				user.setFirstName(label);
				CalendarEventDialog.this.calendarEvent.addParticipant(user);
				newUser.clearValue();
			}
		});
		form.setItems(newUser);

		Tab participantsTab = new Tab();
		participantsTab.setTitle(I18N.message("participants"));
		VLayout layout = new VLayout();
		layout.setWidth100();
		layout.setHeight100();

		if (readOnly)
			layout.setMembers(list);
		else
			layout.setMembers(list, form);
		participantsTab.setPane(layout);
		return participantsTab;
	}

	private Tab prepareDocuments(GUICalendarEvent event) {
		ListGridField title = new ListGridField("title", I18N.message("name"));
		title.setWidth("90%");
		title.setCanEdit(!readOnly);

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

				PreviewPopup iv = new PreviewPopup(id, fileVersion, filename, false);
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

		Tab documentsTab = new Tab();
		documentsTab.setTitle(I18N.message("documents"));
		VLayout layout = new VLayout();
		layout.setWidth100();
		layout.setHeight100();
		layout.setMembers(list);
		documentsTab.setPane(layout);
		return documentsTab;
	}

	private Tab prepareDetailsTab(GUICalendarEvent event) {
		Tab details = new Tab();
		details.setTitle(I18N.message("details"));

		detailsForm.setHeight100();
		detailsForm.setTitleOrientation(TitleOrientation.TOP);
		detailsForm.setNumCols(6);
		detailsForm.setValuesManager(vm);

		TextItem title = ItemFactory.newTextItem("title", "title", event.getTitle());
		title.setRequired(true);
		title.setEndRow(true);
		title.setColSpan(5);
		title.setWidth("100%");
		title.setTitleOrientation(TitleOrientation.LEFT);
		title.setLength(255);
		title.setCanEdit(!readOnly);

		TextItem type = ItemFactory.newTextItem("type", "type", event.getType());
		type.setRequired(false);
		type.setEndRow(true);
		type.setColSpan(5);
		type.setWidth("100%");
		type.setTitleOrientation(TitleOrientation.LEFT);
		type.setLength(255);
		type.setCanEdit(!readOnly);

		DateItem startDate = ItemFactory.newDateItem("startDate", "begin");
		startDate.setRequired(true);
		startDate.setTitleOrientation(TitleOrientation.LEFT);
		startDate.setWrapTitle(false);
		startDate.setValue(event.getStartDate());
		startDate.setCanEdit(!readOnly);
		TimeItem startTime = ItemFactory.newTimeItem("startTime", "   ");
		DateTimeFormat df = DateTimeFormat.getFormat("HH:mm");
		startTime.setValue(df.format(event.getStartDate()));
		startTime.setRequired(true);
		startTime.setShowTitle(false);
		startTime.setTitleOrientation(TitleOrientation.LEFT);
		startTime.setEndRow(true);
		startTime.setCanEdit(!readOnly);
		startTime.setTitleColSpan(1);

		DateItem expirationDate = ItemFactory.newDateItem("expirationDate", "expirationdate");
		expirationDate.setRequired(false);
		expirationDate.setTitleOrientation(TitleOrientation.LEFT);
		expirationDate.setWrapTitle(false);
		expirationDate.setCanEdit(!readOnly);
		if (event.getExpirationDate() != null)
			expirationDate.setValue(event.getExpirationDate());
		TimeItem expirationTime = ItemFactory.newTimeItem("expirationTime", "   ");
		expirationTime.setTitleOrientation(TitleOrientation.LEFT);
		expirationTime.setShowTitle(false);
		expirationTime.setEndRow(true);
		expirationTime.setCanEdit(!readOnly);
		expirationTime.setTitleColSpan(1);
		if (event.getExpirationDate() != null)
			expirationTime.setValue(df.format(event.getExpirationDate()));

		final DateItem deadline = ItemFactory.newDateItem("deadline", "enddate");
		deadline.setRequired(false);
		deadline.setShowTitle(true);
		deadline.setTitleOrientation(TitleOrientation.LEFT);
		deadline.setCanEdit(!readOnly);
		if (event.getDeadline() != null)
			deadline.setValue(event.getDeadline());

		final SelectItem frequency = ItemFactory.newFrequencySelector("frequency", "frequency");
		frequency.setTitleOrientation(TitleOrientation.LEFT);
		frequency.setValue(Integer.toString(event.getFrequency()));
		frequency.setCanEdit(!readOnly);
		frequency.addChangedHandler(new ChangedHandler() {

			@Override
			public void onChanged(ChangedEvent event) {
				deadline.setDisabled("0".equals(event.getValue()));
				if ("0".equals(event.getValue()))
					deadline.setValue((Date) null);
			}
		});

		SpinnerItem remindTimeNumber = new SpinnerItem("remindTime");
		remindTimeNumber.setTitle(I18N.message("remindtime"));
		remindTimeNumber.setTitleOrientation(TitleOrientation.LEFT);
		remindTimeNumber.setDefaultValue(0);
		remindTimeNumber.setMin(0);
		remindTimeNumber.setStep(1);
		remindTimeNumber.setWidth(50);
		remindTimeNumber.setValue(event.getRemindTime());
		remindTimeNumber.setCanEdit(!readOnly);
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
		remindTimeUnit.setCanEdit(!readOnly);

		final DateItem completionDate = ItemFactory.newDateItem("completionDate", "completedon");
		completionDate.setRequired(false);
		completionDate.setShowTitle(false);
		completionDate.setTitleOrientation(TitleOrientation.LEFT);
		completionDate.setCanEdit(!readOnly);
		completionDate.setDisabled(event.getStatus() != GUICalendarEvent.STATUS_COMPLETED);
		if (event.getCompletionDate() != null)
			completionDate.setValue(event.getCompletionDate());

		SelectItem status = ItemFactory.newEventStatusSelector("status", "status");
		status.setTitleOrientation(TitleOrientation.LEFT);
		status.setValue(Integer.toString(event.getStatus()));
		status.setCanEdit(!readOnly);
		status.addChangedHandler(new ChangedHandler() {

			@Override
			public void onChanged(ChangedEvent event) {
				completionDate.setDisabled(!"2".equals(event.getValue()));
				if ("2".equals(event.getValue()))
					completionDate.setValue(new Date());
				else
					completionDate.setValue((Date) null);
			}
		});

		TextAreaItem description = ItemFactory.newTextAreaItem("description", "description", event.getDescription());
		description.setHeight("95%");
		description.setColSpan(5);
		description.setCanEdit(!readOnly);

		detailsForm.setFields(title, type, ItemFactory.newRowSpacer(), startDate, startTime, expirationDate,
				expirationTime, ItemFactory.newRowSpacer(), frequency, deadline, ItemFactory.newRowSpacer(),
				remindTimeNumber, remindTimeUnit, ItemFactory.newRowSpacer(), status, completionDate,
				ItemFactory.newRowSpacer(), description);
		details.setPane(detailsForm);
		return details;
	}

	/**
	 * Save button handler
	 */
	private void onSave() {
		if (vm.validate()) {
			calendarEvent.setTitle(vm.getValueAsString("title"));
			calendarEvent.setType(vm.getValueAsString("type"));
			calendarEvent.setDescription(vm.getValueAsString("description"));
			calendarEvent.setRemindTime(Integer.parseInt(vm.getValueAsString("remindTime")));
			calendarEvent.setRemindUnit(vm.getValueAsString("remindUnit"));

			if (vm.getValue("frequency") != null)
				calendarEvent.setFrequency(Integer.parseInt(vm.getValueAsString("frequency").trim()));

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
				if (vm.getValue("expirationTime") != null)
					try {
						str = str + " " + dfTime.format((Date) vm.getValue("expirationTime"));
					} catch (Throwable t) {
					}
				calendarEvent.setExpirationDate(df.parse(str));
			}

			if (calendarEvent.getExpirationDate() != null
					&& calendarEvent.getExpirationDate().before(calendarEvent.getStartDate())) {
				SC.warn(I18N.message("endbeforestart"));
				return;
			}

			if (vm.getValue("completionDate") != null)
				calendarEvent.setCompletionDate((Date) vm.getValue("completionDate"));
			else
				calendarEvent.setCompletionDate(null);
			calendarEvent.setStatus(Integer.parseInt(vm.getValue("status").toString()));
			if (calendarEvent.getCompletionDate() != null
					&& calendarEvent.getCompletionDate().before(calendarEvent.getStartDate())) {
				SC.warn(I18N.message("compbeforestart"));
				return;
			}
			if (vm.getValue("deadline") != null)
				calendarEvent.setDeadline((Date) vm.getValue("deadline"));
			else
				calendarEvent.setDeadline(null);

			service.saveEvent(Session.get().getSid(), calendarEvent, new AsyncCallback<Void>() {
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
}