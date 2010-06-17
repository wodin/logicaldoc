package com.logicaldoc.web.data;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.util.SessionUtil;

public class TagsDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		SessionUtil.validateSession(request);

		response.setContentType("text/xml");

		// Headers required by Internet Explorer
		response.setHeader("Pragma", "public");
		response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
		response.setHeader("Expires", "0");

		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		HashMap<String, Integer> tgs = (HashMap<String, Integer>) docDao.findTags(request.getParameter("firstLetter"));

		PrintWriter writer = response.getWriter();
		writer.write("<list>");
		for (String tag : tgs.keySet()) {
			writer.print("<tag>");
			writer.print("<word>" + tag + "</word>");
			writer.print("<count>" + tgs.get(tag) + "</count>");
			writer.print("</tag>");
		}
		writer.write("</list>");
	}
}