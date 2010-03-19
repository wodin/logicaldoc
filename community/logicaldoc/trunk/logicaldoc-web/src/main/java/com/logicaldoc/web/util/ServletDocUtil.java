package com.logicaldoc.web.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tools.ant.filters.StringInputStream;

import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.DocumentManager;
import com.logicaldoc.core.document.History;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.document.dao.HistoryDAO;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.UserDoc;
import com.logicaldoc.core.security.dao.UserDocDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.MimeTypeConfig;

/**
 * Some helper utilities to download/upload a document and its resources. The
 * downloaded document is also added to the recent files of the user.
 * 
 * @author Sebastian Stein
 */
public class ServletDocUtil {
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
	 * extracts the mimetype of the file
	 */
	public static String getMimeType(String filename) {
		if (filename == null) {
			return null;
		}
		String extension = FilenameUtils.getExtension(filename);
		MimeTypeConfig mtc = (MimeTypeConfig) Context.getInstance().getBean(MimeTypeConfig.class);
		String mimetype = mtc.getMimeApp(extension);
		if ((mimetype == null) || mimetype.equals("")) {
			mimetype = "application/octet-stream";
		}
		return mimetype;
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
	 */
	public static void downloadDocument(HttpServletRequest request, HttpServletResponse response, long docId,
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
		String mimetype = ServletDocUtil.getMimeType(filename);
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

			history.setPath(doc.getFolder().getPathExtended() + "/" + doc.getFolder().getText());
			history.setPath(history.getPath().replaceAll("//", "/"));
			history.setPath(history.getPath().replaceFirst("/menu.documents/", "/"));
			history.setPath(history.getPath().replaceFirst("/menu.documents", "/"));

			history.setEvent(History.EVENT_DOWNLOADED);
			history.setUserId(user.getId());
			history.setUserName(user.getFullName());

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
	 * Sends the specified document's indexed text to the response object; the
	 * client will receive it as a download
	 * 
	 * @param request the current request
	 * @param response the document is written to this object
	 * @param docId Id of the document
	 * @param version name of the version; if null the latest version will
	 *        returned
	 */
	public static void downloadDocumentText(HttpServletRequest request, HttpServletResponse response, long docId)
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

	/**
	 * sends the specified document to the response object; the client will
	 * receive it as a download
	 * 
	 * @param request the current request
	 * @param response the document is written to this object
	 * @param docId Id of the document
	 * @param fileVersion name of the file version; if null the latest version
	 *        will be returned
	 */
	public static void downloadDocument(HttpServletRequest request, HttpServletResponse response, String docId,
			String fileVersion, User user) throws FileNotFoundException, IOException {
		downloadDocument(request, response, Integer.parseInt(docId), fileVersion, null, user);
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

		DocumentManager docManager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
		File file = docManager.getDocumentFile(doc, fileVersion);
		String path = file.getParent();

		DiskFileItemFactory factory = new DiskFileItemFactory();
		// Configure the factory here, if desired.
		ServletFileUpload upload = new ServletFileUpload(factory);
		// Configure the uploader here, if desired.
		List<FileItem> fileItems = upload.parseRequest(request);
		for (FileItem item : fileItems) {
			if (!item.isFormField()) {
				File savedFile = new File(path, ver + "-" + suffix);
				item.write(savedFile);
			}
		}
	}
}