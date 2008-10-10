package com.logicaldoc.web.document;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.util.Context;

import com.logicaldoc.web.SessionManagement;
import com.logicaldoc.web.util.Constants;

/**
 * This servlet is responsible for document downloads. It searches for the
 * attribute docId in any scope and extracts the proper document's content.
 * 
 * @author Marco Meschieri
 * @version $Id: DocumentDownload.java,v 1.5 2006/08/24 08:35:08 marco Exp $
 * @since 2.6
 */
public class DocumentDownload extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6956612970433309888L;
	protected static Log logger = LogFactory.getLog(DocumentDownload.class);

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
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		String username = null;
		if (session != null)
			username = (String) session.getAttribute(Constants.AUTH_USERNAME);

		if (StringUtils.isEmpty(username))
			username = (String) request.getParameter("username");
		String password = (String) request.getParameter("password");

		String id = request.getParameter("menuId");

		// Flag indicating to download only indexed text
		String downloadText = request.getParameter("downloadText");

		if (StringUtils.isEmpty(id)) {
			id = (String) request.getAttribute("menuId");
		}

		if (StringUtils.isEmpty(id)) {
			id = (String) session.getAttribute("menuId");
		}

		if (StringUtils.isEmpty(downloadText)) {
			downloadText = (String) session.getAttribute("downloadText");
		}

		String version = request.getParameter("versionId");

		if (StringUtils.isEmpty(id)) {
			version = (String) request.getAttribute("versionId");
		}

		if (StringUtils.isEmpty(id)) {
			version = (String) session.getAttribute("versionId");
		}

		logger.debug("Download document menuId=" + id + " " + version);

		if (session != null && SessionManagement.isValid(session)) {
			try {
				// if we have access to the document, return it
				MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(
						MenuDAO.class);
				
				if (mdao.isReadEnable(Integer.parseInt(id), username)) {
					if ("true".equals(downloadText)) {					
						DownloadDocUtil.downloadDocumentText(response, Integer
								.parseInt(id));
					} else {
						DownloadDocUtil.downloadDocument(response, Integer
								.parseInt(id), version);

						// add the file to the recent files of the user
						DownloadDocUtil.addToRecentFiles(username, Integer
								.parseInt(id));
					}
				}
			} catch (Exception ex) {
				logger.error(ex.getMessage(), ex);
			}
		} else {
			try {
				UserDAO udao = (UserDAO) Context.getInstance().getBean(
						UserDAO.class);
				if (!udao.validateUser(username, password))
					throw new Exception("Unknown user " + username);

				// if we have access to the document, return it
				MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(
						MenuDAO.class);
				if (mdao.isReadEnable(Integer.parseInt(id), username)) {
					DownloadDocUtil.downloadDocument(response, Integer
							.parseInt(id), version);
				}
			} catch (Exception ex) {
				logger.error(ex.getMessage(), ex);
			}
		}
	}

	/**
	 * The doPost method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to
	 * post.
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
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");

		PrintWriter out = response.getWriter();
		out
				.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
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
