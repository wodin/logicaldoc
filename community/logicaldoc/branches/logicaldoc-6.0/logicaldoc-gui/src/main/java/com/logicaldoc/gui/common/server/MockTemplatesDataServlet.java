package com.logicaldoc.gui.common.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MockTemplatesDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {

		response.setContentType("text/xml");
		response.setCharacterEncoding("UTF-8");

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
			writer.print("<documents>0</documents>");
			writer.print("</template>");
		}

		// Add 30 dummy templates
		for (int i = 0; i < 30; i++) {
			writer.print("<template>");
			writer.print("<id>" + i + "</id>");
			writer.print("<name>Template" + i + "</name>");
			writer.print("<documents>0</documents>");
			writer.print("<description>Description" + i + "</description>");
			writer.print("</template>");
		}
		writer.write("</list>");
	}
}