package com.logicaldoc.web.security;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;

import com.logicaldoc.core.security.SessionManager;
import com.logicaldoc.core.security.UserSession;
import com.logicaldoc.web.util.SessionUtil;

/**
 * This repository avoid the use of sessions and simply use the current request
 * to store and retrieve the session ID.
 * 
 * @author Marco Meschieri - LogicalDOC
 * @since 7.5
 */
public class LDSecurityContextRepository implements SecurityContextRepository {

	private static Map<String, HttpSession> servletSessionMapping = new HashMap<String, HttpSession>();

	@Override
	public boolean containsContext(HttpServletRequest request) {
		String sid = SessionUtil.getSid(request);
		return sid != null;
	}

	@Override
	public SecurityContext loadContext(HttpRequestResponseHolder request) {
		String sid = SessionUtil.getSid(request.getRequest());
		if (sid == null || !SessionManager.getInstance().isValid(sid))
			sid = null;

		if (sid == null)
			return SecurityContextHolder.createEmptyContext();

		UserSession session = SessionManager.getInstance().get(sid);

		LDAuthenticationToken token = new LDAuthenticationToken(session.getUserName(), "", null);
		token.setSid(sid);

		SecurityContextImpl context = new SecurityContextImpl();
		context.setAuthentication(token);

		return context;
	}

	@Override
	public void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {
		if (context.getAuthentication() == null)
			return;

		Object principal = context.getAuthentication().getPrincipal();

		if (principal != null && principal instanceof LDAuthenticationToken) {
			LDAuthenticationToken token = (LDAuthenticationToken) principal;
			SessionUtil.saveSid(request, response, token.getSid());
		}
	}

	public static HttpSession getServletSession(String sid) {
		return servletSessionMapping.get(sid);
	}
}