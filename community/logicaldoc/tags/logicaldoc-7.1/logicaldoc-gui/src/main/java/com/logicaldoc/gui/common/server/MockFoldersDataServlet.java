package com.logicaldoc.gui.common.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.logicaldoc.gui.common.client.Constants;

public class MockFoldersDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		long parent = Long.parseLong(request.getParameter("parent"));

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

		if (parent == Constants.DOCUMENTS_FOLDERID) {
			// Add the 'Documents' root
			writer.print("<folder>");
			writer.print("<folderId>5</folderId>");
			writer.print("<parent>" + parent + "</parent>");
			writer.print("<name>/</name>");
			writer.print("<" + Constants.PERMISSION_ADD + ">true</" + Constants.PERMISSION_ADD + ">");
			writer.print("<" + Constants.PERMISSION_DELETE + ">true</" + Constants.PERMISSION_DELETE + ">");
			writer.print("<" + Constants.PERMISSION_RENAME + ">true</" + Constants.PERMISSION_RENAME + ">");
			writer.print("<" + Constants.PERMISSION_WRITE + ">true</" + Constants.PERMISSION_WRITE + ">");
			writer.print("<" + Constants.PERMISSION_DOWNLOAD + ">true</" + Constants.PERMISSION_DOWNLOAD + ">");
			writer.print("</folder>");
		} else {
			for (int i = 0; i < 10; i++) {
				writer.print("<folder>");
				writer.print("<folderId>" + parent + "" + i + "</folderId>");
				writer.print("<parent>" + parent + "</parent>");
				writer.print("<name>Folder " + parent + "" + i + "</name>");
				writer.print("<" + Constants.PERMISSION_ADD + ">true</" + Constants.PERMISSION_ADD + ">");
				writer.print("<" + Constants.PERMISSION_DELETE + ">true</" + Constants.PERMISSION_DELETE + ">");
				writer.print("<" + Constants.PERMISSION_RENAME + ">true</" + Constants.PERMISSION_RENAME + ">");
				writer.print("<" + Constants.PERMISSION_WRITE + ">true</" + Constants.PERMISSION_WRITE + ">");
				writer.print("<" + Constants.PERMISSION_DOWNLOAD + ">true</" + Constants.PERMISSION_DOWNLOAD + ">");
				writer.print("</folder>");
			}
		}
		writer.write("</list>");
	}
}
