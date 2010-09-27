package com.logicaldoc.webservice;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.cxf.transport.servlet.CXFServlet;

import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.ContextProperties;

/**
 * Extension of the standard CXF servlet that checks the enabled flag
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.6
 */
public class WebserviceServlet extends CXFServlet {
	private static final long serialVersionUID = 1L;

	private ContextProperties settings;

	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ContextProperties settings = getSettings();

		// Check if the service is enabled
		if ("true".equals(settings.get("webservice.enabled")))
			super.service(request, response);
		else
			response.sendError(HttpServletResponse.SC_MOVED_TEMPORARILY);

		ContextProperties pbean = new ContextProperties();
		pbean.setProperty("webservice.enabled", "true");
		pbean.write();
	}

	public ContextProperties getSettings() {
		if (settings == null)
			settings = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
		return settings;
	}
}