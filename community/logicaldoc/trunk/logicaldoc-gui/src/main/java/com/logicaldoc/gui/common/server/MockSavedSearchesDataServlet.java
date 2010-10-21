package com.logicaldoc.gui.common.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MockSavedSearchesDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		System.out.println("*** saved searches");

		String locale = request.getParameter("locale");
		ResourceBundle bundle = ResourceBundle.getBundle("i18n.frontend", new Locale(locale));

		String sid = (String) request.getParameter("sid");
		if (sid == null)
			throw new IOException("Invalid session");

		response.setContentType("text/xml");
		response.setCharacterEncoding("UTF-8");

		// Headers required by Internet Explorer
		response.setHeader("Pragma", "public");
		response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
		response.setHeader("Expires", "0");

		PrintWriter writer = response.getWriter();
		writer.write("<list>");

		writer.print("<search>");
		writer.print("<name>search_A</name>");
		writer.print("<type>" + bundle.getString("fulltext") + "</type>");
		writer.print("<description>saved fulltext search A</description>");
		writer.print("</search>");

		writer.print("<search>");
		writer.print("<name>search_B</name>");
		writer.print("<type>" + bundle.getString("parametric") + "</type>");
		writer.print("<description>saved parametric search B</description>");
		writer.print("</search>");

		writer.write("</list>");
	}
}
