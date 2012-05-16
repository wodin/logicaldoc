package com.logicaldoc.web.data;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.UserSession;
import com.logicaldoc.core.security.dao.FolderDAO;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.util.SessionUtil;

/**
 * This servlet is responsible for folders data.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class FoldersDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static Log log = LogFactory.getLog(FoldersDataServlet.class);

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		try {
			UserSession session = SessionUtil.validateSession(request);

			long parent = Long.parseLong(request.getParameter("parent"));

			response.setContentType("text/xml");
			response.setCharacterEncoding("UTF-8");

			// Headers required by Internet Explorer
			response.setHeader("Pragma", "public");
			response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
			response.setHeader("Expires", "0");

			Context context = Context.getInstance();
			FolderDAO dao = (FolderDAO) context.getBean(FolderDAO.class);
			UserDAO udao = (UserDAO) context.getBean(UserDAO.class);
			User user = udao.findById(session.getUserId());
			udao.initialize(user);

			PrintWriter writer = response.getWriter();
			writer.write("<list>");

			StringBuffer query = new StringBuffer(
					"select ld_id, ld_parentid, ld_name, ld_type from ld_folder where ld_deleted=0 and not ld_id=ld_parentid and ld_parentid = ? ");
			if (!user.isInGroup("admin")) {
				Collection<Long> accessibleIds = dao.findFolderIdByUserId(session.getUserId());
				String idsStr = accessibleIds.toString().replace('[', '(').replace(']', ')');
				query.append(" and ld_id in " + idsStr);
			}
			query.append(" order by ld_name");

			SqlRowSet rs = dao.queryForRowSet(query.toString(), new Long[] { parent }, null);
			while (rs.next()) {
				writer.print("<folder>");
				writer.print("<folderId>" + rs.getLong(1) + "</folderId>");
				writer.print("<parent>" + rs.getLong(2) + "</parent>");
				writer.print("<name><![CDATA[" + rs.getString(3) + "]]></name>");
				writer.print("<type>" + rs.getInt(4) + "</type>");
				writer.print("</folder>");
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