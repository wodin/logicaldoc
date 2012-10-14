package com.logicaldoc.gui.common.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MockTasksDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static int progress = 0;

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
			writer.print("<task>");
			writer.print("<name>task" + i + "</name>");
			writer.print("<label>Task " + i + "</label>");
			if (i % 2 == 0) {
				writer.print("<eenabled>true</eenabled>");
				writer.print("<enabledIcon>bullet_green</enabledIcon>");
			} else {
				writer.print("<eenabled>false</eenabled>");
				writer.print("<enabledIcon>bullet_red</enabledIcon>");
			}
			if (i != 2)
				writer.print("<status>1</status>");
			else
				writer.print("<status>0</status>");

			writer.print("<scheduling>each 1800 seconds</scheduling>");
			writer.print("<progress>" + progress + "</progress>");
			writer.print("<lastStart>2010-10-26T11:32:23</lastStart>");
			writer.print("<nextStart>2010-10-26T11:32:23</nextStart>");

			if (i == 4)
				writer.print("<indeterminate>true</indeterminate>");
			else
				writer.print("<indeterminate>false</indeterminate>");

			writer.print("</task>");
		}
		writer.write("</list>");
	}
}
