package com.logicaldoc.gui.common.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MockDuplicatesDataServlet extends HttpServlet {

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

		for (int i = 0; i < 200; i++) {
			writer.print("<duplicate>");
			writer.print("<id>" + Long.toString(1000 + i) + "</id>");
			writer.print("<customId>" + Long.toString(1000 + i) + "</customId>");
			writer.print("<docref></docref>");
			writer.print("<icon>word</icon>");
			writer.print("<title>Title " + Long.toString(1000 + i) + "</title>");
			writer.print("<version>1.0</version>");
			writer.print("<lastModified>2010-10-26T11:32:23</lastModified>");
			writer.print("<published>2010-02-12T11:32:23</published>");
			writer.print("<publisher>Marco Meschieri</publisher>");
			writer.print("<created>2010-02-12T11:32:23</created>");
			writer.print("<creator>Admin Admin</creator>");
			writer.print("<size>1234556</size>");
			writer.print("<immutable>blank</immutable>");
			writer.print("<indexed>blank</indexed>");
			writer.print("<signed>" + (i % 2 == 0 ? "blank" : "sign") + "</signed>");
			writer.print("<locked>blank</locked>");
			writer.print("<filename>Title " + Long.toString(1000 + i) + ".doc</filename>");
			writer.print("<status>0</status>");
			writer.print("<digest>123456789" + i + "</digest>");
			writer.print("</duplicate>");
		}
		writer.write("</list>");
	}
}
