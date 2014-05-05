package com.logicaldoc.web;

import gwtupload.server.UploadAction;
import gwtupload.server.exceptions.UploadActionException;
import gwtupload.shared.UConsts;

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

import javax.servlet.ServletException;
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
			setUploadMax();

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
					if (!isAllowedForUpload(item.getName()))
						throw new UploadActionException("Invalid file name " + item.getName());

					OutputStream os = null;
					try {
						File file = new File(uploadFolder, item.getFieldName());

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
		} catch (UploadActionException e) {
			removeSessionFileItems(request);
			throw e;
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
		}
		return null;
	}

	/**
	 * The post method is used to receive the file and save it in the user
	 * session. It returns a very XML page that the client receives in an
	 * iframe.
	 * 
	 * The content of this xml document has a tag error in the case of error in
	 * the upload process or the string OK in the case of success.
	 * 
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		setUploadMax();
		super.doPost(request, response);
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
		setUploadMax();

		String fieldName = request.getParameter(UConsts.PARAM_SHOW);

		HttpSession session = SessionFilter.getServletSession(request.getParameter("sid"));

		if (session == null)
			session = request.getSession();

		for (FileItem item : getSessionFileItems(request)) {
			if (false == item.isFormField()) {
				Map<String, File> receivedFiles = (Map<String, File>) session.getAttribute(RECEIVEDFILES);

				if (receivedFiles == null || !receivedFiles.containsKey(fieldName))
					return;

				File f = receivedFiles.get(fieldName);
				if (f != null) {
					Map<String, String> receivedContentTypes = (Map<String, String>) session
							.getAttribute(RECEIVEDCONTENTTYPES);

					if (receivedContentTypes != null && receivedContentTypes.containsKey(fieldName))
						response.setContentType(receivedContentTypes.get(fieldName));
					FileInputStream is = new FileInputStream(f);
					copyFromInputStreamToOutputStream(is, response.getOutputStream());
				} else {
					renderXmlResponse(request, response, XML_ERROR_ITEM_NOT_FOUND);
				}
			}
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

	protected void setUploadMax() {
		ContextProperties config = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
		super.maxSize = Integer.parseInt(config.getProperty("upload.maxsize")) * 1024 * 1024;
	}

	@Override
	public void checkRequest(HttpServletRequest request) {
		// Load the correct max size specification
		ContextProperties config = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
		int max = Integer.parseInt(config.getProperty("upload.maxsize")) * 1024 * 1024;
		if (max > 0) {
			super.maxSize = Integer.parseInt(config.getProperty("upload.maxsize")) * 1024 * 1024;
			super.checkRequest(request);
		}
	}

	// @SuppressWarnings("unchecked")
	// @Override
	// protected String parsePostRequest(HttpServletRequest request,
	// HttpServletResponse response) {
	// try {
	// String delay = request.getParameter(UConsts.PARAM_DELAY);
	// uploadDelay = Integer.parseInt(delay);
	// } catch (Exception e) {
	// }
	//
	// HttpSession session = request.getSession();
	//
	// logger.debug("UPLOAD-SERVLET (" + session.getId() +
	// ") new upload request received.");
	//
	// AbstractUploadListener listener = getCurrentListener(request);
	// if (listener != null) {
	// if (listener.isFrozen() || listener.isCanceled() || listener.getPercent()
	// >= 100) {
	// removeCurrentListener(request);
	// } else {
	// String error = getMessage("busy");
	// logger.error("UPLOAD-SERVLET (" + session.getId() + ") " + error);
	// return error;
	// }
	// }
	// // Create a file upload progress listener, and put it in the user
	// // session,
	// // so the browser can use ajax to query status of the upload process
	// listener = createNewListener(request);
	//
	// List<FileItem> uploadedItems;
	// try {
	//
	// // Call to a method which the user can override
	// checkRequest(request);
	//
	// // Create the factory used for uploading files,
	// FileItemFactory factory = getFileItemFactory(request.getContentLength());
	// ServletFileUpload uploader = new ServletFileUpload(factory);
	// uploader.setSizeMax(maxSize);
	// uploader.setProgressListener(listener);
	//
	// // Receive the files
	// logger.debug("UPLOAD-SERVLET (" + session.getId() +
	// ") parsing HTTP POST request ");
	// uploadedItems = uploader.parseRequest(request);
	// session.removeAttribute(SESSION_LAST_FILES);
	// logger.debug("UPLOAD-SERVLET (" + session.getId() + ") parsed request, "
	// + uploadedItems.size()
	// + " items received.");
	//
	// // Received files are put in session
	// List<FileItem> sessionFiles = getSessionFileItems(request);
	// if (sessionFiles == null) {
	// sessionFiles = new ArrayList<FileItem>();
	// }
	//
	// String error = "";
	//
	// // Add only the allowed file names
	// if (uploadedItems.size() > 0) {
	// String msg = "";
	//
	// for (FileItem fileItem : uploadedItems) {
	// if (isAllowedForUpload(fileItem.getName())) {
	// sessionFiles.add(fileItem);
	// msg += fileItem.getFieldName() + " => " + fileItem.getName() + "(" +
	// fileItem.getSize()
	// + " bytes),";
	// } else {
	// error += "Invalid file name " + fileItem.getName() + " ";
	// }
	// }
	//
	// logger.debug("UPLOAD-SERVLET (" + session.getId() +
	// ") puting items in session: " + msg);
	// session.setAttribute(SESSION_FILES, sessionFiles);
	// session.setAttribute(SESSION_LAST_FILES, uploadedItems);
	// } else {
	// logger.error("UPLOAD-SERVLET (" + session.getId() +
	// ") error NO DATA received ");
	// error += getMessage("no_data");
	// }
	//
	// return error.length() > 0 ? error : null;
	//
	// // So much silly questions in the list about this issue.
	// } catch (LinkageError e) {
	// logger.error("UPLOAD-SERVLET (" + request.getSession().getId() +
	// ") Exception: " + e.getMessage() + "\n"
	// + stackTraceToString(e));
	// RuntimeException ex = new UploadActionException(getMessage("restricted",
	// e.getMessage()), e);
	// listener.setException(ex);
	// throw ex;
	// } catch (SizeLimitExceededException e) {
	// RuntimeException ex = new UploadSizeLimitException(e.getPermittedSize(),
	// e.getActualSize());
	// listener.setException(ex);
	// throw ex;
	// } catch (UploadSizeLimitException e) {
	// listener.setException(e);
	// throw e;
	// } catch (UploadCanceledException e) {
	// listener.setException(e);
	// throw e;
	// } catch (UploadTimeoutException e) {
	// listener.setException(e);
	// throw e;
	// } catch (Throwable e) {
	// logger.error("UPLOAD-SERVLET (" + request.getSession().getId() +
	// ") Unexpected Exception -> "
	// + e.getMessage() + "\n" + stackTraceToString(e));
	// RuntimeException ex = new UploadException(e);
	// listener.setException(ex);
	// throw ex;
	// }
	// }

	/**
	 * Checks if the passed filename can be uploaded or not on the basis of what
	 * configured in 'upload.disallow'.
	 */
	public static boolean isAllowedForUpload(String filename) {
		ContextProperties config = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
		String disallow = config.getProperty("upload.disallow");

		if (disallow == null || disallow.trim().isEmpty())
			return true;

		// Extract and normalize the extensions
		String[] disallowedExtensions = disallow.split(",");
		for (int i = 0; i < disallowedExtensions.length; i++) {
			disallowedExtensions[i] = disallowedExtensions[i].toLowerCase().trim();
			if (!disallowedExtensions[i].startsWith("."))
				disallowedExtensions[i] = "." + disallowedExtensions[i];
		}

		for (int i = 0; i < disallowedExtensions.length; i++)
			if (filename.toLowerCase().endsWith(disallowedExtensions[i]))
				return false;

		return true;
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