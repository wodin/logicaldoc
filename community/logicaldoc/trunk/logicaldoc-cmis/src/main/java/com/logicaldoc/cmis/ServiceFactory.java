package com.logicaldoc.cmis;

import java.math.BigInteger;
import java.util.Map;

import org.apache.chemistry.opencmis.commons.exceptions.CmisPermissionDeniedException;
import org.apache.chemistry.opencmis.commons.impl.server.AbstractServiceFactory;
import org.apache.chemistry.opencmis.commons.server.CallContext;
import org.apache.chemistry.opencmis.commons.server.CmisService;
import org.apache.chemistry.opencmis.server.support.CmisServiceWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.core.security.SessionManager;
import com.logicaldoc.core.security.UserSession;
import com.logicaldoc.core.security.authentication.AuthenticationChain;
import com.logicaldoc.util.Context;

/**
 * CMIS Service factory
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.5.1
 */
public class ServiceFactory extends AbstractServiceFactory {

	private static final BigInteger DEFAULT_MAX_ITEMS_TYPES = BigInteger.valueOf(50);

	private static final BigInteger DEFAULT_DEPTH_TYPES = BigInteger.valueOf(-1);

	private static final BigInteger DEFAULT_MAX_ITEMS_OBJECTS = BigInteger.valueOf(200);

	private static final BigInteger DEFAULT_DEPTH_OBJECTS = BigInteger.valueOf(10);

	private static final Logger log = LoggerFactory.getLogger(CmisService.class);

	// private ThreadLocal<CmisServiceWrapper<LDCmisService>> threadLocalService
	// = new ThreadLocal<CmisServiceWrapper<LDCmisService>>();

	public ServiceFactory() {
		super();
	}

	@Override
	public CmisService getService(CallContext context) {
		String sid = authenticate(context);
		if (sid == null)
			return null;

		log.debug("Using session " + sid + " for user " + context.getUsername());
		CmisService wrapperService = new CmisServiceWrapper<LDCmisService>(new LDCmisService(context, sid),
				DEFAULT_MAX_ITEMS_TYPES, DEFAULT_DEPTH_TYPES, DEFAULT_MAX_ITEMS_OBJECTS, DEFAULT_DEPTH_OBJECTS);

		return wrapperService;
	}

	@Override
	public void init(Map<String, String> parameters) {
	}

	@Override
	public void destroy() {
		// threadLocalService = null;
	}

	private String authenticate(CallContext context) {
		String username = context.getUsername();
		String password = context.getPassword();

		// Create a pseudo session identifier
		String combinedUserId = context.getUsername() + "-" + CmisServlet.remoteAddress.get()[0];

		// Check if the session already exists
		for (UserSession session : SessionManager.getInstance().getSessions()) {
			try {
				String[] userObject = (String[]) session.getUserObject();
				if (userObject.length > 2 && userObject[2].equals(combinedUserId))
					if (SessionManager.getInstance().isValid(session.getId())) {
						SessionManager.getInstance().renew(session.getId());
						return session.getId();
					}
			} catch (Throwable t) {

			}
		}

		// We need to authenticate the user
		AuthenticationChain chain = (AuthenticationChain) Context.getInstance().getBean(AuthenticationChain.class);
		boolean authenticated = chain.authenticate(username, password,
				new String[] { CmisServlet.remoteAddress.get()[0], CmisServlet.remoteAddress.get()[1], combinedUserId,
						context.getRepositoryId() });
		String sid = null;
		if (authenticated)
			sid = AuthenticationChain.getSessionId();
		else {
			throw new CmisPermissionDeniedException();
		}
		return sid;
	}
}