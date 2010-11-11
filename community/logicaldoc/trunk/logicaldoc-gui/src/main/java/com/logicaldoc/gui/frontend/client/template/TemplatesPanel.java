package com.logicaldoc.gui.frontend.client.template;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUITemplate;
import com.logicaldoc.gui.common.client.data.TemplatesDS;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.widgets.HTMLPanel;
import com.logicaldoc.gui.common.client.widgets.InfoPanel;
import com.logicaldoc.gui.frontend.client.services.TemplateService;
import com.logicaldoc.gui.frontend.client.services.TemplateServiceAsync;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellContextClickEvent;
import com.smartgwt.client.widgets.grid.events.CellContextClickHandler;
import com.smartgwt.client.widgets.grid.events.DataArrivedEvent;
import com.smartgwt.client.widgets.grid.events.DataArrivedHandler;
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
 * Panel showing the list of templates
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class TemplatesPanel extends VLayout {
	private TemplateServiceAsync service = (TemplateServiceAsync) GWT.create(TemplateService.class);

	private Layout listing;

	private Layout detailsContainer;

	private ListGrid list;

	private Canvas details = SELECT_TEMPLATE;

	private InfoPanel infoPanel;

	private ToolStrip toolStrip;

	final static Canvas SELECT_TEMPLATE = new HTMLPanel("&nbsp;" + I18N.message("selecttemplate"));

	public TemplatesPanel() {
		setWidth100();

		infoPanel = new InfoPanel("");

		refresh();
	}

	public void refresh() {
		Canvas[] members = getMembers();
		for (Canvas canvas : members) {
			removeMember(canvas);
		}

		listing = new VLayout();
		detailsContainer = new VLayout();
		details = SELECT_TEMPLATE;

		// Initialize the listing panel
		listing.setAlign(Alignment.CENTER);
		listing.setHeight("60%");
		listing.setShowResizeBar(true);

		ListGridField id = new ListGridField("id", 50);
		id.setHidden(true);

		ListGridField name = new ListGridField("name", I18N.message("name"), 200);
		name.setCanFilter(true);
		name.setCanSort(false);

		ListGridField description = new ListGridField("description", I18N.message("description"), 300);
		description.setCanFilter(true);
		description.setCanSort(false);
		
		ListGridField readonly = new ListGridField("readonly", I18N.message("readonly"), 20);
		readonly.setCanFilter(true);
		readonly.setCanSort(false);
		
		list = new ListGrid();
		list.setShowAllRecords(true);
		list.setAutoFetchData(true);
		list.setWidth100();
		list.setHeight100();
		list.setFields(name, description, readonly);
		list.setSelectionType(SelectionStyle.SINGLE);
		list.setShowRecordComponents(true);
		list.setShowRecordComponentsByCell(true);
		list.setCanFreezeFields(true);
		list.setFilterOnKeypress(true);
		list.setDataSource(new TemplatesDS(false, null));
		list.setShowFilterEditor(true);

		listing.addMember(infoPanel);
		listing.addMember(list);

		toolStrip = new ToolStrip();
		toolStrip.setHeight(20);
		toolStrip.setWidth100();
		toolStrip.addSpacer(2);
		ToolStripButton add = new ToolStripButton();
		add.setTitle(I18N.message("addtemplate"));
		toolStrip.addButton(add);
		add.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				list.deselectAllRecords();
				showTemplateDetails(new GUITemplate());
			}
		});
		toolStrip.addFill();

		list.addCellContextClickHandler(new CellContextClickHandler() {
			@Override
			public void onCellContextClick(CellContextClickEvent event) {
				ListGridRecord record = list.getSelectedRecord();
				if (!record.getAttributeAsBoolean("readonly")) {
					showContextMenu();
				}
				event.cancel();
			}
		});

		list.addSelectionChangedHandler(new SelectionChangedHandler() {
			@Override
			public void onSelectionChanged(SelectionEvent event) {
				Record record = list.getSelectedRecord();
				if (record != null)
					service.getTemplate(Session.get().getSid(), Long.parseLong(record.getAttributeAsString("id")),
							new AsyncCallback<GUITemplate>() {

								@Override
								public void onFailure(Throwable caught) {
									Log.serverError(caught);
								}

								@Override
								public void onSuccess(GUITemplate template) {
									showTemplateDetails(template);
								}
							});
			}
		});

		list.addDataArrivedHandler(new DataArrivedHandler() {
			@Override
			public void onDataArrived(DataArrivedEvent event) {
				infoPanel.setMessage(I18N.message("showtemplates", Integer.toString(list.getTotalRows())));
			}
		});

		detailsContainer.setAlign(Alignment.CENTER);
		detailsContainer.addMember(details);

		setMembers(toolStrip, listing, detailsContainer);
	}

	private void showContextMenu() {
		Menu contextMenu = new Menu();

		final ListGridRecord record = list.getSelectedRecord();
		final long id = Long.parseLong(record.getAttributeAsString("id"));

		MenuItem delete = new MenuItem();
		delete.setTitle(I18N.message("ddelete"));
		delete.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				SC.ask(I18N.message("question"), I18N.message("confirmdelete"), new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						if (value) {
							service.delete(Session.get().getSid(), id, new AsyncCallback<Void>() {
								@Override
								public void onFailure(Throwable caught) {
									Log.serverError(caught);
								}

								@Override
								public void onSuccess(Void result) {
									list.removeSelectedData();
									list.deselectAllRecords();
									showTemplateDetails(null);
								}
							});
						}
					}
				});
			}
		});

		contextMenu.setItems(delete);
		contextMenu.showContextMenu();
	}

	private void showTemplateDetails(GUITemplate template) {
		if (!(details instanceof TemplateDetailsPanel)) {
			detailsContainer.removeMember(details);
			details = new TemplateDetailsPanel(this);
			detailsContainer.addMember(details);
		}
		((TemplateDetailsPanel) details).setTemplate(template);
	}

	public ListGrid getList() {
		return list;
	}

	/**
	 * Updates the selected record with new data
	 */
	public void updateRecord(GUITemplate template) {
		ListGridRecord record = list.getSelectedRecord();
		if (record == null)
			record = new ListGridRecord();

		record.setAttribute("name", template.getName());
		record.setAttribute("description", template.getDescription());

		if (record.getAttributeAsString("id") != null
				&& (template.getId() == Long.parseLong(record.getAttributeAsString("id")))) {
			list.updateData(record);
		} else {
			// Append a new record
			record.setAttribute("id", template.getId());
			list.addData(record);
			list.selectRecord(record);
		}
	}
}