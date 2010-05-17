package com.logicaldoc.gui.frontend.client.system;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIParameter;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.frontend.client.services.SystemService;
import com.logicaldoc.gui.frontend.client.services.SystemServiceAsync;
import com.smartgwt.client.widgets.layout.HLayout;

/**
 * Shows the charts pies
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class PieStats extends HLayout {
	
	private SystemServiceAsync service = (SystemServiceAsync) GWT.create(SystemService.class);
	
	public PieStats() {
		super();
		setMembersMargin(30);
		setWidth100();
		setHeight("10%");
		
		service.getStatistics(Session.get().getSid(), new AsyncCallback<GUIParameter[][]>() {

			@Override
			public void onFailure(Throwable caught) {
				Log.serverError(caught);
			}

			@Override
			public void onSuccess(GUIParameter[][] parameters) {
				addMember(new StatisticWidget(I18N.getMessage("repository"), parameters[0]));
				addMember(new StatisticWidget(I18N.getMessage("documents"), parameters[1]));
				addMember(new StatisticWidget(I18N.getMessage("folders"), parameters[2]));
			}
		});
	}
}
