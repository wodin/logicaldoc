package com.logicaldoc.gui.frontend.client.search;

import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.frontend.client.folder.FoldersTreePanel;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.VisibilityMode;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.layout.SectionStack;
import com.smartgwt.client.widgets.layout.SectionStackSection;

/**
 * The left menu in the search area
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class SearchMenu extends SectionStack {

	private FoldersTreePanel foldersTree;

	public SearchMenu() {
		HTMLFlow htmlFlow = new HTMLFlow();
		htmlFlow.setOverflow(Overflow.AUTO);
		htmlFlow.setPadding(10);

		String contents = "<b>Severity 1</b> - Critical problem<br>System is unavailable in production or "
				+ "is corrupting data, and the error severely impacts the user's operations."
				+ "<br><br><b>Severity 2</b> - Major problem<br>An important function of the system "
				+ "is not available in production, and the user's operations are restricted."
				+ "<br><br><b>Severity 3</b> - Minor problem<br>Inability to use a function of the "
				+ "system occurs, but it does not seriously affect the user's operations.";

		htmlFlow.setContents(contents);

		setVisibilityMode(VisibilityMode.MUTEX);
		setWidth100();

		SectionStackSection savedSearches = new SectionStackSection(I18N.getMessage("savedsearches"));
		savedSearches.setExpanded(true);
		savedSearches.addItem(htmlFlow);
		addSection(savedSearches);
	}
}