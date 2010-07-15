package com.logicaldoc.gui.frontend.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MockLanguagesDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		System.out.println("*** Languages");

		response.setContentType("text/xml");

		// Headers required by Internet Explorer
		response.setHeader("Pragma", "public");
		response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
		response.setHeader("Expires", "0");

		PrintWriter writer = response.getWriter();
		writer.write("<list>");
		Locale[] locales = Locale.getAvailableLocales();
		for (int j = 0; j < locales.length; j++) {
			writer.print("<language>");
			writer.print("<locale>" + locales[j].toString() + "</locale>");
			writer.print("<name>" + locales[j].getDisplayLanguage() + "</name>");
			writer.print("</language>");
		}
		writer.write("</list>");
	}
}