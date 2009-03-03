package com.logicaldoc.web.document;

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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.DocumentManager;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.PropertiesBean;
import com.logicaldoc.web.util.ScaleJPG;
import com.logicaldoc.web.util.ServletDocUtil;

/**
 * This servlet is responsible for document downloads. It searches for the
 * attribute docId in any scope and extracts the proper document's content.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 2.6
 */
public class DocumentPreview extends HttpServlet {

	public static final String DOC_ID = "docId";

	private static final String VERSION_ID = "versionId";

	private static final long serialVersionUID = -6956612970433309888L;

	protected static Log log = LogFactory.getLog(DocumentPreview.class);

	public int thumbnailSize = 150;

	/**
	 * Constructor of the object.
	 */
	public DocumentPreview() {
		super();
		log.fatal("DocumentPreview Constructor Invoked");

		PropertiesBean context = (PropertiesBean) Context.getInstance().getBean("ContextProperties");
		String thumbnailSizeS = context.getProperty("thumbnail.size");
		if (StringUtils.isNotEmpty(thumbnailSizeS))
			thumbnailSize = Integer.parseInt(thumbnailSizeS);
	}

	/**
	 * The doGet method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String id = request.getParameter(DOC_ID);
		String fileVersion = request.getParameter(VERSION_ID);

		log.fatal("Preview document id=" + id + ", versionId= " + fileVersion);

		// 1) verifico se il documento esiste
		long docId = Long.parseLong(id);
		String suffix = "thumb.jpg";
		String documentPath = getDocumentPath(docId, fileVersion, suffix);
		log.fatal("documentPath: " + documentPath);

		boolean edp = existsDocumentPreview(documentPath);
		log.fatal("edp: " + edp);

		// 2) la preview non esiste, provvedo alla sua creazione
		boolean imageProduced = false;
		if (edp == false) {
			imageProduced = buildDocumentPreview(docId, fileVersion, documentPath);
		}
		log.fatal("imageProduced: " + imageProduced);

		if ((edp == false) && (imageProduced == false)) {
			log.fatal("edp == false; imageProduced == false");
			forwardPreviewNotAvailable(request, response);
			return;
		}

		// 3) restituisco il documento
		downloadDocument(request, response, documentPath);
	}

	private void forwardPreviewNotAvailable(HttpServletRequest request, HttpServletResponse response) {
		try {
			RequestDispatcher rd = request.getRequestDispatcher("/skins/default/images/Preview_na.gif");
			rd.forward(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean buildDocumentPreview(long docId, String fileVersion, String documentPath) {
		try {
			File fileDest = new File(documentPath);
			log.fatal("fileDest: " + fileDest);

			String folderSrc = fileDest.getParent();
			log.fatal("folderSrc: " + folderSrc);
			File fileSrc = new File(folderSrc, fileVersion);

			ScaleJPG.scale(fileSrc.getPath(), thumbnailSize, fileDest.getPath());
			return true;
		} catch (Throwable e) {
			//e.printStackTrace();
		}
		return false;
	}

	/**
	 * Sends the specified document to the response object; the client will
	 * receive it as a download
	 * 
	 * @param request
	 *            the current request
	 * @param response
	 *            the document is written to this object
	 * @param fileVersion
	 *            name of the file version; if null the latest version will be
	 *            returned
	 */
	public static void downloadDocument(HttpServletRequest request, HttpServletResponse response, String documentPath)
			throws FileNotFoundException, IOException {

		File file = new File(documentPath);
		String filename = file.getName();
		InputStream is = new FileInputStream(file);

		// get the mimetype
		String mimetype = ServletDocUtil.getMimeType(filename);
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

	private boolean existsDocumentPreview(String documentPath) {
		File file = new File(documentPath);
		if (file.exists()) {
			return true;
		}
		return false;
	}

	private String getDocumentPath(long docId, String fileVersion, String suffix) throws FileNotFoundException {

		DocumentDAO ddao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Document doc = ddao.findById(docId);

		if (doc == null) {
			throw new FileNotFoundException();
		}

		DocumentManager documentManager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
		File file = documentManager.getDocumentFile(doc, fileVersion);
		if (StringUtils.isNotEmpty(suffix)) {
			file = new File(file.getParent(), file.getName() + "-" + suffix);
		}

		return file.getPath();
	}

}