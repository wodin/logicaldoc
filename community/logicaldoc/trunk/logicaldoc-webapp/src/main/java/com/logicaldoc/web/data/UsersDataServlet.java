package com.logicaldoc.web.data;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.logicaldoc.core.security.Group;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.GroupDAO;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.util.SessionUtil;

/**
 * This servlet is responsible for users data.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class UsersDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		SessionUtil.validateSession(request);

		String groupId = request.getParameter("groupId");

		response.setContentType("text/xml");

		// Headers required by Internet Explorer
		response.setHeader("Pragma", "public");
		response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
		response.setHeader("Expires", "0");

		PrintWriter writer = response.getWriter();
		writer.print("<list>");

		if (groupId != null && !groupId.trim().isEmpty()) {
			GroupDAO groupDao = (GroupDAO) Context.getInstance().getBean(GroupDAO.class);
			Group group = groupDao.findById(Long.parseLong(groupId));
			groupDao.initialize(group);

			/*
			 * Iterate over records composing the response XML document
			 */
			for (User user : group.getUsers()) {
				writer.print("<user>");
				writer.print("<id>" + user.getId() + "</id>");
				writer.print("<username><![CDATA[" + user.getUserName() + "]]></username>");
				if (user.getEnabled() == 1)
					writer.print("<eenabled>0</eenabled>");
				else if (user.getEnabled() == 0)
					writer.print("<eenabled>2</eenabled>");
				writer.print("<name><![CDATA[" + user.getName() + "]]></name>");
				writer.print("<firstName><![CDATA[" + user.getFirstName() + "]]></firstName>");
				writer.print("<label><![CDATA[" + user.getFullName() + "]]></label>");
				writer.print("<email><![CDATA[" + user.getEmail() + "]]></email>");
				writer.print("<phone>" + user.getTelephone() + "</phone>");
				writer.print("<cell>" + user.getTelephone2() + "</cell>");
				writer.print("</user>");
			}
		} else {
			UserDAO userDao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
			StringBuffer query = new StringBuffer(
					"select A.id, A.userName, A.enabled, A.name, A.firstName, A.email, A.telephone, A.telephone2 "
							+ "from com.logicaldoc.core.security.User A where A.deleted = 0 ");

			List<Object> records = (List<Object>) userDao.findByQuery(query.toString(), null, null);

			/*
			 * Iterate over records composing the response XML document
			 */
			for (Object record : records) {
				Object[] cols = (Object[]) record;

				writer.print("<user>");
				writer.print("<id>" + cols[0] + "</id>");
				writer.print("<username><![CDATA[" + cols[1] + "]]></username>");
				if ((Integer) cols[2] == 1)
					writer.print("<eenabled>0</eenabled>");
				else if ((Integer) cols[2] == 0)
					writer.print("<eenabled>2</eenabled>");
				writer.print("<name><![CDATA[" + cols[3] + "]]></name>");
				writer.print("<firstName><![CDATA[" + cols[4] + "]]></firstName>");
				writer.print("<label><![CDATA[" + cols[4] + " " + cols[3] + "]]></label>");
				writer.print("<email><![CDATA[" + cols[5] + "]]></email>");
				writer.print("<phone>" + cols[6] + "</phone>");
				writer.print("<cell>" + cols[7] + "</cell>");
				writer.print("</user>");
			}
		}
		writer.print("</list>");
	}
}
