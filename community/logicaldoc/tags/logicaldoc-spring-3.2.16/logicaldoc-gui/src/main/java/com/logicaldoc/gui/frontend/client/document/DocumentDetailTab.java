package com.logicaldoc.gui.frontend.client.document;

import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.DocumentObserver;
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

	protected DocumentObserver observer;

	protected boolean updateEnabled = false;

	protected boolean deleteEnabled = false;

	/**
	 * 
	 * @param document The document this instance refers to
	 * @param changedHandler The handler to be invoked in case of changes in the
	 *        document
	 */
	public DocumentDetailTab(GUIDocument document, ChangedHandler changedHandler, DocumentObserver observer) {
		super();
		this.document = document;
		this.changedHandler = changedHandler;
		this.observer = observer;

		if (Session.get().getUser().isMemberOf(Constants.GROUP_ADMIN) && document.getImmutable() == 0
				&& document.getStatus() == Constants.DOC_UNLOCKED) {
			updateEnabled = true;
			deleteEnabled = true;
		} else {
			updateEnabled = (document.getImmutable() == 0 && document.getStatus() == Constants.DOC_UNLOCKED && document
					.getFolder().isWrite());
			deleteEnabled = (document.getImmutable() == 0 && document.getStatus() == Constants.DOC_UNLOCKED && document
					.getFolder().isDelete());
		}

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
	public void onTabSelected() {

	}

	public DocumentObserver getObserver() {
		return observer;
	}
}
