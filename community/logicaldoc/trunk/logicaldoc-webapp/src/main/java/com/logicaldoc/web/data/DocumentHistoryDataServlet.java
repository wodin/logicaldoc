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

import org.apache.commons.io.FilenameUtils;

import com.logicaldoc.core.document.dao.HistoryDAO;
import com.logicaldoc.core.util.IconSelector;
import com.logicaldoc.i18n.I18N;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.util.SessionUtil;

/**
 * This servlet is responsible for documents history data.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class DocumentHistoryDataServlet extends HttpServlet {

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

		String locale = request.getParameter("locale");
		int max = Integer.parseInt(request.getParameter("max"));

		PrintWriter writer = response.getWriter();
		writer.write("<list>");

		HistoryDAO dao = (HistoryDAO) Context.getInstance().getBean(HistoryDAO.class);
		StringBuffer query = new StringBuffer(
				"select A.userName, A.event, A.version, A.date, A.comment, A.title, A.filename, A.new, A.folderId, A.docId, A.path, A.sessionId, A.userId from History A where 1=1 and A.deleted = 0 ");
		if (request.getParameter("docId") != null)
			query.append(" and A.docId=" + request.getParameter("docId"));
		if (request.getParameter("userId") != null)
			query.append(" and A.userId=" + request.getParameter("userId"));
		query.append(" order by A.date asc ");

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		List<Object> records = (List<Object>) dao.findByQuery(query.toString(), null, max);

		/*
		 * Iterate over records composing the response XML document
		 */
		for (Object record : records) {
			Object[] cols = (Object[]) record;
			writer.print("<history>");
			writer.print("<user><![CDATA[" + cols[0] + "]]></user>");
			writer.print("<event><![CDATA[" + I18N.getMessage((String) cols[1], locale) + "]]></event>");
			writer.print("<version>" + cols[2] + "</version>");
			writer.print("<date>" + df.format((Date) cols[3]) + "</date>");
			writer.print("<comment><![CDATA[" + cols[4] + "]]></comment>");
			writer.print("<title><![CDATA[" + cols[5] + "]]></title>");
			writer.print("<icon>"
					+ FilenameUtils.getBaseName(IconSelector.selectIcon(FilenameUtils.getExtension((String) cols[6])))
					+ "</icon>");
			writer.print("<new>" + (1 == (Integer) cols[7]) + "</new>");
			writer.print("<folderId>" + cols[8] + "</folderId>");
			writer.print("<docId>" + cols[9] + "</docId>");
			writer.print("<path>" + cols[10] + "</path>");
			writer.print("<sid>" + cols[11] + "</sid>");
			writer.print("<userid>" + cols[12] + "</userid>");
			writer.print("</history>");
		}
		writer.write("</list>");
	}
}