package com.logicaldoc.gui.common.server.mock;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MockUsersDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		System.out.println("*** Data users");

		response.setContentType("text/xml");

		// Headers required by Internet Explorer
		response.setHeader("Pragma", "public");
		response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
		response.setHeader("Expires", "0");

		PrintWriter writer = response.getWriter();
		writer.write("<list>");
		
		// Add 5 dummy users
		for (int i = 0; i < 5; i++) {
			writer.print("<user>");
			writer.print("<id>" + i + "</id>");
			writer.print("<groupId>" + (-i) + "</groupId>");
			writer.print("<username>User " + i + "</username>");
			writer.print("<label>Marco Meschieri " + i + "</label>");
			writer.print("</user>");
		}
		writer.write("</list>");
	}
}