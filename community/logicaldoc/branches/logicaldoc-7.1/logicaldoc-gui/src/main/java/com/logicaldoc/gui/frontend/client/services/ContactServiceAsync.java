package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUIContact;

public interface ContactServiceAsync {

	void delete(String sid, long[] ids, AsyncCallback<Void> callback);

	void load(String sid, long id, AsyncCallback<GUIContact> callback);

	void save(String sid, GUIContact contact, AsyncCallback<Void> callback);

	void parseContacts(String sid, boolean preview, String separator, String delimiter, boolean skipFirstRow,
			int firstName, int lastName, int email, int company, int phone, int mobile, int address,
			AsyncCallback<GUIContact[]> callback);

}
