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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.security.UserSession;
import com.logicaldoc.core.util.IconSelector;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.service.DocumentServiceImpl;
import com.logicaldoc.web.util.SessionUtil;

/**
 * This servlet is responsible for garbage data.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class GarbageDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static Log log = LogFactory.getLog(DocumentServiceImpl.class);

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		try {
			UserSession session = SessionUtil.validateSession(request);

			response.setContentType("text/xml");
			// Headers required by Internet Explorer
			response.setHeader("Pragma", "public");
			response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
			response.setHeader("Expires", "0");

			DocumentDAO dao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			df.setTimeZone(TimeZone.getTimeZone("UTC"));

			PrintWriter writer = response.getWriter();
			writer.write("<list>");
			for (Document doc : dao.findDeleted(session.getUserId(), 100)) {
				writer.print("<document>");
				writer.print("<id>" + doc.getId() + "</id>");
				writer.print("<icon>" + FilenameUtils.getBaseName(IconSelector.selectIcon(doc.getFileExtension()))
						+ "</icon>");
				writer.print("<title><![CDATA[" + doc.getTitle() + "]]></title>");
				writer.print("<customId><![CDATA[" + doc.getCustomId() + "]]></customId>");
				writer.print("<lastModified>" + df.format(doc.getLastModified()) + "</lastModified>");
				writer.print("<folderId>" + doc.getFolder().getId() + "</folderId>");
				writer.print("</document>");
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