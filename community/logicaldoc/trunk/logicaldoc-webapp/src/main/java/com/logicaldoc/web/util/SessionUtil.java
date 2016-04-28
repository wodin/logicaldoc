package com.logicaldoc.web.util;

import java.nio.charset.Charset;
import java.util.Base64;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.logicaldoc.core.security.SessionManager;
import com.logicaldoc.core.security.Session;
import com.logicaldoc.web.security.LDAuthenticationToken;
import com.logicaldoc.web.security.LDSecurityContextRepository;

/**
 * Utility class to deal with sessions from within web-application
 * 
 * @author Marco Meschieri - LogicalDOC
 * @since 7.5
 */
public class SessionUtil {

	public static final String COOKIE_SID = "ldoc-sid";

	public static final String PARAM_SID = "sid";

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
	public static String getSid(HttpServletRequest request) {
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
						if (SessionManager.get().isValid(session.getId())) {
							SessionManager.get().renew(session.getId());
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
	public static void saveSid(HttpServletRequest request, HttpServletResponse response, String sid) {
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

	public static HttpSession getServletSession(String sid) {
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
	public static String getCombinedUserId(HttpServletRequest req) {
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
