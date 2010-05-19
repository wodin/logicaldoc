package com.logicaldoc.gui.frontend.client.dashboard;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUITag;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.frontend.client.services.SearchService;
import com.logicaldoc.gui.frontend.client.services.SearchServiceAsync;
import com.smartgwt.client.types.Cursor;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.layout.Portlet;

/**
 * Portlet specialized in showing the tag cloud.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class TagCloudPortlet extends Portlet {
	private SearchServiceAsync service = (SearchServiceAsync) GWT.create(SearchService.class);

	public TagCloudPortlet() {
		service.getTagCloud(new AsyncCallback<GUITag[]>() {
			@Override
			public void onFailure(Throwable caught) {
				Log.serverError(caught);
			}

			@Override
			public void onSuccess(GUITag[] cloud) {
				for (GUITag tag : cloud) {
					Label t = new Label(tag.getTag() + "&nbsp;&nbsp;&nbsp;");
					t.setStyleName("cloud" + tag.getScale());
					t.setCursor(Cursor.HAND);
				}
			}
		});
	}
}