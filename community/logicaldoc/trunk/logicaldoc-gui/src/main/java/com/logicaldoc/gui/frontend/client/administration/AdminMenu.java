package com.logicaldoc.gui.frontend.client.administration;

import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.frontend.client.security.SecurityMenu;
import com.logicaldoc.gui.frontend.client.security.SysConfMenu;
import com.smartgwt.client.types.VisibilityMode;
import com.smartgwt.client.widgets.layout.SectionStack;
import com.smartgwt.client.widgets.layout.SectionStackSection;

/**
 * The left menu in the administration area
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class AdminMenu extends SectionStack {

	private static AdminMenu instance;

	public static AdminMenu get() {
		if (instance == null)
			instance = new AdminMenu();
		return instance;
	}

	private AdminMenu() {
		setVisibilityMode(VisibilityMode.MUTEX);
		setWidth100();

		SectionStackSection securitySection = new SectionStackSection(I18N.getMessage("security"));
		securitySection.setExpanded(true);
		securitySection.addItem(new SecurityMenu());
		addSection(securitySection);

		SectionStackSection sysConfSection = new SectionStackSection(I18N.getMessage("sysconf"));
		sysConfSection.setExpanded(false);
		sysConfSection.addItem(new SysConfMenu());
		addSection(sysConfSection);
	}
}