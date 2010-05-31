package com.logicaldoc.gui.frontend.client.dashboard;

import gdurelle.tagcloud.client.tags.TagCloud;
import gdurelle.tagcloud.client.tags.WordTag;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUITag;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.frontend.client.services.SearchService;
import com.logicaldoc.gui.frontend.client.services.SearchServiceAsync;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.widgets.HeaderControl;
import com.smartgwt.client.widgets.HeaderControl.HeaderIcon;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Portlet;

/**
 * Portlet specialized in showing the tag cloud.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class TagCloudPortlet extends Portlet {
	private SearchServiceAsync service = (SearchServiceAsync) GWT.create(SearchService.class);

	private HLayout container = new HLayout();

	public TagCloudPortlet() {
		setTitle(I18N.message("tagcloud"));
		HeaderIcon portletIcon = ItemFactory.newHeaderIcon("tag_blue.png");
		setHeaderControls(new HeaderControl(portletIcon), HeaderControls.HEADER_LABEL, HeaderControls.MINIMIZE_BUTTON);

		container.setWidth100();
		container.setHeight100();
		container.setAlign(Alignment.CENTER);
		container.setMargin(25);

		addChild(container);

		service.getTagCloud(new AsyncCallback<GUITag[]>() {
			@Override
			public void onFailure(Throwable caught) {
				Log.serverError(caught);
			}

			@Override
			public void onSuccess(GUITag[] cloud) {
				TagCloud tc = new TagCloud();
				tc.setWidth("95%");
				tc.setMaxNumberOfWords(cloud.length);
				for (GUITag tag : cloud) {
					WordTag wordTag = new WordTag(tag.getTag());
					wordTag.setNumberOfOccurences(tag.getScale());
					wordTag.setLink("javascript:window.searchTag(\"" + tag.getTag() + "\");");
					tc.addWord(wordTag);
				}
				container.addMember(tc);
			}
		});
	}
}