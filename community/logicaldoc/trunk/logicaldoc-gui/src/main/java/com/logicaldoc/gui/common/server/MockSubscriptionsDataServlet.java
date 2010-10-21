package com.logicaldoc.gui.common.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MockSubscriptionsDataServlet extends HttpServlet {

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
		for (int i = 0; i < 50; i++) {
			writer.print("<subscription>");
			writer.print("<id>" + i + "</id>");
			if (i % 2 == 0) {
				writer.print("<icon>word</icon>");
				writer.print("<type>document</type>");
			} else {
				writer.print("<icon>folder</icon>");
				writer.print("<type>folder</type>");
			}
			writer.print("<name>Subscription " + i + "</name>");
			writer.print("<created>2010-10-26T11:32:23</created>");
			writer.print("<objectid>"+i+"</objectid>");
			writer.print("</subscription>");
		}
		writer.write("</list>");
	}
}