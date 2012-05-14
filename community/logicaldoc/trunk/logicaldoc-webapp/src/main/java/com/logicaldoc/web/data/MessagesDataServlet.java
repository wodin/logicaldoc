package com.logicaldoc.web.data;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.communication.SystemMessage;
import com.logicaldoc.core.communication.SystemMessageDAO;
import com.logicaldoc.core.security.UserSession;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.util.SessionUtil;

/**
 * This servlet is responsible for messages data.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class MessagesDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static Log log = LogFactory.getLog(MessagesDataServlet.class);

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		try {
			UserSession session = SessionUtil.validateSession(request);

			response.setContentType("text/xml");
			response.setCharacterEncoding("UTF-8");

			// Headers required by Internet Explorer
			response.setHeader("Pragma", "public");
			response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
			response.setHeader("Expires", "0");

			/*
			 * Execute the Query
			 */
			Context context = Context.getInstance();
			SystemMessageDAO dao = (SystemMessageDAO) context.getBean(SystemMessageDAO.class);
			dao.deleteExpiredMessages(session.getUserName());

			List<SystemMessage> records = dao.findByRecipient(session.getUserName(), SystemMessage.TYPE_SYSTEM, null);

			DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			df.setTimeZone(TimeZone.getTimeZone("UTC"));

			PrintWriter writer = response.getWriter();
			writer.write("<list>");

			/*
			 * Iterate over records composing the response XML document
			 */
			for (SystemMessage record : records) {
				writer.print("<message>");
				writer.print("<id>" + record.getId() + "</id>");
				writer.print("<subject><![CDATA[" + record.getSubject() + "]]></subject>");
				writer.print("<priority>" + record.getPrio() + "</priority>");
				writer.print("<from><![CDATA[" + record.getAuthor() + "]]></from>");
				writer.print("<sent>" + df.format(record.getSentDate()) + "</sent>");
				writer.print("<read>" + (record.getRead() == 1) + "</read>");
				writer.print("<text><![CDATA[" + record.getMessageText() + "]]></text>");
				writer.print("</message>");
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