package com.logicaldoc.web.admin;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.util.config.LoggingConfigurator;
import com.logicaldoc.web.SessionManagement;

/**
 * This servlet grant access to log files
 * 
 * @author Marco Meschieri
 * @since 3.0
 */
public class LogDownload extends HttpServlet {
	protected static Log log = LogFactory.getLog(LogDownload.class);

	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String appender = request.getParameter("appender");
		response.setContentType("text/html");
		
		if (SessionManagement.isValid(request.getSession())) {
			try {
				LoggingConfigurator conf = new LoggingConfigurator();
				String file = conf.getFile(appender, true);

				// StringBuffer content = FileBean.readFile(file);
				// Reader reader=new BufferedReader(new FileReader(file));
				InputStream is = new BufferedInputStream(new FileInputStream(file));
				byte[] buf = new byte[1024];
				int read = 1;

				while (read > 0) {
					read = is.read(buf, 0, buf.length);

					if (read > 0) {
						response.getOutputStream().write(buf, 0, read);
					}
				}

				is.close();
			} catch (Exception ex) {
				//log.error(ex.getMessage(), ex);
			}
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");

		PrintWriter out = response.getWriter();
		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		out.println("<HTML>");
		out.println("  <HEAD><TITLE>Download Logs Servlet</TITLE></HEAD>");
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