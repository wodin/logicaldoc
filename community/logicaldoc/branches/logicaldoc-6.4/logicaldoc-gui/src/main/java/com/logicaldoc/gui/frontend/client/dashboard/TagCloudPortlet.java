package com.logicaldoc.gui.frontend.client.dashboard;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.beans.GUITag;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.widgets.FeatureDisabled;
import com.logicaldoc.gui.frontend.client.services.TagService;
import com.logicaldoc.gui.frontend.client.services.TagServiceAsync;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.DragAppearance;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.widgets.HeaderControl;
import com.smartgwt.client.widgets.HeaderControl.HeaderIcon;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Portlet;

/**
 * Portlet specialized in showing the tag cloud in a 3-D fashon.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class TagCloudPortlet extends Portlet {
	private TagServiceAsync service = (TagServiceAsync) GWT.create(TagService.class);

	private HLayout container = null;

	public TagCloudPortlet() {
		if (Feature.enabled(Feature.TAGS)) {
			refresh();
		} else
			addItem(new FeatureDisabled());
	}

	public void refresh() {
		if (container != null)
			removeChild(container);

		HeaderControl refresh = new HeaderControl(HeaderControl.REFRESH, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				refresh();
			}
		});

		setTitle(I18N.message("tagcloud"));

		HeaderIcon portletIcon = ItemFactory.newHeaderIcon("tag_blue.png");
		HeaderControl hcicon = new HeaderControl(portletIcon);
		hcicon.setSize(16);

		setHeaderControls(hcicon, HeaderControls.HEADER_LABEL, refresh);

		setCanDrag(false);
		setCanDrop(false);
		setDragAppearance(DragAppearance.OUTLINE);
		setDragOpacity(30);

		container = new HLayout();
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
				tc.setWidth(getWidth() - 20);
				tc.setHeight(getHeight() - 20);
				tc.setMaxNumberOfWords(cloud.length);
				for (GUITag tag : cloud) {
					tag.setLink("javascript:searchTag(\"" + tag.getTag() + "\")");
					tc.addWord(tag);
				}
				container.addMember(tc);
			}
		});
	}
}