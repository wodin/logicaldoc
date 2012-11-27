package com.logicaldoc.gui.common.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MockEventsDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		
		String locale = request.getParameter("locale");
		boolean folder = Boolean.parseBoolean(request.getParameter("folder"));
		boolean workflow = Boolean.parseBoolean(request.getParameter("workflow"));
		boolean user = Boolean.parseBoolean(request.getParameter("user"));

		response.setContentType("text/xml");
		response.setCharacterEncoding("UTF-8");

		// Headers required by Internet Explorer
		response.setHeader("Pragma", "public");
		response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
		response.setHeader("Expires", "0");

		PrintWriter writer = response.getWriter();
		writer.write("<list>");

		for (int i = 0; i < 20; i++) {
			writer.print("<event>");
			writer.print("<code>" + i + "</code>");
			writer.print("<label>Event " + i + "</label>");
			writer.print("<type>document</type>");
			writer.print("</event>");
		}
		writer.write("</list>");
	}
}