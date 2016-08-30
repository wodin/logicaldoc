package com.logicaldoc.gui.frontend.client.reports;

import java.util.LinkedHashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.data.DuplicatesDS;
import com.logicaldoc.gui.common.client.formatters.DateCellFormatter;
import com.logicaldoc.gui.common.client.formatters.FileSizeCellFormatter;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.util.LD;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.common.client.util.WindowUtils;
import com.logicaldoc.gui.common.client.widgets.InfoPanel;
import com.logicaldoc.gui.common.client.widgets.PreviewPopup;
import com.logicaldoc.gui.frontend.client.document.DocumentsPanel;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.DocumentServiceAsync;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.GroupStartOpen;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.DoubleClickEvent;
import com.smartgwt.client.widgets.events.DoubleClickHandler;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.grid.GroupNode;
import com.smartgwt.client.widgets.grid.GroupTitleRenderer;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellContextClickEvent;
import com.smartgwt.client.widgets.grid.events.CellContextClickHandler;
import com.smartgwt.client.widgets.grid.events.DataArrivedEvent;
import com.smartgwt.client.widgets.grid.events.DataArrivedHandler;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

/**
 * This panel shows a list of duplicates documents in a tabular way.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.1
 */
public class DuplicatesPanel extends VLayout {
	protected DocumentServiceAsync docService = (DocumentServiceAsync) GWT.create(DocumentService.class);

	private DuplicatesDS dataSource;

	private ListGrid list;

	private InfoPanel infoPanel;

	private boolean filters;

	public DuplicatesPanel() {
		ListGridField id = new ListGridField("id");
		id.setHidden(true);
		id.setCanGroupBy(false);

		ListGridField title = new ListGridField("title", I18N.message("title"), 200);
		title.setCanFilter(true);
		title.setHidden(true);

		ListGridField size = new ListGridField("size", I18N.message("size"), 70);
		size.setAlign(Alignment.RIGHT);
		size.setType(ListGridFieldType.FLOAT);
		size.setCellFormatter(new FileSizeCellFormatter());
		size.setCanFilter(false);
		size.setCanGroupBy(false);

		ListGridField icon = new ListGridField("icon", " ", 24);
		icon.setType(ListGridFieldType.IMAGE);
		icon.setCanSort(false);
		icon.setAlign(Alignment.CENTER);
		icon.setShowDefaultContextMenu(false);
		icon.setImageURLPrefix(Util.imagePrefix());
		icon.setImageURLSuffix(".png");
		icon.setCanFilter(false);
		icon.setCanGroupBy(false);

		ListGridField version = new ListGridField("version", I18N.message("version"), 55);
		version.setAlign(Alignment.CENTER);
		version.setCanFilter(true);
		version.setCanGroupBy(false);

		ListGridField lastModified = new ListGridField("lastModified", I18N.message("lastmodified"), 110);
		lastModified.setAlign(Alignment.CENTER);
		lastModified.setType(ListGridFieldType.DATE);
		lastModified.setCellFormatter(new DateCellFormatter(false));
		lastModified.setCanFilter(false);
		lastModified.setCanGroupBy(false);

		ListGridField publisher = new ListGridField("publisher", I18N.message("publisher"), 90);
		publisher.setAlign(Alignment.CENTER);
		publisher.setCanFilter(true);
		publisher.setHidden(true);
		publisher.setCanGroupBy(false);

		ListGridField customId = new ListGridField("customId", I18N.message("customid"), 110);
		customId.setType(ListGridFieldType.TEXT);
		customId.setHidden(true);
		customId.setCanGroupBy(false);

		ListGridField digest = new ListGridField("digest", I18N.message("digest"), 250);
		digest.setType(ListGridFieldType.TEXT);
		digest.setCanFilter(true);

		ListGridField filename = new ListGridField("filename", I18N.message("filename"), 200);
		filename.setCanFilter(true);

		ListGridField folderName = new ListGridField("foldername", I18N.message("folder"), 200);
		folderName.setCanFilter(true);

		ListGridField type = new ListGridField("type", I18N.message("type"), 55);
		type.setType(ListGridFieldType.TEXT);
		type.setAlign(Alignment.CENTER);
		type.setHidden(true);
		type.setCanGroupBy(false);

		list = new ListGrid() {
			@Override
			protected String getCellCSSText(ListGridRecord record, int rowNum, int colNum) {
				if (getFieldName(colNum).equals("title") || getFieldName(colNum).equals("filename")) {
					if ("stop".equals(record.getAttribute("immutable"))
							|| !"blank".equals(record.getAttribute("locked"))) {
						return "color: #888888; font-style: italic;";
					} else {
						return super.getCellCSSText(record, rowNum, colNum);
					}
				} else {
					return super.getCellCSSText(record, rowNum, colNum);
				}
			}
		};
		list.setEmptyMessage(I18N.message("notitemstoshow"));
		list.setShowRecordComponents(true);
		list.setShowRecordComponentsByCell(true);
		list.setCanFreezeFields(false);
		list.setAutoFetchData(true);
		list.setFilterOnKeypress(true);
		list.setSelectionType(SelectionStyle.MULTIPLE);
		dataSource = new DuplicatesDS();
		list.setDataSource(dataSource);
		// Initial group by
		list.setGroupStartOpen(GroupStartOpen.ALL);
		list.setGroupByField("digest");

		filename.setGroupTitleRenderer(new GroupTitleRenderer() {
			public String getGroupTitle(Object groupValue, GroupNode groupNode, ListGridField field, String fieldName,
					ListGrid grid) {
				String baseTitle = I18N.message("filename") + ": " + groupValue.toString();
				return baseTitle;
			}
		});

		digest.setGroupTitleRenderer(new GroupTitleRenderer() {
			public String getGroupTitle(Object groupValue, GroupNode groupNode, ListGridField field, String fieldName,
					ListGrid grid) {
				String baseTitle = I18N.message("digest") + ": " + groupValue.toString();
				return baseTitle;
			}
		});

		list.setCanDrag(true);
		list.setCanDragRecordsOut(true);

		list.setFields(icon, filename, title, folderName, lastModified, size, version, publisher, customId, digest,
				type);

		ToolStrip toolStrip = new ToolStrip();
		toolStrip.setHeight(20);
		toolStrip.setWidth100();
		toolStrip.addSpacer(2);
		final SelectItem groupBy = new SelectItem("groupBy", I18N.message("groupby"));
		groupBy.setWrapTitle(false);
		groupBy.setWidth(100);
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("", " ");
		map.put("filename", I18N.message("filename"));
		map.put("digest", I18N.message("digest"));
		groupBy.setValueMap(map);
		groupBy.setPickListWidth(100);
		groupBy.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				if (event.getValue() != null && !"".equals((String) event.getValue())) {
					list.ungroup();
					list.groupBy((String) event.getValue());
				}

			}
		});
		toolStrip.addFormItem(groupBy);

		ToolStripButton print = new ToolStripButton();
		print.setIcon(ItemFactory.newImgIcon("printer.png").getSrc());
		print.setTooltip(I18N.message("print"));
		print.setAutoFit(true);
		print.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				Canvas.printComponents(new Object[] { list });
			}
		});
		toolStrip.addSeparator();
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
				public void onClick(ClickEvent event) {
					Util.exportCSV(list, false);
				}
			});
			if (!Feature.enabled(Feature.EXPORT_CSV)) {
				export.setDisabled(true);
				export.setTooltip(I18N.message("featuredisabled"));
			}
		}

		toolStrip.addFill();

		// Prepare a panel containing a title and the documents list
		infoPanel = new InfoPanel("");

		addMember(toolStrip);
		addMember(infoPanel);
		addMember(list);

		list.addCellContextClickHandler(new CellContextClickHandler() {
			@Override
			public void onCellContextClick(CellContextClickEvent event) {
				showContextMenu();
				event.cancel();
			}
		});

		list.addDoubleClickHandler(new DoubleClickHandler() {
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				String id = list.getSelectedRecord().getAttribute("id");
				if (Session.get().getCurrentFolder().isDownload())
					WindowUtils.openUrl(Util.downloadURL(Long.parseLong(id), null, false));
			}
		});

		list.addDataArrivedHandler(new DataArrivedHandler() {
			@Override
			public void onDataArrived(DataArrivedEvent event) {
				infoPanel.setMessage(I18N.message("showndocuments", Integer.toString(list.getTotalRows())));
			}
		});
	}

	@Override
	public void destroy() {
		super.destroy();
		if (dataSource != null)
			dataSource.destroy();
	}

	public void toggleFilters() {
		list.setShowFilterEditor(!filters);
		filters = !filters;
	}

	private void showContextMenu() {
		Menu contextMenu = new Menu();
		final ListGridRecord[] selection = list.getSelectedRecords();

		MenuItem delete = new MenuItem();
		delete.setTitle(I18N.message("ddelete"));
		delete.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				if (selection == null || selection.length == 0)
					return;
				final long[] ids = new long[selection.length];
				for (int i = 0; i < selection.length; i++) {
					ids[i] = Long.parseLong(selection[i].getAttribute("id"));
				}

				if (ids.length > 0)
					LD.ask(I18N.message("question"), I18N.message("confirmdelete"), new BooleanCallback() {
						@Override
						public void execute(Boolean value) {
							if (value) {
								docService.delete(ids, new AsyncCallback<Void>() {
									@Override
									public void onFailure(Throwable caught) {
										Log.serverError(caught);
									}

									@Override
									public void onSuccess(Void result) {
										list.removeSelectedData();
										DocumentsPanel.get().getDocumentsMenu().refresh("trash");
									}
								});
							}
						}
					});
			}
		});

		MenuItem preview = new MenuItem();
		preview.setTitle(I18N.message("preview"));
		preview.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				long id = Long.parseLong(list.getSelectedRecord().getAttribute("id"));
				docService.getById(id, new AsyncCallback<GUIDocument>() {

					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(GUIDocument doc) {
						PreviewPopup iv = new PreviewPopup(doc);
						iv.show();
					}
				});
			}
		});

		MenuItem download = new MenuItem();
		download.setTitle(I18N.message("download"));
		download.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				Long id = list.getSelectedRecord().getAttributeAsLong("id");
				WindowUtils.openUrl(Util.downloadURL(id));
			}
		});

		MenuItem openInFolder = new MenuItem();
		openInFolder.setTitle(I18N.message("openinfolder"));
		openInFolder.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				ListGridRecord record = list.getSelectedRecord();
				DocumentsPanel.get().openInFolder(Long.parseLong(record.getAttributeAsString("folderId")),
						Long.parseLong(record.getAttributeAsString("id")));
			}
		});

		if (!(list.getSelectedRecords() != null && list.getSelectedRecords().length == 1)) {
			download.setEnabled(false);
			preview.setEnabled(false);
			openInFolder.setEnabled(false);
		}

		for (ListGridRecord record : selection) {
			if (!"blank".equals(record.getAttribute("locked")) || !"blank".equals(record.getAttribute("immutable"))) {
				delete.setEnabled(false);
			}
		}

		contextMenu.setItems(download, preview, delete, openInFolder);
		contextMenu.showContextMenu();
	}
}
