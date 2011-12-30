package com.logicaldoc.web;

import java.io.FileNotFoundException;
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
import com.logicaldoc.core.security.Permission;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.UserSession;
import com.logicaldoc.core.security.dao.FolderDAO;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.util.ServletIOUtil;
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

		// Load the user associated to the session
		UserDAO udao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
		User user = udao.findById(session.getUserId());
		if (user == null)
			return;

		try {
			if (request.getParameter("pluginId") != null)
				ServletIOUtil.downloadPluginResource(request, response, request.getParameter("pluginId"),
						request.getParameter("resourcePath"), request.getParameter("fileName"));
			else
				downloadDocument(request, response, user);
		} catch (Throwable ex) {
			log.error(ex.getMessage(), ex);
		}
	}

	protected void downloadDocument(HttpServletRequest request, HttpServletResponse response, User user)
			throws FileNotFoundException, IOException, ServletException {
		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		VersionDAO versDao = (VersionDAO) Context.getInstance().getBean(VersionDAO.class);
		FolderDAO folderDao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);

		// Flag indicating to download only indexed text
		String downloadText = request.getParameter("downloadText");
		String docId = request.getParameter("docId");
		String versionId = request.getParameter("versionId");
		String fileVersion = request.getParameter("fileVersion");
		String filename = "";

		Version version = null;
		Document doc = null;

		if (!StringUtils.isEmpty(docId)) {
			doc = docDao.findById(Long.parseLong(docId));

			if (!folderDao.isPermissionEnabled(Permission.DOWNLOAD, doc.getFolder().getId(), user.getId()))
				throw new IOException("You don't have the DOWNLOAD permission");
			
			/*
			 * In case of alias we have to work on the real document
			 */
			if (doc.getDocRef() != null)
				doc = docDao.findById(doc.getDocRef());
		}

		if (!StringUtils.isEmpty(versionId)) {
			version = versDao.findById(Long.parseLong(versionId));
			if (doc == null){
				doc = docDao.findById(version.getDocId());
				
				if (!folderDao.isPermissionEnabled(Permission.DOWNLOAD, doc.getFolder().getId(), user.getId()))
					throw new IOException("You don't have the DOWNLOAD permission");
				
				/*
				 * In case of alias we have to work on the real document
				 */
				if (doc.getDocRef() != null)
					doc = docDao.findById(doc.getDocRef());
			}
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
		} else {
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
			ServletIOUtil.downloadDocumentText(request, response, doc.getId(), user);
		} else {
			ServletIOUtil.downloadDocument(request, response, doc.getId(), fileVersion, filename, suffix, user);
		}
	}
}
