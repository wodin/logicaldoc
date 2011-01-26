package com.logicaldoc.gui.frontend.client.document;

import com.bramosystems.oss.player.core.client.AbstractMediaPlayer;
import com.bramosystems.oss.player.core.client.PlayerUtil;
import com.bramosystems.oss.player.core.client.Plugin;
import com.google.gwt.core.client.GWT;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.common.client.widgets.FeatureDisabled;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClientEvent;
import com.smartgwt.client.widgets.events.ResizedEvent;
import com.smartgwt.client.widgets.events.ResizedHandler;

/**
 * This popup window is used to show the document preview.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class PreviewPopup extends Window {

	private String fileName = "";

	private Long id = null;

	private AbstractMediaPlayer media = null;

	public PreviewPopup(long docId, String version, String filename) {
		this.fileName = filename;
		this.id = docId;

		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.message("preview"));
		setWidth(620);
		setHeight(490);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();
		setMargin(2);

		String f = filename.toLowerCase();

		if (Util.isImageFile(filename) || (Feature.enabled(Feature.PREVIEW) == false && f.endsWith(".pdf"))) {
			final PreviewPortlet image = new PreviewPortlet(PreviewPortlet.IMAGE, fileName, id, "" + (getWidth() - 15),
					"" + (getHeight() - 15));
			addChild(image);

			addResizedHandler(new ResizedHandler() {

				@Override
				public void onResized(ResizedEvent event) {
					if (image != null)
						image.refresh(PreviewPortlet.IMAGE, fileName, id, "" + (getWidth() - 15), ""
								+ (getHeight() - 15));
				}
			});
		} else if (Util.isMediaFile(filename)) {
			Plugin plugin = null;
			try {
				if (PlayerUtil.getWindowsMediaPlayerPluginVersion() != null)
					plugin = Plugin.WinMediaPlayer;
			} catch (Exception e) {

			}
			try {
				if (plugin == null && PlayerUtil.getQuickTimePluginVersion() != null)
					plugin = Plugin.QuickTimePlayer;
			} catch (Exception e) {

			}
			try {
				if (plugin == null && PlayerUtil.getVLCPlayerPluginVersion() != null)
					plugin = Plugin.VLCPlayer;
			} catch (Exception e) {

			}
			try {
				if (plugin == null && PlayerUtil.getDivXPlayerPluginVersion() != null)
					plugin = Plugin.DivXPlayer;
			} catch (Exception e) {

			}
			if (plugin == null)
				plugin = Plugin.Native;
			
			try {
				Canvas mediaCanvas = new Canvas();
				media = PlayerUtil.getPlayer(plugin, GWT.getHostPageBaseURL() + "download?sid="
						+ Session.get().getSid() + "&docId=" + docId + "&open=true&filename=" + filename, true,
						getHeight() - 25 + "px", getWidth() - 15 + "px");
				mediaCanvas.addChild(media);
				setCanDragResize(false);
				addItem(mediaCanvas);
			} catch (Exception e) {
				Log.error("An error occured while loading the multimedia player", null, e);
			}
		} else {
			if (Feature.enabled(Feature.PREVIEW)) {
				final PreviewPortlet applet = new PreviewPortlet(PreviewPortlet.APPLET, fileName, id, ""
						+ (getWidth() - 15), "" + (getHeight() - 15));
				addChild(applet);

				addResizedHandler(new ResizedHandler() {

					@Override
					public void onResized(ResizedEvent event) {
						if (applet != null)
							applet.refresh(PreviewPortlet.APPLET, fileName, id, "" + (getWidth() - 15), ""
									+ (getHeight() - 15));
					}
				});
			} else {
				addItem(new FeatureDisabled(Feature.PREVIEW));
			}
		}

		addCloseClickHandler(new CloseClickHandler() {
			@Override
			public void onCloseClick(CloseClientEvent event) {
				if (media != null)
					media.stopMedia();
				destroy();
			}
		});
	}
}