package com.logicaldoc.web.data;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.logicaldoc.core.folder.Folder;
import com.logicaldoc.core.folder.FolderDAO;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.UserSession;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.core.util.IconSelector;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.util.ServiceUtil;

/**
 * This servlet is responsible for folders data.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class FoldersDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(FoldersDataServlet.class);

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		try {
			response.setContentType("text/xml");
			response.setCharacterEncoding("UTF-8");

			// Avoid resource caching
			response.setHeader("Pragma", "no-cache");
			response.setHeader("Cache-Control", "no-store");
			response.setDateHeader("Expires", 0);

			if (request.getParameter("parent") != null && request.getParameter("parent").startsWith("d-")) {
				// The user clicked on a file
				PrintWriter writer = response.getWriter();
				writer.write("<list></list>");
				return;
			}

			UserSession session = ServiceUtil.validateSession(request);
			long tenantId = session.getTenantId();

			FolderDAO folderDao = (FolderDAO) Context.get().getBean(FolderDAO.class);
			long parent = Folder.ROOTID;

			if ("/".equals(request.getParameter("parent"))) {
				Folder root = folderDao.findRoot(tenantId);
				if (root == null)
					throw new Exception("Unable to locate the root folder for tenant " + tenantId);
				parent = root.getId();
			} else if (request.getParameter("parent") != null)
				parent = Long.parseLong(request.getParameter("parent"));

			Folder parentFolder = folderDao.findFolder(parent);

			Context context = Context.get();
			UserDAO udao = (UserDAO) context.getBean(UserDAO.class);
			User user = udao.findById(session.getUserId());
			udao.initialize(user);

			PrintWriter writer = response.getWriter();
			writer.write("<list>");

			StringBuffer query = new StringBuffer(
					"select ld_id, ld_parentid, ld_name, ld_type, ld_foldref from ld_folder where ld_deleted=0 and ld_hidden=0 and not ld_id=ld_parentid and ld_parentid = ? and ld_tenantid = ? ");
			if (!user.isInGroup("admin")) {
				Collection<Long> accessibleIds = folderDao.findFolderIdByUserId(session.getUserId(),
						parentFolder.getId(), false);
				String idsStr = accessibleIds.toString().replace('[', '(').replace(']', ')');
				query.append(" and ld_id in " + idsStr);
			}
			query.append(" order by ld_position asc, ld_name asc");

			SqlRowSet rs = folderDao.queryForRowSet(query.toString(), new Long[] { parentFolder.getId(), tenantId },
					null);
			if (rs != null)
				while (rs.next()) {
					writer.print("<folder>");
					writer.print("<folderId>" + rs.getLong(1) + "</folderId>");
					writer.print("<parent>" + parent + "</parent>");
					writer.print("<name><![CDATA[" + rs.getString(3) + "]]></name>");
					writer.print("<type>" + rs.getInt(4) + "</type>");
					if (rs.getObject(5) != null)
						writer.print("<foldRef>" + rs.getLong(5) + "</foldRef>");
					writer.print("<customIcon>" + (rs.getInt(4) == Folder.TYPE_ALIAS ? "folder_alias" : "folder")
							+ "</customIcon>");
					writer.print("<publishedStatus>yes</publishedStatus>");
					writer.print("</folder>");
				}

			if (request.getParameter("withdocs") != null) {
				query = new StringBuffer(
						"select ld_id, ld_filename, ld_title, ld_filesize, ld_published, ld_startpublishing, ld_stoppublishing from ld_document where ld_deleted=0 and ld_folderid=? ");
				if (!user.isInGroup("admin") && !user.isInGroup("publisher")) {
					query.append(" and ld_published=1");
					query.append(" and (ld_startpublishing is null or CURRENT_TIMESTAMP > ld_startpublishing) ");
					query.append(" and (ld_stoppublishing is null or CURRENT_TIMESTAMP < ld_stoppublishing) ");
				}
				query.append(" order by ld_title");

				rs = folderDao.queryForRowSet(query.toString(), new Long[] { parentFolder.getId() }, null);
				if (rs != null)
					while (rs.next()) {
						Date now = new Date();
						boolean published = (rs.getInt(5) == 1) && (rs.getDate(6) == null || now.after(rs.getDate(6)))
								&& (rs.getDate(7) == null || now.before(rs.getDate(7)));

						writer.print("<folder>");
						writer.print("<folderId>d-" + rs.getLong(1) + "</folderId>");
						writer.print("<parent>" + parent + "</parent>");
						writer.print("<name><![CDATA[" + rs.getString(2) + "]]></name>");
						writer.print("<type>file</type>");
						writer.print("<customIcon>"
								+ FilenameUtils.getBaseName(IconSelector.selectIcon(FilenameUtils.getExtension(rs
										.getString(2)))) + "</customIcon>");
						writer.print("<size>" + rs.getInt(4) + "</size>");
						writer.print("<publishedStatus>" + (published ? "yes" : "no") + "</publishedStatus>");
						writer.print("</folder>");
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