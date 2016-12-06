package com.logicaldoc.gui.frontend.client.system;

import com.google.gwt.core.client.GWT;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.common.client.util.WindowUtils;
import com.smartgwt.client.types.ContentsType;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

/**
 * Main log panel
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class LogPanel extends VLayout {
	public LogPanel(String appender) {
		setHeight100();

		final HTMLPane htmlPane = new HTMLPane();
		htmlPane.setWidth100();
		htmlPane.setHeight100();
		htmlPane.setShowEdges(true);
		htmlPane.setContentsURL(GWT.getHostPageBaseURL() + "log?appender=" + appender);
		htmlPane.setContentsType(ContentsType.PAGE);

		ToolStrip toolStrip = new ToolStrip();
		toolStrip.setHeight(20);
		toolStrip.setWidth100();
		toolStrip.addSpacer(2);
		ToolStripButton refresh = new ToolStripButton(I18N.message("refresh"));
		toolStrip.addButton(refresh);
		refresh.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				htmlPane.redraw();
				htmlPane.setWidth100();
				htmlPane.setHeight100();
			}
		});
		ToolStripButton download = new ToolStripButton(I18N.message("downloadlogs"));
		toolStrip.addButton(download);
		download.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				try {
					WindowUtils.openUrl(Util.contextPath() + "log?appender=all");
				} catch (Throwable t) {

				}
			}
		});
		toolStrip.addFill();
		addMember(toolStrip);
		addMember(htmlPane);
		draw();
	}
}
