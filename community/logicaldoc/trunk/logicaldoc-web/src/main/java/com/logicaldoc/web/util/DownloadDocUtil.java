package com.logicaldoc.web.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.security.UserDoc;
import com.logicaldoc.core.security.dao.UserDocDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.MimeTypeConfig;
import com.logicaldoc.util.config.SettingsConfig;

/**
 * some helper utilities to download a document but also to add the document to
 * the recent files of the user
 * 
 * @author Sebastian Stein
 */
public class DownloadDocUtil {
	/**
	 * adds the given document to the recent files entry of the user
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
	 * sends the specified document to the response object; the client will
	 * receive it as a download
	 * 
	 * @param response the document is written to this object
	 * @param docId Id of the document
	 * @param docVerId name of the version; if null the latest version will
	 *        returned
	 */
	public static void downloadDocument(HttpServletResponse response, long docId, String docVerId)
			throws FileNotFoundException, IOException {
		// get document
		DocumentDAO ddao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Document doc = ddao.findById(docId);

		if (doc == null) {
			throw new FileNotFoundException();
		}

		// get the mimetype
		String mimetype = DownloadDocUtil.getMimeType(doc);

		// get path correct file name
		SettingsConfig settings = (SettingsConfig) Context.getInstance().getBean(SettingsConfig.class);
		String path = settings.getValue("docdir") + "/" + doc.getFolder().getPath() + "/" + doc.getId();

		// older versions of a document are stored in the same directory as the
		// current version,
		// but the filename is the version number without extension, e.g.
		// "menuid/2.1"
		String filename = doc.getFileName();

		if (docVerId == null) {
			filename = doc.getFileName();
		} else {
			filename = docVerId;
		}

		// load the file from the file system and output it to the
		// responseWriter
		File file = new File(path + "/" + filename);

		if (!file.exists()) {
			throw new FileNotFoundException();
		}

		// it seems everything is fine, so we can now start writing to the
		// response object
		response.setContentType(mimetype);
		response.setHeader("Content-Disposition", "attachment; filename=\"" + doc.getFileName() + "\"");

		InputStream is = new FileInputStream(file);
		OutputStream os;
		os = response.getOutputStream();

		int letter = 0;

		while ((letter = is.read()) != -1) {
			os.write(letter);
		}

		os.flush();
		os.close();
		is.close();
	}

	/**
	 * sends the specified document to the response object; the client will
	 * receive it as a download
	 * 
	 * @param response the document is written to this object
	 * @param docId Id of the document
	 * @param docVerId name of the version; if null the latest version will
	 *        returned
	 */
	public static void downloadDocument(HttpServletResponse response, String docId, String docVerId)
			throws FileNotFoundException, IOException {
		downloadDocument(response, Long.parseLong(docId), docVerId);
	}
}
