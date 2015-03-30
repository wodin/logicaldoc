package com.logicaldoc.webservice.auth;

import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.handler.MessageContext;

import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.core.security.SessionManager;
import com.logicaldoc.core.security.authentication.AuthenticationChain;
import com.logicaldoc.util.Context;
import com.logicaldoc.webservice.AbstractService;

/**
 * Auth Web Service Implementation (SOAP)
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 5.2
 */
@WebService(endpointInterface = "com.logicaldoc.webservice.auth.AuthService", serviceName = "AuthService")
public class AuthServiceImpl extends AbstractService implements AuthService {
	protected static Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

	@Override
	public String login(String username, String password) throws Exception {
		AuthenticationChain authenticationChain = (AuthenticationChain) Context.getInstance().getBean(
				AuthenticationChain.class);

		HttpServletRequest request = null;
		if (context != null) {
			MessageContext ctx = context.getMessageContext();
			if (ctx != null)
				request = (HttpServletRequest) ctx.get(AbstractHTTPDestination.HTTP_REQUEST);
		}

		if (request == null)
			request = messageContext.getHttpServletRequest();

		if (authenticationChain.authenticate(username, password,
				new String[] { request.getRemoteAddr(), request.getRemoteHost() }))
			return AuthenticationChain.getSessionId();
		else
			throw new Exception("Unable to create a new session");
	}

	@Override
	public void logout(String sid) {
		SessionManager.getInstance().kill(sid);
	}

	@Override
	public boolean valid(String sid) {
		if (!isWebserviceEnabled())
			return false;
		return SessionManager.getInstance().isValid(sid);
	}

	@Override
	public void renew(String sid) {
		if (!isWebserviceEnabled())
			return;
		if (SessionManager.getInstance().isValid(sid))
			SessionManager.getInstance().renew(sid);
	}
}