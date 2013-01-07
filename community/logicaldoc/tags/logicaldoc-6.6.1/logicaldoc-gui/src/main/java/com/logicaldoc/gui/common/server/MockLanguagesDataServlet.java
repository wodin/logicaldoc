package com.logicaldoc.gui.common.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MockLanguagesDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		System.out.println("*** Data languages");

		response.setContentType("text/xml");
		response.setCharacterEncoding("UTF-8");

		// Headers required by Internet Explorer
		response.setHeader("Pragma", "public");
		response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
		response.setHeader("Expires", "0");

		String locale = request.getParameter("locale");

		PrintWriter writer = response.getWriter();
		writer.print("<list>");

		writer.print("<lang>");
		writer.print("<code>en</id>");
		writer.print("<name>English</name>");
		writer.print("<eenabled>0</eenabled>");
		writer.print("</lang>");
		
		writer.print("<lang>");
		writer.print("<code>it</id>");
		writer.print("<name>Italian</name>");
		writer.print("<eenabled>2</eenabled>");
		writer.print("</lang>");
		
		writer.print("</list>");
	}
}