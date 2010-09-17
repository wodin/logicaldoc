package com.logicaldoc.gui.frontend.client.system;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIParameter;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.frontend.client.services.SystemService;
import com.logicaldoc.gui.frontend.client.services.SystemServiceAsync;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * General adnministration panel with statistics
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class GeneralPanel extends VLayout {

	private SystemServiceAsync service = (SystemServiceAsync) GWT.create(SystemService.class);

	public GeneralPanel() {
		setWidth100();
		setHeight100();
		setMembersMargin(10);

		service.getStatistics(Session.get().getSid(), I18N.getLocale(), new AsyncCallback<GUIParameter[][]>() {

			@Override
			public void onFailure(Throwable caught) {
				Log.serverError(caught);
			}

			@Override
			public void onSuccess(GUIParameter[][] parameters) {
				Label lastUpdateLabel = new Label("<b>"
						+ I18N.message("statisticslastupdate", parameters[3][0].getValue()) + "</b>");
				lastUpdateLabel.setShowEdges(false);
				lastUpdateLabel.setHeight(50);
				lastUpdateLabel.setAlign(Alignment.RIGHT);

				PieStats charts = new PieStats(parameters);

				GeneralBottom bottom = new GeneralBottom();
				bottom.setHeight(200);

				setMembers(lastUpdateLabel, charts, bottom);
			}
		});
	}
}
