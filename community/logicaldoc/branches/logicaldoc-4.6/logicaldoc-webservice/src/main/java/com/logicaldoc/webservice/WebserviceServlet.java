package com.logicaldoc.webservice;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.cxf.transport.servlet.CXFServlet;

import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.PropertiesBean;

/**
 * Extension of the standard CXF servlet that checks the enabled flag
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.6
 */
public class WebserviceServlet extends CXFServlet {
	private static final long serialVersionUID = 1L;

	private PropertiesBean settings;

	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PropertiesBean settings = getSettings();

		// Check if the service is enabled
		if ("true".equals(settings.get("webservice.enabled")))
			super.service(request, response);
		else
			response.sendError(HttpServletResponse.SC_MOVED_TEMPORARILY);

		PropertiesBean pbean = new PropertiesBean(getClass().getClassLoader().getResource("context.properties"));
		pbean.setProperty("webservice.enabled", "true");
		pbean.write();
	}

	public PropertiesBean getSettings() {
		if (settings == null)
			settings = (PropertiesBean) Context.getInstance().getBean("ContextProperties");
		return settings;
	}
}