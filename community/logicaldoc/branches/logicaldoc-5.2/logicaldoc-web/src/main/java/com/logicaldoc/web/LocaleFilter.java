package com.logicaldoc.web;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.logicaldoc.web.util.Constants;

/**
 * This filter set the current locale in case of anonymous user. The locale is
 * taken from the HTTP headers
 * 
 * @author Marco Meschieri
 * @version $Id:$
 * @since 3.0
 */
public class LocaleFilter implements Filter {

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpSession session = httpRequest.getSession(false);
		if (session == null || session.getAttribute(Constants.LANGUAGE) == null) {
			Locale locale = request.getLocale();
			httpRequest.getSession().setAttribute(Constants.LANGUAGE, locale.getLanguage());
		}
		chain.doFilter(request, response);
	}

	public void init(FilterConfig arg0) throws ServletException {
	}
}