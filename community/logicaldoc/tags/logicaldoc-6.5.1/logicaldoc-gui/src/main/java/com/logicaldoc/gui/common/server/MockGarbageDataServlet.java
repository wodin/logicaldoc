package com.logicaldoc.gui.common.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MockGarbageDataServlet extends HttpServlet {

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
			writer.print("<document>");
			writer.print("<id>" + Long.toString(100 + i) + "</id>");
			writer.print("<icon>word</icon>");
			writer.print("<title>Title t" + i + "</title>");
			writer.print("<customId>" + i + "</customId>");
			writer.print("<lastModified>2010-10-26T11:32:23</lastModified>");
			writer.print("<folderId>50</folderId>");
			writer.print("</document>");
		}
		writer.write("</list>");
	}
}
