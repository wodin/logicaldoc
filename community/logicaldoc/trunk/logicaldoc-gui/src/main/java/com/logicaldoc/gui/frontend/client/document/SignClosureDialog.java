package com.logicaldoc.gui.frontend.client.document;

import com.google.gwt.user.client.ui.HTML;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIArchive;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.frontend.client.impex.archives.ExportArchivesList;
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
public class SignClosureDialog extends Window {
	private HTML applet = new HTML();

	private ExportArchivesList archivesList = null;

	public SignClosureDialog(ExportArchivesList list, String id, String name) {
		this.archivesList = list;
		final String archiveId = id;

		VLayout layout = new VLayout();
		layout.setMargin(25);

		addCloseClickHandler(new CloseClickHandler() {
			@Override
			public void onCloseClick(CloseClientEvent event) {
				destroy();
				archivesList.refresh(GUIArchive.TYPE_DEFAULT, false);
				archivesList.showDetails(Long.parseLong(archiveId), false);
			}
		});

		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);

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
		tmp += "<param name=\"uploadUrl\" value=\"" + Util.contextPath() + "closearchive?archiveId={id}&sid="
				+ Session.get().getSid() + "\" />";
		tmp += "<param name=\"downloadUrl\" value=\"" + Util.contextPath() + "downloadarchive?archiveId={id}&sid="
				+ Session.get().getSid() + "\" />";
		tmp += "<param name=\"ids\" value=\"" + id + "\" />";
		tmp += "<param name=\"names\" value=\"" + name + "\" />";
		tmp += "<param name=\"forceCRLDownload\" value=\"false\" />";
		tmp += "<param name=\"timestampRequested\" value=\"true\" />";
		tmp += "<param name=\"checkcrl\" value=\"" + Session.get().getInfo().getConfig("checkcrl") + "\" />";
		tmp += "</applet>";

		applet.setHTML(tmp);
		applet.setWidth("420px");
		applet.setHeight("320px");

		layout.addMember(applet);
		addChild(layout);
	}
}