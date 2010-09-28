package com.logicaldoc.web.data;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.security.Group;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.GroupDAO;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.i18n.I18N;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.util.SessionUtil;

/**
 * This servlet is responsible for groups data.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class GroupsDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static Log log = LogFactory.getLog(GroupsDataServlet.class);

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		try {
			SessionUtil.validateSession(request);

			String locale = request.getParameter("locale");
			String excludeUserId = request.getParameter("excludeUserId");

			response.setContentType("text/xml");

			// Headers required by Internet Explorer
			response.setHeader("Pragma", "public");
			response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
			response.setHeader("Expires", "0");

			PrintWriter writer = response.getWriter();
			writer.write("<list>");

			GroupDAO dao = (GroupDAO) Context.getInstance().getBean(GroupDAO.class);
			if (excludeUserId != null && !excludeUserId.trim().isEmpty()) {
				UserDAO userDao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
				User user = userDao.findById(Long.parseLong(excludeUserId));
				userDao.initialize(user);

				/*
				 * Iterate over records composing the response XML document
				 */
				for (Group group : dao.findAll()) {
					if (group.getDeleted() == 1 || user.getGroups().contains(group)
							|| group.getType() != Group.TYPE_DEFAULT)
						continue;

					writer.print("<group>");
					writer.print("<id>" + group.getId() + "</id>");
					writer.print("<name><![CDATA[" + group.getName() + "]]></name>");
					writer.print("<description><![CDATA[" + group.getDescription() + "]]></description>");
					writer.print("<label><![CDATA[" + I18N.message("group", locale) + ": " + group.getName()
							+ "]]></label>");
					writer.print("</group>");
				}
			} else {
				StringBuffer query = new StringBuffer("select A.id, A.name, A.description "
						+ "from com.logicaldoc.core.security.Group A where A.deleted = 0 and A.type = "
						+ Group.TYPE_DEFAULT);

				List<Object> records = (List<Object>) dao.findByQuery(query.toString(), null, null);

				/*
				 * Iterate over records composing the response XML document
				 */
				for (Object record : records) {
					Object[] cols = (Object[]) record;

					writer.print("<group>");
					writer.print("<id>" + cols[0] + "</id>");
					writer.print("<name><![CDATA[" + cols[1] + "]]></name>");
					writer.print("<description><![CDATA[" + cols[2] + "]]></description>");
					writer.print("<label><![CDATA[" + I18N.message("group", locale) + ": " + (String) cols[1]
							+ "]]></label>");
					writer.print("</group>");
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
