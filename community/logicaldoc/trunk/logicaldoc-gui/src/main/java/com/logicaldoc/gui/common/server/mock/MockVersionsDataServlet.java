package com.logicaldoc.gui.common.server.mock;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MockVersionsDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		System.out.println("*** versions");

		long docId = Long.parseLong(request.getParameter("docId"));
		String lang = request.getParameter("lang");

		String sid = (String) request.getParameter("sid");
		if (sid == null)
			throw new IOException("Invalid session");

		response.setContentType("text/xml");

		// Headers required by Internet Explorer
		response.setHeader("Pragma", "public");
		response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
		response.setHeader("Expires", "0");

		PrintWriter writer = response.getWriter();
		writer.write("<list>");

		writer.print("<version>");
		writer.print("<id>" + Long.toString(docId) + 1 + "</id>");
		writer.print("<user>Marco Meschieri</user>");
		writer.print("<event>checkin</event>");
		writer.print("<version>1.1</version>");
		writer.print("<fileVersion>1.0</fileVersion>");
		writer.print("<date>2010-10-26T11:32:23</date>");
		writer.print("<comment>comment</comment>");
		writer.print("</version>");

		writer.print("<version>");
		writer.print("<id>" + Long.toString(docId) + "</id>");
		writer.print("<user>Marco Meschieri</user>");
		writer.print("<event>Document Creation</event>");
		writer.print("<version>1.0</version>");
		writer.print("<fileVersion>1.0</fileVersion>");
		writer.print("<date>2010-10-26T11:32:23</date>");
		writer.print("<comment>comment</comment>");
		writer.print("</version>");

		writer.write("</list>");
	}
}
