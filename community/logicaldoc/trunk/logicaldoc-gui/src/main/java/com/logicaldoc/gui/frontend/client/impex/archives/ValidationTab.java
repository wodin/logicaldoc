package com.logicaldoc.gui.frontend.client.impex.archives;

import com.logicaldoc.gui.common.client.beans.GUISostConfig;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.smartgwt.client.widgets.tab.Tab;

public class ValidationTab extends Tab {

	public ValidationTab(GUISostConfig sostConfig) {
		setTitle(I18N.message("sostdoctype." + sostConfig.getDocumentType()));

	}
}
