package com.logicaldoc.gui.frontend.client.impex.folders;

import com.logicaldoc.gui.common.client.beans.GUIShare;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.layout.HLayout;

/**
 * Superclass for all tab panels in the import folders details area
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public abstract class ImportFolderDetailsTab extends HLayout {
	protected GUIShare share;

	protected ChangedHandler changedHandler;

	/**
	 * 
	 * @param document The share this instance refers to
	 * @param changedHandler The handler to be invoked in case of changes in the
	 *        share
	 */
	public ImportFolderDetailsTab(GUIShare share, ChangedHandler changedHandler) {
		super();
		this.share = share;
		this.changedHandler = changedHandler;
	}

	public GUIShare getShare() {
		return share;
	}

	public ChangedHandler getChangedHandler() {
		return changedHandler;
	}
}
