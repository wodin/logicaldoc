package com.logicaldoc.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

	protected static String PDF2SWF = "command.pdf2swf";

	protected static String IMG2PDF = "command.convert";

	/** For these extensions we are able to directly convert to SWF */
	protected String SWF_DIRECT_CONVERSION_EXTS = "gif, png, pdf, jpeg, jpg, tiff, tif";

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

				String docExtension = FilenameUtils.getExtension(doc.getFileName());
				if (SWF_DIRECT_CONVERSION_EXTS.contains(docExtension)) {
					// Perform a direct conversion using the document's file
					is = storer.getStream(doc.getId(), storer.getResourceName(doc, fileVersion, null));
					document2swf(tmp, docExtension, is);
				} else {
					// Retrieve the previously computed thumbnail
					is = storer.getStream(doc.getId(), thumbResource);

					// Convert the thumbnail to SWF
					document2swf(tmp, "jpg", is);
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
	protected void document2swf(File swfCache, String extension, InputStream docInput) throws IOException {
		File tmpPdf = null;
		try {
			tmpPdf = File.createTempFile("preview", ".pdf");
			if ("pdf".equals(extension.toLowerCase())) {
				FileOutputStream fos = null;
				try {
					fos = new FileOutputStream(tmpPdf);
					IOUtils.copy(docInput, fos);
					fos.flush();
				} catch (Throwable e) {
					throw new IOException("Error in IMG to PDF conversion", e);
				} finally {
					IOUtils.closeQuietly(fos);
				}
			} else
				img2pdf(docInput, extension, tmpPdf);
			pdf2swf(tmpPdf, swfCache);
		} finally {
			if (tmpPdf != null)
				FileUtils.deleteQuietly(tmpPdf);
		}
	}

	/**
	 * Convert IMG to PDF (for document preview feature).
	 */
	protected void img2pdf(InputStream is, String extension, File output) throws IOException {
		File tmp = File.createTempFile("preview", extension);
		String inputFile = tmp.getPath() + "[0]";
		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(tmp);
			IOUtils.copy(is, fos);
			fos.flush();
			fos.close();

			ContextProperties conf = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);

			ProcessBuilder pb = new ProcessBuilder(conf.getProperty(IMG2PDF), inputFile,
					"  -compress None -quality 100 ", output.getPath());
			// Launch the process
			final Process process = pb.start();

			Thread wrapper = new Thread() {
				@Override
				public void run() {
					/*
					 * This will wait until the command terminates. But
					 * sometimes it rest appended.
					 */
					try {
						process.waitFor();
					} catch (InterruptedException e) {
					}
				}
			};
			wrapper.start();

			// Wait 10 seconds
			for (int i = 0; i < 10; i++) {
				if (wrapper.isAlive())
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}
			}
			wrapper.interrupt();

			process.destroy();
		} catch (Throwable e) {
			throw new IOException("Error in IMG to PDF conversion", e);
		} finally {
			IOUtils.closeQuietly(fos);
			tmp.delete();
		}
	}

	/**
	 * Convert a PDF to SWF (for document preview feature).
	 */
	protected void pdf2swf(File input, File output) throws IOException {
		ContextProperties conf = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
		String[] cmd = composeCmd(conf.getProperty(PDF2SWF), input, output);
		BufferedReader stdout = null;
		Process process = null;
		try {
			ProcessBuilder pb = new ProcessBuilder(cmd);
			process = pb.start();
			stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));

			while (stdout.readLine() != null) {
				// Do nothing
			}

			process.waitFor();
		} catch (Throwable e) {
			output.delete();
			log.error("Error in PDF to SWF conversion", e);
		} finally {
			if (process != null)
				process.destroy();
			IOUtils.closeQuietly(stdout);
		}
	}

	/**
	 * Composes the correct command to be executed.
	 */
	protected String[] composeCmd(String command, File input, File output) {
		String standardCmd[] = { command, "-f", "-T 9", "-t", "-G", "-s storeallcharacters", input.getPath(), "-o",
				output.getPath() };
		String imgCmd[] = { command, "-T 9 -q 30", input.getPath(), "-o", output.getPath() };
		if (command.endsWith("convert"))
			return imgCmd;
		else
			return standardCmd;
	}
}