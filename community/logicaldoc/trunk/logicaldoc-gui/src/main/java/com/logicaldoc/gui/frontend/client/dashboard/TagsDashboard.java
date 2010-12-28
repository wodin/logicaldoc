package com.logicaldoc.gui.frontend.client.dashboard;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUITag;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.frontend.client.services.TagService;
import com.logicaldoc.gui.frontend.client.services.TagServiceAsync;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

/**
 * This panel shows the list of system messages and allows the selection.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class TagsDashboard extends VLayout {
	private TagServiceAsync service = (TagServiceAsync) GWT.create(TagService.class);

	private TagCloud tagCloud = null;

	public TagsDashboard() {
		setHeight100();

		ToolStrip toolStrip = new ToolStrip();
		toolStrip.setHeight(20);
		toolStrip.setWidth100();
		toolStrip.addSpacer(2);

		ToolStripButton refresh = new ToolStripButton();
		refresh.setTitle(I18N.message("refresh"));
		toolStrip.addButton(refresh);
		refresh.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				refresh();
			}
		});
		toolStrip.addFill();
		addMember(toolStrip);
		setAlign(Alignment.CENTER);
		setAlign(VerticalAlignment.TOP);
		setWidth("99%");
		refresh();
	}

	private void refresh() {
		service.getTagCloud(new AsyncCallback<GUITag[]>() {
			@Override
			public void onFailure(Throwable caught) {
				Log.serverError(caught);
			}

			@Override
			public void onSuccess(GUITag[] cloud) {
				TagCloud tc = new TagCloud();
				tc.setWidth(Window.getClientWidth() - 30);
				tc.setHeight(Window.getClientHeight() - 250);
				tc.setMaxNumberOfWords(cloud.length);
				for (GUITag tag : cloud) {
					tag.setLink("javascript:searchTag(\"" + tag.getTag() + "\")");
					tc.addWord(tag);
				}
				if (tagCloud != null)
					removeMember(tagCloud);
				tagCloud = tc;
				addMember(tagCloud);
			}
		});
	}
}