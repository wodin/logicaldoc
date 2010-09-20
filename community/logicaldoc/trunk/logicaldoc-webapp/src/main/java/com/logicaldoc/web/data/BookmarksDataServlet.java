package com.logicaldoc.web.data;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;

import com.logicaldoc.core.document.dao.BookmarkDAO;
import com.logicaldoc.core.security.UserSession;
import com.logicaldoc.core.util.IconSelector;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.util.SessionUtil;

/**
 * This servlet is responsible for document bookmarks data.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class BookmarksDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		UserSession session = SessionUtil.validateSession(request);

		response.setContentType("text/xml");

		// Headers required by Internet Explorer
		response.setHeader("Pragma", "public");
		response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
		response.setHeader("Expires", "0");

		/*
		 * Iterate over the collection of bookmarks
		 */

		PrintWriter writer = response.getWriter();
		writer.write("<list>");

		BookmarkDAO dao = (BookmarkDAO) Context.getInstance().getBean(BookmarkDAO.class);
		StringBuffer query = new StringBuffer(
				"select A.id, A.fileType, A.title, A.description, A.position, A.userId, A.docId, B.folder.id "
						+ "from Bookmark A, Document B where A.deleted = 0 and B.deleted = 0 and A.docId = B.id and A.userId = "
						+ session.getUserId());

		List<Object> records = (List<Object>) dao.findByQuery(query.toString(), null, null);

		/*
		 * Iterate over records composing the response XML document
		 */
		for (Object record : records) {
			Object[] cols = (Object[]) record;

			writer.print("<bookmark>");
			writer.print("<id>" + cols[0] + "</id>");
			writer.print("<icon>" + FilenameUtils.getBaseName(IconSelector.selectIcon((String) cols[1])) + "</icon>");
			writer.print("<name><![CDATA[" + cols[2] + "]]></name>");
			writer.print("<description><![CDATA[" + cols[3] + "]]></description>");
			writer.print("<position>" + cols[4] + "</position>");
			writer.print("<userId>" + cols[5] + "</userId>");
			writer.print("<docId>" + cols[6] + "</docId>");
			writer.print("<folderId>" + cols[7] + "</folderId>");
			writer.print("</bookmark>");
		}
		writer.write("</list>");
	}
}
