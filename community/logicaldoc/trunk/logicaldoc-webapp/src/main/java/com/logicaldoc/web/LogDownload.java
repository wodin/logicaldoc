package com.logicaldoc.web;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.icu.text.SimpleDateFormat;
import com.logicaldoc.util.config.ContextProperties;
import com.logicaldoc.util.config.LoggingConfigurator;
import com.logicaldoc.util.config.OrderedProperties;
import com.logicaldoc.web.util.ServiceUtil;

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
		ServiceUtil.validateSession(request);

		String appender = request.getParameter("appender");
		File file = null;

		if ("all".equals(appender)) {
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");

			response.setContentType("application/zip");
			response.setHeader("Pragma", "public");
			response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
			response.setHeader("Expires", "0");
			response.setHeader("Content-Disposition", "attachment; filename=\""
					+ ("ldoc-log-" + df.format(new Date()) + ".zip") + "\"");

			file = prepareAllLogs(response);
		} else {
			response.setContentType("text/html");
			LoggingConfigurator conf = new LoggingConfigurator();
			file = new File(conf.getFile(appender, true));
		}

		if (file == null)
			return;

		response.setHeader("Content-Length", Long.toString(file.length()));
		try {
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
			log.error(ex.getMessage(), ex);
		} finally {
			if ("all".equals(appender) && file != null)
				FileUtils.deleteQuietly(file);
		}
	}

	/**
	 * Prepare all log files plus the context.properties in one single zip.
	 */
	private File prepareAllLogs(HttpServletResponse response) {
		File tmp = null;

		try {
			tmp = File.createTempFile("logs", ".zip");
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(tmp));

			try {
				LoggingConfigurator conf = new LoggingConfigurator();
				Collection<String> appenders = conf.getLoggingFiles();

				// Store the log files in the zip file
				for (String appender : appenders) {
					if (appender.endsWith("_WEB"))
						continue;

					File logFile = new File(conf.getFile(appender, true));
					FileInputStream in = new FileInputStream(logFile);

					// name the file inside the zip file
					out.putNextEntry(new ZipEntry(logFile.getName()));

					// buffer size
					byte[] b = new byte[1024];
					int count;

					while ((count = in.read(b)) > 0) {
						out.write(b, 0, count);
					}
					in.close();
				}

				// Now create a copy of the configuration and store it in thee
				// zip file
				ContextProperties cp = new ContextProperties();
				File buf = File.createTempFile("context", ".properties");
				OrderedProperties prop = new OrderedProperties();
				for (String key : cp.getKeys()) {
					if (key.contains("password"))
						continue;
					else
						prop.put(key, cp.get(key));
				}
				prop.store(new FileOutputStream(buf), "Download Logs");
				FileInputStream in = new FileInputStream(buf);

				// name the file inside the zip file
				out.putNextEntry(new ZipEntry("context.properties"));

				// buffer size
				byte[] b = new byte[1024];
				int count;

				while ((count = in.read(b)) > 0) {
					out.write(b, 0, count);
				}
				in.close();

			} finally {
				out.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return tmp;
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
