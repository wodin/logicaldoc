package com.logicaldoc.web.data;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.security.UserSession;
import com.logicaldoc.core.util.IconSelector;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.util.SessionUtil;

/**
 * This servlet is responsible for documents data.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class DocumentsDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static Log log = LogFactory.getLog(DocumentsDataServlet.class);

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

			Context context = Context.getInstance();
			DocumentDAO dao = (DocumentDAO) context.getBean(DocumentDAO.class);
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			df.setTimeZone(TimeZone.getTimeZone("UTC"));

			int max = 0;
			if (StringUtils.isNotEmpty(request.getParameter("max")))
				max = Integer.parseInt(request.getParameter("max"));

			PrintWriter writer = response.getWriter();
			writer.write("<list>");

			if (StringUtils.isNotEmpty(request.getParameter("status"))) {
				int status = Integer.parseInt(request.getParameter("status"));
				List<Document> docs = dao.findByLockUserAndStatus(session.getUserId(), status);
				int i = 0;
				for (Document doc : docs) {
					if (i == max)
						break;
					writer.print("<document>");
					writer.print("<id>" + doc.getId() + "</id>");
					writer.print("<icon>" + FilenameUtils.getBaseName(IconSelector.selectIcon(doc.getFileExtension()))
							+ "</icon>");
					writer.print("<title><![CDATA[" + doc.getTitle() + "]]></title>");
					writer.print("<lastModified>" + df.format(doc.getLastModified()) + "</lastModified>");
					writer.print("<folderId>" + doc.getFolder().getId() + "</folderId>");
					writer.print("<version>" + doc.getVersion() + "</version>");
					writer.print("</document>");
					i++;
				}
			} else if (StringUtils.isNotEmpty(request.getParameter("docIds"))) {
				String[] idsArray = request.getParameter("docIds").split(",");
				for (String id : idsArray) {
					Document doc = dao.findById(Long.parseLong(id));
					writer.print("<document>");
					writer.print("<id>" + doc.getId() + "</id>");
					writer.print("<icon>" + FilenameUtils.getBaseName(IconSelector.selectIcon(doc.getFileExtension()))
							+ "</icon>");
					writer.print("<title><![CDATA[" + doc.getTitle() + "]]></title>");
					writer.print("<lastModified>" + df.format(doc.getLastModified()) + "</lastModified>");
					writer.print("<folderId>" + doc.getFolder().getId() + "</folderId>");
					writer.print("</document>");
				}
			} else {
				/*
				 * Load some filters from the current request
				 */
				Long folderId = null;
				if (StringUtils.isNotEmpty(request.getParameter("folderId")))
					folderId = new Long(request.getParameter("folderId"));

				String filename = null;
				if (StringUtils.isNotEmpty(request.getParameter("filename")))
					filename = request.getParameter("filename");

				Boolean indexable = null;
				if (StringUtils.isNotEmpty(request.getParameter("indexable")))
					indexable = new Boolean(request.getParameter("indexable"));

				/*
				 * Execute the Query
				 */
				StringBuffer query = new StringBuffer(
						"select A.id, A.customId, A.docRef, A.type, A.title, A.version, A.lastModified, A.date, A.publisher,"
								+ " A.creation, A.creator, A.fileSize, A.immutable, A.indexed, A.lockUserId, A.fileName, A.status, A.signed "
								+ "from Document A where A.deleted = 0 ");
				if (folderId != null)
					query.append(" and A.folder.id=" + folderId);
				if (indexable != null)
					if (indexable == false)
						query.append(" and not(A.indexed=2) ");
					else
						query.append(" and (A.indexed=2) ");
				if (filename != null)
					query.append(" and lower(A.fileName) like '%" + filename.toLowerCase() + "%' ");
				query.append("order by A.lastModified desc");

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
					else {
						writer.print("<icon>" + FilenameUtils.getBaseName(IconSelector.selectIcon((String) cols[3]))
								+ "</icon>");
					}
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
					if (Integer.parseInt(cols[16].toString()) == Constants.DOC_LOCKED)
						writer.print("<locked>document_lock</locked>");
					else if (Integer.parseInt(cols[16].toString()) == Constants.DOC_CHECKED_OUT)
						writer.print("<locked>page_edit</locked>");
					else
						writer.print("<locked>blank</locked>");
					if (cols[14] != null)
						writer.print("<lockUserId>" + cols[14] + "</lockUserId>");
					writer.print("<filename><![CDATA[" + cols[15] + "]]></filename>");
					writer.print("<status>" + cols[16] + "</status>");

					if (Integer.parseInt(cols[17].toString()) == 0)
						writer.print("<signed>blank</signed>");
					else if (Integer.parseInt(cols[17].toString()) == 1)
						writer.print("<signed>sign</signed>");

					writer.print("</document>");
				}

				// For all alias document, we must retrieve the original
				// documents
				// infos
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
					if (doc.getStatus() == Constants.DOC_LOCKED)
						writer.print("<locked>document_lock</locked>");
					else if (doc.getStatus() == Constants.DOC_CHECKED_OUT)
						writer.print("<locked>page_edit</locked>");
					else
						writer.print("<locked>blank</locked>");
					if (doc.getLockUserId() != null)
						writer.print("<lockUserId>" + doc.getLockUserId() + "</lockUserId>");
					writer.print("<filename><![CDATA[" + doc.getFileName() + "]]></filename>");
					writer.print("<status>" + doc.getStatus() + "</status>");
					writer.print("</document>");
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
