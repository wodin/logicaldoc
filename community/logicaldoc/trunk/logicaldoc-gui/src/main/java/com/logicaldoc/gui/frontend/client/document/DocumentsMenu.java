package com.logicaldoc.gui.frontend.client.document;

import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.widgets.FeatureDisabled;
import com.logicaldoc.gui.frontend.client.folder.FoldersNavigator;
import com.smartgwt.client.types.VisibilityMode;
import com.smartgwt.client.widgets.layout.SectionStack;
import com.smartgwt.client.widgets.layout.SectionStackSection;
import com.smartgwt.client.widgets.layout.events.SectionHeaderClickEvent;
import com.smartgwt.client.widgets.layout.events.SectionHeaderClickHandler;

/**
 * The left menu
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class DocumentsMenu extends SectionStack {

	private FoldersNavigator foldersTree;

	private SectionStackSection foldersSection = null;

	private SectionStackSection bookmarksSection = null;

	private SectionStackSection trashSection = null;

	public DocumentsMenu() {
		this(true, true, true);
	}

	public DocumentsMenu(boolean folders, boolean bookmarks, boolean trash) {
		setVisibilityMode(VisibilityMode.MUTEX);
		setWidth(250);

		foldersSection = new SectionStackSection(I18N.message("folders"));
		foldersSection.setName("folders");
		foldersSection.setCanCollapse(true);
		foldersTree = FoldersNavigator.get();
		foldersSection.setItems(foldersTree);
		if (folders)
			addSection(foldersSection);

		if (bookmarks && Feature.visible(Feature.BOOKMARKS)) {
			bookmarksSection = new SectionStackSection(I18N.message("bookmarks"));
			bookmarksSection.setName("bookmarks");
			bookmarksSection.setCanCollapse(true);

			if (Feature.enabled(Feature.BOOKMARKS))
				bookmarksSection.setItems(BookmarksPanel.get());
			else
				bookmarksSection.addItem(new FeatureDisabled());
			addSection(bookmarksSection);
		}

		if (trash && Feature.visible(Feature.TRASH)) {
			trashSection = new SectionStackSection(I18N.message("trash"));
			trashSection.setName("trash");
			trashSection.setCanCollapse(true);
			if (Feature.enabled(Feature.TRASH))
				trashSection.setItems(TrashPanel.get());
			else
				trashSection.addItem(new FeatureDisabled());
			addSection(trashSection);
		}

		addSectionHeaderClickHandler(new SectionHeaderClickHandler() {
			@Override
			public void onSectionHeaderClick(SectionHeaderClickEvent event) {
				if (event.getSection() != null) {
					refresh(event.getSection().getAttributeAsString("name"));
				}
			}
		});
	}

	public void refresh(String sectionNameToExpand) {
		if ("bookmarks".equals(sectionNameToExpand)) {
			BookmarksPanel.get().reloadList();
		} else if ("trash".equals(sectionNameToExpand)) {
			TrashPanel.get().refresh();
		}
	}

	public void openFolder(long folderId) {
		foldersTree.openFolder(folderId);
	}
}