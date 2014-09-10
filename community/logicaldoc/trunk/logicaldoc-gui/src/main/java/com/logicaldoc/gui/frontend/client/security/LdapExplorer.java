package com.logicaldoc.gui.frontend.client.security;

import com.logicaldoc.gui.common.client.beans.GUILdapSettings;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.Util;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * This popup window is used to show a simple LDAP explorer
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.4
 */
public class LdapExplorer extends Window {

	private VLayout layout = null;

	private GUILdapSettings settings;

	public LdapExplorer(GUILdapSettings settings) {
		this.settings = settings;
		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.message("testconnection"));

		VLayout layout = new VLayout();
		layout.setMargin(2);

		setWidth(820);
		setHeight(500);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();

		String tmp = "<applet name=\"LDAP Explorer\" archive=\"" + Util.contextPath() + "applet/logicaldoc-ldap.jar\" "
				+ "code=\"com.logicaldoc.ldap.explorer.Explorer.class\" " + "width=\"" + (getWidth() - 10)
				+ "\" height=\"" + (getHeight() - 35) + "\">";
		tmp += "<param name=\"url\" value=\"" + settings.getUrl() + "\" />";
		tmp += "<param name=\"user\" value=\"" + (settings.isAnonymous() ? "" : settings.getUsername()) + "\" />";
		tmp += "<param name=\"password\" value=\"" + (settings.isAnonymous() ? "" : settings.getPwd()) + "\" /";
		tmp += "</applet>";

		HTMLFlow applet = new HTMLFlow();
		applet.setContents(tmp);
		applet.setWidth(getWidth() - 10);
		applet.setHeight(getHeight() - 35);

		layout.addMember(applet);
		addItem(layout);
	}
}