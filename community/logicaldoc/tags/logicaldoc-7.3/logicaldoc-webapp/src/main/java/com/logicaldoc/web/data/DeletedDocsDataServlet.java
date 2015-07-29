package com.logicaldoc.web.data;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

import com.logicaldoc.core.document.AbstractDocument;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.security.Folder;
import com.logicaldoc.core.security.UserSession;
import com.logicaldoc.core.util.IconSelector;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.util.ServiceUtil;

/**
 * This servlet is responsible for deleted documents data retrieval
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 7.2.1
 */
public class DeletedDocsDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(DeletedDocsDataServlet.class);

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		try {
			final UserSession session = ServiceUtil.validateSession(request);

			Long folderId = request.getParameter("folderId") != null ? Long.parseLong(request.getParameter("folderId"))
					: null;

			Long deleteUserId = StringUtils.isNotEmpty(request.getParameter("userId")) ? Long.parseLong(request.getParameter("userId"))
					: null;

			Integer max = request.getParameter("max") != null ? Integer.parseInt(request.getParameter("max")) : null;

			response.setContentType("text/xml");
			response.setCharacterEncoding("UTF-8");

			// Headers required by Internet Explorer
			response.setHeader("Pragma", "public");
			response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
			response.setHeader("Expires", "0");

			DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			df.setTimeZone(TimeZone.getTimeZone("UTC"));

			PrintWriter writer = response.getWriter();
			writer.write("<list>");

			StringBuffer query = new StringBuffer(
					"select A.ld_id, A.ld_customid, A.ld_type, A.ld_title, A.ld_version, A.ld_fileversion, A.ld_lastmodified, ");
			query.append(" A.ld_publisher, A.ld_filesize, A.ld_filename, A.ld_folderid, B.ld_name, A.ld_creation, A.ld_deleteuserid ");
			query.append(" from ld_document A ");
			query.append(" left outer join ld_folder B on A.ld_folderid=B.ld_id ");
			query.append(" where A.ld_tenantid=");
			query.append(Long.toString(session.getTenantId()));
			query.append(" and A.ld_docref is null ");
			query.append(" and A.ld_deleted > 0 ");

			if (deleteUserId != null) {
				query.append(" and A.ld_deleteuserId=");
				query.append(Long.toString(deleteUserId));
			}

			if (folderId != null) {
				query.append(" and A.ld_folderId=");
				query.append(Long.toString(folderId));
			}

			query.append(" order by A.ld_creation desc ");

			log.error(query.toString());
			
			@SuppressWarnings("unchecked")
			List<Document> records = (List<Document>) docDao.query(query.toString(), null, new RowMapper<Document>() {
				public Document mapRow(ResultSet rs, int rowNum) throws SQLException {
					Document doc = new Document();
					doc.setTenantId(session.getTenantId());
					doc.setId(rs.getLong(1));
					doc.setCustomId(rs.getString(2));
					doc.setType(rs.getString(3));
					doc.setTitle(rs.getString(4));
					doc.setVersion(rs.getString(5));
					doc.setFileVersion(rs.getString(6));
					doc.setLastModified(new Date(rs.getTimestamp(7).getTime()));
					doc.setPublisher(rs.getString(8));
					doc.setFileSize(rs.getLong(9));
					doc.setFileName(rs.getString(10));
					Folder folder = new Folder();
					folder.setId(rs.getLong(11));
					folder.setName(rs.getString(12));
					folder.setTenantId(session.getTenantId());
					doc.setFolder(folder);

					doc.setCreation(new Date(rs.getTimestamp(13).getTime()));
					doc.setDeleteUserId(rs.getLong(14));

					return doc;
				}
			}, max);
			
			/*
			 * Iterate over records composing the response XML document
			 */
			for (Document doc : records) {
				writer.print("<document>");
				writer.print("<id>" + doc.getId() + "</id>");
				if (doc.getCustomId() != null)
					writer.print("<customId><![CDATA[" + doc.getCustomId() + "]]></customId>");
				else
					writer.print("<customId> </customId>");
				writer.print("<icon>" + FilenameUtils.getBaseName(IconSelector.selectIcon(doc.getType())) + "</icon>");
				writer.print("<title><![CDATA[" + doc.getTitle() + "]]></title>");
				writer.print("<version>" + doc.getVersion() + "</version>");
				writer.print("<fileVersion>" + doc.getFileVersion() + "</fileVersion>");
				writer.print("<lastModified>" + df.format(doc.getLastModified()) + "</lastModified>");
				writer.print("<created>" + df.format(doc.getCreation()) + "</created>");
				writer.print("<size>" + doc.getFileSize() + "</size>");
				writer.print("<filename><![CDATA[" + doc.getFileName() + "]]></filename>");
				if (doc.getImmutable() == 0)
					writer.print("<immutable>blank</immutable>");
				else if (doc.getImmutable() == 1)
					writer.print("<immutable>stop</immutable>");
				writer.print("<deleteUserId>" + doc.getDeleteUserId() + "</deleteUserId>");
				writer.print("<folderId>" + doc.getFolder().getId() + "</folderId>");
				writer.print("<folder>" + doc.getFolder().getName() + "</folder>");
				writer.print("<type>" + doc.getType() + "</type>");
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
