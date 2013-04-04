package com.logicaldoc.gui.common.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MockRightsDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {

		long folderId = Long.parseLong(request.getParameter("folderId"));
		System.out.println("*** rights");
		System.out.println("*** folderId=" + folderId);

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
		for (int i = 0; i < 30; i++) {
			writer.print("<right>");
			writer.print("<entityId>" + i + "</entityId>");
			writer.print("<entity>Admin</entity>");
			writer.print("<read>true</read>");
			writer.print("<write>true</write>");
			writer.print("<add>true</add>");
			writer.print("<security>true</security>");
			writer.print("<immutable>true</immutable>");
			writer.print("<delete>true</delete>");
			writer.print("<rename>true</rename>");
			writer.print("<import>true</import>");
			writer.print("<export>true</export>");
			writer.print("<sign>true</sign>");
			writer.print("<archive>true</archive>");
			writer.print("<workflow>true</workflow>");
			writer.print("<download>true</download>");
			writer.print("</right>");
		}
		writer.write("</list>");
	}
}
