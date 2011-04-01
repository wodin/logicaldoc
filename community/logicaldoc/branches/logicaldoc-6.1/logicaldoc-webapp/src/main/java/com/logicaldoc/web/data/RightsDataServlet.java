package com.logicaldoc.web.data;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.security.Folder;
import com.logicaldoc.core.security.FolderGroup;
import com.logicaldoc.core.security.Group;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.MenuGroup;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.FolderDAO;
import com.logicaldoc.core.security.dao.GroupDAO;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.i18n.I18N;
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

	private static Log log = LogFactory.getLog(RightsDataServlet.class);

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		try {
			SessionUtil.validateSession(request);

			Long folderId = null;
			if (StringUtils.isNotEmpty(request.getParameter("folderId")))
				folderId = new Long(request.getParameter("folderId"));

			Long menuId = null;
			if (StringUtils.isNotEmpty(request.getParameter("menuId")))
				menuId = new Long(request.getParameter("menuId"));

			String locale = request.getParameter("locale");

			response.setContentType("text/xml");
			response.setCharacterEncoding("UTF-8");

			// Headers required by Internet Explorer
			response.setHeader("Pragma", "public");
			response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
			response.setHeader("Expires", "0");

			if (folderId != null)
				folderRights(response, folderId, locale);
			else
				menuRights(response, menuId, locale);
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

	private void folderRights(HttpServletResponse response, Long folderId, String locale) throws IOException {
		FolderDAO folderDao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		GroupDAO groupDao = (GroupDAO) Context.getInstance().getBean(GroupDAO.class);
		Folder folder = folderDao.findById(folderId);
		folderDao.initialize(folder);

		Folder ref = folder;
		if (folder.getSecurityRef() != null) {
			ref = folderDao.findById(folder.getSecurityRef());
			folderDao.initialize(ref);
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
				FolderGroup folderGroup = ref.getFolderGroup(group.getId());
				if (folderGroup != null) {
					writer.print("<right>");
					writer.print("<entityId>" + group.getId() + "</entityId>");
					if (group.getType() == Group.TYPE_DEFAULT)
						writer.print("<entity><![CDATA[" + I18N.message("group", locale) + ": " + group.getName()
								+ "]]></entity>");
					else {
						User user = group.getUsers().iterator().next();
						writer.print("<entity><![CDATA[" + I18N.message("user", locale) + ": " + user.getFullName()
								+ " (" + user.getUserName() + ")]]></entity>");
					}
					writer.print("<read>" + true + "</read>");
					writer.print("<write>" + (folderGroup.getWrite() == 1 ? true : false) + "</write>");
					writer.print("<add>" + (folderGroup.getAdd() == 1 ? true : false) + "</add>");
					writer.print("<security>" + (folderGroup.getSecurity() == 1 ? true : false) + "</security>");
					writer.print("<immutable>" + (folderGroup.getImmutable() == 1 ? true : false) + "</immutable>");
					writer.print("<delete>" + (folderGroup.getDelete() == 1 ? true : false) + "</delete>");
					writer.print("<rename>" + (folderGroup.getRename() == 1 ? true : false) + "</rename>");
					writer.print("<import>" + (folderGroup.getImport() == 1 ? true : false) + "</import>");
					writer.print("<export>" + (folderGroup.getExport() == 1 ? true : false) + "</export>");
					writer.print("<sign>" + (folderGroup.getSign() == 1 ? true : false) + "</sign>");
					writer.print("<archive>" + (folderGroup.getArchive() == 1 ? true : false) + "</archive>");
					writer.print("<workflow>" + (folderGroup.getWorkflow() == 1 ? true : false) + "</workflow>");
					writer.print("<download>" + (folderGroup.getDownload() == 1 ? true : false) + "</download>");
					writer.print("<type>" + group.getType() + "</type>");
					writer.print("</right>");
				}
			}
		}
		writer.write("</list>");
	}

	private void menuRights(HttpServletResponse response, Long menuId, String locale) throws IOException {
		MenuDAO menuDao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		GroupDAO groupDao = (GroupDAO) Context.getInstance().getBean(GroupDAO.class);
		Menu menu = menuDao.findById(menuId);
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
					if (group.getType() == Group.TYPE_DEFAULT)
						writer.print("<entity><![CDATA[" + I18N.message("group", locale) + ": " + group.getName()
								+ "]]></entity>");
					else {
						User user = group.getUsers().iterator().next();
						writer.print("<entity><![CDATA[" + I18N.message("user", locale) + ": " + user.getFullName()
								+ " (" + user.getUserName() + ")]]></entity>");
					}
					writer.print("<type>" + group.getType() + "</type>");
					writer.print("</right>");
				}
			}
		}
		writer.write("</list>");
	}
}