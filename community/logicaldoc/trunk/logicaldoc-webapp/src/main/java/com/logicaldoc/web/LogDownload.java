package com.logicaldoc.web;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.util.config.LoggingConfigurator;
import com.logicaldoc.web.util.SessionUtil;

/**
 * This servlet grant access to log files
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class LogDownload extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected static Logger log = LoggerFactory.getLogger(LogDownload.class);

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		SessionUtil.validateSession(request);

		String appender = request.getParameter("appender");

		response.setContentType("text/html");

		try {
			LoggingConfigurator conf = new LoggingConfigurator();
			String file = conf.getFile(appender, true);

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
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
