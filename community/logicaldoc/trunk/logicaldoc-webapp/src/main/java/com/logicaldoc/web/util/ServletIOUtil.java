package com.logicaldoc.web.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.DocumentEvent;
import com.logicaldoc.core.document.History;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.document.dao.HistoryDAO;
import com.logicaldoc.core.searchengine.SearchEngine;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.UserDoc;
import com.logicaldoc.core.security.UserSession;
import com.logicaldoc.core.security.dao.FolderDAO;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.core.security.dao.UserDocDAO;
import com.logicaldoc.core.store.Storer;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.MimeType;
import com.logicaldoc.util.plugin.PluginRegistry;

/**
 * Some helper utilities to download/upload a document and its resources. The
 * downloaded document is also added to the recent files of the user.
 * 
 * @author Sebastian Stein
 */
public class ServletIOUtil {
	/**
	 * Adds the given document to the recent files entry of the user
	 * 
	 * @param userId the id of the user accessing the file
	 * @param docId id of the document the user accessed
	 */
	public static void addToRecentFiles(long userId, long docId) {
		UserDoc userdoc = new UserDoc();
		userdoc.setDocId(docId);
		userdoc.setUserId(userId);

		UserDocDAO uddao = (UserDocDAO) Context.getInstance().getBean(UserDocDAO.class);
		uddao.store(userdoc);
	}

	/**
	 * Downloads a plugin resource
	 * 
	 * @param request
	 * @param response
	 * @param pluginName name of the plug-in
	 * @param resourcePath Relative path ot the plug-in's resource
	 * @param fileName Optional file name
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ServletException
	 */
	public static void downloadPluginResource(HttpServletRequest request, HttpServletResponse response, String sid,
			String pluginName, String resourcePath, String fileName) throws FileNotFoundException, IOException,
			ServletException {

		if (sid != null)
			try {
				SessionUtil.validateSession(sid);
			} catch (InvalidSessionException e) {
				throw new ServletException(e.getMessage(), e);
			}
		else
			SessionUtil.validateSession(request);

		String filename = fileName;
		if (filename == null)
			filename = FilenameUtils.getName(resourcePath);

		File file = PluginRegistry.getPluginResource(pluginName, resourcePath);

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

		// Add this header for compatibility with internal .NET browsers
		response.setHeader("Content-Length", Long.toString(file.length()));

		InputStream is = null;
		OutputStream os = null;

		try {
			is = new FileInputStream(file);
			os = response.getOutputStream();

			int letter = 0;

			byte[] buffer = new byte[128 * 1024];
			while ((letter = is.read(buffer)) != -1) {
				os.write(buffer, 0, letter);
			}
		} finally {
			os.flush();
			os.close();
			is.close();
		}

	}

	/**
	 * Sends the specified document to the response object; the client will
	 * receive it as a download
	 * 
	 * @param request the current request
	 * @param response the document is written to this object
	 * @param sid Session identifier, if not provided the request parameter is
	 *        inspected
	 * @param docId Id of the document
	 * @param fileVersion name of the file version; if null the latest version
	 *        will be returned
	 * @param suffix of the linked document's resource
	 * @throws ServletException
	 */
	public static void downloadDocument(HttpServletRequest request, HttpServletResponse response, String sid,
			long docId, String fileVersion, String fileName, String suffix, User user) throws FileNotFoundException,
			IOException, ServletException {
		UserSession session = null;

		if (sid != null)
			try {
				session = SessionUtil.validateSession(sid);
			} catch (InvalidSessionException e) {
				throw new ServletException(e.getMessage(), e);
			}
		else
			session = SessionUtil.validateSession(request);

		UserDAO udao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
		if (user != null)
			udao.initialize(user);

		DocumentDAO dao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Document doc = dao.findById(docId);

		if (doc != null && user != null && !user.isInGroup("admin") && !user.isInGroup("publisher")
				&& !doc.isPublishing())
			throw new FileNotFoundException("Document not published");

		Storer storer = (Storer) Context.getInstance().getBean(Storer.class);
		String resource = storer.getResourceName(doc, fileVersion, null);

		String filename = fileName;
		if (filename == null)
			filename = doc.getFileName();

		if (StringUtils.isNotEmpty(suffix) && !suffix.endsWith(".p7m") && !suffix.endsWith(".m7m"))
			filename = FilenameUtils.getBaseName(filename);

		if (!storer.exists(doc.getId(), resource)) {
			throw new FileNotFoundException(resource);
		}

		if (StringUtils.isNotEmpty(suffix)) {
			resource = storer.getResourceName(doc, fileVersion, suffix);
			filename = filename + "." + FilenameUtils.getExtension(suffix);
		}
		long size = storer.size(doc.getId(), resource);

		// get the mimetype
		String mimetype = MimeType.getByFilename(filename);
		// it seems everything is fine, so we can now start writing to the
		// response object
		response.setContentType(mimetype);
		setContentDisposition(request, response, filename);

		// Add this header for compatibility with internal .NET browsers
		response.setHeader("Content-Length", Long.toString(size));

		InputStream is = null;
		OutputStream os = null;

		try {
			is = storer.getStream(doc.getId(), resource);
			os = response.getOutputStream();

			int letter = 0;

			byte[] buffer = new byte[128 * 1024];
			while ((letter = is.read(buffer)) != -1) {
				os.write(buffer, 0, letter);
			}
		} finally {
			os.flush();
			os.close();
			is.close();
		}

		if (user != null && StringUtils.isEmpty(suffix)) {
			// Add an history entry to track the download of the document
			History history = new History();
			history.setDocId(doc.getId());
			history.setTitle(doc.getTitle());
			history.setVersion(doc.getVersion());

			FolderDAO fdao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
			history.setPath(fdao.computePathExtended(doc.getFolder().getId()));
			history.setEvent(DocumentEvent.DOWNLOADED.toString());
			history.setFilename(doc.getFileName());
			history.setFolderId(doc.getFolder().getId());
			history.setUser(user);
			if (session != null)
				history.setSessionId(session.getId());
			else
				history.setSessionId(sid);
			HistoryDAO hdao = (HistoryDAO) Context.getInstance().getBean(HistoryDAO.class);
			hdao.store(history);
		}
	}

	/**
	 * Sends the specified file to the response object; the client will receive
	 * it as a download
	 * 
	 * @param request the current request
	 * @param response the file is written to this object
	 * @throws ServletException
	 */
	public static void downloadFile(HttpServletRequest request, HttpServletResponse response, File file, String fileName)
			throws FileNotFoundException, IOException, ServletException {

		String filename = fileName;
		if (filename == null)
			filename = file.getName();

		// get the mimetype
		String mimetype = MimeType.getByFilename(filename);
		// it seems everything is fine, so we can now start writing to the
		// response object
		response.setContentType(mimetype);
		setContentDisposition(request, response, filename);

		// Add this header for compatibility with internal .NET browsers
		response.setHeader("Content-Length", Long.toString(file.length()));

		InputStream is = null;
		OutputStream os = null;

		try {
			is = new BufferedInputStream(new FileInputStream(file), 128 * 1024);
			os = response.getOutputStream();

			int letter = 0;

			byte[] buffer = new byte[128 * 1024];
			while ((letter = is.read(buffer)) != -1) {
				os.write(buffer, 0, letter);
			}
		} finally {
			try {
				if (os != null) {
					os.flush();
					os.close();
				}
			} catch (Throwable t) {
			}
			try {
				if (is != null)
					is.close();
			} catch (Throwable t) {
			}
		}
	}

	/**
	 * Sets the correct Content-Disposition header into the response
	 */
	public static void setContentDisposition(HttpServletRequest request, HttpServletResponse response, String filename)
			throws UnsupportedEncodingException {
		// Encode the filename
		String userAgent = request.getHeader("User-Agent").toLowerCase();

		String encodedFileName = null;
		if (userAgent.contains("msie") || userAgent.contains("opera")) {
			encodedFileName = URLEncoder.encode(filename, "UTF-8");
			encodedFileName = encodedFileName.replace("+", "%20");
		} else if (userAgent.contains("safari") && !userAgent.contains("chrome")) {
			// Chrome User-Agent contains "safari"
			encodedFileName = filename;
		} else {
			encodedFileName = "=?UTF-8?B?" + new String(Base64.encodeBase64(filename.getBytes("UTF-8")), "UTF-8")
					+ "?=";
		}
		response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedFileName + "\"");

		// Headers required by Internet Explorer
		response.setHeader("Pragma", "public");
		response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
		response.setHeader("Expires", "0");
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
	public static void downloadDocumentText(HttpServletRequest request, HttpServletResponse response, long docId,
			User user) throws FileNotFoundException, IOException {

		response.setCharacterEncoding("UTF-8");

		// get document
		DocumentDAO ddao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Document doc = ddao.findById(docId);

		if (doc == null) {
			throw new FileNotFoundException();
		}

		if (doc.getDocRef() != null) {
			doc = ddao.findById(doc.getDocRef());
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

		UserDAO udao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
		udao.initialize(user);
		if (doc != null && !user.isInGroup("admin") && !user.isInGroup("publisher") && !doc.isPublishing())
			throw new FileNotFoundException("Document not published");

		SearchEngine indexer = (SearchEngine) Context.getInstance().getBean(SearchEngine.class);

		String content = indexer.getHit(docId).getContent();
		if (content == null)
			content = "";

		try {
			response.getOutputStream().write(content.getBytes(Charset.forName("UTF-8")));
		} finally {
			response.getOutputStream().flush();
			response.getOutputStream().close();
		}
	}

	/**
	 * Sends the specified document to the response object; the client will
	 * receive it as a download
	 * 
	 * @param request the current request
	 * @param response the document is written to this object
	 * @param docId Id of the document
	 * @param fileVersion name of the file version; if null the latest version
	 *        will be returned
	 * @throws ServletException
	 * @throws NumberFormatException
	 */
	public static void downloadDocument(HttpServletRequest request, HttpServletResponse response, String sid,
			String docId, String fileVersion, String fileName, User user) throws FileNotFoundException, IOException,
			NumberFormatException, ServletException {
		downloadDocument(request, response, sid, Integer.parseInt(docId), fileVersion, fileName, null, user);
	}

	/**
	 * Uploads a document's related resource.
	 * 
	 * The resource will be stored in the folder where the document's files
	 * reside using the following pattern: <b>fileVersion</b>-<b>suffix</b>
	 * 
	 * If no version is specified, the current one is used instead
	 * 
	 * @param request the current request
	 * @param docId Id of the document
	 * @param suffix Suffix of the document
	 * @param fileVersion id of the file version; if null the latest version
	 *        will returned
	 * @param docVersion id of the doc version; if null the latest version will
	 *        returned
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static void uploadDocumentResource(HttpServletRequest request, String docId, String suffix,
			String fileVersion, String docVersion) throws Exception {
		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Document doc = docDao.findById(Long.parseLong(docId));

		String ver = docVersion;
		if (StringUtils.isEmpty(ver))
			ver = fileVersion;
		if (StringUtils.isEmpty(ver))
			ver = doc.getFileVersion();

		Storer storer = (Storer) Context.getInstance().getBean(Storer.class);

		DiskFileItemFactory factory = new DiskFileItemFactory();
		// Configure the factory here, if desired.
		ServletFileUpload upload = new ServletFileUpload(factory);
		// Configure the uploader here, if desired.
		List<FileItem> fileItems = upload.parseRequest(request);
		for (FileItem item : fileItems) {
			if (!item.isFormField()) {
				File savedFile = File.createTempFile("", "");
				item.write(savedFile);

				InputStream is = null;
				try {
					is = item.getInputStream();
					storer.store(item.getInputStream(), Long.parseLong(docId), storer.getResourceName(doc, ver, suffix));
				} finally {
					if (is != null)
						is.close();
					FileUtils.forceDelete(savedFile);
				}
			}
		}
	}
}