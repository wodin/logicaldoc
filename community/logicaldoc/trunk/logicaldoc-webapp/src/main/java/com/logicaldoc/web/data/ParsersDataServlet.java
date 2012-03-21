package com.logicaldoc.web.data;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.parser.ParserFactory;
import com.logicaldoc.core.util.IconSelector;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.ContextProperties;
import com.logicaldoc.web.util.SessionUtil;


/**
 * This servlet is responsible for parsers data.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class ParsersDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static Log log = LogFactory.getLog(ParsersDataServlet.class);

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		try {
			SessionUtil.validateSession(request);
			ContextProperties config = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);

			response.setContentType("text/xml");
			response.setCharacterEncoding("UTF-8");

			// Headers required by Internet Explorer
			response.setHeader("Pragma", "public");
			response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
			response.setHeader("Expires", "0");

			PrintWriter writer = response.getWriter();
			writer.write("<list>");
			int i = 0;
			Set<String> keys = ParserFactory.getParsers().keySet();
			List<String> sort = new ArrayList<String>();
			for (String ext : keys) {
				sort.add(ext);
			}
			Collections.sort(sort);

			for (String ext : sort) {
				writer.print("<parser>");
				writer.print("<id>" + i + "</id>");
				writer.print("<icon>" + FilenameUtils.getBaseName(IconSelector.selectIcon(ext.toLowerCase()))
						+ "</icon>");
				writer.print("<extension>" + ext.toLowerCase() + "</extension>");
				writer.print("<name><![CDATA[" + ParserFactory.getParsers().get(ext).getSimpleName() + "]]></name>");

				String aliasProp = config.getProperty("parser.alias." + ext.toLowerCase());
				writer.print("<aliases><![CDATA[" + (aliasProp != null ? aliasProp : "") + "]]></aliases>");
				writer.print("</parser>");
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