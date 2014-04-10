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

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.core.security.SessionManager;
import com.logicaldoc.core.security.Tenant;
import com.logicaldoc.core.security.UserSession;
import com.logicaldoc.i18n.I18N;

/**
 * This servlet is responsible for sessions data.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class SessionsDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(SessionsDataServlet.class);

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		try {
			// Headers required by Internet Explorer
			response.setHeader("Pragma", "public");
			response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
			response.setHeader("Expires", "0");

			List<UserSession> sessions = SessionManager.getInstance().getSessions();
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			df.setTimeZone(TimeZone.getTimeZone("UTC"));

			String locale = request.getParameter("locale");

			/*
			 * Try to check tenant filter for those users trying to list the sessions from another tenant
			 */
			String tenant = null;
			String sid = request.getParameter("sid");
			if (StringUtils.isNotEmpty(sid)) {
				UserSession session = SessionManager.getInstance().get(sid);
				if (!Tenant.DEFAULT_NAME.equals(session.getTenantName()))
					tenant = session.getTenantName();
			}
			
			PrintWriter writer = response.getWriter();
			writer.print("<list>");

			for (UserSession session : sessions) {
				if(tenant!=null && !tenant.equals(session.getTenantName()))
						continue;
				
				// Just to update the session status
				SessionManager.getInstance().get(session.getId());

				writer.print("<session>");
				writer.print("<sid><![CDATA[" + session.getId() + "]]></sid>");
				writer.print("<status>" + session.getStatus() + "</status>");
				if (session.getStatus() == UserSession.STATUS_OPEN)
					writer.print("<statusLabel>" + I18N.message("opened", locale) + "</statusLabel>");
				else if (session.getStatus() == UserSession.STATUS_CLOSED)
					writer.print("<statusLabel>" + I18N.message("closed", locale) + "</statusLabel>");
				else if (session.getStatus() == UserSession.STATUS_EXPIRED)
					writer.print("<statusLabel>" + I18N.message("expired", locale) + "</statusLabel>");
				writer.print("<username><![CDATA[" + session.getUserName() + "]]></username>");
				writer.print("<tenant><![CDATA[" + session.getTenantName() + "]]></tenant>");
				writer.print("<created>" + df.format((Date) session.getCreation()) + "</created>");
				writer.print("<renew>" + df.format((Date) session.getLastRenew()) + "</renew>");
				writer.print("</session>");
			}
			writer.print("</list>");
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