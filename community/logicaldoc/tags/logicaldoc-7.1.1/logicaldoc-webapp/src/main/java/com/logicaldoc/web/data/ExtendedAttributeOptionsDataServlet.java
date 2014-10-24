package com.logicaldoc.web.data;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.core.ExtendedAttributeOption;
import com.logicaldoc.core.document.dao.ExtendedAttributeOptionDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.util.ServiceUtil;

/**
 * This servlet retrieves the options for extended attributes
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 7.1
 */
public class ExtendedAttributeOptionsDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(ExtendedAttributeOptionsDataServlet.class);

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		try {
			ServiceUtil.validateSession(request);

			response.setContentType("text/xml");
			response.setCharacterEncoding("UTF-8");

			// Headers required by Internet Explorer
			response.setHeader("Pragma", "public");
			response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
			response.setHeader("Expires", "0");

			long templateId = Long.parseLong(request.getParameter("templateId"));
			String attribute = request.getParameter("attribute");
			boolean withempty = "true".equals(request.getParameter("withempty"));

			PrintWriter writer = response.getWriter();
			writer.write("<list>");

			ExtendedAttributeOptionDAO dao = (ExtendedAttributeOptionDAO) Context.getInstance().getBean(
					ExtendedAttributeOptionDAO.class);
			List<ExtendedAttributeOption> options = dao.findByTemplateAndAttribute(templateId, attribute);

			if(withempty){
				writer.print("<option>");
				writer.print("<id>-1</id>");
				writer.print("<attribute></attribute>");
				writer.print("<value></value>");
				writer.print("<position></position>");
				writer.print("</option>");
			}
			
			for (ExtendedAttributeOption option : options) {
				writer.print("<option>");
				writer.print("<id>" + option.getId() + "</id>");
				writer.print("<attribute><![CDATA[" + option.getAttribute() + "]]></attribute>");
				writer.print("<value><![CDATA[" + option.getValue() + "]]></value>");
				writer.print("<position><![CDATA[" + option.getPosition() + "]]></position>");
				writer.print("</option>");
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
