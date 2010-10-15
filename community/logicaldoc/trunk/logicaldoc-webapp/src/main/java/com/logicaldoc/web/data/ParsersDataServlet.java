package com.logicaldoc.web.data;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.text.parser.ParserFactory;
import com.logicaldoc.core.util.IconSelector;
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

			response.setContentType("text/xml");

			// Headers required by Internet Explorer
			response.setHeader("Pragma", "public");
			response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
			response.setHeader("Expires", "0");

			PrintWriter writer = response.getWriter();
			writer.write("<list>");
			int i = 0;
			for (String ext : ParserFactory.getParsers().keySet()) {
				writer.print("<parser>");
				writer.print("<id>" + i + "</id>");
				writer.print("<icon>" + FilenameUtils.getBaseName(IconSelector.selectIcon(ext.toLowerCase()))
						+ "</icon>");
				writer.print("<extension>" + ext.toLowerCase() + "</extension>");
				writer.print("<name>" + ParserFactory.getParsers().get(ext).getSimpleName() + "</name>");
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