package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUIContact;

public interface ContactServiceAsync {

	void delete(long[] ids, AsyncCallback<Void> callback);

	void load(long id, AsyncCallback<GUIContact> callback);

	void save(GUIContact contact, AsyncCallback<Void> callback);

	void parseContacts(boolean preview, String separator, String delimiter, boolean skipFirstRow,
			int firstName, int lastName, int email, int company, int phone, int mobile, int address,
			AsyncCallback<GUIContact[]> callback);

}
