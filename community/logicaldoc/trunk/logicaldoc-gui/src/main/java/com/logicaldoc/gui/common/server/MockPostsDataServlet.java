package com.logicaldoc.gui.common.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MockPostsDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {

		long discussionId = Long.parseLong(request.getParameter("discussionId"));
		System.out.println("*** posts for discussionId=" + discussionId);

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
			writer.print("<post>");
			writer.print("<id>" + i + "</id>");
			writer.print("<title>Post " + discussionId + i + "</title>");
			writer.print("<user>Marco Meschieri</user>");
			writer.print("<indent>" + (i % 2 == 0 ? 0 : 1) + "</indent>");
			writer.print("<date>2010-10-26T11:32:23</date>");
			writer.print("<replyPath>boh!</replyPath>");
			writer
					.print("<message>&lt;b&gt;Lorem Ipsum&lt;/b&gt; is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.</message>");
			writer.print("</post>");
		}
		writer.write("</list>");
	}
}