package com.logicaldoc.gui.frontend.mock;

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
		System.out.println("*** Data templates");

		response.setContentType("text/xml");

		// Headers required by Internet Explorer
		response.setHeader("Pragma", "public");
		response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
		response.setHeader("Expires", "0");

		PrintWriter writer = response.getWriter();
		writer.write("<list>");

		writer.print("<template>");
		writer.print("<id>-1</id>");
		writer.print("<name> </name>");
		writer.print("</template>");
		
		// Add 5 dummy templates
		for (int i = 0; i < 5; i++) {
			writer.print("<template>");
			writer.print("<id>" + i + "</id>");
			writer.print("<name>template" + i + "</name>");
			writer.print("</template>");
		}
		writer.write("</list>");
	}
}