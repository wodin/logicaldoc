package com.logicaldoc.web.data;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.UserSession;
import com.logicaldoc.core.security.dao.FolderDAO;
import com.logicaldoc.core.util.IconSelector;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.util.ServiceUtil;

public class DocumentAliasesDataServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(DocumentAliasesDataServlet.class);

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		try {
			UserSession session = ServiceUtil.validateSession(request);

			Long docId = null;
			if (StringUtils.isNotEmpty(request.getParameter("docId")))
				docId = new Long(request.getParameter("docId"));

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

			StringBuffer query = new StringBuffer(
					"select id, title, fileName, folder.id from Document where deleted = 0 and docRef = " + docId);

			User user = ServiceUtil.getSessionUser(request);
			if (!user.isInGroup("admin")) {
				query.append(" and folder.id in ");
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
				writer.print("<title><![CDATA[" + cols[1] + "]]></title>");
				writer.print("<filename><![CDATA[" + cols[2] + "]]></filename>");
				writer.print("<folderId>" + cols[3] + "</folderId>");
				writer.print("<icon>"
						+ FilenameUtils.getBaseName(IconSelector.selectIcon(FilenameUtils
								.getExtension((String) cols[2]))) + "</icon>");
				writer.print("<path><![CDATA[" + folderDAO.computePathExtended((Long) cols[3]) + "/" + cols[2]
						+ "-sc]]></path>");
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