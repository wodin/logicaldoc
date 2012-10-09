package com.logicaldoc.gui.frontend.client.dashboard;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDashlet;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.frontend.client.dashboard.dashlet.Dashlet;
import com.logicaldoc.gui.frontend.client.dashboard.dashlet.DashletSelector;
import com.logicaldoc.gui.frontend.client.services.SettingService;
import com.logicaldoc.gui.frontend.client.services.SettingServiceAsync;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.PortalLayout;
import com.smartgwt.client.widgets.layout.Portlet;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

/**
 * User dashboard that displays several portlets like a portal page.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class UserDashboard extends VLayout {

	private SettingServiceAsync service = (SettingServiceAsync) GWT.create(SettingService.class);

	private static UserDashboard instance;

	private PortalLayout portal = null;

	public UserDashboard() {
		setWidth100();
		setHeight100();

		ToolStrip toolStrip = new ToolStrip();
		toolStrip.setHeight(20);
		toolStrip.setWidth100();
		toolStrip.addSpacer(2);

		ToolStripButton addDashlet = new ToolStripButton();
		addDashlet.setTitle(I18N.message("adddashlet"));
		toolStrip.addButton(addDashlet);
		addDashlet.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				DashletSelector selector = new DashletSelector(portal);
				selector.show();
			}
		});

		ToolStripButton save = new ToolStripButton();
		save.setTitle(I18N.message("save"));
		toolStrip.addButton(save);
		save.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				save();
			}
		});

		toolStrip.addFill();
		addMember(toolStrip, 0);

		refresh();
	}

	public void refresh() {
		if (portal != null)
			removeMember(portal);

		portal = new PortalLayout();
		portal.setShowColumnMenus(false);
		portal.setShowEdges(false);
		portal.setShowShadow(false);
		portal.setWidth100();
		portal.setHeight100();
		portal.setColumnBorder("0px");
		portal.setColumnOverflow(Overflow.AUTO);
		portal.setOverflow(Overflow.VISIBLE);
		addMember(portal, 1);

		for (GUIDashlet gd : Session.get().getUser().getDashlets()) {
			Dashlet dashlet = Dashlet.getDashlet(gd.getId());
			portal.addPortlet(dashlet, gd.getColumn(), gd.getRow(), gd.getIndex());
		}
	}

	public static UserDashboard get() {
		if (instance == null)
			instance = new UserDashboard();
		return instance;
	}

	/**
	 * Persistently saves the portal layout
	 */
	public void save() {
		Portlet[][][] portlets = portal.getPortletArray();

		GUIDashlet[] dashlets = new GUIDashlet[portal.getPortlets().length];

		int q = 0;
		for (int column = 0; column < portlets.length; column++)
			for (int row = 0; row < portlets[column].length; row++)
				for (int i = 0; i < portlets[column][row].length; i++) {
					Dashlet dashlet = (Dashlet) portlets[column][row][i];
					dashlets[q++] = new GUIDashlet(dashlet.getId(), column, row, i);
				}

		Session.get().getUser().setDashlets(dashlets);
		service.saveDashlets(Session.get().getSid(), dashlets, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				Log.serverError(caught);
			}

			@Override
			public void onSuccess(Void ret) {
				Log.info(I18N.message("settingssaved"), null);
			}
		});
	}
}