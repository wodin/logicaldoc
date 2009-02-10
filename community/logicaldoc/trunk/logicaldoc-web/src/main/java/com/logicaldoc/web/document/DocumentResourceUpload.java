package com.logicaldoc.web.document;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.Permission;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.util.Constants;
import com.logicaldoc.web.util.ServletDocUtil;

/**
 * This servlet is responsible for document resource upload. It receives the
 * document resource and uploads it in LogicalDOC, inside the document's folder.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 4.5
 */
public class DocumentResourceUpload extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected static Log logger = LogFactory.getLog(DocumentResourceUpload.class);

	public static final String DOC_ID = "docId";

	public static final String SUFFIX = "suffix";

	public static final String VERSION_ID = "versionId";

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
		HttpSession session = request.getSession(false);
		String username = null;

		if (session != null)
			username = (String) session.getAttribute(Constants.AUTH_USERNAME);

		if (username == null)
			username = request.getParameter("username");

		String password = (String) request.getParameter("password");

		String docId = request.getParameter(DOC_ID);

		String suffix = request.getParameter(SUFFIX);

		String fileVersion = request.getParameter(VERSION_ID);

		logger.debug("Start Upload resource for document " + docId);

		MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		UserDAO udao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
		User user = udao.findByUserName(username);
		if (user == null)
			return;
		if (!udao.validateUser(username, password)) {
			try {
				throw new Exception("Unknown user " + username);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}

		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Document doc = docDao.findById(Long.parseLong(docId));

		try {
			Menu folder = doc.getFolder();
			if (mdao.isPermissionEnabled(Permission.SIGN, folder.getId(), user.getId())) {
				ServletDocUtil.uploadDocumentResource(request, docId, suffix, fileVersion);
				if (suffix.startsWith("sign")) {
					docDao.initialize(doc);
					doc.setSigned(1);
					docDao.store(doc);
				}
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
	}
}
