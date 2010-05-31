package com.logicaldoc.webapp;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.core.security.SessionManager;
import com.logicaldoc.core.security.UserSession;
import com.logicaldoc.gui.common.client.i18n.I18N;

/**
 * Base class for services implementation
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public abstract class AbstractService extends RemoteServiceServlet {
	
	private static final long serialVersionUID = 1L;

	/**
	 *  Throws a runtime exception id the given session is invalid
	 */
	protected void validateSession(String sid) {
		UserSession session = SessionManager.getInstance().get(sid);
		if (session == null)
			throw new RuntimeException(I18N.message("invalidsession"));
	}
}