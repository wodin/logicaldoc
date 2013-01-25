package com.logicaldoc.web.data;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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

import com.logicaldoc.core.ExtendedAttribute;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.UserSession;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.core.util.IconSelector;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.i18n.I18N;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.LocaleUtil;
import com.logicaldoc.util.config.ContextProperties;
import com.logicaldoc.web.util.SessionUtil;

/**
 * This servlet is responsible for documents data.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class DocumentsDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(DocumentsDataServlet.class);

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		try {
			Context context = Context.getInstance();
			UserSession session = SessionUtil.validateSession(request);
			ContextProperties config = (ContextProperties) context.getBean(ContextProperties.class);
			UserDAO udao = (UserDAO) context.getBean(UserDAO.class);
			User user = udao.findById(session.getUserId());
			udao.initialize(user);

			String locale = request.getParameter("locale");
			if (StringUtils.isEmpty(locale))
				locale = user.getLanguage();

			response.setContentType("text/xml");
			response.setCharacterEncoding("UTF-8");

			// Headers required by Internet Explorer
			response.setHeader("Pragma", "public");
			response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
			response.setHeader("Expires", "0");

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
					writer.print("<filename>" + doc.getFileName() + "</filename>");
					writer.print("<fileVersion>" + doc.getFileVersion() + "</fileVersion>");
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
					writer.print("<version>" + doc.getVersion() + "</version>");
					writer.print("<filename>" + doc.getFileName() + "</filename>");
					writer.print("<fileVersion>" + doc.getFileVersion() + "</fileVersion>");
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

				/*
				 * Retrieve the names of the extended attributes to show
				 */
				String extattrs = config.getProperty("search.extattr");

				/*
				 * Contains the extended attributes of the documents inside the
				 * current folder. The key is documentId-atttributeName, the
				 * value is the attribute value
				 */
				final Map<String, String> extValues = new HashMap<String, String>();

				List<String> attrs = new ArrayList<String>();
				if (StringUtils.isNotEmpty(extattrs)) {
					log.debug("Search for extended attributes " + extattrs);

					attrs = Arrays.asList(extattrs.trim().split(","));

					StringBuffer query = new StringBuffer(
							"select ld_docid, ld_name, ld_type, ld_stringvalue, ld_intvalue, ld_doublevalue, ld_datevalue ");
					query.append(" from ld_document_ext where ld_docid in (");
					query.append("select D.ld_id from ld_document D where D.ld_deleted=0 ");
					if (folderId != null)
						query.append(" and D.ld_folderid=" + Long.toString(folderId));
					query.append(") and ld_name in ");
					query.append(attrs.toString().replaceAll("\\[", "('").replaceAll("\\]", "')")
							.replaceAll(",", "','").replaceAll(" ", ""));

					Locale l = LocaleUtil.toLocale(locale);
					final SimpleDateFormat edf = new SimpleDateFormat(I18N.message("format_dateshort", l));

					dao.query(query.toString(), null, new RowMapper<Long>() {
						@Override
						public Long mapRow(ResultSet rs, int row) throws SQLException {
							Long docId = rs.getLong(1);
							String name = rs.getString(2);
							int type = rs.getInt(3);

							String key = docId + "-" + name;

							if (type == ExtendedAttribute.TYPE_STRING) {
								extValues.put(key, rs.getString(4));
							} else if (type == ExtendedAttribute.TYPE_INT) {
								extValues.put(key, Long.toString(rs.getLong(5)));
							} else if (type == ExtendedAttribute.TYPE_DOUBLE) {
								extValues.put(key, Double.toString(rs.getDouble(6)));
							} else if (type == ExtendedAttribute.TYPE_DATE) {
								extValues.put(key, rs.getDate(7) != null ? edf.format(rs.getDate(7)) : "");
							}

							return null;
						}
					}, null);
				}

				/*
				 * Execute the Query
				 */
				StringBuffer query = new StringBuffer(
						"select A.id, A.customId, A.docRef, A.type, A.title, A.version, A.lastModified, A.date, A.publisher,"
								+ " A.creation, A.creator, A.fileSize, A.immutable, A.indexed, A.lockUserId, A.fileName, A.status,"
								+ " A.signed, A.type, A.sourceDate, A.sourceAuthor, A.rating, A.fileVersion, A.comment, A.workflowStatus, A.startPublishing, A.stopPublishing, A.published "
								+ " from Document A ");
				query.append(" where A.deleted = 0 ");
				if (folderId != null)
					query.append(" and A.folder.id=" + folderId);
				if (StringUtils.isNotEmpty(request.getParameter("indexed")))
					query.append(" and A.indexed=" + request.getParameter("indexed"));

				if (filename != null)
					query.append(" and lower(A.fileName) like '%" + filename.toLowerCase() + "%' ");
				query.append(" order by A.lastModified desc");

				List<Object> records = (List<Object>) dao.findByQuery(query.toString(), null, max);

				List<Long> docRefIds = new ArrayList<Long>();
				/*
				 * Iterate over records composing the response XML document
				 */
				for (Object record : records) {
					Object[] cols = (Object[]) record;

					boolean published = isPublished((Integer) cols[27], (Date) cols[25], (Date) cols[26]);

					if (!published && !user.isInGroup("admin") && !user.isInGroup("publisher")) {
						continue;
					}

					if (cols[2] != null) {
						if (cols[2].toString().equals("0")) {
							cols[2] = null;
						} else {
							docRefIds.add((Long) cols[0]);
							continue;
						}
					}

					writer.print("<document>");
					writer.print("<id>" + cols[0] + "</id>");
					if (cols[1] != null)
						writer.print("<customId><![CDATA[" + cols[1] + "]]></customId>");
					else
						writer.print("<customId> </customId>");
					writer.print("<docref>" + (cols[2] != null ? cols[2] : "") + "</docref>");
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
						writer.print("<locked>lock</locked>");
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
						writer.print("<signed>rosette</signed>");

					writer.print("<type>" + cols[18] + "</type>");

					if (cols[19] != null)
						writer.print("<sourceDate>" + (cols[19] != null ? df.format(cols[19]) : "") + "</sourceDate>");

					if (cols[20] != null)
						writer.print("<sourceAuthor><![CDATA[" + cols[20] + "]]></sourceAuthor>");

					if (cols[21] == null)
						writer.print("<rating>rating0</rating>");
					else
						writer.print("<rating>rating" + cols[21] + "</rating>");

					writer.print("<fileVersion><![CDATA[" + cols[22] + "]]></fileVersion>");

					if (cols[23] == null)
						writer.print("<comment></comment>");
					else
						writer.print("<comment><![CDATA[" + cols[23] + "]]></comment>");

					if (cols[24] == null)
						writer.print("<workflowStatus></workflowStatus>");
					else
						writer.print("<workflowStatus><![CDATA[" + cols[24] + "]]></workflowStatus>");

					writer.print("<startPublishing>" + df.format(cols[25]) + "</startPublishing>");
					if (cols[26] != null)
						writer.print("<stopPublishing>" + df.format(cols[26]) + "</stopPublishing>");
					else
						writer.print("<stopPublishing></stopPublishing>");
					writer.print("<publishedStatus>" + (published ? "yes" : "no") + "</publishedStatus>");

					if (!extValues.isEmpty())
						for (String name : attrs) {
							String val = extValues.get(cols[0] + "-" + name);
							if (val != null)
								writer.print("<ext_" + name + "><![CDATA[" + val + "]]></ext_" + name + ">");
						}

					writer.print("</document>");
				}

				// For all alias document, we must retrieve the original
				// documents infos
				for (Long id : docRefIds) {
					Document aliasDoc = dao.findById(id);
					Document doc = dao.findById(aliasDoc.getDocRef());

					boolean published = isPublished(doc.getPublished(), doc.getStartPublishing(),
							doc.getStopPublishing());

					if (!published && !user.isInGroup("admin") && !user.isInGroup("publisher")) {
						continue;
					}

					writer.print("<document>");
					writer.print("<id>" + id + "</id>");
					if (doc.getCustomId() != null)
						writer.print("<customId><![CDATA[" + doc.getCustomId() + "]]></customId>");
					else
						writer.print("<customId> </customId>");
					writer.print("<docref>" + doc.getId() + "</docref>");
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
						writer.print("<locked>lock</locked>");
					else if (doc.getStatus() == Constants.DOC_CHECKED_OUT)
						writer.print("<locked>page_edit</locked>");
					else
						writer.print("<locked>blank</locked>");
					if (doc.getLockUserId() != null)
						writer.print("<lockUserId>" + doc.getLockUserId() + "</lockUserId>");
					writer.print("<filename><![CDATA[" + doc.getFileName() + "]]></filename>");
					writer.print("<status>" + doc.getStatus() + "</status>");
					if (doc.getSigned() == 0)
						writer.print("<signed>blank</signed>");
					else if (doc.getSigned() == 1)
						writer.print("<signed>rosette</signed>");

					writer.print("<aliasId>" + id + "</aliasId>");

					writer.print("<sourceDate>" + (doc.getSourceDate() != null ? df.format(doc.getSourceDate()) : "")
							+ "</sourceDate>");
					if (doc.getRating() == null)
						writer.print("<rating>rating0</rating>");
					else
						writer.print("<rating>rating" + doc.getRating() + "</rating>");
					writer.print("<fileVersion><![CDATA[" + doc.getFileVersion() + "]]></fileVersion>");
					if (doc.getComment() == null)
						writer.print("<comment></comment>");
					else
						writer.print("<comment><![CDATA[" + doc.getComment() + "]]></comment>");
					if (doc.getWorkflowStatus() == null)
						writer.print("<workflowStatus></workflowStatus>");
					else
						writer.print("<workflowStatus><![CDATA[" + doc.getWorkflowStatus() + "]]></workflowStatus>");

					writer.print("<startPublishing>" + df.format(doc.getStartPublishing()) + "</startPublishing>");
					if (doc.getStopPublishing() != null)
						writer.print("<stopPublishing>" + df.format(doc.getStopPublishing()) + "</stopPublishing>");
					else
						writer.print("<stopPublishing></stopPublishing>");
					writer.print("<publishedStatus>" + (doc.isPublishing() ? "yes" : "no") + "</publishedStatus>");

					if (!extValues.isEmpty())
						for (String name : attrs) {
							String val = extValues.get(doc.getId() + "-" + name);
							if (val != null)
								writer.print("<ext_" + name + "><![CDATA[" + val + "]]></ext_" + name + ">");
						}

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

	protected static boolean isPublished(int published, Date startPublishing, Date stopPublishing) {
		Date now = new Date();
		if (published != 1)
			return false;
		else if (now.before(startPublishing))
			return false;
		else if (stopPublishing == null)
			return true;
		else
			return now.before(stopPublishing);
	}
}
