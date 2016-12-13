package com.logicaldoc.web;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.hsqldb.lib.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.DocumentManager;
import com.logicaldoc.core.document.Version;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.document.dao.VersionDAO;
import com.logicaldoc.core.document.pdf.PdfConverterManager;
import com.logicaldoc.core.folder.FolderDAO;
import com.logicaldoc.core.security.Permission;
import com.logicaldoc.core.security.Session;
import com.logicaldoc.core.security.User;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.util.ServletUtil;

/**
 * This servlet is responsible for document downloads. It searches for the
 * attribute docId in any scope and extracts the proper document's content.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class DownloadServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected static Logger log = LoggerFactory.getLogger(DownloadServlet.class);

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
		Session session = null;

		if (!ServletUtil.isPreviewAgent(request)) {
			/*
			 * We can reach this point only if a valid session was created
			 */
			session = ServletUtil.validateSession(request);
		}

		try {
			if (request.getParameter("pluginId") != null)
				ServletUtil.downloadPluginResource(request, response, session.getId(),
						request.getParameter("pluginId"), request.getParameter("resourcePath"),
						request.getParameter("fileName"));
			else
				downloadDocument(request, response, session);
		} catch (Throwable ex) {
			log.error(ex.getMessage(), ex);
		}
	}

	protected void downloadDocument(HttpServletRequest request, HttpServletResponse response, Session session)
			throws FileNotFoundException, IOException, ServletException {
		DocumentDAO docDao = (DocumentDAO) Context.get().getBean(DocumentDAO.class);
		VersionDAO versDao = (VersionDAO) Context.get().getBean(VersionDAO.class);
		FolderDAO folderDao = (FolderDAO) Context.get().getBean(FolderDAO.class);

		// Flag indicating to download only indexed text
		String downloadText = request.getParameter("downloadText");
		String docId = request.getParameter("docId");
		String versionId = request.getParameter("versionId");
		String fileVersion = request.getParameter("fileVersion");
		String filename = "";
		String suffix = request.getParameter("suffix");

		Version version = null;
		Document doc = null;

		if (StringUtils.isNotEmpty(docId)) {
			doc = docDao.findById(Long.parseLong(docId));

			if (session.getUser() != null
					&& !folderDao
							.isPermissionEnabled(Permission.DOWNLOAD, doc.getFolder().getId(), session.getUserId()))
				throw new IOException("You don't have the DOWNLOAD permission");

			/*
			 * In case of alias to PDF, we have to serve the PDF conversion
			 */
			if (doc.getDocRef() != null && StringUtil.isEmpty(downloadText)
					&& (doc.getDocRefType() != null && doc.getDocRefType().contains("pdf"))
					&& !doc.getFileName().toLowerCase().endsWith(".pdf")) {

				// Generate the PDF conversion
				PdfConverterManager manager = (PdfConverterManager) Context.get().getBean(PdfConverterManager.class);
				manager.createPdf(doc, fileVersion, session.getId());

				suffix = PdfConverterManager.SUFFIX;
			}

			/*
			 * In case of alias we have to work on the real document
			 */
			if (doc.getDocRef() != null)
				doc = docDao.findById(doc.getDocRef());
		}

		if (StringUtils.isNotEmpty(versionId)) {
			version = versDao.findById(Long.parseLong(versionId));
			if (doc == null) {
				doc = docDao.findDocument(version.getDocId());

				if (!folderDao.isPermissionEnabled(Permission.DOWNLOAD, doc.getFolder().getId(), session.getUserId()))
					throw new IOException("You don't have the DOWNLOAD permission");
			}
		}
		
		if(doc.isPasswordProtected() && !session.getUnprotectedDocs().containsKey(doc.getId()))
			throw new IOException("The document is protected by a password");
		
		if (version != null)
			filename = version.getFileName();
		else
			filename = doc.getFileName();

		ServletUtil.setContentDisposition(request, response, filename);

		if (StringUtils.isEmpty(fileVersion)) {
			if (version != null)
				fileVersion = version.getFileVersion();
			else
				fileVersion = doc.getFileVersion();
		}

		if (StringUtils.isEmpty(suffix)) {
			suffix = "";
		}

		if (version != null)
			log.debug("Download version id=" + versionId);
		else
			log.debug("Download document id=" + docId);

		if ("true".equals(downloadText)) {
			ServletUtil.downloadDocumentText(request, response, doc.getId(), session.getUser());
		} else {
			ServletUtil.downloadDocument(request, response, session.getId(), doc.getId(), fileVersion, filename, suffix, session.getUser());
		}
	}
}
