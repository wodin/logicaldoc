package com.logicaldoc.gui.common.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MockEmailAccountsDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {

		response.setContentType("text/xml");
		response.setCharacterEncoding("UTF-8");

		// Headers required by Internet Explorer
		response.setHeader("Pragma", "public");
		response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
		response.setHeader("Expires", "0");

		PrintWriter writer = response.getWriter();
		writer.write("<list>");

		// Add 30 dummy folders
		for (int i = 0; i < 30; i++) {
			writer.print("<account>");
			writer.print("<id>" + i + "</id>");
			writer.print("<email>account" + i + "@acme.com</email>");

			if (i % 2 == 0) {
				writer.print("<eenabled>0</eenabled>");
			} else {
				writer.print("<eenabled>2</eenabled>");
			}

			writer.print("</account>");
		}
		writer.write("</list>");
	}
}