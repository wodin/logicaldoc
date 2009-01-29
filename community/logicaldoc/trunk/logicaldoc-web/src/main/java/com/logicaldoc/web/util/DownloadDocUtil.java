package com.logicaldoc.web.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.tools.ant.filters.StringInputStream;

import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.DocumentManager;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.security.UserDoc;
import com.logicaldoc.core.security.dao.UserDocDAO;
import com.logicaldoc.util.CharsetDetector;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.MimeTypeConfig;

/**
 * Some helper utilities to download a document but also to add the document to
 * the recent files of the user
 * 
 * @author Sebastian Stein
 */
public class DownloadDocUtil {
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
	 * extracts the mimetype of the document
	 */
	public static String getMimeType(Document document) {
		if (document == null) {
			return null;
		}

		String extension = document.getFileName().substring(document.getFileName().lastIndexOf(".") + 1);
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
	 * @param docVerId name of the version; if null the latest version will
	 *        returned
	 */
	public static void downloadDocument(HttpServletRequest request, HttpServletResponse response, long docId,
			String docVerId) throws FileNotFoundException, IOException {
		DocumentDAO ddao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Document doc = ddao.findById(docId);

		if (doc == null) {
			throw new FileNotFoundException();
		}

		// get the mimetype
		String mimetype = DownloadDocUtil.getMimeType(doc);
		DocumentManager documentManager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
		File file = documentManager.getDocumentFile(doc, docVerId);
		if (!file.exists()) {
			throw new FileNotFoundException(file.getPath());
		}

		// it seems everything is fine, so we can now start writing to the
		// response object
		response.setContentType(mimetype);

		setContentDisposition(request, response, doc.getFileName());

		// Headers required by Internet Explorer
		response.setHeader("Pragma", "public");
		response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
		response.setHeader("Expires", "0");

		InputStream is = new FileInputStream(file);
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
	 * Sets the correct Content-Disposition header into the response
	 */
	private static void setContentDisposition(HttpServletRequest request, HttpServletResponse response, String filename)
			throws UnsupportedEncodingException {
		// Encode the filename
		String userAgent = request.getHeader("User-Agent");
		String encodedFileName = null;
		if (userAgent.contains("MSIE") || userAgent.contains("Opera")) {
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
	 * @param docVerId name of the version; if null the latest version will
	 *        returned
	 */
	public static void downloadDocumentText(HttpServletRequest request, HttpServletResponse response, long docId)
			throws FileNotFoundException, IOException {
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

		InputStream is = new StringInputStream(content);
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
	 * @param docVerId name of the version; if null the latest version will
	 *        returned
	 */
	public static void downloadDocument(HttpServletRequest request, HttpServletResponse response, String docId,
			String docVerId) throws FileNotFoundException, IOException {
		downloadDocument(request, response, Integer.parseInt(docId), docVerId);
	}
}