package com.logicaldoc.web.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.ContextProperties;
import com.logicaldoc.web.util.SessionUtil;

public class LDAnonymousLoginFilter extends GenericFilterBean {

	private static Logger log = LoggerFactory.getLogger(LDAnonymousLoginFilter.class);

	static final String FILTER_APPLIED = "__com_logicaldoc_core_security_spring_AnonymousLoginFilter_applied";

	public LDAnonymousLoginFilter() {

	}

	@Override
	public void doFilter(ServletRequest rec, ServletResponse res, FilterChain chain) throws IOException,
			ServletException {
		HttpServletRequest request = (HttpServletRequest) rec;
		HttpServletResponse response = (HttpServletResponse) res;

		if (request.getAttribute(FILTER_APPLIED) != null) {
			chain.doFilter(request, response);
			return;
		}

		request.setAttribute(FILTER_APPLIED, Boolean.TRUE);

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if ((authentication == null || (authentication != null && !authentication.isAuthenticated()))
				&& "login".equals(request.getParameter("anonymous"))) {

			String tenant = "default";
			if (StringUtils.isNotEmpty(request.getParameter("tenant")))
				tenant = request.getParameter("tenant");

			ContextProperties config = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
			boolean enabled = "true".equals(config.get(tenant + ".anonymous.enabled"));
			if (enabled) {
				LDAuthenticationToken auth = new LDAuthenticationToken(config.getProperty(tenant + ".anonymous.user"));
				AuthenticationManager authenticationManager = (AuthenticationManager) Context.getInstance().getBean(
						AuthenticationManager.class);
				try {
					Authentication anonAuthentication = authenticationManager.authenticate(auth);
					if (anonAuthentication.isAuthenticated()) {
						String sid = ((LDAuthenticationToken) anonAuthentication).getSid();
						SessionUtil.saveSid(request, response, sid);
					}
				} catch (AuthenticationException ae) {

				} catch (Throwable t) {
					log.error(t.getMessage(), t);
				}
			}
		}

		chain.doFilter(request, response);
	}
}