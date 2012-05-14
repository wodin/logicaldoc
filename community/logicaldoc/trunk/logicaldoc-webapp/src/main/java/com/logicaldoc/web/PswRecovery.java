package com.logicaldoc.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.SystemInfo;
import com.logicaldoc.core.communication.EMail;
import com.logicaldoc.core.communication.EMailSender;
import com.logicaldoc.core.communication.Recipient;
import com.logicaldoc.core.document.DownloadTicket;
import com.logicaldoc.core.document.dao.DownloadTicketDAO;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.i18n.I18N;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.ContextProperties;
import com.logicaldoc.util.security.PasswordGenerator;

/**
 * This class allows the user to recover a password.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.1
 */
public class PswRecovery extends HttpServlet {

	private static final long serialVersionUID = 9088160958327454062L;

	protected static Log log = LogFactory.getLog(PswRecovery.class);

	/**
	 * Constructor of the object.
	 */
	public PswRecovery() {
		super();
	}

	/**
	 * The doGet method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		String ticketId = request.getParameter("ticketId");
		String userId = request.getParameter("userId");

		if (StringUtils.isEmpty(ticketId)) {
			ticketId = (String) request.getAttribute("ticketId");
		}

		if (StringUtils.isEmpty(ticketId)) {
			ticketId = (String) session.getAttribute("ticketId");
		}

		log.debug("Recover password for ticket with ticketId=" + ticketId);

		try {
			DownloadTicketDAO ticketDao = (DownloadTicketDAO) Context.getInstance().getBean(DownloadTicketDAO.class);
			DownloadTicket ticket = ticketDao.findByTicketId(ticketId);

			if ((ticket != null) && ticket.getType() == DownloadTicket.PSW_RECOVERY) {

				if (ticket.isTicketExpired()) {
					response.getWriter().println("Request not valid");
					return;
				}

				UserDAO userDao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
				User user = userDao.findById(Long.parseLong(userId));

				EMail email;
				try {
					email = new EMail();
					Recipient recipient = new Recipient();
					recipient.setAddress(user.getEmail());
					email.addRecipient(recipient);
					email.setFolder("outbox");

					// Generate a new password
					ContextProperties pbean = (ContextProperties) Context.getInstance()
							.getBean(ContextProperties.class);
					String password = new PasswordGenerator().generate(pbean.getInt("password.size"));
					user.setDecodedPassword(password);
					user.setPasswordChanged(new Date());

					boolean stored = userDao.store(user);
					if (stored) {
						Locale locale = new Locale(user.getLanguage());

						email.setRead(1);
						email.setSentDate(new Date());
						email.setUserName(user.getUserName());
						email.setLocale(locale);

						/*
						 * Prepare the template
						 */
						Map<String, String> args = new HashMap<String, String>();
						String address = request.getScheme() + "://" + request.getServerName() + ":"
								+ request.getServerPort() + request.getContextPath();
						args.put("_url", address);
						args.put("_product", SystemInfo.get().getProduct());
						args.put(
								"_message",
								I18N.message("emailnotifyaccount", locale, new Object[] {
										user.getFirstName() + " " + user.getName(), "", user.getUserName(), password,
										address }));

						EMailSender sender = (EMailSender) Context.getInstance().getBean(EMailSender.class);
						sender.send(email, "psw.rec1", args);

						response.getWriter().println("A message was sent to " + user.getEmail());

						ticket.setCount(ticket.getCount() + 1);
						ticketDao.store(ticket);
					} else {
						response.getWriter().println("Unable to recover password");
					}
				} catch (Throwable e) {
					log.error(e.getMessage(), e);
					response.getWriter().println("Request not valid");
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * The doPost method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to
	 * post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");

		PrintWriter out = response.getWriter();
		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		out.println("<HTML>");
		out.println("  <HEAD><TITLE>Download Ticket Action</TITLE></HEAD>");
		out.println("  <BODY>");
		out.print("    This is ");
		out.print(this.getClass());
		out.println(", using the POST method");
		out.println("  </BODY>");
		out.println("</HTML>");
		out.flush();
		out.close();
	}
}
