package com.logicaldoc.web.data;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.folder.FolderDAO;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.Session;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.util.ServiceUtil;

public class FolderAliasesDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(FolderAliasesDataServlet.class);

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		try {
			Session session = ServiceUtil.validateSession(request);

			Long folderId = null;
			if (StringUtils.isNotEmpty(request.getParameter("folderId")))
				folderId = new Long(request.getParameter("folderId"));

			response.setContentType("text/xml");
			response.setCharacterEncoding("UTF-8");

			// Avoid resource caching
			response.setHeader("Pragma", "no-cache");
			response.setHeader("Cache-Control", "no-store");
			response.setDateHeader("Expires", 0);

			PrintWriter writer = response.getWriter();
			writer.write("<list>");

			Context context = Context.get();
			DocumentDAO dao = (DocumentDAO) context.getBean(DocumentDAO.class);
			FolderDAO folderDAO = (FolderDAO) context.getBean(FolderDAO.class);
			Collection<Long> ids = folderDAO.findFolderIdByUserId(session.getUserId(), null, true);

			StringBuffer query = new StringBuffer("select id, name from Folder where deleted = 0 and foldRef = " + folderId);

			User user = ServiceUtil.getSessionUser(request);
			if (!user.isInGroup("admin")) {
				query.append(" and id in ");
				query.append(ids.toString().replace('[', ' ').replace(']', ' '));
			}

			List<Object> records = (List<Object>) dao.findByQuery(query.toString(), null, null);

			/*
			 * Iterate over records composing the response XML document
			 */
			for (Object record : records) {
				Object[] cols = (Object[]) record;

				writer.print("<alias>");
				writer.print("<id>" + cols[0] + "</id>");
				writer.print("<name><![CDATA[" + cols[1] + "]]></name>");
				writer.print("<icon>folder_alias_closed</icon>");
				writer.print("<path><![CDATA[" + folderDAO.computePathExtended((Long) cols[0]) + "]]></path>");
				writer.print("</alias>");
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