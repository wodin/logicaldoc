package com.logicaldoc.gui.common.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MockFeedMessageDataServlet extends HttpServlet {

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
		for (int i = 0; i < 20; i++) {
			writer.print("<feedmessage>");
			writer.print("<id>" + i + "</id>");
			writer.print("<guid>Feed Message Guid" + i + "</guid>");
			writer.print("<title>Feed Message " + i + "</title>");
			writer.print("<description>Feed Message Description" + i + "</description>");
			writer.print("<link>Feed Message Link" + i + "</link>");
			writer.print("<pubDate>2010-10-26T11:32:23</pubDate>");
			if (i % 2 == 0) {
				writer.print("<read>0</read>");
			} else {
				writer.print("<read>1</read>");
			}
			writer.print("</feedmessage>");
		}
		writer.write("</list>");
	}
}