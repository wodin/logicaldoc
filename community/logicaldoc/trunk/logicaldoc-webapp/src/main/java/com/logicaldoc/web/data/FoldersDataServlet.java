package com.logicaldoc.web.data;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.logicaldoc.core.document.dao.FolderDAO;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.Permission;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.util.SessionUtil;

/**
 * 
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class FoldersDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		SessionUtil.validateSession(request);

		long parent = Long.parseLong(request.getParameter("parent"));

		response.setContentType("text/xml");

		// Headers required by Internet Explorer
		response.setHeader("Pragma", "public");
		response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
		response.setHeader("Expires", "0");

		Context context = Context.getInstance();
		FolderDAO dao = (FolderDAO) context.getBean(FolderDAO.class);

		PrintWriter writer = response.getWriter();
		writer.write("<list>");

		if (parent == Constants.ROOT_FOLDERID) {
			// Add the 'Documents' root
			writer.print("<folder>");
			writer.print("<id>" + Menu.MENUID_DOCUMENTS + "</id>");
			writer.print("<parent>" + parent + "</parent>");
			writer.print("<name>/</name>");
			writer.print("<"
					+ Constants.PERMISSION_ADD
					+ ">"
					+ dao.isPermissionEnabled(Permission.ADD_CHILD, Menu.MENUID_DOCUMENTS, SessionUtil.getSessionUser(
							request).getId()) + "</" + Constants.PERMISSION_ADD + ">");
			writer.print("<"
					+ Constants.PERMISSION_DELETE
					+ ">"
					+ dao.isPermissionEnabled(Permission.DELETE, Menu.MENUID_DOCUMENTS, SessionUtil.getSessionUser(
							request).getId()) + "</" + Constants.PERMISSION_DELETE + ">");
			writer.print("<"
					+ Constants.PERMISSION_RENAME
					+ ">"
					+ dao.isPermissionEnabled(Permission.RENAME, Menu.MENUID_DOCUMENTS, SessionUtil.getSessionUser(
							request).getId()) + "</" + Constants.PERMISSION_RENAME + ">");
			writer.print("<"
					+ Constants.PERMISSION_WRITE
					+ ">"
					+ dao.isPermissionEnabled(Permission.WRITE, Menu.MENUID_DOCUMENTS, SessionUtil.getSessionUser(
							request).getId()) + "</" + Constants.PERMISSION_WRITE + ">");
			writer.print("</folder>");
			writer.write("</list>");
			return;
		}
		/*
		 * Execute the Query
		 */

		StringBuffer query = new StringBuffer("select A.id, A.parentId, A.text from Menu A where A.type = "
				+ Menu.MENUTYPE_DIRECTORY + " and A.parentId = " + parent);

		List<Object> records = (List<Object>) dao.findByQuery(query.toString(), null, null);

		/*
		 * Iterqte over records composing the response XML document
		 */

		for (Object record : records) {
			Object[] cols = (Object[]) record;
			writer.print("<folder>");
			writer.print("<id>" + cols[0] + "</id>");
			writer.print("<parent>" + cols[1] + "</parent>");
			writer.print("<name><![CDATA[" + cols[2] + "]]></name>");
			writer.print("<"
					+ Constants.PERMISSION_ADD
					+ ">"
					+ dao.isPermissionEnabled(Permission.ADD_CHILD, Long.parseLong(cols[0].toString()), SessionUtil
							.getSessionUser(request).getId()) + "</" + Constants.PERMISSION_ADD + ">");
			writer.print("<"
					+ Constants.PERMISSION_DELETE
					+ ">"
					+ dao.isPermissionEnabled(Permission.DELETE, Long.parseLong(cols[0].toString()), SessionUtil
							.getSessionUser(request).getId()) + "</" + Constants.PERMISSION_DELETE + ">");
			writer.print("<"
					+ Constants.PERMISSION_RENAME
					+ ">"
					+ dao.isPermissionEnabled(Permission.RENAME, Long.parseLong(cols[0].toString()), SessionUtil
							.getSessionUser(request).getId()) + "</" + Constants.PERMISSION_RENAME + ">");
			writer.print("<"
					+ Constants.PERMISSION_WRITE
					+ ">"
					+ dao.isPermissionEnabled(Permission.WRITE, Long.parseLong(cols[0].toString()), SessionUtil
							.getSessionUser(request).getId()) + "</" + Constants.PERMISSION_WRITE + ">");
			writer.print("</folder>");
		}

		writer.write("</list>");
	}
}