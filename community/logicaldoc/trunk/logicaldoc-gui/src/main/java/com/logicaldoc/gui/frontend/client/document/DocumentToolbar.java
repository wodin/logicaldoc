package com.logicaldoc.gui.frontend.client.document;

import com.google.gwt.user.client.Window;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.FolderObserver;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.smartgwt.client.types.SelectionType;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.IntegerItem;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

/**
 * The toolbar to handle some documents aspects
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class DocumentToolbar extends ToolStrip implements FolderObserver {
	private ToolStripButton download = new ToolStripButton();

	private ToolStripButton rss = new ToolStripButton();

	private ToolStripButton pdf = new ToolStripButton();

	private ToolStripButton add = new ToolStripButton();

	private GUIDocument document;

	public DocumentToolbar() {
		download.setTooltip(I18N.message("download"));
		download.setIcon(ItemFactory.newImg("download.png").getSrc());
		download.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (document == null)
					return;
				Window.open("download?sid=" + Session.get().getSid() + "&sid=" + Session.get().getSid() + "&docId="
						+ document.getId(), "_self", "");
			}
		});

		rss.setIcon(ItemFactory.newImg("rss.png").getSrc());
		rss.setTooltip(I18N.message("rssfeed"));
		if (!Session.get().isFeatureEnabled("Feature_9"))
			rss.setTooltip(I18N.message("featuredisabled"));
		rss.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Window.open("doc_rss?sid=" + Session.get().getSid() + "&docId=" + document.getId(), "_blank", "");
			}
		});

		pdf.setTooltip(I18N.message("exportpdf"));
		pdf.setIcon(ItemFactory.newImg("pdf.png").getSrc());
		if (!Session.get().isFeatureEnabled("Feature_8"))
			pdf.setTooltip(I18N.message("featuredisabled"));
		pdf.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Window.open("convertpdf?sid=" + Session.get().getSid() + "&docId=" + document.getId() + "&version="
						+ document.getVersion(), "_blank", "");
			}
		});

		add.setTooltip(I18N.message("adddocuments"));
		add.setIcon(ItemFactory.newImg("document_add.png").getSrc());
		add.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				final DocumentsUploader uploader = new DocumentsUploader();
				uploader.show();
				event.cancel();
			}
		});

		setHeight(27);
		addButton(download);
		addButton(rss);
		addButton(pdf);
		addSeparator();
		addButton(add);

		addSeparator();
		final IntegerItem max = ItemFactory.newValidateIntegerItem("max", "", null, 1, null);
		max.setHint(I18N.message("elements"));
		max.setShowTitle(false);
		max.setDefaultValue(100);
		max.setWidth(40);

		ToolStripButton display = new ToolStripButton();
		display.setTitle(I18N.message("display"));
		addButton(display);
		addFormItem(max);
		display.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (max.validate()) {
					DocumentsPanel.get().refresh((Integer) max.getValue());
				}
			}
		});

		addSeparator();
		ToolStripButton filter = new ToolStripButton();
		filter.setActionType(SelectionType.CHECKBOX);
		filter.setTitle(I18N.message("filter"));
		addButton(filter);
		filter.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				DocumentsPanel.get().toggleFilters();
			}
		});

		update(null);
		Session.get().addFolderObserver(this);
	}

	/**
	 * Updates the toolbar state on the basis of the passed document
	 */
	public void update(final GUIDocument document) {
		this.document = document;

		if (document == null) {
			download.setDisabled(true);
		} else {
			download.setDisabled(false);
		}

		if (document != null && Session.get().isFeatureEnabled("Feature_9")) {
			rss.setDisabled(false);
		} else {
			rss.setDisabled(true);
		}

		if (document != null && Session.get().isFeatureEnabled("Feature_8")) {
			pdf.setDisabled(false);
		} else {
			pdf.setDisabled(true);
		}

		GUIFolder folder = Session.get().getCurrentFolder();

		if (folder != null && folder.hasPermission(Constants.PERMISSION_WRITE)) {
			add.setDisabled(false);
		} else {
			add.setDisabled(true);
		}
	}

	@Override
	public void onFolderSelect(GUIFolder folder) {
		update(null);
	}
}