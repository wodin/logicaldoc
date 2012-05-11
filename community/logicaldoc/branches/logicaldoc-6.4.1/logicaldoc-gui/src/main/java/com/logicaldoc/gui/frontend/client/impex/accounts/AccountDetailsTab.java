package com.logicaldoc.gui.frontend.client.impex.accounts;

import com.logicaldoc.gui.common.client.beans.GUIEmailAccount;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * Superclass for all tab panels in the account details area
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public abstract class AccountDetailsTab extends VLayout {
	protected GUIEmailAccount account;

	protected ChangedHandler changedHandler;

	/**
	 * 
	 * @param document The share this instance refers to
	 * @param changedHandler The handler to be invoked in case of changes in the
	 *        share
	 */
	public AccountDetailsTab(GUIEmailAccount account, ChangedHandler changedHandler) {
		super();
		this.account = account;
		this.changedHandler = changedHandler;
		setMembersMargin(4);
	}

	public GUIEmailAccount getAccount() {
		return account;
	}

	public ChangedHandler getChangedHandler() {
		return changedHandler;
	}
}
