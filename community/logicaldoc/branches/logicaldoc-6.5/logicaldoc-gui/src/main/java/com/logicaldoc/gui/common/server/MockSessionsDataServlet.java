package com.logicaldoc.gui.common.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MockSessionsDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		System.out.println("*** Data sessions");

		response.setContentType("text/xml");
		response.setCharacterEncoding("UTF-8");

		// Headers required by Internet Explorer
		response.setHeader("Pragma", "public");
		response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
		response.setHeader("Expires", "0");

		PrintWriter writer = response.getWriter();
		writer.print("<list>");

		for (int i = 0; i < 30; i++) {
			writer.print("<session>");
			writer.print("<sid>" + UUID.randomUUID().toString() + "</sid>");
			if (i % 2 == 0) {
				writer.print("<status>0</status>");
				writer.print("<statusLabel>Open</statusLabel>");
			} else {
				writer.print("<status>2</status>");
				writer.print("<statusLabel>Closed</statusLabel>");
			}
			writer.print("<username>User " + i + "</username>");
			writer.print("<created>2010-02-12T11:32:23</created>");
			writer.print("<renew>2010-02-12T11:32:23</renew>");

			writer.print("</session>");
		}
		writer.print("</list>");
	}
}