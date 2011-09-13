package com.logicaldoc.web;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.document.thumbnail.ThumbnailManager;
import com.logicaldoc.core.store.Storer;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.Exec;
import com.logicaldoc.util.MimeType;
import com.logicaldoc.util.config.ContextProperties;
import com.logicaldoc.web.util.SessionUtil;

/**
 * This servlet is responsible for document preview. It searches for the
 * attribute docId in any scope and extracts the proper document's content. You
 * may specify the suffix to download the thumbnail or the flash.
 * 
 * @author Alessandro Gasparini - Logical Objects
 * @since 4.5
 */
public class DocumentPreview extends HttpServlet {

	/** Format can be thumb.jpg or preview.swf */
	protected static final String SUFFIX = "suffix";

	public static final String DOC_ID = "docId";

	private static final String FILE_VERSION = "fileVersion";

	private static final long serialVersionUID = -6956612970433309888L;

	protected static Log log = LogFactory.getLog(DocumentPreview.class);

	protected static String SWFTOOLSPATH = "swftools.path";

	protected static String CONVERT = "command.convert";

	/** For these extensions we are able to directly convert to SWF */
	protected String SWF_DIRECT_CONVERSION_EXTS = "gif, png, pdf, jpeg, jpg, tif, tiff, bmp";

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
		String suffix = request.getParameter(SUFFIX);

		InputStream stream = null;
		try {
			Storer storer = (Storer) Context.getInstance().getBean(Storer.class);

			// 1) check if the document exists
			long docId = Long.parseLong(id);
			if (StringUtils.isEmpty(suffix))
				suffix = "thumb.jpg";
			DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
			Document doc = docDao.findById(docId);
			if (StringUtils.isEmpty(fileVersion))
				fileVersion = doc.getFileVersion();

			SessionUtil.validateSession(request.getParameter("sid"));

			String resource = storer.getResourceName(docId, fileVersion, suffix);

			// 2) the thumbnail/preview doesn't exist, create it
			if (!storer.exists(docId, resource)) {
				log.debug("Need for preview creation");
				createPreviewResource(doc, fileVersion, resource);
			}

			stream = storer.getStream(docId, resource);

			if (stream == null) {
				log.debug("thumbnail not available");
				forwardPreviewNotAvailable(request, response);
				return;
			}

			// 3) return the the thumbnail/preview resource
			downloadDocument(request, response, stream, storer.getResourceName(doc, fileVersion, suffix));
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			new IOException(t.getMessage());
		} finally {
			if (stream != null)
				stream.close();
		}
	}

	/**
	 * Creates the preview resource according to the specified format storing it
	 * in the repository for future access.
	 */
	protected void createPreviewResource(Document doc, String fileVersion, String resource) {
		Storer storer = (Storer) Context.getInstance().getBean(Storer.class);
		String thumbResource = storer.getResourceName(doc, fileVersion, "thumb.jpg");

		// In any case try to produce the thumbnail
		if (!storer.exists(doc.getId(), thumbResource)) {
			ThumbnailManager thumbManager = (ThumbnailManager) Context.getInstance().getBean(ThumbnailManager.class);
			try {
				thumbManager.createTumbnail(doc, fileVersion);
				log.debug("Created thumbnail " + resource);
			} catch (Throwable t) {
				log.error(t.getMessage(), t);
			}
		}

		if (resource.endsWith(".jpg"))
			return;

		/*
		 * We need to produce the SWF conversion
		 */
		if (!storer.exists(doc.getId(), resource)) {
			InputStream is = null;
			File tmp = null;
			try {
				tmp = File.createTempFile("preview", "");

				String docExtension = FilenameUtils.getExtension(doc.getFileName()).toLowerCase();
				if (SWF_DIRECT_CONVERSION_EXTS.contains(docExtension)) {
					// Perform a direct conversion using the document's file
					is = storer.getStream(doc.getId(), storer.getResourceName(doc, fileVersion, null));
					document2swf(is, docExtension, tmp);
				} else {
					// Retrieve the previously computed thumbnail
					is = storer.getStream(doc.getId(), thumbResource);

					// Convert the thumbnail to SWF
					document2swf(is, "jpg", tmp);
				}

				storer.store(tmp, doc.getId(), resource);
				log.debug("Created preview " + resource);
			} catch (Throwable e) {
				log.error(e.getMessage(), e);
			} finally {
				if (tmp != null)
					FileUtils.deleteQuietly(tmp);
				if (is != null)
					try {
						is.close();
					} catch (IOException e) {

					}
			}
		}
	}

	protected void forwardPreviewNotAvailable(HttpServletRequest request, HttpServletResponse response) {
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
	 */
	public static void downloadDocument(HttpServletRequest request, HttpServletResponse response, InputStream is,
			String filename) throws FileNotFoundException, IOException {

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

	/**
	 * Convert a generic document(image or PDF) to SWF (for document preview
	 * feature).
	 */
	protected void document2swf(InputStream is, String extension, File swf) throws IOException {
		File tmp = File.createTempFile("preview", "." + extension.toLowerCase());
		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(tmp);
			IOUtils.copy(is, fos);
			fos.flush();
			fos.close();
			ContextProperties conf = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
			String command = conf.getProperty(SWFTOOLSPATH);
			if (extension.equalsIgnoreCase("pdf"))
				command += File.separatorChar + "pdf2swf";
			else if (extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg"))
				command += File.separatorChar + "jpeg2swf";
			else if (extension.equalsIgnoreCase("png"))
				command += File.separatorChar + "png2swf";
			else if (extension.equalsIgnoreCase("gif"))
				command += File.separatorChar + "gif2swf";
			else if (extension.equalsIgnoreCase("tif") || extension.equalsIgnoreCase("tiff")
					|| extension.equalsIgnoreCase("bmp")) {
				// In this case we have to convert to temporary jpg first
				File jpegTmp = File.createTempFile("preview", ".jpg");
				String jpegCommand = "\"" + new File(conf.getProperty(CONVERT)).getPath() + "\" \"" + tmp.getPath()
						+ "\" \"" + jpegTmp.getPath() + "\"";
				Exec.exec(jpegCommand, null, null, 10);
				FileUtils.deleteQuietly(tmp);
				tmp = jpegTmp;

				command += File.separatorChar + "jpeg2swf";
			}

			command = "\"" + new File(command).getPath() + "\" -T 9 ";

			if (extension.equalsIgnoreCase("pdf"))
				command += "-f -t -G -s storeallcharacters";

			command += " \"" + tmp.getPath() + "\" -o \"" + swf.getPath() + "\"";

			Exec.exec(command, null, null, 10);
		} catch (Throwable e) {
			FileUtils.deleteQuietly(swf);
			log.error("Error in document to SWF conversion", e);
		} finally {
			IOUtils.closeQuietly(fos);
			FileUtils.deleteQuietly(tmp);
		}
	}
}