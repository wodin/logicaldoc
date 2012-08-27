package com.logicaldoc.gui.frontend.client.search;

import com.logicaldoc.gui.common.client.beans.GUISearchOptions;
import com.logicaldoc.gui.frontend.client.folder.FolderSearchForm;

/**
 * Shows a folders search form
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.4
 */
public class FoldersForm extends FolderSearchForm {
	public FoldersForm() {
		super();
	}

	@Override
	protected void search(GUISearchOptions options) {
		Search.get().setOptions(options);
		Search.get().search();
	}
}