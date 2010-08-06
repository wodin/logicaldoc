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

import com.logicaldoc.core.security.UserSession;
import com.logicaldoc.web.util.SessionUtil;

/**
 * Check if the user session was expired or active. In addition a binding
 * between UserSession and ServletSession is maintained as internal state.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class SessionFilter implements Filter {
	private static Map<String, HttpSession> servletSessionMapping = new HashMap<String, HttpSession>();

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain next) throws IOException,
			ServletException {
		String sid = request.getParameter("sid");
		if (StringUtils.isNotEmpty(sid)) {
			try {
				SessionUtil.validateSession(sid);

				/*
				 * If a servlet session exists, bind it to the LogicalDOC user
				 * session
				 */
				HttpSession servletSession = ((HttpServletRequest) request).getSession(false);
				if (servletSession != null) {
					servletSessionMapping.put(sid, servletSession);
					System.out.println("** " + servletSession.getId());
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