package com.logicaldoc.web.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

/**
 * This handler gets the j_successurl request parameter and use it's value to
 * redirect the user after a successful login.
 * 
 * @author Marco Meschieri - LogicalDOC
 * @since 7.5
 */
public class LDAuthenticationFailureHandler implements AuthenticationFailureHandler {

	private static Logger log = LoggerFactory.getLogger(LDAuthenticationFailureHandler.class);

	private static final String COOKIE_LDOC_FAILURE = "ldoc-failure";

	private static final String PARAM_FAILUREURL = "j_failureurl";

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		
		StringBuffer failureUrl = new StringBuffer(
				request.getParameter(PARAM_FAILUREURL) != null ? request.getParameter(PARAM_FAILUREURL) : "");
		if (failureUrl.toString().indexOf('?') != -1)
			failureUrl.append("&");
		else
			failureUrl.append("?");
		failureUrl.append("failure=");
		failureUrl.append(exception.getMessage());

		Cookie failureCookie = new Cookie(COOKIE_LDOC_FAILURE, exception.getMessage());
		response.addCookie(failureCookie);

		log.info("Authentication was unsuccesful, redirecting to " + failureUrl.toString());
		response.sendRedirect(failureUrl.toString());
	}
}