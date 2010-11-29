package com.logicaldoc.gui.frontend.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.common.client.beans.GUIEmailAccount;
import com.logicaldoc.gui.common.client.beans.GUIEmailRule;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.frontend.client.services.EmailAccountService;

/**
 * Mock implementation of the EmailAccountService
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class MockEmailAccountServiceImpl extends RemoteServiceServlet implements EmailAccountService {

	private static final long serialVersionUID = 1L;

	@Override
	public void delete(String sid, long id) throws InvalidSessionException {

	}

	@Override
	public GUIEmailAccount save(String sid, GUIEmailAccount account) throws InvalidSessionException {
		if (account.getId() == 0)
			account.setId(9999L);

		return account;
	}

	@Override
	public GUIEmailAccount get(String sid, long id) throws InvalidSessionException {
		GUIEmailAccount account = new GUIEmailAccount();
		account.setId(id);
		account.setMailAddress("account" + id + "@acme.com");
		account.setLanguage("en");
		GUIFolder folder = new GUIFolder();
		folder.setId(id);
		folder.setName("test" + id);
		account.setTarget(folder);
		account.setHost("pop3.acme.com");
		account.setProvider("pop3");

		GUIEmailRule rule1 = new GUIEmailRule();
		rule1.setExpression("test expression 1");
		rule1.setTarget(folder);

		GUIEmailRule rule2 = new GUIEmailRule();
		rule2.setExpression("test expression 2");
		rule2.setTarget(folder);

		account.setRules(new GUIEmailRule[] { rule1, rule2 });
		return account;
	}

	@Override
	public boolean test(String sid, long id) throws InvalidSessionException {
		return (id % 2) == 0;
	}

	@Override
	public void changeStatus(String sid, long id, boolean enabled) {
	}

	@Override
	public void resetCache(String sid, long id) throws InvalidSessionException {
	}
}