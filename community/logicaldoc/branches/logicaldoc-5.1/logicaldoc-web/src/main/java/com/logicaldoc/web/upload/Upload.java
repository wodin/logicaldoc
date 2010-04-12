package com.logicaldoc.web.upload;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.web.util.Constants;

/**
 * Uploads a file into the upload temporary directory upload/<session_id>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.5
 */
public class Upload extends HttpServlet {

	private static final String SID = "sid";

	private static final String FILE_NAME = "fileName";

	private static final String UPLOAD = "upload";

	private static final long serialVersionUID = 1L;

	protected static Log log = LogFactory.getLog(Upload.class);

	/**
	 * Constructor of the object.
	 */
	public Upload() {
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
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		out.println("<HTML>");
		out.println("  <HEAD><TITLE>Upload File Servlet</TITLE></HEAD>");
		out.println("  <BODY>");
		out.print(" This servlet doesn't support GET method. Use POST instead. ");
		out.println("  </BODY>");
		out.println("</HTML>");
		out.flush();
		out.close();
	}

	/**
	 * The doPost method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to
	 * post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws FileUploadException
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	@SuppressWarnings("unchecked")
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		String fileName = request.getParameter(FILE_NAME);
		String sid = request.getParameter(SID);
		Map<Object, Object> sessions = (Map<Object, Object>) getServletContext().getAttribute(Constants.SESSIONS);
		HttpSession session = (HttpSession) sessions.get(sid);

		session.removeAttribute("uploadedFile");
		log.debug("Start Upload of file " + fileName);

		DiskFileItemFactory factory = new DiskFileItemFactory();
		// Configure the factory here, if desired.
		ServletFileUpload upload = new ServletFileUpload(factory);
		// Configure the uploader here, if desired.
		List<FileItem> fileItems;
		try {
			fileItems = upload.parseRequest(request);
			for (FileItem item : fileItems) {
				if (!item.isFormField()) {
					File savedFile = new File(getUploadDir(request), fileName);
					item.write(savedFile);
				}
			}

			session.setAttribute("uploadedFile", fileName);
			log.debug("Saved file " + fileName);
		} catch (Throwable e) {
			log.error(e.getMessage());
		}
	}

	/**
	 * Computes the upload directory for the current session and creates it if
	 * needed
	 * 
	 * @param request
	 * @return The upload folder
	 */
	protected File getUploadDir(HttpServletRequest request) {
		String sid = request.getParameter(SID);
		if (StringUtils.isEmpty(sid))
			sid = request.getSession().getId();
		File uploadDir = new File(request.getSession().getServletContext().getRealPath(UPLOAD));
		uploadDir = new File(uploadDir, sid);
		uploadDir.mkdirs();
		uploadDir.mkdir();
		return uploadDir;
	}
}