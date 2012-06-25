package com.logicaldoc.gui.frontend.client.security;

import com.logicaldoc.gui.common.client.beans.GUILdapSettings;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.Util;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.ResizedEvent;
import com.smartgwt.client.widgets.events.ResizedHandler;
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

		setWidth(820);
		setHeight(500);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();
		setMargin(2);

		layout = new VLayout(5);
		layout.setTop(20);
		layout.setMargin(5);

		try {
			reload();
		} catch (Throwable t) {
			SC.warn(t.getMessage());
		}
		addCloseClickHandler(new CloseClickHandler() {
			@Override
			public void onCloseClick(CloseClickEvent event) {
				destroy();
			}
		});

		addResizedHandler(new ResizedHandler() {
			@Override
			public void onResized(ResizedEvent event) {
				reload();
			}
		});

		addChild(layout);
	}

	private void reload() {
		HTMLFlow html = new HTMLFlow();
		String tmp = "<applet name=\"LDAP Explorer\" archive=\"" + Util.contextPath() + "applet/logicaldoc-ldap.jar\" "
				+ "code=\"com.logicaldoc.ldap.explorer.Explorer.class\" " + "width=\"" + (getWidth() - 10)
				+ "\" height=\"" + (getHeight() - 10) + "\">";
		tmp += "<param name=\"url\" value=\"" + settings.getUrl() + "\" />";
		tmp += "<param name=\"user\" value=\"" + (settings.isAnonymous() ? "" : settings.getUsername()) + "\" />";
		tmp += "<param name=\"password\" value=\"" + (settings.isAnonymous() ? "" : settings.getPwd()) + "\" /";
		tmp += "</applet>";
		html.setContents(tmp);
		layout.addMember(html);
	}
}