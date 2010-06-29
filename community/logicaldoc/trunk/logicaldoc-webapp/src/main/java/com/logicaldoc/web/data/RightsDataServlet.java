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

/**
 * This servlet is responsible for rights data.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
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
		
		Menu ref=menu;
		if(menu.getSecurityRef()!=null){
			ref=menuDao.findById(menu.getSecurityRef());
		    menuDao.initialize(ref);
		}

		PrintWriter writer = response.getWriter();
		writer.write("<list>");

		/*
		 * Iterate over records composing the response XML document
		 */
		for (Group group : groupDao.findAll()) {
			if (group.getType() == Group.TYPE_DEFAULT
					|| ((group.getType() != Group.TYPE_DEFAULT) && (group.getUsers().isEmpty() || group.getUsers()
							.iterator().next().getType() == User.TYPE_DEFAULT))) {
				MenuGroup menuGroup = ref.getMenuGroup(group.getId());
				if (menuGroup != null) {
					writer.print("<right>");
					writer.print("<entityId>" + group.getId() + "</entityId>");
					writer.print("<entity><![CDATA[" + group.getName() + "]]></entity>");
					writer.print("<read>" + true + "</read>");
					writer.print("<write>" + (menuGroup.getWrite() == 1 ? true : false) + "</write>");
					writer.print("<add>" + (menuGroup.getAddChild() == 1 ? true : false) + "</add>");
					writer.print("<security>" + (menuGroup.getManageSecurity() == 1 ? true : false) + "</security>");
					writer.print("<immutable>" + (menuGroup.getManageImmutability() == 1 ? true : false)
							+ "</immutable>");
					writer.print("<delete>" + (menuGroup.getDelete() == 1 ? true : false) + "</delete>");
					writer.print("<rename>" + (menuGroup.getRename() == 1 ? true : false) + "</rename>");
					writer.print("<import>" + (menuGroup.getBulkImport() == 1 ? true : false) + "</import>");
					writer.print("<export>" + (menuGroup.getBulkExport() == 1 ? true : false) + "</export>");
					writer.print("<sign>" + (menuGroup.getSign() == 1 ? true : false) + "</sign>");
					writer.print("<archive>" + (menuGroup.getArchive() == 1 ? true : false) + "</archive>");
					writer.print("<workflow>" + (menuGroup.getWorkflow() == 1 ? true : false) + "</workflow>");
					writer.print("</right>");
				}
			}
		}
		writer.write("</list>");
	}
}