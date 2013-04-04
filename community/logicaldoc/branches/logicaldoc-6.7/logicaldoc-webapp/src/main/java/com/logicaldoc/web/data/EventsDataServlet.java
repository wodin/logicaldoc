package com.logicaldoc.web.data;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.core.document.DocumentEvent;
import com.logicaldoc.core.security.FolderEvent;
import com.logicaldoc.core.security.UserHistory;
import com.logicaldoc.i18n.I18N;

/**
 * This servlet is responsible for document posts data.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class EventsDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(EventsDataServlet.class);

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		try {
			String locale = request.getParameter("locale");
			boolean folder = Boolean.parseBoolean(request.getParameter("folder"));
			boolean workflow = Boolean.parseBoolean(request.getParameter("workflow"));
			boolean user = Boolean.parseBoolean(request.getParameter("user"));

			response.setContentType("text/xml");
			response.setCharacterEncoding("UTF-8");

			// Headers required by Internet Explorer
			response.setHeader("Pragma", "public");
			response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
			response.setHeader("Expires", "0");

			PrintWriter writer = response.getWriter();
			writer.write("<list>");

			for (DocumentEvent event : DocumentEvent.values()) {
				writer.print("<event>");
				writer.print("<code>" + event.toString() + "</code>");
				writer.print("<label><![CDATA[" + I18N.message(event.toString(), locale) + "]]></label>");
				writer.print("<type>document</type>");
				writer.print("</event>");
			}

			if (folder)
				for (FolderEvent event : FolderEvent.values()) {
					writer.print("<event>");
					writer.print("<code>" + event.toString() + "</code>");
					writer.print("<label><![CDATA[" + I18N.message(event.toString(), locale) + "]]></label>");
					writer.print("<type>folder</type>");
					writer.print("</event>");
				}

			if (user) {
				String[] events = new String[] { UserHistory.EVENT_USER_LOGIN, UserHistory.EVENT_USER_DELETED,
						UserHistory.EVENT_USER_LOGOUT, UserHistory.EVENT_USER_PASSWORDCHANGED,
						UserHistory.EVENT_USER_TIMEOUT };
				for (String event : events) {
					writer.print("<event>");
					writer.print("<code>" + event + "</code>");
					writer.print("<label><![CDATA[" + I18N.message(event, locale) + "]]></label>");
					writer.print("<type>user</type>");
					writer.print("</event>");
				}
			}

			if (workflow) {
				String[] events = new String[] { "event.workflow.start", "event.workflow.end",
						"event.workflow.task.start", "event.workflow.task.start", "event.workflow.task.end",
						"event.workflow.task.end", "event.workflow.task.suspended", "event.workflow.task.suspended",
						"event.workflow.task.resumed", "event.workflow.task.resumed", "event.workflow.task.reassigned",
						"event.workflow.task.reassigned", "event.workflow.docappended" };
				for (String event : events) {
					writer.print("<event>");
					writer.print("<code>" + event + "</code>");
					writer.print("<label><![CDATA[" + I18N.message(event, locale) + "]]></label>");
					writer.print("<type>workflow</type>");
					writer.print("</event>");
				}
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
