package com.logicaldoc.gui.common.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MockTagsDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		response.setContentType("text/xml");
		response.setCharacterEncoding("UTF-8");

		System.out.println("*** Data tags");
		String sid = (String) request.getParameter("sid");
		if (sid == null)
			throw new IOException("Invalid session");

		String firstLetter = (String) request.getParameter("firstLetter");

		// Headers required by Internet Explorer
		response.setHeader("Pragma", "public");
		response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
		response.setHeader("Expires", "0");

		PrintWriter writer = response.getWriter();
		writer.write("<list>");
		for (int i = 0; i < 1000; i++) {
			writer.print("<tag>");
			writer.print("<index>" + i + "</index>");
			if (firstLetter != null) {
				writer.print("<word>" + firstLetter.charAt(0) + "tag" + i + "</word>");
				writer.print("<count>" + i + "</count>");
			} else {
				writer.print("<word>tag" + i + "</word>");
				writer.print("<count></count>");
			}
			writer.print("</tag>");
		}
		writer.write("</list>");
	}
}