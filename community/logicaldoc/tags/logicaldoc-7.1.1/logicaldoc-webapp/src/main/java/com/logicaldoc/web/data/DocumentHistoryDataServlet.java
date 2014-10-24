package com.logicaldoc.web.data;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.core.document.dao.HistoryDAO;
import com.logicaldoc.core.util.IconSelector;
import com.logicaldoc.i18n.I18N;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.util.ServiceUtil;

/**
 * This servlet is responsible for documents history data.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class DocumentHistoryDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(DocumentHistoryDataServlet.class);

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		try {
			ServiceUtil.validateSession(request);

			response.setContentType("text/xml");
			response.setCharacterEncoding("UTF-8");

			// Headers required by Internet Explorer
			response.setHeader("Pragma", "public");
			response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
			response.setHeader("Expires", "0");

			String locale = request.getParameter("locale");
			int max = Integer.parseInt(request.getParameter("max"));

			PrintWriter writer = response.getWriter();
			writer.write("<list>");

			// Used only to cache the already encountered documents when the
			// history
			// is related to a single user (for dashboard visualization)
			Set<Long> docIds = new HashSet<Long>();

			HistoryDAO dao = (HistoryDAO) Context.getInstance().getBean(HistoryDAO.class);
			StringBuffer query = new StringBuffer(
					"select A.userName, A.event, A.version, A.date, A.comment, A.title, A.filename, A.new, A.folderId, A.docId, A.path, A.sessionId, A.userId from History A where 1=1 and A.deleted = 0 ");
			if (request.getParameter("docId") != null)
				query.append(" and A.docId=" + request.getParameter("docId"));
			if (request.getParameter("userId") != null)
				query.append(" and A.userId=" + request.getParameter("userId"));
			if (request.getParameter("event") != null)
				query.append(" and A.event='" + request.getParameter("event") + "' ");
			query.append(" order by A.date desc ");

			DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			df.setTimeZone(TimeZone.getTimeZone("UTC"));

			List<Object> records = (List<Object>) dao.findByQuery(query.toString(), null, max);

			/*
			 * Iterate over records composing the response XML document
			 */
			for (Object record : records) {
				Object[] cols = (Object[]) record;
				if (request.getParameter("userId") != null) {
					// Discard a record if already visited
					if (docIds.contains(cols[9]))
						continue;
					else
						docIds.add((Long) cols[9]);
				}

				writer.print("<history>");
				writer.print("<user><![CDATA[" + cols[0] + "]]></user>");
				writer.print("<event><![CDATA[" + I18N.message((String) cols[1], locale) + "]]></event>");
				writer.print("<version>" + cols[2] + "</version>");
				writer.print("<date>" + df.format((Date) cols[3]) + "</date>");
				writer.print("<comment><![CDATA[" + (cols[4] == null ? "" : cols[4]) + "]]></comment>");
				writer.print("<title><![CDATA[" + (cols[5] == null ? "" : cols[5]) + "]]></title>");
				writer.print("<icon>"
						+ FilenameUtils.getBaseName(IconSelector.selectIcon(FilenameUtils
								.getExtension((String) cols[6]))) + "</icon>");
				writer.print("<new>" + (1 == (Integer) cols[7]) + "</new>");
				writer.print("<folderId>" + cols[8] + "</folderId>");
				writer.print("<docId>" + cols[9] + "</docId>");
				writer.print("<path><![CDATA[" + (cols[10] == null ? "" : cols[10]) + "]]></path>");
				writer.print("<sid><![CDATA[" + (cols[11] == null ? "" : cols[11]) + "]]></sid>");
				writer.print("<userid>" + cols[12] + "</userid>");
				writer.print("</history>");
			}
			writer.write("</list>");
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			if (e instanceof ServletException)
				throw (ServletException) e;
			else if (e instanceof IOException)
				throw (IOException) e;
			else
				throw new ServletException(e.getMessage(), e);
		}
	}
}