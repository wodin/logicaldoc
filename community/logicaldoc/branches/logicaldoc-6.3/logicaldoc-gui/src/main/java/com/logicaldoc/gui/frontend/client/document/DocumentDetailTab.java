package com.logicaldoc.gui.frontend.client.document;

import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.layout.HLayout;

/**
 * Superclass for all tab panels in the document details area
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public abstract class DocumentDetailTab extends HLayout {
	protected GUIDocument document;

	protected ChangedHandler changedHandler;

	protected boolean update = false;

	/**
	 * 
	 * @param document The document this instance refers to
	 * @param changedHandler The handler to be invoked in case of changes in the
	 *        document
	 */
	public DocumentDetailTab(GUIDocument document, ChangedHandler changedHandler) {
		super();
		this.document = document;
		this.changedHandler = changedHandler;

		if (Session.get().getUser().isMemberOf(Constants.GROUP_ADMIN))
			update = true;
		else
			update = (document.getImmutable() == 0 && document.getStatus() == Constants.DOC_UNLOCKED && document
					.getFolder().isWrite());
	}

	public GUIDocument getDocument() {
		return document;
	}

	public ChangedHandler getChangedHandler() {
		return changedHandler;
	}
	
	/**
	 * Place here special logic that will be invoked when the user opens the tab
	 */
	protected void onTabSelected(){
		
	}
}
