package com.logicaldoc.web.data;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.document.DiscussionComment;
import com.logicaldoc.core.document.DiscussionThread;
import com.logicaldoc.core.document.dao.DiscussionThreadDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.util.SessionUtil;

/**
 * This servlet is responsible for document posts data.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class PostsDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static Log log = LogFactory.getLog(PostsDataServlet.class);

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		try {
			SessionUtil.validateSession(request);

			long discussionId = Long.parseLong(request.getParameter("discussionId"));

			response.setContentType("text/xml");
			response.setCharacterEncoding("UTF-8");

			// Headers required by Internet Explorer
			response.setHeader("Pragma", "public");
			response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
			response.setHeader("Expires", "0");

			DiscussionThreadDAO dao = (DiscussionThreadDAO) Context.getInstance().getBean(DiscussionThreadDAO.class);
			DiscussionThread thread = dao.findById(discussionId);
			dao.initialize(thread);
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			df.setTimeZone(TimeZone.getTimeZone("UTC"));

			PrintWriter writer = response.getWriter();
			writer.write("<list>");

			int i = 0;
			for (DiscussionComment post : thread.getComments()) {
				writer.print("<post>");
				writer.print("<id>" + i + "</id>");
				writer.print("<title>" + post.getSubject() + "</title>");
				writer.print("<user>" + post.getUserName() + "</user>");
				writer.print("<indent>" + post.getIndentLevel() + "</indent>");
				writer.print("<date>" + df.format(post.getDate()) + "</date>");
				writer.print("<replyPath>" + post.getReplyPath() + "</replyPath>");
				writer.print("<message>" + post.getBody() + "</message>");
				writer.print("</post>");
				i++;
			}
			writer.write("</list>");

			if (thread.getComments().size() > 0) {
				thread.setViews(thread.getViews() + 1);
				dao.store(thread);
			}
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