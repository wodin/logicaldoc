package com.logicaldoc.gui.frontend.client.document;

import com.google.gwt.user.client.ui.HTML;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.Util;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClientEvent;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * This popup window is used to sign documents or view signatures.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class SignDialog extends Window {
	private HTML applet = new HTML();

	public SignDialog(String docId, String ids, String names, boolean validation) {
		VLayout layout = new VLayout();
		layout.setMargin(25);

		final boolean archiveValidation = validation;

		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		if (!archiveValidation)
			addCloseClickHandler(new CloseClickHandler() {

				@Override
				public void onCloseClick(CloseClientEvent event) {
					DocumentsPanel.get().refresh();
					destroy();
				}
			});

		setTitle(I18N.message("signdocuments"));
		setWidth(460);
		setHeight(340);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();

		String tmp = "<applet name=\"SignApplet\" archive=\""
				+ Util.contextPath()
				+ "applet/logicaldoc-sign.jar\"  code=\"com.logicaldoc.sign.applet.Signer\" width=\"400\" height=\"300\">";
		tmp += "<param name=\"lang\" value=\"" + I18N.getLocale() + "\" />";
		// tmp += "<param name=\"uploadUrl\" value=\"" + Util.contextPath() +
		// "uploadresource?docId=" + docId + "&sid="
		// + Session.get().getSid() + "&suffix=sign.p7m\" />";
		tmp += "<param name=\"uploadUrl\" value=\"" + Util.contextPath() + "uploadresource?docId={id}&sid="
				+ Session.get().getSid() + "&suffix=sign.p7m\" />";
		tmp += "<param name=\"downloadUrl\" value=\"" + Util.contextPath() + "download?docId=" + docId + "&sid="
				+ Session.get().getSid() + "\" />";
		tmp += "<param name=\"ids\" value=\"" + ids + "\" />";
		tmp += "<param name=\"names\" value=\"" + names + "\" />";
		tmp += "<param name=\"forceCRLDownload\" value=\"true\" />";
		tmp += "</applet>";

		applet.setHTML(tmp);
		applet.setWidth("420px");
		applet.setHeight("320px");

		layout.addMember(applet);
		addChild(layout);
	}
}