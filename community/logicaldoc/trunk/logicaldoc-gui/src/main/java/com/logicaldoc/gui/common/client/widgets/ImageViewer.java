package com.logicaldoc.gui.common.client.widgets;

import com.logicaldoc.gui.common.client.i18n.I18N;
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

	public ImageViewer(String url, int baseSize) {
		this.baseSize = baseSize;
		this.url = url;
		setWidth100();
		setHeight100();

		setupToolbar();

		ImagePreloader.load(url, new ImageLoadHandler() {
			@Override
			public void imageLoaded(ImageLoadEvent event) {
				ImageViewer.this.baseSize = event.getDimensions().getWidth() < 600 ? event.getDimensions().getWidth()
						: 600;

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

		toolBar.addFill();
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
}