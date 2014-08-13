package com.logicaldoc.cmis;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.chemistry.opencmis.server.impl.atompub.CmisAtomPubServlet;

import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.ContextProperties;

/**
 * Extension of a Cmis servlet compliant with AtomPub
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.5.1
 */
public class CmisServlet extends CmisAtomPubServlet {

	private static final long serialVersionUID = 1L;

	public static ThreadLocal<String[]> remoteAddress = new ThreadLocal<String[]>();

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ContextProperties settings = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);

		// Save the remote identification as thread local variable
		String[] addr = new String[] { request.getRemoteAddr(), request.getRemoteHost() };
		remoteAddress.set(addr);

		// Check if the service is enabled
		if ("true".equals(settings.get("cmis.enabled"))) {
			if (request.getHeader("Authorization") == null) {
				response.setHeader("WWW-Authenticate", "Basic realm=\"CMIS\"");
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authorization Required");
			} else {
				super.service(request, response);
			}
		} else
			response.sendError(HttpServletResponse.SC_MOVED_TEMPORARILY);
	}
}