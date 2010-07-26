package com.logicaldoc.gui.frontend.server;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MockDownloadServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	/**
	 * The doGet method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		if (request.getParameter("open") == null) {
			response.setHeader("Pragma", "public");
			response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
			response.setHeader("Expires", "0");
			response.setHeader("Content-Disposition", "attachment; filename=\"preview_na.gif\"");
		}

		try {
			if ("true".equals(request.getParameter("downloadText")))
				response.getWriter().write("Document text");
			else {
				RequestDispatcher rd = request
						.getRequestDispatcher("skin/images/preview_na.gif");
				rd.forward(request, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}