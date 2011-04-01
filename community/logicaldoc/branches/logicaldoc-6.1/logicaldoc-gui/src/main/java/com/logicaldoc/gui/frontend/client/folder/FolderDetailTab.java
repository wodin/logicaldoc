package com.logicaldoc.gui.frontend.client.folder;

import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.layout.HLayout;

/**
 * Superclass for all tab panels in the folder details area
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public abstract class FolderDetailTab extends HLayout {
	protected GUIFolder folder;

	protected ChangedHandler changedHandler;

	/**
	 * 
	 * @param document The document this instance refers to
	 * @param changedHandler The handler to be invoked in case of changes in the
	 *        folder
	 */
	public FolderDetailTab(GUIFolder folder, ChangedHandler changedHandler) {
		super();
		this.folder = folder;
		this.changedHandler = changedHandler;
	}

	public GUIFolder getFolder() {
		return folder;
	}

	public ChangedHandler getChangedHandler() {
		return changedHandler;
	}
}
