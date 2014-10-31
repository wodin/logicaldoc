package com.logicaldoc.core.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.util.config.ContextProperties;

/**
 * Repository of all current user sessions.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.6
 */
public class SessionManager extends ConcurrentHashMap<String, UserSession> {

	private static Logger log = LoggerFactory.getLogger(SessionManager.class);

	private static final long serialVersionUID = 1L;

	private static final SessionManager instance = new SessionManager();

	// The maximum number of closed session maintained in memory
	private static int MAX_CLOSED_SESSIONS = 50;

	private SessionManager() {
	}

	public final static SessionManager getInstance() {
		return instance;
	}

	/**
	 * Creates a new session and stores it in the pool of opened sessions.
	 * 
	 * @param username
	 * @return
	 */
	public synchronized String newSession(String username, String password, Object userObject) {
		UserSession session = new UserSession(username, password, userObject);
		put(session.getId(), session);
		log.warn("Created new session " + session.getId() + " for user '" + username + "'");
		cleanClosedSessions();
		return session.getId();
	}

	/**
	 * Kills an existing session
	 */
	public void kill(String sessionId) {
		UserSession session = get(sessionId);
		if (session != null) {
			session.setClosed();
			log.warn("Killed session " + sessionId);
		}
	}

	@Override
	public UserSession remove(Object key) {
		kill((String) key);
		return super.remove(key);
	}

	/**
	 * Renews an opened session
	 * 
	 * @param sessionId The session to be renewed
	 */
	public void renew(String sessionId) {
		if (isValid(sessionId)) {
			get(sessionId).renew();
		}
	}

	/**
	 * Checks if a sessions is valid or not. A valid session is a one that
	 * exists and is in state OPEN
	 * 
	 * @param sessionId The session identifier
	 * @return true only if the session exists
	 */
	public boolean isValid(String sessionId) {
		if (get(sessionId) == null)
			return false;
		else
			return get(sessionId).getStatus() == UserSession.STATUS_OPEN;
	}

	/**
	 * Checks if the session is expired. Note that if timeout occurred after the
	 * last renewal, the session state will be set to EXPIRED.
	 */
	private boolean isExpired(UserSession session) {
		if (session == null || session.getStatus() != UserSession.STATUS_OPEN)
			return true;

		Date lastRenew = session.getLastRenew();
		int timeout = 30;
		try {
			ContextProperties config = new ContextProperties();
			if (config.getInt(session.getTenantName() + ".session.timeout") > 0)
				timeout = config.getInt(session.getTenantName() + ".session.timeout");
		} catch (IOException e) {
		}
		Date now = new Date();
		long offset = now.getTime() - lastRenew.getTime();
		boolean expired = offset > (timeout * 60 * 1000);
		if (expired)
			session.setExpired();
		return expired;
	}

	@Override
	public UserSession get(Object sessionId) {
		UserSession session = super.get(sessionId);
		isExpired(session);
		return session;
	}

	/**
	 * Counts the number of opened sessions
	 */
	public int countOpened() {
		int count = 0;
		for (UserSession session : getSessions()) {
			if (!isExpired(session))
				count++;
		}
		return count;
	}

	/**
	 * Counts the number of opened sessions per tenant
	 */
	public int countOpened(long tenantId) {
		int count = 0;
		for (UserSession session : getSessions()) {
			if (!isExpired(session) && session.getTenantId() == tenantId)
				count++;
		}
		return count;
	}

	/**
	 * Returns the list of sessions ordered by ascending status and creation
	 * date.
	 */
	public List<UserSession> getSessions() {
		List<UserSession> sessions = new ArrayList<UserSession>(values());
		Collections.sort(sessions);
		return sessions;
	}

	/**
	 * Returns the list of sessions of the specified tenant ordered by ascending
	 * status and creation date.
	 */
	public List<UserSession> getSessions(long tenantId) {
		List<UserSession> sessions = new ArrayList<UserSession>(values());
		List<UserSession> tenantSessions = new ArrayList<UserSession>();
		for (UserSession session : sessions)
			if (session.getTenantId() == tenantId)
				tenantSessions.add(session);
		Collections.sort(tenantSessions);
		return tenantSessions;
	}

	/**
	 * Returns the list of sessions for the specified user object
	 */
	public List<UserSession> getSessionsByUserObject(Object userObject) {
		List<UserSession> sessions = new ArrayList<UserSession>();
		if (userObject == null)
			return sessions;
		for (UserSession userSession : values()) {
			if (userObject.equals(userSession.getUserObject()))
				sessions.add(userSession);
		}
		return sessions;
	}

	/**
	 * Clean method that removes all closed sessions that exceed the number of
	 * MAX_CLOSED_SESSIONS
	 */
	private void cleanClosedSessions() {
		List<String> garbage = new ArrayList<String>();
		int counter = 0;
		for (UserSession session : getSessions()) {
			if (session.getStatus() != UserSession.STATUS_OPEN)
				counter++;
			if (counter > MAX_CLOSED_SESSIONS)
				garbage.add(session.getId());
		}
		for (String sessionId : garbage) {
			remove(sessionId);
		}
	}
}