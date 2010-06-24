package com.logicaldoc.web.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.filters.StringInputStream;

import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.DocumentManager;
import com.logicaldoc.core.document.History;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.document.dao.HistoryDAO;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.UserDoc;
import com.logicaldoc.core.security.UserSession;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.core.security.dao.UserDocDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.MimeType;
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

		if (request.getParameter("open") == null) {
			response.setHeader("Pragma", "public");
			response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
			response.setHeader("Expires", "0");
			response.setHeader("Content-Disposition", "attachment; filename=\"document.gif\"");
		}

		try {
			UserDAO udao = (UserDAO) Context.getInstance().getBean(UserDAO.class);

			// Load the user associated to the session
			User user = udao.findById(session.getUserId());
			if (user == null)
				return;

			// Flag indicating to download only indexed text
			String downloadText = request.getParameter("downloadText");

			String id = request.getParameter("docId");

			String fileVersion = request.getParameter("versionId");
			Document doc = null;
			if (StringUtils.isEmpty(fileVersion)) {
				DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
				doc = docDao.findById(Long.parseLong(id));
				fileVersion = doc.getFileVersion();
			}

			String suffix = request.getParameter("suffix");
			if (StringUtils.isEmpty(suffix)) {
				suffix = "";
			}

			log.debug("Download document id=" + id);

			if ("true".equals(downloadText)) {
				downloadDocumentText(request, response, Long.parseLong(id));
			} else {
				downloadDocument(request, response, Long.parseLong(id), fileVersion, suffix, user);

				// add the file to the recent files of the user
				addToRecentFiles(user.getId(), Long.parseLong(id));
			}
		} catch (Throwable ex) {
			log.error(ex.getMessage(), ex);
		}
	}

	private void downloadDocument(HttpServletRequest request, HttpServletResponse response, long docId,
			String fileVersion, String suffix, User user) throws FileNotFoundException, IOException {
		DocumentDAO ddao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Document doc = ddao.findById(docId);

		if (doc == null) {
			throw new FileNotFoundException();
		}

		DocumentManager documentManager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
		File file = documentManager.getDocumentFile(doc, fileVersion);
		String filename = doc.getFileName();
		if (!file.exists()) {
			throw new FileNotFoundException(file.getPath());
		}

		if (StringUtils.isNotEmpty(suffix)) {
			file = new File(file.getParent(), file.getName() + "-" + suffix);
			filename = filename + "." + FilenameUtils.getExtension(suffix);
		}
		InputStream is = new FileInputStream(file);

		// get the mimetype
		String mimetype = MimeType.getByFilename(filename);
		// it seems everything is fine, so we can now start writing to the
		// response object
		response.setContentType(mimetype);
		setContentDisposition(request, response, filename);

		// Headers required by Internet Explorer
		response.setHeader("Pragma", "public");
		response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
		response.setHeader("Expires", "0");

		OutputStream os;
		os = response.getOutputStream();

		int letter = 0;

		try {
			while ((letter = is.read()) != -1) {
				os.write(letter);
			}
		} finally {
			os.flush();
			os.close();
			is.close();
		}

		if (user != null && StringUtils.isEmpty(suffix)) {
			// Add an history entry to track the download of the document
			History history = new History();
			history.setDocId(docId);
			history.setTitle(doc.getTitle());
			history.setVersion(doc.getVersion());

			MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
			history.setPath(mdao.computePathExtended(doc.getFolder().getId()));
			history.setEvent(History.EVENT_DOWNLOADED);
			history.setUser(user);

			HistoryDAO hdao = (HistoryDAO) Context.getInstance().getBean(HistoryDAO.class);
			hdao.store(history);
		}
	}

	/**
	 * Sets the correct Content-Disposition header into the response
	 */
	private static void setContentDisposition(HttpServletRequest request, HttpServletResponse response, String filename)
			throws UnsupportedEncodingException {
		// Encode the filename
		String userAgent = request.getHeader("User-Agent");
		String encodedFileName = null;
		if (userAgent.contains("MSIE") || userAgent.contains("Opera") || userAgent.contains("Safari")) {
			encodedFileName = URLEncoder.encode(filename, "UTF-8");
			encodedFileName = encodedFileName.replace("+", "%20");
		} else {
			encodedFileName = "=?UTF-8?B?" + new String(Base64.encodeBase64(filename.getBytes("UTF-8")), "UTF-8")
					+ "?=";
		}
		response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedFileName + "\"");
	}

	/**
	 * Adds the given document to the recent files entry of the user
	 * 
	 * @param userId the id of the user accessing the file
	 * @param docId id of the document the user accessed
	 */
	private void addToRecentFiles(long userId, long docId) {
		UserDoc userdoc = new UserDoc();
		userdoc.setDocId(docId);
		userdoc.setUserId(userId);

		UserDocDAO uddao = (UserDocDAO) Context.getInstance().getBean(UserDocDAO.class);
		uddao.store(userdoc);
	}

	/**
	 * Sends the specified document's indexed text to the response object; the
	 * client will receive it as a download
	 * 
	 * @param request the current request
	 * @param response the document is written to this object
	 * @param docId Id of the document
	 * @param version name of the version; if null the latest version will
	 *        returned
	 */
	private void downloadDocumentText(HttpServletRequest request, HttpServletResponse response, long docId)
			throws FileNotFoundException, IOException {

		response.setCharacterEncoding("UTF-8");

		// get document
		DocumentDAO ddao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Document doc = ddao.findById(docId);

		if (doc == null) {
			throw new FileNotFoundException();
		}

		String mimetype = "text/plain";

		// it seems everything is fine, so we can now start writing to the
		// response object
		response.setContentType(mimetype);

		setContentDisposition(request, response, doc.getFileName() + ".txt");

		// Headers required by Internet Explorer
		response.setHeader("Pragma", "public");
		response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
		response.setHeader("Expires", "0");

		DocumentManager manager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
		String content = manager.getDocumentContent(doc.getId());

		InputStream is = new StringInputStream(content.trim(), "UTF-8");
		OutputStream os;
		os = response.getOutputStream();
		int letter = 0;
		try {
			while ((letter = is.read()) != -1) {
				os.write(letter);
			}
		} finally {
			os.flush();
			os.close();
			is.close();
		}
	}
}
