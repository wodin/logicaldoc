package com.logicaldoc.gui.common.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MockDiscussionsDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {

		long docId = Long.parseLong(request.getParameter("docId"));
		System.out.println("*** discussions for docId=" + docId);

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
			writer.print("<discussion>");
			writer.print("<id>" + Long.toString(docId + 1000 + i) + "</id>");
			writer.print("<title>Discussion " + Long.toString(docId + 1000 + i) + "</title>");
			writer.print("<user>Marco Meschieri</user>");
			writer.print("<posts>3</posts>");
			writer.print("<visits>53</visits>");
			writer.print("<lastPost>2010-10-26T11:32:23</lastPost>");
			writer.print("</discussion>");
		}
		writer.write("</list>");
	}
}