package com.logicaldoc.gui.frontend.client.document;

import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.frontend.client.folder.FoldersNavigator;
import com.smartgwt.client.types.VisibilityMode;
import com.smartgwt.client.widgets.layout.SectionStack;
import com.smartgwt.client.widgets.layout.SectionStackSection;

/**
 * The left menu
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class DocumentsMenu extends SectionStack {

	private FoldersNavigator foldersTree;

	public DocumentsMenu() {
		setVisibilityMode(VisibilityMode.MUTEX);
		setWidth100();

		SectionStackSection foldersSection = new SectionStackSection(I18N.message("folders"));
		foldersSection.setExpanded(true);
		foldersTree = FoldersNavigator.get();
		foldersSection.addItem(foldersTree);
		addSection(foldersSection);

		SectionStackSection bookmarksSection = new SectionStackSection(I18N.message("bookmarks"));
		bookmarksSection.setExpanded(false);
		bookmarksSection.setCanCollapse(true);
		bookmarksSection.addItem(BookmarksPanel.get());
		addSection(bookmarksSection);

		SectionStackSection trashSection = new SectionStackSection(I18N.message("trash"));
		trashSection.setExpanded(false);
		trashSection.setCanCollapse(true);
		trashSection.addItem(TrashPanel.get());
		addSection(trashSection);
	}

	public void openFolder(long folderId) {
		foldersTree.openFolder(folderId);
	}
}