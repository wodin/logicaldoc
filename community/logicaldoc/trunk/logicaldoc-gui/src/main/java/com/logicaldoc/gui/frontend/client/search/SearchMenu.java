package com.logicaldoc.gui.frontend.client.search;

import com.logicaldoc.gui.common.client.I18N;
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

	private static final int FULLTEXT_SECTION = 0;

	private static SearchMenu instance;

	public static SearchMenu get() {
		if (instance == null)
			instance = new SearchMenu();
		return instance;
	}

	private SearchMenu() {

		String contents = "<b>Severity 1</b> - Critical problem<br>System is unavailable in production or "
				+ "is corrupting data, and the error severely impacts the user's operations."
				+ "<br><br><b>Severity 2</b> - Major problem<br>An important function of the system "
				+ "is not available in production, and the user's operations are restricted."
				+ "<br><br><b>Severity 3</b> - Minor problem<br>Inability to use a function of the "
				+ "system occurs, but it does not seriously affect the user's operations.";

		HTMLFlow html2 = new HTMLFlow();
		html2.setOverflow(Overflow.AUTO);
		html2.setPadding(10);
		html2.setContents(contents);

		setVisibilityMode(VisibilityMode.MUTEX);
		setWidth100();

		SectionStackSection fulltextSection = new SectionStackSection(I18N.getMessage("fulltextsearches"));
		fulltextSection.setExpanded(true);
		fulltextSection.addItem(new FulltextForm());
		addSection(fulltextSection);

		SectionStackSection savedSection = new SectionStackSection(I18N.getMessage("savedsearches"));
		savedSection.setExpanded(false);
		savedSection.addItem(html2);
		addSection(savedSection);
	}

	public void openFulltextSection() {
		expandSection(FULLTEXT_SECTION);
	}
}