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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.core.document.DocumentNote;
import com.logicaldoc.core.document.dao.DocumentNoteDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.util.SessionUtil;

/**
 * This servlet is responsible for document notes data.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.2
 */
public class DocumentNotesDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(DocumentNotesDataServlet.class);

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		try {
			SessionUtil.validateSession(request);

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

			DocumentNoteDAO dao = (DocumentNoteDAO) Context.getInstance().getBean(DocumentNoteDAO.class);
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			df.setTimeZone(TimeZone.getTimeZone("UTC"));

			List<DocumentNote> notes = dao.findByDocId(Long.parseLong(request.getParameter("docId")));

			/*
			 * Iterate over records composing the response XML document
			 */
			for (DocumentNote note : notes) {
				writer.print("<note>");
				writer.print("<id>" + note.getId() + "</id>");
				writer.print("<docId>" + note.getDocId() + "</docId>");
				writer.print("<userId>" + note.getUserId() + "</userId>");
				writer.print("<username><![CDATA[" + note.getUsername() + "]]></username>");
				writer.print("<date>" + df.format((Date) note.getLastModified()) + "</date>");
				writer.print("<message><![CDATA[" + note.getMessage() + "]]></message>");
				writer.print("</note>");
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
