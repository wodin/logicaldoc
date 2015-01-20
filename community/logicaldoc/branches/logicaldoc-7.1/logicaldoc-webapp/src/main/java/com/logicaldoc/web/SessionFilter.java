package com.logicaldoc.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import com.logicaldoc.core.security.SessionManager;
import com.logicaldoc.web.util.ServiceUtil;

/**
 * Check if the user session was expired or active. In addition a binding
 * between UserSession and ServletSession is maintained as internal state. If
 * the kill parameter is found in the request, the given session is killed.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class SessionFilter implements Filter {
	private static Map<String, HttpSession> servletSessionMapping = new HashMap<String, HttpSession>();

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain next) throws IOException,
			ServletException {
		if (request instanceof HttpServletRequest) {
			HttpServletRequest http = (HttpServletRequest) request;
			if (!http.getMethod().equals("GET")) {
				next.doFilter(request, response);
				return;
			}
		}

		String sid = request.getParameter("sid");
		String kill = request.getParameter("kill");
		HttpSession servletSession = ((HttpServletRequest) request).getSession(false);

		if (StringUtils.isNotEmpty(kill)) {
			try {
				SessionManager.getInstance().kill(sid);
				servletSessionMapping.remove(sid);
			} catch (Throwable e) {

			}
		} else if (StringUtils.isNotEmpty(sid)) {
			try {
				ServiceUtil.validateSession(sid);

				/*
				 * If a servlet session exists, bind it to the LogicalDOC user
				 * session
				 */
				if (servletSession != null) {
					servletSessionMapping.put(sid, servletSession);
				}
			} catch (Throwable e) {
				servletSessionMapping.remove(sid);
				throw new ServletException(e);
			}
		}
		next.doFilter(request, response);
	}

	@Override
	public void destroy() {
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}

	/**
	 * Retrieves (if any) the servlet session binded to the passed user session
	 */
	public static HttpSession getServletSession(String sid) {
		return servletSessionMapping.get(sid);
	}
}