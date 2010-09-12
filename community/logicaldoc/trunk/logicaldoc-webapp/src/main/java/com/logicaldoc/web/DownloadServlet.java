package com.logicaldoc.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.Version;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.document.dao.VersionDAO;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.UserSession;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.util.ServletDocUtil;
import com.logicaldoc.web.util.SessionUtil;

/**
 * This servlet is responsible for document downloads. It searches for the
 * attribute docId in any scope and extracts the proper document's content.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class DownloadServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected static Log log = LogFactory.getLog(DownloadServlet.class);

	/**
	 * The doGet method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		UserSession session = SessionUtil.validateSession(request);

		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		UserDAO udao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
		VersionDAO versDao = (VersionDAO) Context.getInstance().getBean(VersionDAO.class);
		// Load the user associated to the session
		User user = udao.findById(session.getUserId());
		if (user == null)
			return;

		// Flag indicating to download only indexed text
		String downloadText = request.getParameter("downloadText");
		String docId = request.getParameter("docId");
		String versionId = request.getParameter("versionId");
		String fileVersion = request.getParameter("fileVersion");
		String filename = "";

		Version version = null;
		Document doc = null;

		try {
			if (!StringUtils.isEmpty(docId))
				doc = docDao.findById(Long.parseLong(docId));
			if (!StringUtils.isEmpty(versionId)) {
				version = versDao.findById(Long.parseLong(versionId));
				if (doc == null)
					doc = docDao.findById(version.getDocId());
			}

			if (version != null)
				filename = version.getFileName();
			else
				filename = doc.getFileName();

			response.setHeader("Content-Length", Long.toString(doc.getFileSize()));
			response.setHeader("Pragma", "public");
			response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
			response.setHeader("Expires", "0");
			
			if (request.getParameter("open") == null) {
				response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
			}else{
				response.setHeader("Content-Disposition", "inline; filename=\"" + filename + "\"");
			}

			if (StringUtils.isEmpty(fileVersion)) {
				if (version != null)
					fileVersion = version.getFileVersion();
				else
					fileVersion = doc.getFileVersion();
			}

			String suffix = request.getParameter("suffix");
			if (StringUtils.isEmpty(suffix)) {
				suffix = "";
			}

			if (version != null)
				log.debug("Download version id=" + versionId);
			else
				log.debug("Download document id=" + docId);

			if ("true".equals(downloadText)) {
				ServletDocUtil.downloadDocumentText(request, response, doc.getId());
			} else {
				ServletDocUtil.downloadDocument(request, response, doc.getId(), fileVersion, suffix, user);
			}
		} catch (Throwable ex) {
			log.error(ex.getMessage(), ex);
		}
	}
}
