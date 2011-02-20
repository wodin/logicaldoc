package com.logicaldoc.web.document;

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
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.SessionManager;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.util.Constants;
import com.logicaldoc.web.util.ServletDocUtil;

/**
 * This servlet is responsible for document downloads. It searches for the
 * attribute docId in any scope and extracts the proper document's content.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 2.6
 */
public class DocumentDownload extends HttpServlet {

	// The file version
	public static final String VERSION_ID = "versionId";

	public static final String SUFFIX = "suffix";

	public static final String DOC_ID = "docId";

	public static final String SESSION = "session";

	private static final long serialVersionUID = -6956612970433309888L;

	protected static Log log = LogFactory.getLog(DocumentDownload.class);

	/**
	 * Constructor of the object.
	 */
	public DocumentDownload() {
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
		try {
			String userSession = request.getParameter(SESSION);
			if (userSession == null && request.getSession(false) != null)
				userSession = (String) request.getSession().getAttribute(Constants.USER_SESSION);

			if (!SessionManager.getInstance().isValid(userSession)) {
				log.error("Invalid session " + userSession);
				return;
			} else {
				SessionManager.getInstance().renew(userSession);
			}

			UserDAO udao = (UserDAO) Context.getInstance().getBean(UserDAO.class);

			// Load the user associated to the session
			User user = udao.findByUserName(SessionManager.getInstance().get(userSession).getUserName());
			if (user == null)
				return;

			// Flag indicating to download only indexed text
			String downloadText = request.getParameter("downloadText");

			String suffix = request.getParameter(SUFFIX);
			if (StringUtils.isEmpty(suffix)) {
				suffix = (String) request.getAttribute(SUFFIX);
			}

			String id = request.getParameter(DOC_ID);

			if (StringUtils.isEmpty(id)) {
				id = (String) request.getAttribute(DOC_ID);
			}

			String fileVersion = request.getParameter(VERSION_ID);

			if (StringUtils.isEmpty(fileVersion)) {
				fileVersion = (String) request.getAttribute(VERSION_ID);
			}

			log.debug("Download document id=" + id + " " + fileVersion);

			MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);

			DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
			Document doc = docDao.findById(Long.parseLong(id));
			Menu folder = doc.getFolder();
			// if we have access to the document, return it
			if (mdao.isReadEnable(folder.getId(), user.getId())) {
				if ("true".equals(downloadText)) {
					ServletDocUtil.downloadDocumentText(request, response, doc.getId());
				} else {
					ServletDocUtil.downloadDocument(request, response, doc.getId(), fileVersion, doc.getFileName(),
							suffix, user);

					// add the file to the recent files of the user
					ServletDocUtil.addToRecentFiles(user.getId(), doc.getId());
				}
			}
		} catch (Throwable ex) {
			log.error(ex.getMessage(), ex);
		}
	}

	/**
	 * The doPost method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to
	 * post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");

		PrintWriter out = response.getWriter();
		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		out.println("<HTML>");
		out.println("  <HEAD><TITLE>Download Document Servlet</TITLE></HEAD>");
		out.println("  <BODY>");
		out.print("    This is ");
		out.print(this.getClass());
		out.println(", using the POST method");
		out.println("  </BODY>");
		out.println("</HTML>");
		out.flush();
		out.close();
	}
}