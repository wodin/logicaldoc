package com.logicaldoc.web.data;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.core.document.DocumentNote;
import com.logicaldoc.core.document.dao.DocumentNoteDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.util.ServiceUtil;

/**
 * This servlet is responsible for document posts data.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class PostsDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(PostsDataServlet.class);

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		try {
			ServiceUtil.validateSession(request);

			Long userId = null;
			if (request.getParameter("userId") != null)
				userId = Long.parseLong(request.getParameter("userId"));

			response.setContentType("text/xml");
			response.setCharacterEncoding("UTF-8");

			// Headers required by Internet Explorer
			response.setHeader("Pragma", "public");
			response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
			response.setHeader("Expires", "0");

			DocumentNoteDAO dao = (DocumentNoteDAO) Context.getInstance().getBean(DocumentNoteDAO.class);
			List<DocumentNote> posts = new ArrayList<DocumentNote>();
			posts = dao.findByUserId(userId);

			DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			df.setTimeZone(TimeZone.getTimeZone("UTC"));

			PrintWriter writer = response.getWriter();
			writer.write("<list>");

			for (DocumentNote post : posts) {
				writer.print("<post>");
				writer.print("<id>" + post.getId() + "</id>");
				writer.print("<title><![CDATA[" + StringUtils.abbreviate(post.getMessage(), 100) + "]]></title>");
				writer.print("<user><![CDATA[" + post.getUsername() + "]]></user>");
				writer.print("<date>" + df.format(post.getDate()) + "</date>");
				writer.print("<message><![CDATA[" + post.getMessage() + "]]></message>");
				writer.print("<docId>" + post.getDocId() + "</docId>");
				writer.print("</post>");
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
