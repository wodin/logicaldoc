package com.logicaldoc.gui.common.client.widgets;

import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.Util;
import com.reveregroup.gwt.imagepreloader.FitImage;
import com.reveregroup.gwt.imagepreloader.ImageLoadEvent;
import com.reveregroup.gwt.imagepreloader.ImageLoadHandler;
import com.reveregroup.gwt.imagepreloader.ImagePreloader;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

/**
 * This panel is used to show an image with zoom-in and zoom-out feature.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class ImageViewer extends VLayout {
	private ToolStrip toolBar;

	private FitImage image;

	private int zoom = 0;

	private VLayout imageContainer = new VLayout();

	private int baseSize = 600;

	private String url;

	private GUIDocument document;

	/**
	 * Constructs the image respecting the original image size
	 * 
	 * @param url the image URL
	 */
	public ImageViewer(GUIDocument document) {
		this.baseSize = Integer.parseInt(Session.get().getConfig("gui.thumbnail.size"));
		this.document = document;
		this.url = Util.contextPath() + "/preview?docId=" + document.getId() + "&sid=" + Session.get().getSid();

		setWidth100();
		setHeight100();

		setupToolbar();

		ImagePreloader.load(url, new ImageLoadHandler() {
			@Override
			public void imageLoaded(ImageLoadEvent event) {
				try {
					image = new FitImage(ImageViewer.this.url, ImageViewer.this.baseSize, ImageViewer.this.baseSize);
					imageContainer = new VLayout();
					imageContainer.setMargin(0);
					imageContainer.setPadding(0);
					imageContainer.setWidth100();
					imageContainer.setHeight100();
					imageContainer.setOverflow(Overflow.SCROLL);
					imageContainer.addMember(image);

					addMember(toolBar);
					addMember(imageContainer);

					ImageViewer.this.baseSize = ImageViewer.this.image.getOriginalHeight();

					onEffectiveSize();
				} catch (Throwable t) {

				}
			}
		});
	}

	private void setupToolbar() {
		toolBar = new ToolStrip();
		toolBar.setHeight(20);
		toolBar.setWidth100();
		toolBar.addSpacer(2);

		ToolStripButton zoomin = new ToolStripButton();
		zoomin.setTitle(I18N.message("zoomin"));
		toolBar.addButton(zoomin);
		zoomin.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ImageViewer.this.zoom++;
				refreshImage();
			}
		});

		ToolStripButton zoomout = new ToolStripButton();
		zoomout.setTitle(I18N.message("zoomout"));
		toolBar.addButton(zoomout);
		zoomout.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ImageViewer.this.zoom--;
				refreshImage();
			}
		});

		ToolStripButton effectiveSize = new ToolStripButton();
		effectiveSize.setTitle(I18N.message("effectivesize"));
		toolBar.addButton(effectiveSize);
		effectiveSize.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onEffectiveSize();
			}
		});

		ToolStripButton preview = new ToolStripButton();
		preview.setTitle(I18N.message("preview"));
		toolBar.addButton(preview);
		preview.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onPreview();
			}
		});

		toolBar.addFill();
	}

	protected void onPreview() {
		try {
			String filename = document.getFileName();
			String version = document.getFileVersion();

			// In the search hitlist we don't have the filename
			if (filename == null)
				filename = document.getTitle() + "." + document.getType();

			GUIFolder folder = document.getFolder();
			PreviewPopup iv = new PreviewPopup(document.getId(), version, filename, folder != null
					&& folder.isDownload());
			iv.show();
		} catch (Throwable t) {

		}
	}

	protected void onEffectiveSize() {
		try {
			int originalWidth = ImageViewer.this.image.getOriginalWidth();
			int originalHeight = ImageViewer.this.image.getOriginalHeight();

			int maxSize = Math.max(originalWidth, originalHeight);
			ImageViewer.this.baseSize = maxSize;
			refreshImageEffectiveSize();
		} catch (Throwable t) {

		}
	}

	private void refreshImage() {
		if (contains(imageContainer))
			removeMember(imageContainer);

		image = new FitImage(url, baseSize + (ImageViewer.this.zoom * 50), baseSize + (ImageViewer.this.zoom * 50));

		imageContainer = new VLayout();
		imageContainer.setMargin(0);
		imageContainer.setPadding(0);
		imageContainer.setWidth100();
		imageContainer.setHeight100();
		imageContainer.addMember(image);
		imageContainer.setOverflow(Overflow.SCROLL);

		addMember(imageContainer);
	}

	private void refreshImageEffectiveSize() {
		if (contains(imageContainer))
			removeMember(imageContainer);

		image = new FitImage(url, baseSize, baseSize);

		imageContainer = new VLayout();
		imageContainer.setMargin(0);
		imageContainer.setPadding(0);
		imageContainer.setWidth100();
		imageContainer.setHeight100();
		imageContainer.addMember(image);
		imageContainer.setOverflow(Overflow.SCROLL);

		addMember(imageContainer);
	}
}