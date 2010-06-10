package com.logicaldoc.web.data;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.SessionBean;

/**
 * 
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class DocumentsDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		SessionBean.validateSession(request);

		/*
		 * Load some filters from the current request
		 */
		int max = Integer.parseInt(request.getParameter("max"));

		Long folderId = null;
		if (StringUtils.isNotEmpty(request.getParameter("folderId")))
			folderId = new Long(request.getParameter("folderId"));

		String filename = null;
		if (StringUtils.isNotEmpty(request.getParameter("filename")))
			filename = request.getParameter("filename");

		Boolean indexable = null;
		if (StringUtils.isNotEmpty(request.getParameter("indexable")))
			indexable = new Boolean(request.getParameter("indexable"));

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

		List<Long> docRefIds = new ArrayList<Long>();
		/*
		 * Iterate over records composing the response XML document
		 */
		for (Object record : records) {
			Object[] cols = (Object[]) record;
			if (cols[2] != null) {
				docRefIds.add((Long) cols[2]);
				continue;
			}
			writer.print("<document>");
			writer.print("<id>" + cols[0] + "</id>");
			if (cols[1] != null)
				writer.print("<customId><![CDATA[" + cols[1] + "]]></customId>");
			else
				writer.print("<customId> </customId>");
			writer.print("<docref>" + cols[2] + "</docref>");
			if (cols[2] != null)
				writer.print("<icon>alias</icon>");
			else if (cols[3].toString().toLowerCase().equals("doc") || cols[3].toString().toLowerCase().equals("docx")
					|| cols[3].toString().toLowerCase().equals("odt"))
				writer.print("<icon>word</icon>");
			else if (cols[3].toString().toLowerCase().equals("ppt"))
				writer.print("<icon>powerpoint</icon>");
			else if (cols[3].toString().toLowerCase().equals("pdf"))
				writer.print("<icon>pdf</icon>");
			else if (cols[3].toString().toLowerCase().equals("jpeg") || cols[3].toString().toLowerCase().equals("jpg")
					|| cols[3].toString().toLowerCase().equals("gif") || cols[3].toString().toLowerCase().equals("png")
					|| cols[3].toString().toLowerCase().equals("tif")
					|| cols[3].toString().toLowerCase().equals("tiff"))
				writer.print("<icon>picture</icon>");
			else if (cols[3].toString().toLowerCase().equals("zip"))
				writer.print("<icon>zip</icon>");
			else if (cols[3].toString().toLowerCase().equals("txt"))
				writer.print("<icon>text</icon>");
			else if (cols[3].toString().toLowerCase().equals("html") || cols[3].toString().toLowerCase().equals("htm"))
				writer.print("<icon>html</icon>");
			else
				writer.print("<icon>generic</icon>");
			writer.print("<title><![CDATA[" + cols[4] + "]]></title>");
			writer.print("<version>" + cols[5] + "</version>");
			writer.print("<lastModified>" + df.format(cols[6]) + "</lastModified>");
			writer.print("<published>" + df.format(cols[7]) + "</published>");
			writer.print("<publisher><![CDATA[" + cols[8] + "]]></publisher>");
			writer.print("<created>" + df.format(cols[9]) + "</created>");
			writer.print("<creator><![CDATA[" + cols[10] + "]]></creator>");
			writer.print("<size>" + cols[11] + "</size>");
			if (Integer.parseInt(cols[12].toString()) == 0)
				writer.print("<immutable>blank</immutable>");
			else if (Integer.parseInt(cols[12].toString()) == 1)
				writer.print("<immutable>stop</immutable>");
			if (Integer.parseInt(cols[13].toString()) == Constants.INDEX_TO_INDEX)
				writer.print("<indexed>blank</indexed>");
			else if (Integer.parseInt(cols[13].toString()) == Constants.INDEX_INDEXED)
				writer.print("<indexed>indexed</indexed>");
			else if (Integer.parseInt(cols[13].toString()) == Constants.INDEX_SKIP)
				writer.print("<indexed>unindexable</indexed>");
			if (cols[14] != null)
				writer.print("<locked>document_lock</locked>");
			else
				writer.print("<locked>blank</locked>");
			writer.print("<filename><![CDATA[" + cols[15] + "]]></filename>");
			writer.print("<status>" + cols[16] + "</status>");
			writer.print("</document>");
		}

		// For all alias document, we must retrieve the original documents infos
		for (Long docRef : docRefIds) {
			Document doc = dao.findById(docRef);
			writer.print("<document>");
			writer.print("<id>" + doc.getId() + "</id>");
			if (doc.getCustomId() != null)
				writer.print("<customId><![CDATA[" + doc.getCustomId() + "]]></customId>");
			else
				writer.print("<customId> </customId>");
			writer.print("<docref>" + docRef + "</docref>");
			writer.print("<icon>alias</icon>");
			writer.print("<title><![CDATA[" + doc.getTitle() + "]]></title>");
			writer.print("<version>" + doc.getVersion() + "</version>");
			writer.print("<lastModified>" + df.format(doc.getLastModified()) + "</lastModified>");
			writer.print("<published>" + df.format(doc.getDate()) + "</published>");
			writer.print("<publisher><![CDATA[" + doc.getPublisher() + "]]></publisher>");
			writer.print("<created>" + df.format(doc.getCreation()) + "</created>");
			writer.print("<creator><![CDATA[" + doc.getCreator() + "]]></creator>");
			writer.print("<size>" + doc.getFileSize() + "</size>");
			if (doc.getImmutable() == 0)
				writer.print("<immutable>blank</immutable>");
			else if (doc.getImmutable() == 1)
				writer.print("<immutable>stop</immutable>");
			if (doc.getIndexed() == Constants.INDEX_TO_INDEX)
				writer.print("<indexed>blank</indexed>");
			else if (doc.getIndexed() == Constants.INDEX_INDEXED)
				writer.print("<indexed>indexed</indexed>");
			else if (doc.getIndexed() == Constants.INDEX_SKIP)
				writer.print("<indexed>unindexable</indexed>");
			if (doc.getLockUserId() != null)
				writer.print("<locked>document_lock</locked>");
			else
				writer.print("<locked>blank</locked>");
			writer.print("<filename><![CDATA[" + doc.getFileName() + "]]></filename>");
			writer.print("<status>" + doc.getStatus() + "</status>");
			writer.print("</document>");
		}

		writer.write("</list>");
	}
}
