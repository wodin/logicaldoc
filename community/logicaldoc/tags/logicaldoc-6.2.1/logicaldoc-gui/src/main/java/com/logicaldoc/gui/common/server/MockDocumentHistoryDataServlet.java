package com.logicaldoc.gui.common.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MockDocumentHistoryDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		System.out.println("*** history");
		response.setContentType("text/xml");
		response.setCharacterEncoding("UTF-8");

		// Headers required by Internet Explorer
		response.setHeader("Pragma", "public");
		response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
		response.setHeader("Expires", "0");

		String sid = (String) request.getParameter("sid");
		if (sid == null)
			throw new IOException("Invalid session");

		if (request.getParameter("id") != null)
			documentHistory(request, response);
		else if (request.getParameter("userId") != null)
			userHistory(request, response);
	}

	private void documentHistory(HttpServletRequest request, HttpServletResponse response) throws IOException {
		long docId = Long.parseLong(request.getParameter("docId"));
		String locale = request.getParameter("locale");

		PrintWriter writer = response.getWriter();
		writer.write("<list>");

		writer.print("<history>");
		writer.print("<user>Marco Meschieri</user>");
		writer.print("<event>checkin</event>");
		writer.print("<version>1.1</version>");
		writer.print("<date>2010-10-26T11:32:23</date>");
		writer.print("<comment>comment</comment>");
		writer.print("<title>Document 1</title>");
		writer.print("<icon>word</icon>");
		writer.print("<new>false</new>");
		writer.print("<docId>" + docId + "</docId>");
		writer.print("</history>");

		writer.print("<history>");
		writer.print("<user>Marco Meschieri</user>");
		writer.print("<event>Document Creation</event>");
		writer.print("<version>1.0</version>");
		writer.print("<date>2010-10-26T11:32:23</date>");
		writer.print("<comment>comment</comment>");
		writer.print("<title>Document 2</title>");
		writer.print("<icon>word</icon>");
		writer.print("<new>false</new>");
		writer.print("<docId>" + docId + "</docId>");
		writer.print("</history>");

		writer.write("</list>");
	}

	private void userHistory(HttpServletRequest request, HttpServletResponse response) throws IOException {
		long userId = Long.parseLong(request.getParameter("userId"));
		String event = request.getParameter("event");
		String locale = request.getParameter("locale");
		int max = Integer.parseInt(request.getParameter("max"));

		PrintWriter writer = response.getWriter();
		writer.write("<list>");

		for (int i = 0; i < max; i++) {
			writer.print("<history>");
			writer.print("<user>Marco Meschieri</user>");
			writer.print("<event>checkin</event>");
			writer.print("<version>1." + i + "</version>");
			writer.print("<date>2010-10-26T11:32:23</date>");
			writer.print("<comment>comment</comment>");
			writer.print("<title>Document " + i + "</title>");
			writer.print("<icon>word</icon>");
			writer.print("<new>" + (i % 2 == 0) + "</new>");
			writer.print("<userId>" + userId + "</userId>");
			writer.print("<folderId>5</folderId>");
			writer.print("<docId>" + i + "</docId>");
			writer.print("</history>");
		}

		writer.write("</list>");
	}
}