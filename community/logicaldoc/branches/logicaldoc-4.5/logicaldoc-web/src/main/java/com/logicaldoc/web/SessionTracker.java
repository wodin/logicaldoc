package com.logicaldoc.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.web.util.Constants;

/**
 * This listener is used to track sessions. Each time a new session is created,
 * is is registered in the 'sessions' context map
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.5
 */
public class SessionTracker implements HttpSessionListener {
	protected static Log log = LogFactory.getLog(SessionTracker.class);

	/**
	 * Adds sessions to the context scoped HashMap when they begin.
	 */
	@SuppressWarnings("unchecked")
	public void sessionCreated(HttpSessionEvent event) {
		HttpSession session = event.getSession();
		log.debug("Created session "+session.getId());
		ServletContext context = session.getServletContext();
		if(context.getAttribute(Constants.SESSIONS)==null){
			context.setAttribute(Constants.SESSIONS, new HashMap<Object,Object>());
		}
		Map sessions = (Map) context.getAttribute(Constants.SESSIONS);
		sessions.put(session.getId(), session);
		log.debug("Created session "+session.getId());
	}

	/**
	 * Removes sessions from the context scoped HashMap when they expire or are
	 * invalidated.
	 */
	@SuppressWarnings("unchecked")
	public void sessionDestroyed(HttpSessionEvent event) {
		HttpSession session = event.getSession();
		ServletContext context = session.getServletContext();
		if(context.getAttribute(Constants.SESSIONS)==null){
			context.setAttribute(Constants.SESSIONS, new HashMap<Object,Object>());
		}
		Map sessions = (Map) context.getAttribute(Constants.SESSIONS);
		sessions.remove(session.getId());
		log.debug("Destroyed session "+session.getId());
	}
}