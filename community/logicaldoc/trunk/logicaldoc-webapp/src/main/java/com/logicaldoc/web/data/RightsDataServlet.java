package com.logicaldoc.web.data;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.logicaldoc.core.security.Group;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.MenuGroup;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.GroupDAO;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.util.SessionUtil;

public class RightsDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		SessionUtil.validateSession(request);

		Long folderId = null;
		if (StringUtils.isNotEmpty(request.getParameter("folderId")))
			folderId = new Long(request.getParameter("folderId"));

		response.setContentType("text/xml");

		// Headers required by Internet Explorer
		response.setHeader("Pragma", "public");
		response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
		response.setHeader("Expires", "0");

		MenuDAO menuDao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		GroupDAO groupDao = (GroupDAO) Context.getInstance().getBean(GroupDAO.class);
		Menu menu = menuDao.findById(folderId);
		menuDao.initialize(menu);

		PrintWriter writer = response.getWriter();
		writer.write("<list>");

		/*
		 * Iterate over records composing the response XML document
		 */
		for (Group group : groupDao.findAll()) {
			if (group.getType() == Group.TYPE_DEFAULT
					|| ((group.getType() != Group.TYPE_DEFAULT) && (group.getUsers().isEmpty() || group.getUsers()
							.iterator().next().getType() == User.TYPE_DEFAULT))) {
				MenuGroup menuGroup = menu.getMenuGroup(group.getId());
				if (menuGroup != null) {
					writer.print("<right>");
					writer.print("<entityId>" + group.getId() + "</entityId>");
					writer.print("<entity><![CDATA[" + group.getName() + "]]></entity>");
					writer.print("<read>true</read>");
					writer.print("<write>" + menuGroup.getWrite() + "</write>");
					writer.print("<add>" + menuGroup.getAddChild() + "</add>");
					writer.print("<security>" + menuGroup.getManageSecurity() + "</security>");
					writer.print("<immutable>" + menuGroup.getManageImmutability() + "</immutable>");
					writer.print("<delete>" + menuGroup.getDelete() + "</delete>");
					writer.print("<rename>" + menuGroup.getRename() + "</rename>");
					writer.print("<import>" + menuGroup.getBulkImport() + "</import>");
					writer.print("<export>" + menuGroup.getBulkExport() + "</export>");
					writer.print("<sign>" + menuGroup.getSign() + "</sign>");
					writer.print("<archive>" + menuGroup.getArchive() + "</archive>");
					writer.print("<workflow>" + menuGroup.getWorkflow() + "</workflow>");
					writer.print("</right>");
				}
			}
		}
		writer.write("</list>");
	}
}