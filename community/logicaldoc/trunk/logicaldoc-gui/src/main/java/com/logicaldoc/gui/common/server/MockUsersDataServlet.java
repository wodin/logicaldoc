package com.logicaldoc.gui.common.server;

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
		response.setCharacterEncoding("UTF-8");

		// Headers required by Internet Explorer
		response.setHeader("Pragma", "public");
		response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
		response.setHeader("Expires", "0");

		String groupId = request.getParameter("groupId");

		PrintWriter writer = response.getWriter();
		writer.print("<list>");

		// Add 15 dummy users
		for (int i = 0; i < 15; i++) {
			writer.print("<user>");
			writer.print("<id>" + i + "</id>");
			writer.print("<username>User " + i + "</username>");
			writer.print("<label>Marco Meschieri " + i + "</label>");
			writer.print("<eenabled>0</eenabled>");
			writer.print("<name>Meschieri</name>");
			writer.print("<firstName>Marco</firstName>");
			writer.print("<email>m.meschieri@logicalobjects.it</email>");
			writer.print("<phone>059 2345634</phone>");
			writer.print("<cell>338 923453245</cell>");
			writer.print("</user>");
		}
		writer.print("</list>");
	}
}