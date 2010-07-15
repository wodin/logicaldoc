package com.logicaldoc.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.security.SessionManager;
import com.logicaldoc.core.security.authentication.AuthenticationChain;
import com.logicaldoc.web.util.Constants;

/**
 * This listener is used to track sessions. Each time a new session is created,
 * is is registered in the 'sessions' context map
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.5
 */
public class SessionTracker implements HttpSessionListener, HttpSessionAttributeListener, ServletRequestListener {
	protected static Log log = LogFactory.getLog(SessionTracker.class);

	/**
	 * Adds sessions to the context scoped HashMap when they begin.
	 */
	@SuppressWarnings("unchecked")
	public void sessionCreated(HttpSessionEvent event) {
		HttpSession session = event.getSession();
		log.debug("Created session " + session.getId());
		ServletContext context = session.getServletContext();
		if (context.getAttribute(Constants.SESSIONS) == null) {
			context.setAttribute(Constants.SESSIONS, new HashMap<Object, Object>());
		}

		Map sessions = (Map) context.getAttribute(Constants.SESSIONS);
		sessions.put(session.getId(), session);
		log.debug("Created session " + session.getId());
	}

	/**
	 * Removes sessions from the context scoped HashMap when they expire or are
	 * invalidated.
	 */
	@SuppressWarnings("unchecked")
	public void sessionDestroyed(HttpSessionEvent event) {
		HttpSession session = event.getSession();
		ServletContext context = session.getServletContext();
		if (context.getAttribute(Constants.SESSIONS) == null) {
			context.setAttribute(Constants.SESSIONS, new HashMap<Object, Object>());
		}
		Map sessions = (Map) context.getAttribute(Constants.SESSIONS);
		sessions.remove(session.getId());

		if (session.getAttribute(Constants.USER_SESSION) != null)
			SessionManager.getInstance().kill((String) session.getAttribute(Constants.USER_SESSION));

		log.debug("Destroyed session " + session.getId());
	}

	@Override
	public void attributeAdded(HttpSessionBindingEvent event) {
		if (event.getName().equals(Constants.AUTH_USERNAME)) {
			// Bind this servlet session to the user session
			String sessionId = AuthenticationChain.getSessionId();
			event.getSession().setAttribute(Constants.USER_SESSION, sessionId);
			SessionManager sm = SessionManager.getInstance();
			sm.get(sessionId).setExternalSession(event.getSession().getId());
		}
	}

	@Override
	public void attributeRemoved(HttpSessionBindingEvent event) {
	}

	@Override
	public void attributeReplaced(HttpSessionBindingEvent event) {
	}

	@Override
	public void requestDestroyed(ServletRequestEvent arg0) {
	}

	@Override
	public void requestInitialized(ServletRequestEvent event) {
		HttpSession session = ((HttpServletRequest) event.getServletRequest()).getSession(false);
		if (session != null && session.getAttribute(Constants.USER_SESSION) != null) {
			if (SessionManager.getInstance().isValid((String) session.getAttribute(Constants.USER_SESSION))) {
				// Renew the valid session
				SessionManager.getInstance().renew((String) session.getAttribute(Constants.USER_SESSION));
			} else {
				session.invalidate();
			}
		}
	}
}