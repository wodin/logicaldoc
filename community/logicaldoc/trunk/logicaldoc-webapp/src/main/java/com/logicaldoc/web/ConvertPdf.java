package com.logicaldoc.web;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.util.PDFMergerUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.Version;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.document.dao.VersionDAO;
import com.logicaldoc.core.document.pdf.PdfConverterManager;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.UserSession;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.util.ServiceUtil;
import com.logicaldoc.web.util.ServletIOUtil;

/**
 * This servlet simply download the document it is a PDF.
 * 
 * @author Marco Meschieri - LogicalDOC
 * @since 7.4.2
 */
public class ConvertPdf extends HttpServlet {

	private static final String VERSION = "version";

	private static final String DOCUMENT_ID = "docId";

	private static final long serialVersionUID = 1L;

	protected static Logger log = LoggerFactory.getLogger(ConvertPdf.class);

	/**
	 * Constructor of the object.
	 */
	public ConvertPdf() {
		super();
	}

	/**
	 * The doGet method of the servlet. <br/>
	 * 
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User user = ServiceUtil.getSessionUser(request);

		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		VersionDAO versionDao = (VersionDAO) Context.getInstance().getBean(VersionDAO.class);

		try {
			long docId = Long.parseLong(request.getParameter(DOCUMENT_ID));
			Document document = docDao.findById(docId);

			if (!document.getFileName().toLowerCase().endsWith(".pdf"))
				throw new Exception("Unsupported format");

			String ver = document.getVersion();
			if (StringUtils.isNotEmpty(request.getParameter(VERSION)))
				ver = request.getParameter(VERSION);
			Version version = versionDao.findByVersion(docId, ver);

			String suffix = null;

			// Download the already stored resource
			ServletIOUtil
					.downloadDocument(request, response, null, docId, version.getFileVersion(), null, suffix, user);
		} catch (Throwable r) {
			log.error(r.getMessage(), r);

			ServletIOUtil.setContentDisposition(request, response, "notavailable.pdf");

			InputStream is = ConvertPdf.class.getResourceAsStream("/pdf/notavailable.pdf");
			OutputStream os;
			os = response.getOutputStream();
			int letter = 0;

			try {
				while ((letter = is.read()) != -1)
					os.write(letter);
			} finally {
				os.flush();
				os.close();
				is.close();
			}
		}
	}

	/**
	 * Convert a selection of documents into PDF and stores them in a temporary
	 * folder
	 * 
	 * @param session Current session
	 * @param docIds List of documents to be converted
	 * @return The temporary folder
	 * @throws IOException
	 */
	private File preparePdfs(UserSession session, List<String> docIds) throws IOException {
		File temp;
		DecimalFormat nf = new DecimalFormat("00000000");
		temp = File.createTempFile("merge", "");
		temp.delete();
		temp.mkdir();

		int i = 0;
		for (String docId : docIds) {
			try {
				i++;
				long id = Long.parseLong(docId);
				DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
				Document document = docDao.findById(id);

				PdfConverterManager manager = (PdfConverterManager) Context.getInstance().getBean(
						PdfConverterManager.class);
				manager.createPdf(document, session.getId());

				File pdf = new File(temp, nf.format(i) + ".pdf");

				manager.writePdfToFile(document, null, pdf, session.getId());
			} catch (Throwable t) {
				log.error(t.getMessage(), t);
			}
		}
		return temp;
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	/**
	 * Merges different PDFs into a single PDF-
	 * 
	 * @param pdfs ordered array of pdf files to be merged
	 * @return The merged Pdf file
	 * 
	 * @throws IOException
	 * @throws COSVisitorException
	 */
	private static File mergePdf(File[] pdfs) throws IOException, COSVisitorException {
		File dst = null;
		dst = File.createTempFile("merge", ".pdf");

		PDFMergerUtility merger = new PDFMergerUtility();
		for (File file : pdfs) {
			merger.addSource(file);
		}

		merger.setDestinationFileName(dst.getAbsolutePath());
		merger.mergeDocuments();

		return dst;
	}
}