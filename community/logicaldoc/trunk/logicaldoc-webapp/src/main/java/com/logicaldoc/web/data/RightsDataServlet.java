package com.logicaldoc.web.data;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.logicaldoc.core.security.Folder;
import com.logicaldoc.core.security.FolderGroup;
import com.logicaldoc.core.security.Group;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.FolderDAO;
import com.logicaldoc.core.security.dao.GroupDAO;
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
					writer.print("<entity><![CDATA[" + group.getName() + "]]></entity>");
					writer.print("<read>" + true + "</read>");
					writer.print("<write>" + (folderGroup.getWrite() == 1 ? true : false) + "</write>");
					writer.print("<add>" + (folderGroup.getAddChild() == 1 ? true : false) + "</add>");
					writer.print("<security>" + (folderGroup.getManageSecurity() == 1 ? true : false) + "</security>");
					writer.print("<immutable>" + (folderGroup.getManageImmutability() == 1 ? true : false)
							+ "</immutable>");
					writer.print("<delete>" + (folderGroup.getDelete() == 1 ? true : false) + "</delete>");
					writer.print("<rename>" + (folderGroup.getRename() == 1 ? true : false) + "</rename>");
					writer.print("<import>" + (folderGroup.getBulkImport() == 1 ? true : false) + "</import>");
					writer.print("<export>" + (folderGroup.getBulkExport() == 1 ? true : false) + "</export>");
					writer.print("<sign>" + (folderGroup.getSign() == 1 ? true : false) + "</sign>");
					writer.print("<archive>" + (folderGroup.getArchive() == 1 ? true : false) + "</archive>");
					writer.print("<workflow>" + (folderGroup.getWorkflow() == 1 ? true : false) + "</workflow>");
					writer.print("</right>");
				}
			}
		}
		writer.write("</list>");
	}
}