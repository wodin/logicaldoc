package com.logicaldoc.gui.frontend.client.document;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.frontend.client.folder.Navigator;
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
public class BulkCheckinDialog extends Window {
	private HTMLFlow applet = new HTMLFlow();

	protected DocumentServiceAsync service = (DocumentServiceAsync) GWT.create(DocumentService.class);

	public BulkCheckinDialog(final long[] docIds) {
		VLayout layout = new VLayout();
		layout.setTop(23);
		layout.setLeft(5);

		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.message("bulkcheckout"));
		setWidth(412);
		setHeight(322);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();

		String tmp = "<applet name=\"CheckinApplet\" archive=\""
				+ Util.contextPath()
				+ "applet/logicaldoc-enterprise-core.jar\"  code=\"com.logicaldoc.enterprise.checkin.CheckinApplet\" width=\"400\" height=\"290\">";
		tmp += "<param name=\"language\" value=\"" + I18N.getLocale() + "\" />";
		tmp += "<param name=\"sid\" value=\"" + Session.get().getSid() + "\" />";
		tmp += "<param name=\"baseUrl\" value=\"" + Util.contextPath() + "\" />";
		tmp += "<param name=\"documents\" value=\"" + docIds.toString().substring(1).replaceAll("]", "") + "\" />";
		tmp += "</applet>";

		applet.setContents(tmp);
		applet.setWidth("400px");
		applet.setHeight("290px");

		layout.addMember(applet);
		addChild(layout);

		addCloseClickHandler(new CloseClickHandler() {

			@Override
			public void onCloseClick(CloseClickEvent event) {
				service.unlock(Session.get().getSid(), docIds, new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable t) {
						//Log.serverError(t);
						destroy();
						Navigator.get().reload();
					}

					@Override
					public void onSuccess(Void arg) {
						destroy();
						Navigator.get().reload();
					}
				});
			}
		});
	}
}