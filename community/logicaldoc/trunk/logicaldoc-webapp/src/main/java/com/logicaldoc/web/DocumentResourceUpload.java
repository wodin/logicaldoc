package com.logicaldoc.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.Version;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.document.dao.VersionDAO;
import com.logicaldoc.core.security.Folder;
import com.logicaldoc.core.security.Permission;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.UserSession;
import com.logicaldoc.core.security.dao.FolderDAO;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.util.ServletIOUtil;
import com.logicaldoc.web.util.SessionUtil;

/**
 * This servlet is responsible for document resource upload. It receives the
 * document resource and uploads it in LogicalDOC, inside the document's folder.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 4.5
 */
public class DocumentResourceUpload extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected static Log log = LogFactory.getLog(DocumentResourceUpload.class);

	public static final String DOC_ID = "docId";

	public static final String SUFFIX = "suffix";

	// The file version
	public static final String VERSION_ID = "versionId";

	// The document version
	public static final String VERSION_DOC = "versionDoc";

	/**
	 * Constructor of the object.
	 */
	public DocumentResourceUpload() {
		super();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		out.println("<HTML>");
		out.println("  <HEAD><TITLE>Upload Document Resource Servlet</TITLE></HEAD>");
		out.println("  <BODY>");
		out.print(" This servlet doesn't support GET method. Use POST instead. ");
		out.println("  </BODY>");
		out.println("</HTML>");
		out.flush();
		out.close();
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		UserSession session = SessionUtil.validateSession(request);

		UserDAO udao = (UserDAO) Context.getInstance().getBean(UserDAO.class);

		// Load the user associated to the session
		User user = udao.findByUserName(session.getUserName());
		if (user == null)
			return;

		String docId = request.getParameter(DOC_ID);

		String suffix = request.getParameter(SUFFIX);

		String fileVersion = request.getParameter(VERSION_ID);

		String docVersion = request.getParameter(VERSION_DOC);

		log.debug("Start Upload resource for document " + docId);

		FolderDAO fdao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);

		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Document doc = docDao.findById(Long.parseLong(docId));

		try {
			Folder folder = doc.getFolder();
			if (fdao.isPermissionEnabled(Permission.SIGN, folder.getId(), user.getId())) {
				ServletIOUtil.uploadDocumentResource(request, docId, suffix, fileVersion, docVersion);
				if (suffix.startsWith("sign")) {
					docDao.initialize(doc);
					doc.setSigned(1);
					docDao.store(doc);
					VersionDAO vdao = (VersionDAO) Context.getInstance().getBean(VersionDAO.class);
					Version version = null;
					if (StringUtils.isNotEmpty(docVersion))
						version = vdao.findByVersion(doc.getId(), docVersion);
					else
						version = vdao.findByVersion(doc.getId(), doc.getVersion());
					vdao.initialize(version);
					version.setSigned(1);
					vdao.store(version);
				}
			}
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}
	}
}