package com.logicaldoc.gui.common.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MockMessagesDataServlet extends HttpServlet {

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
			writer.print("<message>");
			writer.print("<id>" + i + "</id>");
			writer.print("<subject>Message " + i + "</subject>");
			writer.print("<text>Text Message " + i + "</text>");

			if (i % 2 == 0) {
				writer.print("<priority>1</priority>");
			} else {
				writer.print("<priority>2</priority>");
			}

			writer.print("<from>Homer Simpson</from>");

			writer.print("<sent>2010-10-26T11:32:23</sent>");

			if (i != 2)
				writer.print("<read>true</read>");
			else
				writer.print("<read>false</read>");
			writer.print("</message>");
		}
		writer.write("</list>");
	}
}