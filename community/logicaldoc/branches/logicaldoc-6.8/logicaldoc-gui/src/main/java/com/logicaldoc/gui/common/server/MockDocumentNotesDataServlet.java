package com.logicaldoc.gui.common.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MockDocumentNotesDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {

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
		for (int i = 0; i < 100; i++) {
			writer.print("<note>");
			writer.print("<id>" + Long.toString(100 + i) + "</id>");
			writer.print("<docId>" + Long.toString(100 + i) + "</docId>");
			writer.print("<userId>user" + i + "</userId>");
			writer.print("<username>username" + i + "</username>");
			writer.print("<date>2011-04-18T11:32:23</date>");
			writer.print("<message>message" + i + "</message>");
			writer.print("</note>");
		}
		writer.write("</list>");
	}
}