package com.logicaldoc.gui.frontend.client.search;

import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.widgets.FeatureDisabled;
import com.smartgwt.client.types.VisibilityMode;
import com.smartgwt.client.widgets.layout.SectionStack;
import com.smartgwt.client.widgets.layout.SectionStackSection;
import com.smartgwt.client.widgets.layout.events.SectionHeaderClickEvent;
import com.smartgwt.client.widgets.layout.events.SectionHeaderClickHandler;

/**
 * The left menu in the search area
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class SearchMenu extends SectionStack {

	private static final int FULLTEXT_SECTION = 0;

	private static final int FOLDERS_SECTION = 1;

	private static final int TAGS_SECTION = 2;

	private static final int PARAMETRIC_SECTION = 3;

	private static SearchMenu instance;

	public static SearchMenu get() {
		if (instance == null)
			instance = new SearchMenu();
		return instance;
	}

	private SearchMenu() {
		setVisibilityMode(VisibilityMode.MUTEX);

		SectionStackSection fulltextSection = new SectionStackSection(I18N.message("fulltextsearches"));
		fulltextSection.setName("fulltext");
		fulltextSection.setExpanded(true);
		fulltextSection.setItems(new FulltextForm());
		addSection(fulltextSection);

		SectionStackSection foldersSection = new SectionStackSection(I18N.message("folders"));
		foldersSection.setName("folders");
		foldersSection.setExpanded(false);
		foldersSection.setItems(new FoldersForm());
		addSection(foldersSection);

		if (Feature.visible(Feature.TAGS)) {
			SectionStackSection tagsSection = new SectionStackSection(I18N.message("tags"));
			tagsSection.setName("tags");
			if (Feature.enabled(Feature.TAGS))
				tagsSection.setItems(TagsForm.get());
			else
				tagsSection.setItems(new FeatureDisabled());
			addSection(tagsSection);
		}

		if (Feature.visible(Feature.PARAMETRIC_SEARCHES)) {
			SectionStackSection parametricSection = new SectionStackSection(I18N.message("parametricsearches"));
			parametricSection.setName("parametric");
			if (Feature.enabled(Feature.PARAMETRIC_SEARCHES))
				parametricSection.setItems(ParametricForm.get());
			else
				parametricSection.setItems(new FeatureDisabled());
			addSection(parametricSection);
		}

		if (Feature.visible(Feature.SAVED_SEARCHES)) {
			SectionStackSection savedSection = new SectionStackSection(I18N.message("savedsearches"));
			savedSection.setName("saved");
			savedSection.setExpanded(false);
			if (Feature.enabled(Feature.SAVED_SEARCHES))
				savedSection.setItems(SavedSearchesPanel.get());
			else
				savedSection.setItems(new FeatureDisabled());
			addSection(savedSection);
		}
	}

	public void openFulltextSection() {
		expandSection(FULLTEXT_SECTION);
	}

	public void openTagsSection() {
		expandSection(TAGS_SECTION);
	}

	public void openFoldersSection() {
		expandSection(FOLDERS_SECTION);
	}

	public void openParametricSection() {
		if (Feature.visible(Feature.PARAMETRIC_SEARCHES))
			expandSection(PARAMETRIC_SECTION);
	}
}