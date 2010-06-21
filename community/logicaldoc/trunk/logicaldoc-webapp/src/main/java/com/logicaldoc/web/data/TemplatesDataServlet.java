package com.logicaldoc.web.data;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.logicaldoc.core.document.DocumentTemplate;
import com.logicaldoc.core.document.dao.DocumentTemplateDAO;
import com.logicaldoc.util.Context;

/**
 * This servlet is responsible for document templates data.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class TemplatesDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {

		response.setContentType("text/xml");

		// Headers required by Internet Explorer
		response.setHeader("Pragma", "public");
		response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
		response.setHeader("Expires", "0");

		PrintWriter writer = response.getWriter();
		writer.write("<list>");

		if ("true".equals(request.getParameter("withempty"))) {
			writer.print("<template>");
			writer.print("<id></id>");
			writer.print("<name> </name>");
			writer.print("</template>");
		}

		DocumentTemplateDAO dao = (DocumentTemplateDAO) Context.getInstance().getBean(DocumentTemplateDAO.class);

		/*
		 * Iterate over the collection of templates
		 */
		for (DocumentTemplate template : dao.findAll()) {
			writer.print("<template>");
			writer.print("<id>" + template.getId() + "</id>");
			writer.print("<name><![CDATA[" + template.getName() + "]]></name>");
			writer.print("<description><![CDATA[" + template.getDescription() + "]]></description>");
			writer.print("</template>");
		}

		writer.write("</list>");
	}
}