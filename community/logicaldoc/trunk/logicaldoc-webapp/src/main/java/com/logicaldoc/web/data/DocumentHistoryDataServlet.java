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
import com.logicaldoc.core.security.UserSession;
import com.logicaldoc.core.util.IconSelector;
import com.logicaldoc.i18n.I18N;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.SessionBean;

/**
 * 
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class DocumentHistoryDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		UserSession session = SessionBean.validateSession(request);

		System.out.println("******** history data servlet!!! " + session.getId());

		try {
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
					"select A.id, A.userName, A.event, A.version, A.date, A.comment, A.title, A.filename, A.new, A.folderId, A.docId, A.path, A.sessionId, A.userId from History A where 1=1 ");
			if (request.getParameter("docId") != null)
				query.append(" and A.docId=" + request.getParameter("docId"));
			if (request.getParameter("userId") != null)
				query.append(" and A.userId=" + request.getParameter("userId"));
			query.append(" order by A.date asc ");

			DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			List<Object> records = (List<Object>) dao.findByQuery(query.toString(), null, max);

			System.out.println("******** records: " + records.size());

			/*
			 * Iterate over records composing the response XML document
			 */
			for (Object record : records) {
				Object[] cols = (Object[]) record;
				writer.print("<history>");
				writer.print("<id>" + cols[0] + "</id>");
				writer.print("<user><![CDATA[" + cols[1] + "]]></user>");
				writer.print("<event><![CDATA[" + I18N.getMessage((String) cols[2], locale) + "]]></event>");
				writer.print("<version>" + cols[3] + "</version>");
				writer.print("<date>" + df.format((Date) cols[4]) + "</date>");
				writer.print("<comment><![CDATA[" + cols[5] + "]]></comment>");
				writer.print("<title><![CDATA[" + cols[6] + "]]></title>");
				writer.print("<icon>"
						+ FilenameUtils.getBaseName(IconSelector.selectIcon(FilenameUtils
								.getExtension((String) cols[7]))) + "</icon>");
				writer.print("<new>" + (1 == (Integer) cols[8]) + "</new>");
				writer.print("<folderId>" + cols[9] + "</folderId>");
				writer.print("<docId>" + cols[10] + "</docId>");
				writer.print("<path>" + cols[11] + "</path>");
				writer.print("<sid>" + cols[12] + "</sid>");
				writer.print("<userid>" + cols[13] + "</userid>");
				writer.print("</history>");
			}
			writer.write("</list>");
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}