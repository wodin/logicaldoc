package com.logicaldoc.web.document;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.core.transfer.ZipExport;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.SessionManagement;
import com.logicaldoc.web.util.Constants;

/**
 * This servlet is responsible of zip export
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 2.1
 */
public class ExportZip extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected static Log logger = LogFactory.getLog(ExportZip.class);

	/**
	 * Constructor of the object.
	 */
	public ExportZip() {
		super();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (SessionManagement.isValid(request.getSession(false))) {
			try {
				Long userId = (Long) request.getSession(false).getAttribute(Constants.AUTH_USERID);
				String menuId = request.getParameter("menuId");
				String level = request.getParameter("level");

				if (level == null) {
					level = "all";
				}

				ZipExport exporter = new ZipExport();

				if (level.equals("all")) {
					exporter.setAllLevel(true);
				}

				MenuDAO menuDao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);

				ByteArrayOutputStream bos = exporter.process(Long.parseLong(menuId), userId.longValue());
				response.setContentType("application/zip");
				response.setContentLength(bos.size());
				response.setHeader("Content-Disposition", "attachment; filename=\""
						+ menuDao.findById(Long.parseLong(menuId)).getText() + ".zip\"");

				// Headers required by MS Internet Explorer
				response.setHeader("Pragma", "public");
				response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
				response.setHeader("Expires", "0");

				OutputStream os;
				os = response.getOutputStream();
				bos.flush();
				os.write(bos.toByteArray());
				os.flush();
				os.close();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
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
