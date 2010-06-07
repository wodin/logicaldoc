package com.logicaldoc.web.data;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.security.UserSession;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.SessionBean;

public class DocumentsDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {

		System.out.println("**** DocumentsDataServlet!!!");

		/*
		 * Validate the session
		 */
		String sid = (String) request.getParameter("sid");

		System.out.println("**** sid: " + sid);

		UserSession session = SessionBean.validateSession(sid);

		System.out.println("**** session: " + session.getId());

		/*
		 * Load some filters from the current request
		 */
		int max = Integer.parseInt(request.getParameter("max"));
		System.out.println("**** max: " + max);

		Long folderId = null;
		if (StringUtils.isNotEmpty(request.getParameter("folderId")))
			folderId = new Long(request.getParameter("folderId"));
		System.out.println("**** folderId: " + folderId);

		String filename = null;
		if (StringUtils.isNotEmpty(request.getParameter("filename")))
			filename = request.getParameter("filename");
		System.out.println("**** filename: " + filename);

		Boolean indexable = null;
		if (StringUtils.isNotEmpty(request.getParameter("indexable")))
			indexable = new Boolean(request.getParameter("indexable"));
		System.out.println("**** indexable: " + indexable);

		response.setContentType("text/xml");

		// Headers required by Internet Explorer
		response.setHeader("Pragma", "public");
		response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
		response.setHeader("Expires", "0");

		PrintWriter writer = response.getWriter();
		writer.write("<list>");

		/*
		 * Execute the Query
		 */
		Context context = Context.getInstance();
		DocumentDAO dao = (DocumentDAO) context.getBean(DocumentDAO.class);
		StringBuffer query = new StringBuffer(
				"select A.id, A.customId, A.docRef, A.type, A.title, A.version, A.lastModified, A.date, A.publisher,"
						+ " A.creation, A.creator, A.fileSize, A.immutable, A.indexed, A.lockUserId, A.fileName, A.status  "
						+ "from Document A where 1=1 ");
		if (folderId != null)
			query.append(" and A.folder.id=" + folderId);
		if (indexable != null)
			if (indexable == false)
				query.append(" and not(A.indexed=2) ");
			else
				query.append(" and (A.indexed=2) ");
		if (filename != null)
			query.append(" and lower(A.fileName) like '%" + filename.toLowerCase() + "%' ");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		List<Object> records = (List<Object>) dao.findByQuery(query.toString(), null, max);

		/*
		 * Iterqte over records composing the response XML document
		 */

		for (Object record : records) {
			Object[] cols = (Object[]) record;
			writer.print("<document>");
			writer.print("<id>" + cols[0] + "</id>");
			if (cols[1] != null)
				writer.print("<customId>" + cols[1] + "</customId>");
			else
				writer.print("<customId> </customId>");
			writer.print("<docref>" + cols[2] + "</docref>");
			writer.print("<icon>" + cols[3] + "</icon>");
			writer.print("<title>" + cols[4] + "</title>");
			writer.print("<version>" + cols[5] + "</version>");
			writer.print("<lastModified>" + df.format(cols[6]) + "</lastModified>");
			writer.print("<published>" + df.format(cols[7]) + "</published>");
			writer.print("<publisher>" + cols[8] + "</publisher>");
			writer.print("<created>" + df.format(cols[9]) + "</created>");
			writer.print("<creator>" + cols[10] + "</creator>");
			writer.print("<size>" + cols[11] + "</size>");
			writer.print("<immutable>" + cols[12] + "</immutable>");
			writer.print("<indexed>" + cols[13] + "</indexed>");
			if (cols[14] != null)
				writer.print("<locked>" + 1 + "</locked>");
			else
				writer.print("<locked>" + 0 + "</locked>");
			writer.print("<filename>" + cols[15] + "</filename>");
			writer.print("<status>" + cols[16] + "</status>");
			writer.print("</document>");
		}

		writer.write("</list>");
	}
}
