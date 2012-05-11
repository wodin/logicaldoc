package com.logicaldoc.gui.common.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MockWorkflowTriggersDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {

		long folderId = Long.parseLong(request.getParameter("folderId"));
		System.out.println("*** links for docId=" + folderId);

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
			writer.print("<workflowtrigger>");
			writer.print("<id>" + Long.toString(folderId + 1000 + i) + "</id>");
			writer.print("<workflowId>" + Long.toString(folderId + 1000 + i) + "</workflowId>");
			writer.print("<templateId>" + Long.toString(folderId + 1000 + i) + "</templateId>");
			writer.print("<workflow>" + "Workflow " + Long.toString(folderId + 1000 + i) + "</workflow>");
			writer.print("<template>" + "Template " + Long.toString(folderId + 1000 + i) + "</template>");
			writer.print("</workflowtrigger>");
		}
		writer.write("</list>");
	}
}