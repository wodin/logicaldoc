package com.logicaldoc.gui.frontend.client.dashboard.dashlet;

import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.layout.PortalLayout;
import com.smartgwt.client.widgets.layout.Portlet;

/**
 * This dialog allows the selection of a portlet
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.6
 */
public class DashletSelector extends Window {
	private PortalLayout portal;

	public DashletSelector(PortalLayout portal) {
		this.portal = portal;

		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);

		setTitle(I18N.message("dashletselector"));

		setWidth(280);
		setHeight(100);
		setMembersMargin(6);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();
		setPadding(6);
		setAutoSize(true);

		final DynamicForm form = new DynamicForm();
		form.setTitleOrientation(TitleOrientation.TOP);
		form.setNumCols(1);

		SelectItem dashlet;
		dashlet = ItemFactory.newDashletSelector("dashlet", I18N.message("dashlet"));

		dashlet.setEndRow(true);

		ButtonItem select = new ButtonItem();
		select.setTitle(I18N.message("add"));
		select.setAutoFit(true);
		select.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				// Compute each column size (number of dashlets)
				int[] size = new int[DashletSelector.this.portal.getNumColumns()];
				for (int i = 0; i < size.length; i++)
					size[i] = 0;
				Portlet[][][] portlets = DashletSelector.this.portal.getPortletArray();
				for (int column = 0; column < portlets.length; column++)
					for (int row = 0; row < portlets[column].length; row++)
						size[column] += portlets[column][row].length;

				// Find the column with less dashlets
				int smallerColumn = 0;
				int smallerSize = 9999;
				for (int i = 0; i < size.length; i++) {
					if (size[i] < smallerSize) {
						smallerColumn = i;
						smallerSize = size[i];
					}
				}

				Dashlet dashlet = Dashlet.getDashlet(Integer.parseInt(form.getValueAsString("dashlet")));
				DashletSelector.this.portal.addPortlet(dashlet, smallerColumn, 0);
			}
		});

		form.setItems(dashlet, select);
		addItem(form);
	}
}
