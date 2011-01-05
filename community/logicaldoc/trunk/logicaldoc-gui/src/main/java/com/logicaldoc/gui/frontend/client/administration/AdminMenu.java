package com.logicaldoc.gui.frontend.client.administration;

import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.Menu;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.frontend.client.impex.ImpexMenu;
import com.logicaldoc.gui.frontend.client.metadata.MetadataMenu;
import com.logicaldoc.gui.frontend.client.security.SecurityMenu;
import com.logicaldoc.gui.frontend.client.settings.SettingsMenu;
import com.logicaldoc.gui.frontend.client.system.SystemMenu;
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
		if (Menu.enabled(Menu.SECURITY))
			addSection(securitySection);

		if ((Feature.visible(Feature.TEMPLATE) || Feature.visible(Feature.WORKFLOW) || Feature
				.visible(Feature.TAGS_ADMIN)) && Menu.enabled(Menu.METADATA)) {
			SectionStackSection metadataSection = new SectionStackSection(I18N.message("documentmetadata"));
			metadataSection.setExpanded(false);
			metadataSection.addItem(new MetadataMenu());
			addSection(metadataSection);
		}

		if ((Feature.visible(Feature.ARCHIVES) || Feature.visible(Feature.EMAIL_IMPORT)
				|| Feature.visible(Feature.IMPORT_LOCAL_FOLDERS) || Feature.visible(Feature.IMPORT_REMOTE_FOLDERS))
				&& Menu.enabled(Menu.IMPEX)) {
			SectionStackSection impexSection = new SectionStackSection(I18N.message("impex"));
			impexSection.setExpanded(false);
			impexSection.addItem(new ImpexMenu());
			addSection(impexSection);
		}

		if (Menu.enabled(Menu.SETTINGS)) {
			SectionStackSection sysConfSection = new SectionStackSection(I18N.message("settings"));
			sysConfSection.setExpanded(false);
			sysConfSection.addItem(new SettingsMenu());
			addSection(sysConfSection);
		}
	}
}