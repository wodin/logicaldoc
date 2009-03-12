package com.logicaldoc.web.util;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Only a temporary patch for Firefox 3.0.7
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.5
 */
public class InputFileFrameServlet extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		try {
			String iframe = request.getSession().getAttribute("input.file.iframe.content").toString();
			out.println(iframe);
		} catch (Exception e) {
			e.printStackTrace(out);
		} finally {
			out.close();
		}
	}
}
