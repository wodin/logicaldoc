package com.logicaldoc.gui.frontend.client.document;

import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.Util;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * This popup window is used to upload documents to the server.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class ScanDialog extends Window {
	public ScanDialog() {
		VLayout layout = new VLayout();
		layout.setMargin(2);

		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.message("scandocument"));
		setWidth(630);
		setHeight(440);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();

		String tmp = "<applet name=\"ScanApplet\" archive=\"" + Util.contextPath()
				+ "applet/logicaldoc-scan.jar\"  code=\"com.logicaldoc.scan.applet.ScanApplet\" width=\""
				+ (getWidth() - 10) + "\" height=\"" + (getHeight() - 35) + "\">";
		tmp += "<param name=\"baseUrl\" value=\"" + Util.contextPath() + "\" />";
		tmp += "<param name=\"sid\" value=\"" + Session.get().getSid() + "\" />";
		tmp += "<param name=\"language\" value=\"" + I18N.getDefaultLocaleForDoc() + "\" />";
		tmp += "</applet>";

		HTMLFlow applet = new HTMLFlow();
		applet.setContents(tmp);
		applet.setWidth(getWidth() - 10);
		applet.setHeight(getHeight() - 35);

		layout.addMember(applet);
		addItem(layout);
	}
}