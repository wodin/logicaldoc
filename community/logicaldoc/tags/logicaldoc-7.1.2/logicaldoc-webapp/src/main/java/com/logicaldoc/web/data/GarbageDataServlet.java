package com.logicaldoc.web.data;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.security.Folder;
import com.logicaldoc.core.security.UserSession;
import com.logicaldoc.core.security.dao.FolderDAO;
import com.logicaldoc.core.util.IconSelector;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.util.ServiceUtil;

/**
 * This servlet is responsible for garbage data.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class GarbageDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(GarbageDataServlet.class);

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		try {
			UserSession session = ServiceUtil.validateSession(request);

			response.setContentType("text/xml");
			response.setCharacterEncoding("UTF-8");

			// Headers required by Internet Explorer
			response.setHeader("Pragma", "public");
			response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
			response.setHeader("Expires", "0");

			DocumentDAO documentDAO = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
			FolderDAO folderDAO = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);

			DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			df.setTimeZone(TimeZone.getTimeZone("UTC"));

			PrintWriter writer = response.getWriter();
			writer.write("<list>");
			for (Document doc : documentDAO.findDeleted(session.getUserId(), 100)) {
				writer.print("<entry>");
				writer.print("<id>" + doc.getId() + "</id>");
				writer.print("<icon>" + FilenameUtils.getBaseName(IconSelector.selectIcon(doc.getFileExtension()))
						+ "</icon>");
				writer.print("<title><![CDATA[" + doc.getTitle() + "]]></title>");
				writer.print("<fileName><![CDATA[" + doc.getFileName() + "]]></fileName>");
				writer.print("<customId><![CDATA[" + doc.getCustomId() + "]]></customId>");
				writer.print("<lastModified>" + df.format(doc.getLastModified()) + "</lastModified>");
				writer.print("<folderId>" + doc.getFolder().getId() + "</folderId>");
				writer.print("<type>document</type>");
				writer.print("</entry>");
			}
			
			for (Folder fld : folderDAO.findDeleted(session.getUserId(), 100)) {
				writer.print("<entry>");
				writer.print("<id>" + fld.getId() + "</id>");
				writer.print("<icon>folder_closed</icon>");
				writer.print("<title><![CDATA[" + fld.getName() + "]]></title>");
				writer.print("<lastModified>" + df.format(fld.getLastModified()) + "</lastModified>");
				writer.print("<folderId>" + fld.getParentId() + "</folderId>");
				writer.print("<type>folder</type>");
				writer.print("</entry>");
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