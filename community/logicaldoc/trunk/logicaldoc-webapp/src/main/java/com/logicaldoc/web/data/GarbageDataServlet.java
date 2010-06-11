package com.logicaldoc.web.data;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.security.UserSession;
import com.logicaldoc.core.util.IconSelector;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.SessionBean;

public class GarbageDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		UserSession session = SessionBean.validateSession(request);

		response.setContentType("text/xml");

		// Headers required by Internet Explorer
		response.setHeader("Pragma", "public");
		response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
		response.setHeader("Expires", "0");

		DocumentDAO dao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

		PrintWriter writer = response.getWriter();
		writer.write("<list>");
		for (Document doc : dao.findDeleted(session.getUserId(), 100)) {
			writer.print("<document>");
			writer.print("<id>" + doc.getId() + "</id>");
			writer.print("<icon>" + IconSelector.selectIcon(doc.getFileExtension()) + "</icon>");
			writer.print("<title><![CDATA[" + doc.getTitle() + "]]></title>");
			writer.print("<customId><![CDATA[" + doc.getCustomId() + "]]></customId>");
			writer.print("<lastModified>" + df.format(doc.getLastModified()) + "</lastModified>");
			writer.print("<folderId>" + doc.getFolder().getId() + "</folderId>");
			writer.print("</document>");
		}
		writer.write("</list>");
	}
}
