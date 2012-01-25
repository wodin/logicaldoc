package com.logicaldoc.gui.frontend.client.panels;

import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.layout.HLayout;

/**
 * The Login entry point
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class TopPanel extends HLayout {
	public TopPanel() {
		setStyleName("topPanel");
		setWidth100();
		setHeight(45);

		// Prepare the logo image to be shown inside the banner
		Img logoImage = ItemFactory.newBrandImg("logo_head.png");
		logoImage.setStyleName("logo_head");
		logoImage.setWidth(205);
		logoImage.setHeight(40);
		addMember(logoImage);
	
		Img separator = ItemFactory.newImg("blank.png");
		separator.setWidth100();
		addMember(separator);
		
		// Prepare the OEM logo image to be shown inside the banner
		Img logoOemImage = ItemFactory.newBrandImg("logo_head_oem.png");
		logoOemImage.setStyleName("logo_head_oem");
		logoOemImage.setWidth(205);
		logoOemImage.setHeight(40);
		addMember(logoOemImage);
	}
}