package com.logicaldoc.web.data;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.ContextProperties;
import com.logicaldoc.web.util.ServiceUtil;

/**
 * This servlet is responsible for storages data.
 * 
 * @author Marco Meschieri - LogicalDOC
 * @since 7.5.3
 */
public class StoragesDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(StoragesDataServlet.class);

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		try {
			ServiceUtil.validateSession(request);

			response.setContentType("text/xml");
			response.setCharacterEncoding("UTF-8");

			// Avoid resource caching
			response.setHeader("Pragma", "no-cache");
			response.setHeader("Cache-Control", "no-store");
			response.setDateHeader("Expires", 0);

			PrintWriter writer = response.getWriter();
			writer.write("<list>");

			if ("true".equals(request.getParameter("empty"))) {
				writer.print("<storage>");
				writer.print("<id />");
				writer.print("<name />");
				writer.print("<path />");
				writer.print("<write>blank</write>");
				writer.print("</storage>");
			}

			ContextProperties conf = Context.get().getProperties();

			// Prepare the stores
			for (int i = 1; i <= 99; i++) {
				String path = conf.getProperty("store." + i + ".dir");
				if (StringUtils.isNotEmpty(path)) {
					writer.print("<storage>");
					writer.print("<id>" + i + "</id>");
					writer.print("<name><![CDATA[Storage " + i + "]]></name>");
					writer.print("<path><![CDATA[" + path + "]]></path>");
					writer.print("<write>" + (conf.getInt("store.write") == i ? "database_edit" : "blank") + "</write>");
					writer.print("</storage>");
				}
			}

			writer.write("</list>");
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			if (e instanceof ServletException)
				throw (ServletException) e;
			else if (e instanceof IOException)
				throw (IOException) e;
			else
				throw new ServletException(e.getMessage(), e);
		}
	}
}
