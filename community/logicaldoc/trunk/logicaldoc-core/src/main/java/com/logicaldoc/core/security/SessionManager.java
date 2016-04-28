package com.logicaldoc.core.security;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.logicaldoc.core.security.spring.LDAuthenticationToken;
import com.logicaldoc.core.security.spring.LDSecurityContextRepository;
import com.logicaldoc.util.config.ContextProperties;
import com.logicaldoc.util.time.TimeDiff;
import com.logicaldoc.util.time.TimeDiff.TimeField;

/**
 * Repository of all current user sessions.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.6
 */
public class SessionManager extends ConcurrentHashMap<String, Session> {

	public static final String COOKIE_SID = "ldoc-sid";

	public static final String PARAM_SID = "sid";

	private static Logger log = LoggerFactory.getLogger(SessionManager.class);

	private static final long serialVersionUID = 1L;

	private static final SessionManager instance = new SessionManager();

	// The maximum number of closed session maintained in memory
	private static int MAX_CLOSED_SESSIONS = 50;

	private SessionManager() {
	}

	public final static SessionManager get() {
		return instance;
	}

	/**
	 * Creates a new session and stores it in the pool of opened sessions
	 */
	public synchronized String newSession(String username, String password, String key, Object userObject) {
		Session session = new Session(username, password, key, userObject);
		put(session.getId(), session);
		log.warn("Created new session " + session.getId() + " for user '" + username + "'");
		cleanClosedSessions();
		return session.getId();
	}

	/**
	 * Creates a new session and stores it in the pool of opened sessions.
	 * 
	 * @param username
	 * @return
	 */
	public synchronized String newSession(String username, String password, Object userObject) {
		return newSession(username, password, null, userObject);
	}

	/**
	 * Kills an existing session
	 */
	public void kill(String sessionId) {
		Session session = get(sessionId);
		if (session != null) {
			session.setClosed();
			log.warn("Killed session " + sessionId);
		}
	}

	@Override
	public Session remove(Object key) {
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
	 * Checks if a session is valid or not. A valid session is a one that exists
	 * and is in state OPEN
	 * 
	 * @param sessionId The session identifier
	 * @return true only if the session exists
	 */
	public boolean isValid(String sessionId) {
		if (sessionId == null)
			return false;
		Session session = get(sessionId);
		return session != null && !isExpired(session) && session.getStatus() == Session.STATUS_OPEN;
	}

	/**
	 * Checks if the session is expired. Note that if timeout occurred after the
	 * last renewal, the session state will be set to EXPIRED.
	 */
	private boolean isExpired(Session session) {
		if (session == null || session.getStatus() != Session.STATUS_OPEN)
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

		// long offset = now.getTime() - lastRenew.getTime();
		long offset = Math.abs(TimeDiff.getTimeDifference(lastRenew, now, TimeField.MINUTE));

		boolean expired = offset > timeout;
		if (expired)
			session.setExpired();
		return expired;
	}

	@Override
	public Session get(Object sessionId) {
		Session session = super.get(sessionId);
		isExpired(session);
		return session;
	}

	/**
	 * Counts the number of opened sessions
	 */
	public int countOpened() {
		int count = 0;
		for (Session session : getSessions()) {
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
		for (Session session : getSessions()) {
			if (!isExpired(session) && session.getTenantId() == tenantId)
				count++;
		}
		return count;
	}

	/**
	 * Returns the list of sessions ordered by ascending status and creation
	 * date.
	 */
	public List<Session> getSessions() {
		List<Session> sessions = new ArrayList<Session>(values());
		Collections.sort(sessions);
		return sessions;
	}

	/**
	 * Returns the list of sessions of the specified tenant ordered by ascending
	 * status and creation date.
	 */
	public List<Session> getSessions(long tenantId) {
		List<Session> sessions = new ArrayList<Session>(values());
		List<Session> tenantSessions = new ArrayList<Session>();
		for (Session session : sessions)
			if (session.getTenantId() == tenantId)
				tenantSessions.add(session);
		Collections.sort(tenantSessions);
		return tenantSessions;
	}

	/**
	 * Returns the list of sessions for the specified user object
	 */
	public List<Session> getSessionsByUserObject(Object userObject) {
		List<Session> sessions = new ArrayList<Session>();
		if (userObject == null)
			return sessions;
		for (Session userSession : values()) {
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
		for (Session session : getSessions()) {
			if (session.getStatus() != Session.STATUS_OPEN)
				counter++;
			if (counter > MAX_CLOSED_SESSIONS)
				garbage.add(session.getId());
		}
		for (String sessionId : garbage) {
			remove(sessionId);
		}
	}

	/**
	 * Gets the Session returned by <code>getSid(request)</code>
	 */
	public Session getSession(HttpServletRequest request) {
		String sid = getSid(request);
		if (sid == null)
			return null;
		if (isValid(sid))
			return get(sid);
		return null;
	}

	/**
	 * Gets the Session ID specification from the current request following this
	 * lookup strategy:
	 * <ol>
	 * <li>Session attribute <code>PARAM_SID</code></li>
	 * <li>Request attribute <code>PARAM_SID</code></li>
	 * <li>Request parameter <code>PARAM_SID</code></li>
	 * <li>Cookie <code>COOKIE_SID</code></li>
	 * <li>Spring SecurityContextHolder</li>
	 * 
	 * @param request The current request to inspect
	 * @return The SID if any
	 */
	public String getSid(HttpServletRequest request) {
		if (request.getSession(false) != null && request.getSession(false).getAttribute(PARAM_SID) != null)
			return (String) request.getSession(false).getAttribute(PARAM_SID);
		if (request.getAttribute(PARAM_SID) != null)
			return (String) request.getAttribute(PARAM_SID);
		if (request.getParameter(PARAM_SID) != null)
			return (String) request.getParameter(PARAM_SID);

		Cookie cookies[] = request.getCookies();
		if (cookies != null)
			for (Cookie cookie : cookies) {
				if (COOKIE_SID.equals(cookie.getName()))
					return cookie.getValue();
			}

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth instanceof LDAuthenticationToken)
			return ((LDAuthenticationToken) auth).getSid();

		String combinedUserId = getCombinedUserId(request);
		if (combinedUserId != null)
			for (Session session : SessionManager.get().getSessions()) {
				try {
					String[] userObject = (String[]) session.getUserObject();
					if (userObject.length > 2 && userObject[2].equals(combinedUserId))
						if (isValid(session.getId())) {
							return session.getId();
						}
				} catch (Throwable t) {

				}
			}

		return null;
	}

	/**
	 * Saves the session identifier in the request and session attribute
	 * <code>PARAM_SID</code> and Cookie <code>COOKIE_SID</code>
	 * 
	 * @param request
	 * @param sid
	 */
	public void saveSid(HttpServletRequest request, HttpServletResponse response, String sid) {
		request.setAttribute(PARAM_SID, sid);
		if (request.getSession(false) != null)
			request.getSession(false).setAttribute(PARAM_SID, sid);

		Cookie sidCookie = new Cookie(COOKIE_SID, sid);
		response.addCookie(sidCookie);
	}

	/**
	 * Retrieves the session ID of the current thread execution
	 */
	public static String getCurrentSid() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth instanceof LDAuthenticationToken)
			return ((LDAuthenticationToken) auth).getSid();
		else
			return null;
	}

	public HttpSession getServletSession(String sid) {
		if (sid == null)
			return null;
		return LDSecurityContextRepository.getServletSession(sid);
	}

	/**
	 * Create a pseudo session identifier, useful to handle session bindings in
	 * basic authentication.
	 * 
	 * @param req The request to process
	 * @return The combined user Id.
	 */
	public String getCombinedUserId(HttpServletRequest req) {
		String[] credentials = getBasicCredentials(req);
		if (credentials != null)
			return String.format("%s-%s-%s", credentials[0], credentials[1] == null ? "0" : credentials[1].hashCode(),
					req.getRemoteAddr());
		else
			return null;
	}

	private static String[] getBasicCredentials(HttpServletRequest req) {
		final String authorization = req.getHeader("Authorization");
		if (authorization != null && authorization.startsWith("Basic")) {
			// Authorization: Basic base64credentials
			String base64Credentials = authorization.substring("Basic".length()).trim();
			String credentials = new String(Base64.getDecoder().decode(base64Credentials), Charset.forName("UTF-8"));

			// credentials = username:password
			return credentials.split(":", 2);
		} else
			return null;
	}
}