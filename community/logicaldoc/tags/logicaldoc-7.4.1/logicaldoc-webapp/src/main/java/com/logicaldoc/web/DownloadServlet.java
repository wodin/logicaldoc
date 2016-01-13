package com.logicaldoc.web;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.naming.AuthenticationException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.hsqldb.lib.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.Version;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.document.dao.VersionDAO;
import com.logicaldoc.core.security.Permission;
import com.logicaldoc.core.security.SessionManager;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.UserSession;
import com.logicaldoc.core.security.authentication.AuthenticationChain;
import com.logicaldoc.core.security.dao.FolderDAO;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.util.AuthenticationUtil;
import com.logicaldoc.web.util.AuthenticationUtil.Credentials;
import com.logicaldoc.web.util.ServiceUtil;
import com.logicaldoc.web.util.ServletIOUtil;

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
		String sessionId = null;
		User user = null;

		AuthenticationChain authenticationChain = (AuthenticationChain) Context.getInstance().getBean(
				AuthenticationChain.class);

		if (!ServletIOUtil.isPreviewAgent(request)) {
			try {
				sessionId = ServiceUtil.validateSession(request).getId();
			} catch (Throwable e) {
				/*
				 * No current session, try to handle a basic authentication
				 */
				if (request.getHeader(AuthenticationUtil.HEADER_AUTHORIZATION) != null) {
					Credentials credentials = null;
					try {
						credentials = AuthenticationUtil.authenticate(request);
					} catch (AuthenticationException e1) {
						AuthenticationUtil.sendAuthorisationCommand(response);
						return;
					}

					// Check the credentials
					if (!authenticationChain.validate(credentials.getUserName(), credentials.getPassword())) {
						AuthenticationUtil.sendAuthorisationCommand(response);
						return;
					}

					// No active session found, new login required
					boolean isLoggedOn = authenticationChain.authenticate(credentials.getUserName(),
							credentials.getPassword());
					if (isLoggedOn == false) {
						AuthenticationUtil.sendAuthorisationCommand(response);
						return;
					} else {
						sessionId = AuthenticationChain.getSessionId();
					}
				} else {
					AuthenticationUtil.sendAuthorisationCommand(response);
					return;
				}
			}

			/*
			 * We can reach this point only if a valid session was created
			 */
			UserSession session = SessionManager.getInstance().get(sessionId);
			// Load the user associated to the session
			UserDAO udao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
			user = udao.findById(session.getUserId());
			if (user == null)
				return;
		}

		try {
			if (request.getParameter("pluginId") != null)
				ServletIOUtil.downloadPluginResource(request, response, sessionId, request.getParameter("pluginId"),
						request.getParameter("resourcePath"), request.getParameter("fileName"));
			else
				downloadDocument(request, response, sessionId, user);
		} catch (Throwable ex) {
			log.error(ex.getMessage(), ex);
		} finally {
			if (request.getHeader(AuthenticationUtil.HEADER_AUTHORIZATION) != null && sessionId != null)
				SessionManager.getInstance().kill(sessionId);
		}
	}

	protected void downloadDocument(HttpServletRequest request, HttpServletResponse response, String sid, User user)
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

			if (user!=null && !folderDao.isPermissionEnabled(Permission.DOWNLOAD, doc.getFolder().getId(), user.getId()))
				throw new IOException("You don't have the DOWNLOAD permission");

			/*
			 * In case of alias to PDF, we have to redirect to PDF conversion
			 */
			if (doc.getDocRef() != null && StringUtil.isEmpty(downloadText) && "pdf".equals(doc.getDocRefType())) {
				String redirectUrl = "/convertpdf?sid=" + sid + "&docId=" + doc.getDocRef();
				if (versionId != null)
					redirectUrl += "&versionId=" + versionId;
				if (fileVersion != null)
					redirectUrl += "&fileVersion=" + fileVersion;
				response.sendRedirect(redirectUrl);
				return;
			}

			/*
			 * In case of alias we have to work on the real document
			 */
			if (doc.getDocRef() != null)
				doc = docDao.findById(doc.getDocRef());
		}

		if (!StringUtils.isEmpty(versionId)) {
			version = versDao.findById(Long.parseLong(versionId));
			if (doc == null) {
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

		ServletIOUtil.setContentDisposition(request, response, filename);

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
			ServletIOUtil.downloadDocument(request, response, sid, doc.getId(), fileVersion, filename, suffix, user);
		}
	}
}
