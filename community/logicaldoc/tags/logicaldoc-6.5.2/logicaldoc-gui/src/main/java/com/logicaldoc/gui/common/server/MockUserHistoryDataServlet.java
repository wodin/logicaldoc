package com.logicaldoc.gui.common.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MockUserHistoryDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		long userId = Long.parseLong(request.getParameter("id"));
		String locale = request.getParameter("locale");

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

		writer.print("<history>");
		writer.print("<user>Marco Meschieri</user>");
		writer.print("<event>checkin</event>");
		writer.print("<version>1.1</version>");
		writer.print("<date>2010-10-26T11:32:23</date>");
		writer.print("<comment>comment</comment>");
		writer.print("<title>title</title>");
		writer.print("<path>path</path>");
		writer.print("<sid>aJKHGEFGRIU32OR43O4UTW4F8G5EIU</sid>");
		writer.print("<userId>1</userId>");
		writer.print("<folderId>5</folderId>");
		writer.print("</history>");

		writer.print("<history>");
		writer.print("<user>Marco Meschieri</user>");
		writer.print("<event>Document Creation</event>");
		writer.print("<version>1.0</version>");
		writer.print("<date>2010-10-26T11:32:23</date>");
		writer.print("<comment>comment</comment>");
		writer.print("<title>title</title>");
		writer.print("<path>path</path>");
		writer.print("<sid>awjkshgb3o9ui42pHJEWGIHEJ2345</sid>");
		writer.print("<userId>1</userId>");
		writer.print("<folderId>5</folderId>");
		writer.print("</history>");

		writer.write("</list>");
	}
}