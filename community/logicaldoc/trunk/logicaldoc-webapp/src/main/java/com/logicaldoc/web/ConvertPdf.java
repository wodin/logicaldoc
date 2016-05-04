package com.logicaldoc.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.Version;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.document.dao.VersionDAO;
import com.logicaldoc.core.security.Session;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.util.ServletUtil;

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
		Session session = ServletUtil.validateSession(request);

		DocumentDAO docDao = (DocumentDAO) Context.get().getBean(DocumentDAO.class);
		VersionDAO versionDao = (VersionDAO) Context.get().getBean(VersionDAO.class);

		try {
			long docId = Long.parseLong(request.getParameter(DOCUMENT_ID));
			Document document = docDao.findById(docId);
			if (document.getDocRef() != null)
				document = docDao.findById(document.getDocRef());

			if (!document.getFileName().toLowerCase().endsWith(".pdf"))
				throw new Exception("Unsupported format");

			String ver = document.getVersion();
			if (StringUtils.isNotEmpty(request.getParameter(VERSION)))
				ver = request.getParameter(VERSION);
			Version version = versionDao.findByVersion(docId, ver);

			String suffix = null;

			// Download the already stored resource
			ServletUtil.downloadDocument(request, response, null, document.getId(), version.getFileVersion(), null,
					suffix, session.getUser());
		} catch (Throwable r) {
			log.error(r.getMessage(), r);

			ServletUtil.setContentDisposition(request, response, "notavailable.pdf");

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

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}