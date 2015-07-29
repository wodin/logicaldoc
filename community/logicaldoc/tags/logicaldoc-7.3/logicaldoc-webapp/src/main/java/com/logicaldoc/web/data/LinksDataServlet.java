package com.logicaldoc.web.data;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.util.IconSelector;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.util.ServiceUtil;

public class LinksDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(LinksDataServlet.class);

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		try {
			ServiceUtil.validateSession(request);

			Long docId = null;
			if (StringUtils.isNotEmpty(request.getParameter("docId")))
				docId = new Long(request.getParameter("docId"));

			response.setContentType("text/xml");
			response.setCharacterEncoding("UTF-8");

			// Headers required by Internet Explorer
			response.setHeader("Pragma", "public");
			response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
			response.setHeader("Expires", "0");

			PrintWriter writer = response.getWriter();
			writer.write("<list>");

			Context context = Context.getInstance();
			DocumentDAO dao = (DocumentDAO) context.getBean(DocumentDAO.class);
			StringBuffer query = new StringBuffer(
					"select A.id, B.folder.id, A.type, A.document1.id, A.document1.title, A.document1.type, A.document2.id, A.document2.title, A.document2.type "
							+ "from DocumentLink A, Document B where A.deleted = 0 and B.deleted = 0 ");
			if (docId != null) {
				query.append(" and ((A.document1.id = B.id and A.document1.id=" + docId + " )");
				query.append(" or  (A.document2.id = B.id and A.document2.id=" + docId + " ))");
			}

			List<Object> records = (List<Object>) dao.findByQuery(query.toString(), null, null);

			/*
			 * Iterate over records composing the response XML document
			 */
			for (Object record : records) {
				Object[] cols = (Object[]) record;

				writer.print("<link>");
				writer.print("<id>" + cols[0] + "</id>");
				writer.print("<folderId>" + cols[1] + "</folderId>");
				writer.print("<type>" + cols[2] + "</type>");
				if (docId.longValue() == (Long) cols[3]) {
					writer.print("<documentId>" + cols[6] + "</documentId>");
					writer.print("<title><![CDATA[" + (String) cols[7] + "]]></title>");
					writer.print("<icon>" + FilenameUtils.getBaseName(IconSelector.selectIcon((String) cols[8]))
							+ "</icon>");
					writer.print("<direction>out</direction>");
				} else {
					writer.print("<documentId>" + cols[3] + "</documentId>");
					writer.print("<title><![CDATA[" + (String) cols[4] + "]]></title>");
					writer.print("<icon>" + FilenameUtils.getBaseName(IconSelector.selectIcon((String) cols[5]))
							+ "</icon>");
					writer.print("<direction>in</direction>");
				}
				writer.print("</link>");
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