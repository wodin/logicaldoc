package com.logicaldoc.dropbox;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dropbox.core.DbxEntry;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.util.IconSelector;

/**
 * This servlet is responsible for retrieving Dropbox entries.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 7.0
 */
public class DropboxDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(DropboxDataServlet.class);

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		try {
			SessionUtil.validateSession(request);
			User user = SessionUtil.getSessionUser(request);

			Dropbox dbox = new Dropbox();
			boolean connected = dbox.login(DropboxServiceImpl.loadAccessToken(user), user.getLocale());
			if (!connected)
				throw new IOException("Unable to connect to Dropbox");

			boolean folders = "true".equals(request.getParameter("folders"));

			String parent = request.getParameter("parent");
			if (parent == null)
				parent = "#parent#";

			response.setContentType("text/xml");
			response.setCharacterEncoding("UTF-8");

			// Headers required by Internet Explorer
			response.setHeader("Pragma", "public");
			response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
			response.setHeader("Expires", "0");

			PrintWriter writer = response.getWriter();
			writer.write("<list>");

			if ("#parent#".equals(parent)) {
				writer.print("<entry>");
				writer.print("<path>/</path>");
				writer.print("<parent><![CDATA[" + parent + "]]></parent>");
				writer.print("<name>/</name>");
				writer.print("<type>folder</type>");
				writer.print("<iicon>folder</iicon>");
				writer.print("</entry>");
			} else {

				DbxEntry ent = dbox.get(parent);
				if (ent != null && ent instanceof DbxEntry.Folder) {
					List<DbxEntry> entries = dbox.list(parent);
					for (DbxEntry entry : entries) {
						if (folders && entry instanceof DbxEntry.File)
							continue;
						writer.print("<entry>");
						writer.print("<path><![CDATA[" + entry.path + "]]></path>");
						writer.print("<parent><![CDATA[" + parent + "]]></parent>");
						writer.print("<name><![CDATA[" + entry.name + "]]></name>");
						writer.print("<type>" + ((entry instanceof DbxEntry.File) ? "file" : "folder") + "</type>");
						if (entry instanceof DbxEntry.File)
							writer.print("<iicon>"
									+ FilenameUtils.getBaseName(IconSelector.selectIcon(FilenameUtils
											.getExtension(entry.name).toLowerCase().trim())) + "</iicon>");
						else
							writer.print("<iicon>folder</iicon>");
						writer.print("</entry>");
					}
				}
			}
			writer.write("</list>");
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			if (e instanceof ServletException)
				throw (ServletException) e;
			else if (e instanceof IOException)
				throw (IOException) e;
			else
				throw new ServletException(e.getMessage(), e);
		}
	}
}