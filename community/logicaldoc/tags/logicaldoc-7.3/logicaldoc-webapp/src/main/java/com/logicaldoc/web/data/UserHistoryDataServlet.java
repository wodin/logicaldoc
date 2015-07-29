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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.core.security.UserHistory;
import com.logicaldoc.core.security.UserSession;
import com.logicaldoc.core.security.dao.UserHistoryDAO;
import com.logicaldoc.i18n.I18N;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.util.ServiceUtil;

/**
 * This servlet is responsible for user history data.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class UserHistoryDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(UserHistoryDataServlet.class);

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		try {
			ServiceUtil.validateSession(request);

			long userId = Long.parseLong(request.getParameter("id"));
			String locale = request.getParameter("locale");

			response.setContentType("text/xml");
			response.setCharacterEncoding("UTF-8");

			// Headers required by Internet Explorer
			response.setHeader("Pragma", "public");
			response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
			response.setHeader("Expires", "0");

			PrintWriter writer = response.getWriter();
			writer.write("<list>");

			UserHistoryDAO dao = (UserHistoryDAO) Context.getInstance().getBean(UserHistoryDAO.class);
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			df.setTimeZone(TimeZone.getTimeZone("UTC"));

			/*
			 * Iterate over the collection of user histories
			 */

			for (UserHistory history : dao.findByUserId(userId)) {
				writer.print("<history>");
				writer.print("<user><![CDATA[" + history.getUserName() + "]]></user>");
				writer.print("<event><![CDATA[" + I18N.message(history.getEvent(), locale) + "]]></event>");
				writer.print("<date>" + df.format(history.getDate()) + "</date>");
				writer.print("<comment><![CDATA[" + history.getComment() + "]]></comment>");
				writer.print("<sid><![CDATA[" + history.getSessionId() + "]]></sid>");
				writer.print("<userId>" + history.getUserId() + "</userId>");
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