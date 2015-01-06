package com.logicaldoc.gui.frontend.client.document;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.frontend.client.folder.FolderNavigator;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.DocumentServiceAsync;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * This popup window is used to handle a bulk checkout.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.8
 */
public class BulkCheckoutDialog extends Window {
	private HTMLFlow applet = new HTMLFlow();

	protected DocumentServiceAsync service = (DocumentServiceAsync) GWT.create(DocumentService.class);

	public BulkCheckoutDialog(final long[] docIds) {
		VLayout layout = new VLayout();
		layout.setMargin(2);

		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.message("bulkcheckout"));
		setWidth(412);
		setHeight(322);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();

		String docIdsStr = "";
		for (long id : docIds) {
			if (!docIdsStr.isEmpty())
				docIdsStr += ",";
			docIdsStr += id;
		}

		String tmp = "<applet name=\"CheckinApplet\" archive=\""
				+ Util.contextPath()
				+ "applet/logicaldoc-enterprise-core.jar\"  code=\"com.logicaldoc.enterprise.checkin.CheckinApplet\" width=\""
				+ (getWidth() - 10) + "\" height=\"" + (getHeight() - 35) + "\">";
		tmp += "<param name=\"language\" value=\"" + I18N.getLocale() + "\" />";
		tmp += "<param name=\"sid\" value=\"" + Session.get().getSid() + "\" />";
		tmp += "<param name=\"baseUrl\" value=\"" + Util.contextPath() + "\" />";
		tmp += "<param name=\"documents\" value=\"" + docIdsStr + "\" />";
		tmp += "</applet>";

		applet.setContents(tmp);
		applet.setWidth(getWidth() - 10);
		applet.setHeight(getHeight() - 35);

		layout.addMember(applet);
		addItem(layout);

		addCloseClickHandler(new CloseClickHandler() {

			@Override
			public void onCloseClick(CloseClickEvent event) {
				service.unlock(Session.get().getSid(), docIds, new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable t) {
						// Log.serverError(t);
						destroy();
						FolderNavigator.get().reload();
					}

					@Override
					public void onSuccess(Void arg) {
						destroy();
						FolderNavigator.get().reload();
					}
				});
			}
		});
	}
}