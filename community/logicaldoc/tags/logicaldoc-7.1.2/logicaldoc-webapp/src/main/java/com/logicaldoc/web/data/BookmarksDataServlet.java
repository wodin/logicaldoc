package com.logicaldoc.web.data;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.core.document.Bookmark;
import com.logicaldoc.core.document.dao.BookmarkDAO;
import com.logicaldoc.core.security.UserSession;
import com.logicaldoc.core.util.IconSelector;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.util.ServiceUtil;

/**
 * This servlet is responsible for document bookmarks data.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class BookmarksDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(BookmarksDataServlet.class);

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		try {
			UserSession session = ServiceUtil.validateSession(request);

			response.setContentType("text/xml");
			response.setCharacterEncoding("UTF-8");

			// Headers required by Internet Explorer
			response.setHeader("Pragma", "public");
			response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
			response.setHeader("Expires", "0");

			/*
			 * Iterate over the collection of bookmarks
			 */
			PrintWriter writer = response.getWriter();
			writer.write("<list>");

			List<Object> records = new ArrayList<Object>();
			BookmarkDAO dao = (BookmarkDAO) Context.getInstance().getBean(BookmarkDAO.class);

			/*
			 * Search for folders first.
			 */
			StringBuffer query = new StringBuffer(
					"select A.id, A.fileType, A.title, A.description, A.position, A.userId, A.targetId, A.type, B.folder.id "
							+ " from Bookmark A, Document B where A.type=" + Bookmark.TYPE_DOCUMENT
							+ " and A.deleted = 0 and B.deleted = 0 and A.targetId = B.id and A.userId = "
							+ session.getUserId());
			records.addAll(dao.findByQuery(query.toString(), null, null));

			/*
			 * Than for documents
			 */
			query = new StringBuffer(
					"select A.id, A.fileType, A.title, A.description, A.position, A.userId, A.targetId, A.type, A.targetId "
							+ " from Bookmark A where A.type=" + Bookmark.TYPE_FOLDER + " and A.deleted = 0 and A.userId = "
							+ session.getUserId());
			records.addAll(dao.findByQuery(query.toString(), null, null));
			
			/*
			 * Iterate over records composing the response XML document
			 */
			for (Object record : records) {
				Object[] cols = (Object[]) record;

				writer.print("<bookmark>");
				writer.print("<id>" + cols[0] + "</id>");
				if(cols[7].toString().equals("0"))
				writer.print("<icon>" + FilenameUtils.getBaseName(IconSelector.selectIcon((String) cols[1]))
						+ "</icon>");
				else
					writer.print("<icon>folder_closed</icon>");	
				writer.print("<name><![CDATA[" + (cols[2] == null ? "" : cols[2]) + "]]></name>");
				writer.print("<description><![CDATA[" + (cols[3] == null ? "" : cols[3]) + "]]></description>");
				writer.print("<position>" + (cols[4] == null ? "" : cols[4]) + "</position>");
				writer.print("<userId>" + (cols[5] == null ? "" : cols[5]) + "</userId>");
				writer.print("<targetId>" + (cols[6] == null ? "" : cols[6]) + "</targetId>");
				writer.print("<type>" + (cols[7] == null ? "" : cols[7]) + "</type>");
				writer.print("<folderId>" + (cols[8] == null ? "" : cols[8]) + "</folderId>");
				writer.print("</bookmark>");
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
