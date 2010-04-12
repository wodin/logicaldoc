package com.logicaldoc.web.sessions;

import java.util.List;

import javax.faces.context.FacesContext;

import com.logicaldoc.core.security.SessionManager;
import com.logicaldoc.core.security.UserSession;
import com.logicaldoc.web.SessionManagement;

/**
 * JSF front-end to the SessionManager
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.6
 */
public class SessionsBean {

	public List<UserSession> getSessions() {
		return SessionManager.getInstance().getSessions();
	}

	public void kill() {
		UserSession session = (UserSession) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get(
				"userSession");
		SessionManager.getInstance().kill(session.getId());
		if (session.getExternalSession() != null) {
			SessionManagement.getSession((String) session.getExternalSession()).invalidate();
		}
	}

	public String getCurrentSessionId() {
		return SessionManagement.getCurrentUserSessionId();
	}
}
