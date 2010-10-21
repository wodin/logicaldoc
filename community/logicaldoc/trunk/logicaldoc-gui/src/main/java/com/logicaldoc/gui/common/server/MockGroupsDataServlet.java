package com.logicaldoc.gui.common.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MockGroupsDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		System.out.println("*** Data groups");

		response.setContentType("text/xml");
		response.setCharacterEncoding("UTF-8");

		// Headers required by Internet Explorer
		response.setHeader("Pragma", "public");
		response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
		response.setHeader("Expires", "0");

		PrintWriter writer = response.getWriter();
		writer.write("<list>");
		// Add 5 dummy groups
		for (int i = 200; i < 205; i++) {
			writer.print("<group>");
			writer.print("<id>" + (-i) + "</id>");
			writer.print("<name>Group " + i + "</name>");
			writer.print("<description>Description " + i + "</description>");
			writer.print("<label>Group: Group " + i + "</label>");
			writer.print("</group>");
		}
		writer.write("</list>");
	}
}