package com.logicaldoc.web.data;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.logicaldoc.core.document.Bookmark;
import com.logicaldoc.core.document.dao.BookmarkDAO;
import com.logicaldoc.core.security.UserSession;
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

		BookmarkDAO dao = (BookmarkDAO) Context.getInstance().getBean(BookmarkDAO.class);

		/*
		 * Iterate over the collection of bookmarks
		 */

		PrintWriter writer = response.getWriter();
		writer.write("<list>");
		for (Bookmark bookmark : dao.findByUserId(session.getUserId())) {
			writer.print("<bookmark>");
			writer.print("<id>" + bookmark.getId() + "</id>");
			writer.print("<icon>" + bookmark.getIcon() + "</icon>");
			writer.print("<name><![CDATA[" + bookmark.getTitle() + "]]></name>");
			writer.print("<description><![CDATA[" + bookmark.getDescription() + "]]></description>");
			writer.print("<position>" + bookmark.getPosition() + "</position>");
			writer.print("<userId>" + bookmark.getUserId() + "</userId>");
			writer.print("<docId>" + bookmark.getDocId() + "</docId>");
			writer.print("</bookmark>");
		}
		writer.write("</list>");
	}
}
