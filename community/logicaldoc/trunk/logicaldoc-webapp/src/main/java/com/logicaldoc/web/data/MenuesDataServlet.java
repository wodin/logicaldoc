package com.logicaldoc.web.data;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.UserSession;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.util.ServiceUtil;

/**
 * This servlet is responsible for menus data.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class MenuesDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(MenuesDataServlet.class);

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		try {
			UserSession session = ServiceUtil.validateSession(request);

			response.setContentType("text/xml");
			response.setCharacterEncoding("UTF-8");

			// Headers required by Internet Explorer
			response.setHeader("Pragma", "public");
			response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
			response.setHeader("Expires", "0");

			Context context = Context.getInstance();
			MenuDAO dao = (MenuDAO) context.getBean(MenuDAO.class);

			PrintWriter writer = response.getWriter();
			writer.write("<list>");

			/*
			 * Get the visible children
			 */
			List<Menu> menues = dao.findByUserId(session.getUserId());

			/*
			 * Iterate over records composing the response XML document
			 */
			for (Menu menu : menues) {
				writer.print("<menu>");
				writer.print("<id>" + menu.getId() + "</id>");
				writer.print("<name><![CDATA[" + menu.getText() + "]]></name>");
				writer.print("<position><![CDATA[" + menu.getPosition() + "]]></position>");
				writer.print("</menu>");
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