package com.logicaldoc.gui.frontend.client.dashboard;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.beans.GUITag;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.widgets.FeatureDisabled;
import com.logicaldoc.gui.frontend.client.search.TagsForm;
import com.logicaldoc.gui.frontend.client.services.TagService;
import com.logicaldoc.gui.frontend.client.services.TagServiceAsync;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.DragAppearance;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.widgets.HeaderControl;
import com.smartgwt.client.widgets.HeaderControl.HeaderIcon;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Portlet;

/**
 * Portlet specialized in listing history records
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class MostUsedTagsPortlet extends Portlet {

	private ListGrid list;

	private TagServiceAsync service = (TagServiceAsync) GWT.create(TagService.class);

	private HLayout container = null;

	public MostUsedTagsPortlet() {
		if (Feature.enabled(Feature.TAGS)) {
			refresh();
		} else
			addItem(new FeatureDisabled());
	}

	private void refresh() {
		setTitle(I18N.message("mostusedtags"));

		if (container != null)
			removeChild(container);

		container = new HLayout();
		container.setWidth100();
		container.setHeight100();
		container.setAlign(Alignment.CENTER);
		container.setMargin(25);
		
		addChild(container);
		
		setShowShadow(true);
		setAnimateMinimize(true);
		setDragAppearance(DragAppearance.OUTLINE);
		setDragOpacity(30);
		setCanDrag(false);
		setCanDrop(false);
		HeaderControl refresh = new HeaderControl(HeaderControl.REFRESH, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				refresh();
			}
		});

		HeaderIcon portletIcon = ItemFactory.newHeaderIcon("tag_blue.png");
		HeaderControl hcicon = new HeaderControl(portletIcon);
		hcicon.setSize(16);

		setHeaderControls(hcicon, HeaderControls.HEADER_LABEL, refresh);
		
		service.getTagCloud(new AsyncCallback<GUITag[]>() {
			@Override
			public void onFailure(Throwable caught) {
				Log.serverError(caught);
			}

			@Override
			public void onSuccess(GUITag[] cloud) {
				ListGridField word = new ListGridField("word", I18N.message("tag"), 150);
				ListGridField count = new ListGridField("count", I18N.message("count"), 60);
				list = new ListGrid();
				list.setEmptyMessage(I18N.message("notitemstoshow"));
				list.setWidth100();
				list.setHeight100();
				list.setFields(word, count);
				list.setSelectionType(SelectionStyle.SINGLE);
				container.addMember(list);

				ListGridRecord[] records = new ListGridRecord[cloud.length];
				for (int i = 0; i < cloud.length; i++) {
					records[i] = new ListGridRecord();
					records[i].setAttribute("word", cloud[i].getTag());
					records[i].setAttribute("count", cloud[i].getCount());
				}
				list.setRecords(records);

				list.addCellDoubleClickHandler(new CellDoubleClickHandler() {
					@Override
					public void onCellDoubleClick(CellDoubleClickEvent event) {
						ListGridRecord record = event.getRecord();
						TagsForm.searchTag(record.getAttributeAsString("word"));
					}
				});
			}
		});
	}

}