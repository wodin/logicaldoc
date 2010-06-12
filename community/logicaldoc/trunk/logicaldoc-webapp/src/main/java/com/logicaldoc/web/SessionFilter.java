package com.logicaldoc.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.lang.StringUtils;

import com.logicaldoc.web.util.SessionUtil;

/**
 * Check if the user session was expired or active
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class SessionFilter implements Filter {
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain next) throws IOException,
			ServletException {
		String sid=request.getParameter("sid");
		if (StringUtils.isNotEmpty(sid)) {
			SessionUtil.validateSession(sid);
		} else {
			next.doFilter(request, response);
		}
	}

	@Override
	public void destroy() {
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}
}