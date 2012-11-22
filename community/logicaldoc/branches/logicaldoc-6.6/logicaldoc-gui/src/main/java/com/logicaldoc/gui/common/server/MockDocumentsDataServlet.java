package com.logicaldoc.gui.common.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

public class MockDocumentsDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {

		int max = Integer.parseInt(request.getParameter("max"));
		long folderId = 0;
		if (StringUtils.isNotEmpty(request.getParameter("folderId")))
			folderId = Long.parseLong(request.getParameter("folderId"));
		String filename = request.getParameter("filename");

		boolean indexable = "true".equals(request.getParameter("index")) ? true : false;

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

		System.out.println("*max=" + max);

		for (int i = 0; i < max; i++) {
			writer.print("<document>");
			writer.print("<id>" + Long.toString(folderId + 1000 + i) + "</id>");
			writer.print("<customId>" + Long.toString(folderId + 1000 + i) + "</customId>");
			writer.print("<docref></docref>");
			writer.print("<icon>word</icon>");
			writer.print("<title>Title " + Long.toString(folderId + 1000 + i) + "</title>");
			writer.print("<version>1.0</version>");
			writer.print("<lastModified>2010-10-26T11:32:23</lastModified>");
			writer.print("<published>2010-02-12T11:32:23</published>");
			writer.print("<publisher>Marco Meschieri</publisher>");
			writer.print("<created>2010-02-12T11:32:23</created>");
			writer.print("<creator>Admin Admin</creator>");
			writer.print("<size>1234556</size>");
			writer.print("<immutable>blank</immutable>");
			if (indexable)
				writer.print("<indexed>blank</indexed>");
			else
				writer.print("<indexed>indexed</indexed>");
			writer.print("<signed>" + (i % 2 == 0 ? "blank" : "sign") + "</signed>");
			writer.print("<locked>blank</locked>");
			if (StringUtils.isEmpty(filename))
				writer.print("<filename>Title " + Long.toString(folderId + 1000 + i) + ".doc</filename>");
			else
				writer.print("<filename>Title " + filename + Long.toString(folderId + 1000 + i) + ".doc</filename>");
			writer.print("<status>0</status>");
			writer.print("</document>");
		}
		writer.write("</list>");
	}
}
