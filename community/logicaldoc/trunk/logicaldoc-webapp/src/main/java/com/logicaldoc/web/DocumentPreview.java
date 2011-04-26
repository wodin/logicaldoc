package com.logicaldoc.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.document.thumbnail.ThumbnailManager;
import com.logicaldoc.core.store.Storer;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.MimeType;
import com.logicaldoc.util.config.ContextProperties;
import com.logicaldoc.util.io.FileUtil;

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

	private static String PDF2SWF = "pdf2swf";

	private static String EXTS_AVAILABLE = "gif, png, pdf, jpeg, jpg, tiff, tif";

	public static String IMG2PDF = "img2swf";

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

		Storer storer = (Storer) Context.getInstance().getBean(Storer.class);
		File swfFile = File.createTempFile("LDOC", ".swf");
		InputStream is = null;
		File tmpFile = null;

		try {
			long docId = Long.parseLong(id);
			DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
			Document doc = docDao.findById(docId);
			String docExtension = FilenameUtils.getExtension(doc.getFileName());
			String res = "";
			InputStream docInput = null;
			if (!EXTS_AVAILABLE.contains(docExtension)) {
				res = storer.getResourceName(doc, fileVersion, "thumb.jpg");
				docInput = storer.getStream(docId, res);

				// the thumbnail doesn't exist, create it
				if (docInput == null) {
					ThumbnailManager thumbManaher = (ThumbnailManager) Context.getInstance().getBean(
							ThumbnailManager.class);
					try {
						thumbManaher.createTumbnail(doc, fileVersion);
					} catch (Throwable t) {
						log.error(t.getMessage(), t);
					}
					docInput = storer.getStream(doc.getId(), res);
				}
				tmpFile = File.createTempFile("LDOC", ".jpg");
			} else {
				res = storer.getResourceName(doc, fileVersion, null);
				docInput = storer.getStream(doc.getId(), res);
				tmpFile = File.createTempFile("LDOC", "." + docExtension);
			}

			if (docInput == null) {
				log.debug("resource not available");
				forwardPreviewNotAvailable(request, response);
				return;
			}

			if (docExtension.equals("pdf")) {
				FileUtil.writeFile(docInput, tmpFile.getPath());
				convert2swf(tmpFile, swfFile);
			} else {
				convertImageToPdf(swfFile, docExtension, docInput);
			}

			is = new FileInputStream(swfFile);

			downloadDocument(request, response, is, doc.getFileName());
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		} finally {
			is.close();
			// Delete temporary resources
			FileUtils.forceDelete(tmpFile);
			FileUtils.forceDelete(swfFile);
		}
	}

	/**
	 * Convert IMG to PDF (for document preview feature).
	 * 
	 * @param swfCache
	 * @param docExtension
	 * @param docInput
	 * @throws IOException
	 */
	protected void convertImageToPdf(File swfCache, String docExtension, InputStream docInput) throws IOException {
		File tmpPdf = File.createTempFile("tmpPdf", ".pdf");
		img2pdf(docInput, docExtension, tmpPdf);
		convert2swf(tmpPdf, swfCache);
	}

	private void forwardPreviewNotAvailable(HttpServletRequest request, HttpServletResponse response) {
		try {
			RequestDispatcher rd = request.getRequestDispatcher("/skin/images/preview_na.gif");
			rd.forward(request, response);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
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
	 * Convert IMG to PDF (for document preview feature).
	 */
	private void img2pdf(InputStream is, String mimeType, File output) throws IOException {
		File tmp = File.createTempFile("LDOC", mimeType);
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
			Process process = pb.start();
			process.waitFor();
			process.destroy();

			// Check return code
			if (process.exitValue() == 1) {
				// log.warn(info);
			}

			// log.debug("Elapse img2pdf time: {}",
			// FormatUtil.formatSeconds(System.currentTimeMillis() - start));
		} catch (Exception e) {
			// log.error("Error in IMG to PDF conversion", e);
			output.delete();
			throw new IOException("Error in IMG to PDF conversion", e);
		} finally {
			IOUtils.closeQuietly(fos);
			tmp.delete();
		}
	}

	/**
	 * Convert to SWF (for document preview feature).
	 */
	private void convert2swf(File input, File output) throws IOException {
		ContextProperties conf = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
		String[] cmd = composeCmd(conf.getProperty(PDF2SWF), input, output);
		// log.debug("Command: {}", Arrays.toString(cmd));
		// System.out.println(Arrays.toString(cmd));
		BufferedReader stdout = null;
		String line;

		try {
			ProcessBuilder pb = new ProcessBuilder(cmd);
			Process process = pb.start();
			stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));

			while ((line = stdout.readLine()) != null) {
				// log.debug("STDOUT: {}", line);
			}

			process.waitFor();

			// Check return code
			if (process.exitValue() != 0) {
				// log.warn("Abnormal program termination: {}" +
				// process.exitValue());
				// log.warn("STDERR: {}",
				// IOUtils.toString(process.getErrorStream()));
			} else {
				// log.debug("Normal program termination");
			}

			process.destroy();
			// log.debug("Elapse pdf2swf time: {}",
			// FormatUtil.formatSeconds(System.currentTimeMillis() - start));
		} catch (Throwable e) {
			output.delete();
			log.error("Error in PDF to SWF conversion", e);
		} finally {
			IOUtils.closeQuietly(stdout);
		}
	}

	/**
	 * Composes the correct command to be executed.
	 */
	private String[] composeCmd(String command, File input, File output) {
		String standardCmd[] = { command, "-T 9", input.getPath(), "-o", output.getPath() };
		String imgCmd[] = { command, "-T 9 -q 30", input.getPath(), "-o", output.getPath() };
		if (command.endsWith("convert"))
			return imgCmd;
		else
			return standardCmd;
	}
}