package com.logicaldoc.gui.frontend.client.search;

import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.Session;
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

		HTMLFlow disabled = new HTMLFlow();
		disabled.setOverflow(Overflow.AUTO);
		disabled.setPadding(10);
		disabled.setContents("<b>" + I18N.getMessage("featuredisabled") + "</b>");

		setVisibilityMode(VisibilityMode.MUTEX);
		setWidth100();

		SectionStackSection fulltextSection = new SectionStackSection(I18N.getMessage("fulltextsearches"));
		fulltextSection.setExpanded(true);
		fulltextSection.addItem(new FulltextForm());
		addSection(fulltextSection);

		SectionStackSection savedSection = new SectionStackSection(I18N.getMessage("savedsearches"));
		savedSection.setExpanded(false);
		if (!Session.get().isFeatureEnabled("ENTERPRISE"))
			savedSection.addItem(disabled);
		else
			savedSection.addItem(disabled);
		addSection(savedSection);
	}

	public void openFulltextSection() {
		expandSection(FULLTEXT_SECTION);
	}
}