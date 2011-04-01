package com.logicaldoc.gui.frontend.client.document;

import com.google.gwt.core.client.GWT;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.common.client.widgets.ImageViewer;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.layout.Portlet;

/**
 * This portlet is used to resize the visualization of images and documents on
 * the preview.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.1
 */
public class PreviewPortlet extends Portlet {

	public static final int IMAGE = 1;

	public static final int APPLET = 2;

	private ImageViewer image = null;

	private HTMLFlow applet = null;

	public PreviewPortlet(int type, String filename, long docId, String width, String height) {
		refresh(type, filename, docId, width, height);
	}

	public void refresh(int type, String filename, long docId, String width, String height) {
		setWidth(width);
		setHeight(height);
		setTop(23);
		setCanDrag(false);
		setCanDrop(false);
		setCanDragResize(false);
		setShowHeader(false);
		setHeaderControls();
		setCanDragReposition(false);
		setCanDragResize(false);
		setCanDragScroll(false);

		if (type == IMAGE && image != null) {
			removeItem(image);
			image.destroy();
		} else if (type == APPLET && applet != null) {
			removeItem(applet);
			applet.destroy();
		}

		String f = filename.toLowerCase();
		if (type == IMAGE) {
			if (f.endsWith(".tif") || f.endsWith(".tiff") || f.endsWith(".bmp") || f.endsWith(".pdf")) {
				image = new ImageViewer(GWT.getHostPageBaseURL() + "thumbnail?sid=" + Session.get().getSid()
						+ "&docId=" + docId, Integer.parseInt(width), Integer.parseInt(height));
			} else {
				image = new ImageViewer(GWT.getHostPageBaseURL() + "download?sid=" + Session.get().getSid() + "&docId="
						+ docId + "&open=true", Integer.parseInt(width), Integer.parseInt(height));
			}
			addItem(image);
		} else if (type == APPLET) {
			applet = new HTMLFlow();
			String tmp = "<applet name=\"PreviewApplet\" archive=\""
					+ Util.contextPath()
					+ "applet/logicaldoc-enterprise-core.jar\" code=\"com.logicaldoc.enterprise.preview.PreviewApplet\" width=\""
					+ (Integer.parseInt(width) - 15) + "\" height=\"" + (Integer.parseInt(height) - 15) + "\">";
			tmp += "<param name=\"url\" value=\"" + GWT.getHostPageBaseURL() + "convertpdf?sid="
					+ Session.get().getSid() + "&docId=" + docId + "\" />";
			tmp += "</applet>";
			applet.setContents(tmp);
			applet.setSize((Integer.parseInt(width) - 15) + "px", (Integer.parseInt(height) - 15) + "px");
			addItem(applet);
		}
	}
}