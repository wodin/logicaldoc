package com.logicaldoc.gui.frontend.client.document;

import com.google.gwt.user.client.Window;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.FolderObserver;
import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.common.client.util.Util;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
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

	public DocumentToolbar() {
		addButton(download);
		addButton(rss);
		addButton(pdf);
		addSeparator();
		addButton(add);
		update(null);
		Session.get().addFolderObserver(this);
	}

	/**
	 * Updates the toolbar state on the basis of the passed document
	 */
	public void update(final GUIDocument document) {
		if (document != null) {
			download.setTooltip(I18N.getMessage("download"));
			download.setIcon(Util.imageUrl("application/download.png"));
			download.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					Window.open("download?sid=" + Session.get().getSid() + "&sid="
							+ Session.get().getSid() + "&docId=" + document.getId(), "_self", "");
				}
			});
		} else {
			download.setIcon(Util.imageUrl("application/download_gray.png"));
		}

		if (document != null && Session.get().isFeatureEnabled("Feature_9")) {
			rss.setTooltip(I18N.getMessage("rssfeed"));
			rss.setIcon(Util.imageUrl("application/rss.png"));
			rss.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					Window.open("doc_rss?sid=" + Session.get().getSid() + "&docId=" + document.getId(),
							"_blank", "");
				}
			});
		} else {
			rss.setTooltip(I18N.getMessage("featuredisabled"));
			rss.setIcon(Util.imageUrl("application/rss_gray.png"));
		}

		if (document != null && Session.get().isFeatureEnabled("Feature_8")) {
			pdf.setTooltip(I18N.getMessage("exportpdf"));
			pdf.setIcon(Util.imageUrl("application/pdf.png"));
			pdf.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					Window.open("convertpdf?sid=" + Session.get().getSid() + "&docId=" + document.getId()
							+ "&version=" + document.getVersion(), "_blank", "");
				}
			});
		} else {
			pdf.setTooltip(I18N.getMessage("featuredisabled"));
			pdf.setIcon(Util.imageUrl("application/pdf_gray.png"));
		}

		GUIFolder folder = Session.get().getCurrentFolder();

		if (folder != null && folder.hasPermission(Constants.PERMISSION_WRITE)) {
			add.setTooltip(I18N.getMessage("adddocuments"));
			add.setIcon(Util.imageUrl("application/document_add.png"));
			add.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					final DocumentsUploader uploader = new DocumentsUploader();
					uploader.show();
					event.cancel();
				}
			});
		} else {
			add.setTooltip(I18N.getMessage("featuredisabled"));
			add.setIcon(Util.imageUrl("application/document_add_gray.png"));
		}
	}

	@Override
	public void onFolderSelect(GUIFolder folder) {
		update(null);
	}
}