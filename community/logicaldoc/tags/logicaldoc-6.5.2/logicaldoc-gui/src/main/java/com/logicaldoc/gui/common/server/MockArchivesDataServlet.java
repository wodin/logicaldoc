package com.logicaldoc.gui.common.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.logicaldoc.i18n.I18N;

public class MockArchivesDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {

		int mode = Integer.parseInt(request.getParameter("mode"));

		Integer status = null;
		if (request.getParameter("status") != null)
			status = new Integer(request.getParameter("status"));

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
			writer.print("<name>Archive " + i + "</name>");
			writer.print("<description>Description Archive " + i + "</description>");
			writer.print("<size>1234556</size>");
			writer.print("<status>" + (i % 2 == 0 ? 0 : 1) + "</status>");
			writer.print("<statusicon>" + (i % 2 == 0 ? "lock_open" : "lock") + "</statusicon>");
			writer.print("<type>" + (i % 2 == 0 ? 0 : 1) + "</type>");
			writer.print("<typelabel>"
					+ (i % 2 == 0 ? I18N.message("paperdematerialization", locale) : I18N.message("default", locale))
					+ "</typelabel>");
			writer.print("<created>2010-02-12T11:32:23</created>");
			writer.print("<creator>Marco Meschieri</creator>");
			writer.print("<closer>Alessandro Gasparini</closer>");
			writer.print("</archive>");
		}
		writer.write("</list>");
	}
}