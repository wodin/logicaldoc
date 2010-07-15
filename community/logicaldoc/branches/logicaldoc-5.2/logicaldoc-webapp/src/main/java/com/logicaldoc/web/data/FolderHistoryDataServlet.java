package com.logicaldoc.web.data;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.logicaldoc.core.document.dao.HistoryDAO;
import com.logicaldoc.i18n.I18N;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.util.SessionUtil;

public class FolderHistoryDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		SessionUtil.validateSession(request);

		String locale = request.getParameter("locale");

		response.setContentType("text/xml");

		// Headers required by Internet Explorer
		response.setHeader("Pragma", "public");
		response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
		response.setHeader("Expires", "0");

		PrintWriter writer = response.getWriter();
		writer.write("<list>");

		HistoryDAO dao = (HistoryDAO) Context.getInstance().getBean(HistoryDAO.class);
		StringBuffer query = new StringBuffer(
				"select A.userName, A.event, A.date, A.comment, A.title, A.path, A.sessionId from History A where A.deleted = 0 ");
		if (request.getParameter("id") != null)
			query.append(" and A.folderId=" + request.getParameter("id"));
		query.append(" order by A.date asc ");

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		List<Object> records = (List<Object>) dao.findByQuery(query.toString(), null, null);

		/*
		 * Iterate over records composing the response XML document
		 */
		for (Object record : records) {
			Object[] cols = (Object[]) record;

			writer.print("<history>");
			writer.print("<user><![CDATA[" + cols[0] + "]]></user>");
			writer.print("<event><![CDATA[" + I18N.getMessage((String) cols[1], locale) + "]]></event>");
			writer.print("<date>" + df.format((Date) cols[2]) + "</date>");
			writer.print("<comment><![CDATA[" + cols[3] + "]]></comment>");
			writer.print("<title><![CDATA[" + cols[4] + "]]></title>");
			writer.print("<path>" + cols[5] + "</path>");
			writer.print("<sid>" + cols[6] + "</sid>");
			writer.print("</history>");
		}
		writer.write("</list>");
	}
}