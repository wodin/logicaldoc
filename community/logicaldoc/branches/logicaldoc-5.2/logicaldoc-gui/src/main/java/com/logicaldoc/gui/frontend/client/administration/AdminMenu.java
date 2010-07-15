package com.logicaldoc.gui.frontend.client.administration;

import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.frontend.client.security.SecurityMenu;
import com.logicaldoc.gui.frontend.client.settings.SettingsMenu;
import com.logicaldoc.gui.frontend.client.system.SystemMenu;
import com.logicaldoc.gui.frontend.client.template.MetadataAndWorkflowMenu;
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

		SectionStackSection systemSection = new SectionStackSection(I18N.message("system"));
		systemSection.setExpanded(true);
		systemSection.addItem(new SystemMenu());
		addSection(systemSection);

		SectionStackSection securitySection = new SectionStackSection(I18N.message("security"));
		securitySection.setExpanded(false);
		securitySection.addItem(new SecurityMenu());
		addSection(securitySection);
		
		SectionStackSection metadataAndWorkflowSection = new SectionStackSection(I18N.message("metadataandworkflow"));
		metadataAndWorkflowSection.setExpanded(false);
		metadataAndWorkflowSection.addItem(new MetadataAndWorkflowMenu());
		addSection(metadataAndWorkflowSection);

		SectionStackSection sysConfSection = new SectionStackSection(I18N.message("settings"));
		sysConfSection.setExpanded(false);
		sysConfSection.addItem(new SettingsMenu());
		addSection(sysConfSection);
	}
}