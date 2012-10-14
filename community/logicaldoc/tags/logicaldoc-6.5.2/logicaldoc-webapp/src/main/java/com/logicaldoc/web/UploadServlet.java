package com.logicaldoc.web;

import gwtupload.server.UploadAction;
import gwtupload.server.exceptions.UploadActionException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.ContextProperties;

/**
 * This servlet is responsible for document uploads operations.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class UploadServlet extends UploadAction {

	private static final long serialVersionUID = 1L;

	public static String RECEIVEDFILES = "receivedFiles";

	public static String RECEIVEDCONTENTTYPES = "receivedContentTypes";

	public static String RECEIVEDFILENAMES = "receivedFileNames";

	protected static Logger log = LoggerFactory.getLogger(UploadServlet.class);

	/**
	 * Override executeAction to save the received files in a custom place and
	 * delete this items from session.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String executeAction(HttpServletRequest request, List<FileItem> sessionFiles) throws UploadActionException {
		try {
			HttpSession session = SessionFilter.getServletSession(request.getParameter("sid"));

			if (session == null)
				session = request.getSession();

			/**
			 * Maintain a list with received files and their content types
			 */
			Map<String, File> receivedFiles = (Map<String, File>) session.getAttribute(RECEIVEDFILES);
			if (receivedFiles == null) {
				receivedFiles = new Hashtable<String, File>();
				session.setAttribute(RECEIVEDFILES, receivedFiles);
			}

			Map<String, String> receivedContentTypes = (Map<String, String>) session.getAttribute(RECEIVEDCONTENTTYPES);
			if (receivedContentTypes == null) {
				receivedContentTypes = new Hashtable<String, String>();
				session.setAttribute(RECEIVEDCONTENTTYPES, receivedContentTypes);
			}

			Map<String, String> receivedFileNames = (Map<String, String>) session.getAttribute(RECEIVEDFILENAMES);
			if (receivedFileNames == null) {
				receivedFileNames = new Hashtable<String, String>();
				session.setAttribute(RECEIVEDFILENAMES, receivedFileNames);
			}

			String path = getServletContext().getRealPath("/upload/" + session.getId());
			File uploadFolder = new File(path);

			// Google App Engine doesn't support disk writing
			uploadFolder.mkdirs();
			uploadFolder.mkdir();

			for (FileItem item : sessionFiles) {
				if (false == item.isFormField()) {
					OutputStream os = null;
					try {
						File file = new File(uploadFolder, item.getFieldName());
						log.debug("Received file " + item.getName());

						os = new FileOutputStream(file);
						copyFromInputStreamToOutputStream(item.getInputStream(), os);

						receivedFiles.put(item.getFieldName(), file);
						receivedContentTypes.put(item.getFieldName(), item.getContentType());
						receivedFileNames.put(item.getFieldName(),
								URLDecoder.decode(FilenameUtils.getName(item.getName()), "UTF-8"));
					} catch (Throwable e) {
						e.printStackTrace();
						throw new UploadActionException(e.getMessage());
					} finally {
						if (os != null) {
							try {
								os.flush();
								os.close();
							} catch (IOException e) {
								log(e.getMessage());
							}

						}
					}
				}
			}

			removeSessionFileItems(request);
		} catch (Throwable t) {
			log.error(t.getMessage(), t);

		}
		return null;
	}

	/**
	 * Remove a file when the user sends a delete request
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void removeItem(HttpServletRequest request, String fieldName) throws UploadActionException {
		HttpSession session = SessionFilter.getServletSession(request.getParameter("sid"));
		if (session == null)
			session = request.getSession();

		Map<String, File> receivedFiles = (Map<String, File>) session.getAttribute(RECEIVEDFILES);
		if (receivedFiles == null || !receivedFiles.containsKey(fieldName))
			return;

		File file = receivedFiles.get(fieldName);
		receivedFiles.remove(fieldName);
		if (file != null && file.exists())
			file.delete();

		Map<String, String> receivedContentTypes = (Map<String, String>) session.getAttribute(RECEIVEDCONTENTTYPES);
		if (receivedContentTypes == null || !receivedContentTypes.containsKey(fieldName))
			return;
		receivedContentTypes.remove(fieldName);

		Map<String, String> receivedFileNames = (Map<String, String>) session.getAttribute(RECEIVEDFILENAMES);
		if (receivedFileNames == null || !receivedFileNames.containsKey(fieldName))
			return;
		receivedFileNames.remove(fieldName);
	}

	/**
	 * Get the content of an uploaded file
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void getUploadedFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String fieldName = request.getParameter(PARAM_SHOW);

		HttpSession session = SessionFilter.getServletSession(request.getParameter("sid"));

		if (session == null)
			session = request.getSession();

		Map<String, File> receivedFiles = (Map<String, File>) session.getAttribute(RECEIVEDFILES);

		if (receivedFiles == null || !receivedFiles.containsKey(fieldName))
			return;

		File f = receivedFiles.get(fieldName);
		if (f != null) {
			Map<String, String> receivedContentTypes = (Map<String, String>) session.getAttribute(RECEIVEDCONTENTTYPES);

			if (receivedContentTypes != null && receivedContentTypes.containsKey(fieldName))
				response.setContentType(receivedContentTypes.get(fieldName));
			FileInputStream is = new FileInputStream(f);
			copyFromInputStreamToOutputStream(is, response.getOutputStream());
		} else {
			renderXmlResponse(request, response, ERROR_ITEM_NOT_FOUND);
		}
	}

	@SuppressWarnings("unchecked")
	public static Map<String, File> getReceivedFiles(HttpServletRequest request, String sid) {
		HttpSession session = SessionFilter.getServletSession(sid);

		if (session == null)
			session = request.getSession();

		return (Map<String, File>) session.getAttribute(RECEIVEDFILES);
	}

	@SuppressWarnings("unchecked")
	public static Map<String, String> getReceivedFileNames(HttpServletRequest request, String sid) {
		HttpSession session = SessionFilter.getServletSession(sid);
		if (session == null)
			session = request.getSession();

		return (Map<String, String>) session.getAttribute(RECEIVEDFILENAMES);
	}

	@Override
	public void checkRequest(HttpServletRequest request) {
		ContextProperties config = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
		int max = Integer.parseInt(config.getProperty("upload.maxsize")) * 1024 * 1024;
		if (max > 0) {
			super.maxSize = Integer.parseInt(config.getProperty("upload.maxsize")) * 1024 * 1024;
			super.checkRequest(request);
		}
	}

	public static void cleanReceivedFiles(String sid) {
		HttpSession session = SessionFilter.getServletSession(sid);
		cleanReceivedFiles(session);
	}

	public static void cleanReceivedFiles(HttpSession session) {
		if (session == null)
			return;
		try {
			session.setAttribute(RECEIVEDFILES, new Hashtable<String, File>());
			session.setAttribute(RECEIVEDCONTENTTYPES, new Hashtable<String, String>());
			session.setAttribute(RECEIVEDFILENAMES, new HashMap<String, String>());
			String path = session.getServletContext().getRealPath("/upload/" + session.getId());
			FileUtils.forceDelete(new File(path));
		} catch (IOException e) {
		}
	}
}