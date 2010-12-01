package com.logicaldoc.gui.frontend.client.document;

import com.bramosystems.oss.player.core.client.AbstractMediaPlayer;
import com.bramosystems.oss.player.core.client.LoadException;
import com.bramosystems.oss.player.core.client.PlayerUtil;
import com.bramosystems.oss.player.core.client.Plugin;
import com.bramosystems.oss.player.core.client.PluginNotFoundException;
import com.bramosystems.oss.player.core.client.PluginVersionException;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HTML;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.common.client.widgets.FeatureDisabled;
import com.logicaldoc.gui.common.client.widgets.ImageViewer;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClientEvent;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * This popup window is used to show the document preview.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class PreviewPopup extends Window {

	private HTML applet = new HTML();
	
	public PreviewPopup(long docId, String version, String filename) {
		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.message("preview"));
		setWidth(620);
		setHeight(490);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();
		
		addCloseClickHandler(new CloseClickHandler() {
			@Override
			public void onCloseClick(CloseClientEvent event) {
				if (applet != null)
					applet.setHTML("");
				destroy();
			}
		});

		String f=filename.toLowerCase();

        if (Util.isImageFile(filename)) {
			ImageViewer iv = null;
			if (f.endsWith(".tif") || f.endsWith(".tiff") || f.endsWith(".bmp")) {
				iv = new ImageViewer(GWT.getHostPageBaseURL() + "thumbnail?sid=" + Session.get().getSid() + "&docId="
						+ docId, 600);
			} else {
				iv = new ImageViewer(GWT.getHostPageBaseURL() + "download?sid=" + Session.get().getSid() + "&docId="
						+ docId + "&open=true", 600);
			}

			iv.setTop(23);
			iv.setWidth(602);
			iv.setHeight(460);
			addChild(iv);
		} else if (Util.isMediaFile(filename)) {
			VLayout panel = new VLayout();
			panel.setTop(23);
			panel.setWidth(602);
			panel.setHeight(460);
			AbstractMediaPlayer p = null;
			try {
				p = PlayerUtil.getPlayer(Plugin.Auto, GWT.getHostPageBaseURL() + "download?sid="
						+ Session.get().getSid() + "&docId=" + docId + "&open=true&filename=" + filename, true,
						"460px", "455px");
				panel.addMember(p);
			} catch (LoadException e) {
				Log.error("An error occured while loading the multimedia player", null, e);
			} catch (PluginVersionException e) {
				// catch PluginVersionException, thrown if required plugin
				// version is not found
				panel.addMember(PlayerUtil.getMissingPluginNotice(e.getPlugin()));
			} catch (PluginNotFoundException e) {
				// catch PluginNotFoundException, thrown if no plugin is not
				// found
				panel.addMember(PlayerUtil.getMissingPluginNotice(e.getPlugin()));
			}
			addChild(panel);
		} else {
			VLayout panel = new VLayout();
			panel.setTop(23);
			panel.setWidth(602);
			panel.setHeight(460);
			addChild(panel);
			if (Feature.enabled(Feature.PREVIEW)) {
				applet = new HTML();
				String tmp = "<applet name=\"PreviewApplet\" archive=\""
						+ Util.contextPath()
						+ "applet/logicaldoc-enterprise-core.jar\"  code=\"com.logicaldoc.enterprise.preview.PreviewApplet\" width=\"600\" height=\"460\">";
				tmp += "<param name=\"url\" value=\"" + GWT.getHostPageBaseURL() + "convertpdf?sid="
						+ Session.get().getSid() + "&docId=" + docId + "\" />";
				tmp += "</applet>";
				applet.setHTML(tmp);
				applet.setWidth("600px");
				applet.setHeight("460px");
				panel.addMember(applet);
			} else {
				panel.addMember(new FeatureDisabled(Feature.PREVIEW));
			}
		}
	}
}