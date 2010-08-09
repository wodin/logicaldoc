package com.logicaldoc.web.data;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;

import com.logicaldoc.core.document.dao.VersionDAO;
import com.logicaldoc.core.util.IconSelector;
import com.logicaldoc.i18n.I18N;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.util.SessionUtil;

/**
 * This servlet is responsible for document versions data.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class VersionsDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		SessionUtil.validateSession(request);

		String locale = request.getParameter("locale");

		response.setContentType("text/xml");

		// Headers required by Internet Explorer
		response.setHeader("Pragma", "public");
		response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
		response.setHeader("Expires", "0");

		PrintWriter writer = response.getWriter();
		writer.write("<list>");

		VersionDAO dao = (VersionDAO) Context.getInstance().getBean(VersionDAO.class);
		StringBuffer query = new StringBuffer(
				"select A.id, A.username, A.event, A.version, A.fileVersion, A.versionDate, A.comment, A.docId, A.title, A.customId, A.fileSize, A.type  from Version A where A.deleted = 0 ");
		query.append(" and A.docId=" + request.getParameter("docId"));
		query.append(" order by A.versionDate asc ");

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		List<Object> records = (List<Object>) dao.findByQuery(query.toString(), null, null);

		/*
		 * Iterate over records composing the response XML document
		 */
		for (Object record : records) {
			Object[] cols = (Object[]) record;

			writer.print("<version>");
			writer.print("<id>" + cols[0] + "</id>");
			writer.print("<user><![CDATA[" + cols[1] + "]]></user>");
			writer.print("<event><![CDATA[" + I18N.message((String) cols[2], locale) + "]]></event>");
			writer.print("<version>" + cols[3] + "</version>");
			writer.print("<fileVersion>" + cols[4] + "</fileVersion>");
			writer.print("<date>" + df.format((Date) cols[5]) + "</date>");
			writer.print("<comment><![CDATA[" + cols[6] + "]]></comment>");
			writer.print("<docid>" + cols[7] + "</docid>");
			writer.print("<title><![CDATA[" + cols[8] + "]]></title>");
			writer.print("<customid><![CDATA[" + cols[9] + "]]></customid>");
			writer.print("<size>" + cols[10] + "</size>");
			writer.print("<icon>" + FilenameUtils.getBaseName(IconSelector.selectIcon((String) cols[11])) + "</icon>");
			writer.print("</version>");
		}

		writer.write("</list>");
	}
}