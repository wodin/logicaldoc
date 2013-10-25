package com.logicaldoc.gui.common.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.logicaldoc.i18n.I18N;

public class MockIncrementalArchivesDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {

		String sid = (String) request.getParameter("sid");
		if (sid == null)
			throw new IOException("Invalid session");

		String locale = request.getParameter("locale");

		response.setContentType("text/xml");
		response.setCharacterEncoding("UTF-8");

		// Headers required by Internet Explorer
		response.setHeader("Pragma", "public");
		response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
		response.setHeader("Expires", "0");

		PrintWriter writer = response.getWriter();
		writer.write("<list>");

		for (int i = 0; i < 30; i++) {
			writer.print("<archive>");
			writer.print("<id>" + i + "</id>");
			writer.print("<prefix>Prefix" + i + "</prefix>");
			writer.print("<frequency>" + (i + 1) + "</frequency>");
			writer.print("<type>" + (i % 2 == 0 ? 0 : 1) + "</type>");
			writer.print("<typelabel>"
					+ (i % 2 == 0 ? I18N.message("paperdematerialization", locale) : I18N.message("default", locale))
					+ "</typelabel>");
			writer.print("</archive>");
		}
		writer.write("</list>");
	}
}