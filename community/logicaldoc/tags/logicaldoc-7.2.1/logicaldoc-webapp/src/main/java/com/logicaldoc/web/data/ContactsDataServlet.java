package com.logicaldoc.web.data;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.core.contact.Contact;
import com.logicaldoc.core.contact.ContactDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.util.ServiceUtil;

/**
 * This servlet is responsible for contacts data.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.8
 */
public class ContactsDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(ContactsDataServlet.class);

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		try {
			ServiceUtil.validateSession(request);

			String userId = request.getParameter("userId");

			response.setContentType("text/xml");
			response.setCharacterEncoding("UTF-8");

			// Headers required by Internet Explorer
			response.setHeader("Pragma", "public");
			response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
			response.setHeader("Expires", "0");

			PrintWriter writer = response.getWriter();
			writer.write("<list>");

			ContactDAO dao = (ContactDAO) Context.getInstance().getBean(ContactDAO.class);

			/*
			 * Iterate over records composing the response XML document
			 */
			for (Contact contact : dao.findByUser(Long.parseLong(userId), null)) {
				if (contact.getDeleted() == 1)
					continue;

				writer.print("<contact>");
				writer.print("<id>" + contact.getId() + "</id>");
				writer.print("<email><![CDATA[" + contact.getEmail() + "]]></email>");
				if (contact.getFirstName() != null)
					writer.print("<firstName><![CDATA[" + contact.getFirstName() + "]]></firstName>");
				if (contact.getLastName() != null)
					writer.print("<lastName><![CDATA[" + contact.getLastName() + "]]></lastName>");
				if (contact.getCompany() != null)
					writer.print("<company><![CDATA[" + contact.getCompany() + "]]></company>");
				writer.print("</contact>");
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
