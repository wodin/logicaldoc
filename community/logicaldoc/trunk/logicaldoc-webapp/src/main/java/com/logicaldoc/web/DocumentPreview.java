package com.logicaldoc.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.document.DocumentManager;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.document.thumbnail.ThumbnailManager;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.MimeType;

/**
 * This servlet is responsible for document preview. It searches for the
 * attribute docId in any scope and extracts the proper document's content.
 * 
 * @author Alessandro Gasparini - Logical Objects
 * @since 4.5
 */
public class DocumentPreview extends HttpServlet {

	public static final String DOC_ID = "docId";

	private static final String FILE_VERSION = "fileVersion";

	private static final long serialVersionUID = -6956612970433309888L;

	protected static Log log = LogFactory.getLog(DocumentPreview.class);

	/**
	 * Constructor of the object.
	 */
	public DocumentPreview() {
		super();
	}

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
		String id = request.getParameter(DOC_ID);
		String fileVersion = request.getParameter(FILE_VERSION);

		DocumentManager manager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);

		// 1) check if the document exists
		long docId = Long.parseLong(id);
		String suffix = "thumb.jpg";
		File thumbnail = manager.getDocumentFile(docId, fileVersion, suffix);
		log.debug("thumbnail: " + thumbnail.getPath());

		// 2) the thumbnail doesn't exist, create it
		if (thumbnail.exists() == false) {
			DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
			ThumbnailManager thumbManaher = (ThumbnailManager) Context.getInstance().getBean(ThumbnailManager.class);
			try {
				thumbManaher.createTumbnail(docDao.findById(docId), fileVersion);
			} catch (Throwable t) {
				log.error(t.getMessage(), t);
			}
		}

		if (thumbnail.exists() == false) {
			log.debug("thumbnail.exists == false");
			forwardPreviewNotAvailable(request, response);
			return;
		}

		// 3) return the the thumbnail
		downloadDocument(request, response, thumbnail);
	}

	private void forwardPreviewNotAvailable(HttpServletRequest request, HttpServletResponse response) {
		try {
			RequestDispatcher rd = request.getRequestDispatcher("/skin/images/preview_na.gif");
			rd.forward(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sends the specified document to the response object; the client will
	 * receive it as a download
	 * 
	 * @param request the current request
	 * @param response the document is written to this object
	 * @param fileVersion name of the file version; if null the latest version
	 *        will be returned
	 */
	public static void downloadDocument(HttpServletRequest request, HttpServletResponse response, File file)
			throws FileNotFoundException, IOException {

		String filename = file.getName();
		InputStream is = new FileInputStream(file);

		// get the mimetype
		String mimetype = MimeType.getByFilename(filename);
		// it seems everything is fine, so we can now start writing to the
		// response object
		response.setContentType(mimetype);

		OutputStream os = response.getOutputStream();

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