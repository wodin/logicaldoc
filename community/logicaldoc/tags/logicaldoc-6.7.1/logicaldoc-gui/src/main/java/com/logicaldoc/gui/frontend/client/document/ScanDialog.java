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
	private HTMLFlow applet = new HTMLFlow();

	public ScanDialog() {
		VLayout layout = new VLayout();
		layout.setMargin(25);

		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.message("scandocument"));
		setWidth(650);
		setHeight(445);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();

		String tmp = "<applet name=\"ScanApplet\" archive=\""
				+ Util.contextPath()
				+ "applet/logicaldoc-scan.jar\"  code=\"com.logicaldoc.scan.applet.ScanApplet\" width=\"600\" height=\"400\">";
		tmp += "<param name=\"lang\" value=\"" + I18N.getLocale() + "\" />";
		tmp += "<param name=\"uploadUrl\" value=\"" + Util.contextPath() + "servlet.gupld?new_session=true&sid="
				+ Session.get().getSid() + "\" />";
		tmp += "</applet>";

		applet.setContents(tmp);
		applet.setWidth("620px");
		applet.setHeight("400px");

		layout.addMember(applet);
		addChild(layout);
	}
}