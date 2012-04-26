package com.logicaldoc.webservice.auth;

import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.handler.MessageContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.transport.http.AbstractHTTPDestination;

import com.logicaldoc.core.security.SessionManager;
import com.logicaldoc.core.security.authentication.AuthenticationChain;
import com.logicaldoc.util.Context;
import com.logicaldoc.webservice.AbstractService;

/**
 * Auth Web Service Implementation
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 5.2
 */
@WebService(endpointInterface = "com.logicaldoc.webservice.auth.AuthService", serviceName = "AuthService")
public class AuthServiceImpl extends AbstractService implements AuthService {

	protected static Log log = LogFactory.getLog(AuthServiceImpl.class);

	@Override
	public String login(String username, String password) throws Exception {
		AuthenticationChain authenticationChain = (AuthenticationChain) Context.getInstance().getBean(
				AuthenticationChain.class);
		MessageContext ctx = context.getMessageContext();
		HttpServletRequest request = (HttpServletRequest) ctx.get(AbstractHTTPDestination.HTTP_REQUEST);

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
}