package com.logicaldoc.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This servlet is used to serve those requests for displaying details of a
 * document or a folder. It will be protected by basic authentication.
 * 
 * @author Marco Meschieri - LogicaLDOC
 * @since 7.5.1
 */
public class DisplayServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public static final String DOC_ID = "docId";

	public static final String FOLDER_ID = "folderId";

	protected static Logger log = LoggerFactory.getLogger(DisplayServlet.class);

	public DisplayServlet() {
		super();
	}

	/**
	 * Redirects the request to the proper frontend URL
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String docId = request.getParameter(DOC_ID);
		String folderId = request.getParameter(FOLDER_ID);

		try {
			String redirectUrl = request.getContextPath() + "/frontend.jsp?";
			if (StringUtils.isNotEmpty(docId))
				redirectUrl += DOC_ID + "=" + docId;
			else
				redirectUrl += FOLDER_ID + "=" + folderId;
			response.sendRedirect(redirectUrl);
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			new IOException(t.getMessage());
		}
	}
}