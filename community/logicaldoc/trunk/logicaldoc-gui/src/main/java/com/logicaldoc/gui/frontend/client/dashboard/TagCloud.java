package com.logicaldoc.gui.frontend.client.dashboard;

import gdurelle.tagcloud.client.tags.WordTag;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUITag;
import com.logicaldoc.gui.frontend.client.services.TagService;
import com.logicaldoc.gui.frontend.client.services.TagServiceAsync;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.events.VisibilityChangedEvent;
import com.smartgwt.client.widgets.events.VisibilityChangedHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * This represent a tag cloud using a 3-D ball.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class TagCloud extends VLayout {

	private TagServiceAsync tagService = (TagServiceAsync) GWT.create(TagService.class);

	private HTMLFlow container = null;

	private Layout layout = null;

	public TagCloud() {
		addVisibilityChangedHandler(new VisibilityChangedHandler() {

			@Override
			public void onVisibilityChanged(VisibilityChangedEvent event) {
				refresh();
			}
		});
	}

	public void refresh() {
		tagService.getTagCloud(new AsyncCallback<GUITag[]>() {
			@Override
			public void onFailure(Throwable arg0) {

			}

			@Override
			public void onSuccess(GUITag[] tags) {
				refresh(tags);
			}
		});
	}

	private void refresh(GUITag[] tags) {
		if (layout != null) {
			removeMember(layout);
			layout = null;
		}

		gdurelle.tagcloud.client.tags.TagCloud tagCloud = new gdurelle.tagcloud.client.tags.TagCloud();

		for (GUITag tag : tags) {
			WordTag word = new WordTag(tag.getTag(), "javascript:searchTag(\"" + tag.getTag() + "\");");
			word.setNumberOfOccurences(Integer.parseInt(Long.toString(tag.getCount())));
			tagCloud.addWord(word);
		}

		tagCloud.setPixelSize(getWidth() - 20, getHeight() - 2);

		layout = new HLayout();
		layout.setWidth100();
		layout.setHeight100();
		layout.addMember(tagCloud.asWidget());

		addMember(layout);
	}
}