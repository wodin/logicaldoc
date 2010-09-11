package com.logicaldoc.web.data;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.logicaldoc.core.document.dao.DiscussionThreadDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.util.SessionUtil;

/**
 * This servlet is responsible for document discussions data.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class DiscussionsDataServlet extends HttpServlet {

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

		PrintWriter writer = response.getWriter();
		writer.write("<list>");

		DiscussionThreadDAO dao = (DiscussionThreadDAO) Context.getInstance().getBean(DiscussionThreadDAO.class);
		StringBuffer query = new StringBuffer(
				"select A.id, A.subject, A.creatorName, A.replies, A.views, A.lastPost from DiscussionThread A where A.deleted = 0 ");
		query.append(" and A.docId=" + request.getParameter("docId"));
		query.append(" order by A.lastPost desc ");

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
		List<Object> records = (List<Object>) dao.findByQuery(query.toString(), null, null);

		/*
		 * Iterate over records composing the response XML document
		 */
		for (Object record : records) {
			Object[] cols = (Object[]) record;
			writer.print("<discussion>");
			writer.print("<id>" + cols[0] + "</id>");
			writer.print("<title><![CDATA[" + cols[1] + "]]></title>");
			writer.print("<user><![CDATA[" + cols[2] + "]]></user>");
			writer.print("<posts>" + cols[3] + "</posts>");
			writer.print("<visits>" + cols[4] + "</visits>");
			writer.print("<lastPost>" + df.format((Date) cols[5]) + "</lastPost>");
			writer.print("</discussion>");
		}
		writer.write("</list>");
	}
}