package com.logicaldoc.gui.frontend.client.menu;

import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClientEvent;
import com.smartgwt.client.widgets.layout.VStack;

/**
 * This is the about dialog.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class AboutDialog extends Window {
	
	public AboutDialog() {
		super();

		addCloseClickHandler(new CloseClickHandler() {
			@Override
			public void onCloseClick(CloseClientEvent event) {
				destroy();
			}
		});

		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.message("about") + " " + Session.get().getInfo().getProductName());
		setWidth(300);
		setHeight(280);		
		setPadding(5);
		setAutoSize(true);
		centerInPage();

		HTMLPane vspacer1 = new HTMLPane();
		vspacer1.setContents("<div>&nbsp;</div>");
		vspacer1.setPixelSize(100, 5);
		vspacer1.setOverflow(Overflow.HIDDEN);
		
		Img logoImage = ItemFactory.newBrandImg("logo.png");
		logoImage.setWidth(205);
		logoImage.setHeight(40);
		
		Label version = new Label(I18N.message("version") + " " + Session.get().getInfo().getRelease());
		version.setWrap(false);
		version.setHeight(20);
		version.setAlign(Alignment.CENTER);
		
		Label copyright = new Label("&copy; " + Session.get().getInfo().getYear() + " "
				+ Session.get().getInfo().getVendor());		
		copyright.setWrap(false);
		copyright.setHeight(20);
		copyright.setAlign(Alignment.CENTER);
		
		Label trademark = new Label("LogicalDOC e i loghi di LogicalDOC sono marchi registrati di Logical Objects Srl.");				
		trademark.setWidth("90%");
		trademark.setHeight(40);
		trademark.setAlign(Alignment.CENTER);

		// Prepare the website link
		String wsurl = Session.get().getInfo().getUrl();
		String htmlUrl = "<div style='text-align: center;'><a href='"+ wsurl + "' target='_blank'>" + wsurl + "</a></div>";
		HTMLPane sitelink = new HTMLPane();		
		sitelink.setContents(htmlUrl);
		sitelink.setPixelSize(200, 16);
		sitelink.setAlign(Alignment.CENTER);
		sitelink.setLayoutAlign(Alignment.CENTER);
		
		// Prepare the support link
		String support = Session.get().getInfo().getSupport();
		String htmlSupp = "<div style='text-align: center;'><a href='mailto:"+ support + "'>" + support + "</a></div>";
		HTMLPane maillink = new HTMLPane();
		maillink.setContents(htmlSupp);
		maillink.setPixelSize(200, 16);
		maillink.setAlign(Alignment.CENTER);
		maillink.setLayoutAlign(Alignment.CENTER);
		
		HTMLPane vspacer2 = new HTMLPane();
		vspacer2.setContents("<div>&nbsp;</div>");
		vspacer2.setPixelSize(100, 10);
		vspacer2.setOverflow(Overflow.HIDDEN);
		
		Button button = new Button("OK");
		button.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				destroy();
			}
		});
		
		VStack content = new VStack();
		content.setWidth("100%");
		content.setMembersMargin(5);
		content.setTop(20);
		content.setMargin(4);
		content.setAlign(Alignment.CENTER);
		content.setDefaultLayoutAlign(Alignment.CENTER);
		content.setBackgroundColor("#ffffff");
		content.setMembers(vspacer1, logoImage, version, copyright, trademark, sitelink, maillink, vspacer2, button);

		addChild(content);
	}
}
