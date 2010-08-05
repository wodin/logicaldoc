package com.logicaldoc.gui.frontend.client.search;

import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.widgets.FeatureDisabled;
import com.smartgwt.client.types.VisibilityMode;
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

	private static final int TAGS_SECTION = 1;

	private static final int PARAMETRIC_SECTION = 2;

	private static SearchMenu instance;

	public static SearchMenu get() {
		if (instance == null)
			instance = new SearchMenu();
		return instance;
	}

	private SearchMenu() {
		setVisibilityMode(VisibilityMode.MUTEX);
		setWidth100();

		SectionStackSection fulltextSection = new SectionStackSection(I18N.message("fulltextsearches"));
		fulltextSection.setExpanded(true);
		fulltextSection.addItem(new FulltextForm());
		addSection(fulltextSection);

		SectionStackSection tagsSection = new SectionStackSection(I18N.message("tags"));
		tagsSection.addItem(TagsForm.get());
		addSection(tagsSection);

		if (Feature.visible(Feature.PARAMETRIC_SEARCHES)) {
			SectionStackSection parametricSection = new SectionStackSection(I18N.message("parametricsearches"));
			if (Feature.enabled(Feature.PARAMETRIC_SEARCHES))
				parametricSection.addItem(new ParametricForm());
			else
				parametricSection.addItem(new FeatureDisabled());
			addSection(parametricSection);
		}

		SectionStackSection savedSection = new SectionStackSection(I18N.message("savedsearches"));
		savedSection.setExpanded(false);
		if (Feature.enabled(12)) {
			savedSection.addItem(SavedSearchesPanel.get());
			addSection(savedSection);
		} else if (Feature.showDisabled()) {
			savedSection.addItem(new FeatureDisabled());
			addSection(savedSection);
		}
	}

	public void openFulltextSection() {
		expandSection(FULLTEXT_SECTION);
	}

	public void openTagsSection() {
		expandSection(TAGS_SECTION);
	}

	public void openParametricSection() {
		if (Feature.visible(Feature.PARAMETRIC_SEARCHES))
			expandSection(PARAMETRIC_SECTION);
	}
}