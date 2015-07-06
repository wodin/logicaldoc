package com.logicaldoc.web.data;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import com.ibm.icu.util.StringTokenizer;
import com.logicaldoc.core.ExtendedAttribute;
import com.logicaldoc.core.document.AbstractDocument;
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
import com.logicaldoc.web.util.ServiceUtil;

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
			UserSession session = ServiceUtil.validateSession(request);
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

			int max = 100;
			if (StringUtils.isNotEmpty(request.getParameter("max")))
				max = Integer.parseInt(request.getParameter("max"));

			int page = 1;
			if (StringUtils.isNotEmpty(request.getParameter("page")))
				page = Integer.parseInt(request.getParameter("page"));

			Integer status = null;
			if (StringUtils.isNotEmpty(request.getParameter("status")))
				status = Integer.parseInt(request.getParameter("status"));

			PrintWriter writer = response.getWriter();
			writer.write("<list>");

			if (status != null && status.intValue() != AbstractDocument.DOC_ARCHIVED) {
				List<Document> docs = dao.findByLockUserAndStatus(session.getUserId(), status);
				int begin = (page - 1) * max;
				int end = Math.min(begin + max - 1, docs.size() - 1);
				for (int i = begin; i <= end; i++) {
					Document doc = docs.get(i);
					writer.print("<document>");
					writer.print("<id>" + doc.getId() + "</id>");
					writer.print("<icon>" + FilenameUtils.getBaseName(IconSelector.selectIcon(doc.getFileExtension()))
							+ "</icon>");
					writer.print("<title><![CDATA[" + doc.getTitle() + "]]></title>");
					writer.print("<lastModified>" + df.format(doc.getLastModified()) + "</lastModified>");
					writer.print("<folderId>" + doc.getFolder().getId() + "</folderId>");
					writer.print("<version>" + doc.getVersion() + "</version>");
					writer.print("<filename><![CDATA[" + doc.getFileName() + "]]></filename>");
					writer.print("<fileVersion>" + doc.getFileVersion() + "</fileVersion>");
					writer.print("</document>");
				}
			} else if (StringUtils.isNotEmpty(request.getParameter("docIds"))) {
				String[] idsArray = request.getParameter("docIds").split(",");
				for (String id : idsArray) {
					Document doc = dao.findById(Long.parseLong(id));
					if (doc == null || doc.getDeleted() == 1)
						continue;
					writer.print("<document>");
					writer.print("<id>" + doc.getId() + "</id>");
					writer.print("<icon>" + FilenameUtils.getBaseName(IconSelector.selectIcon(doc.getFileExtension()))
							+ "</icon>");
					writer.print("<title><![CDATA[" + doc.getTitle() + "]]></title>");
					writer.print("<lastModified>" + df.format(doc.getLastModified()) + "</lastModified>");
					writer.print("<folderId>" + doc.getFolder().getId() + "</folderId>");
					writer.print("<version>" + doc.getVersion() + "</version>");
					writer.print("<filename><![CDATA[" + doc.getFileName() + "]]></filename>");
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
				String extattrs = config.getProperty(session.getTenantName() + ".search.extattr");

				/*
				 * Contains the extended attributes of the documents inside the
				 * current folder. The key is documentId-atttributeName, the
				 * value is the attribute value
				 */
				final Map<String, String> extValues = new HashMap<String, String>();

				List<String> attrs = new ArrayList<String>();
				if (StringUtils.isNotEmpty(extattrs)) {
					log.debug("Search for extended attributes " + extattrs);

					StringTokenizer st = new StringTokenizer(extattrs.trim(), ",;");
					while (st.hasMoreElements())
						attrs.add(st.nextToken().trim());

					StringBuffer query = new StringBuffer(
							"select ld_docid, ld_name, ld_type, ld_stringvalue, ld_intvalue, ld_doublevalue, ld_datevalue ");
					query.append(" from ld_document_ext where ld_docid in (");
					query.append("select D.ld_id from ld_document D where D.ld_deleted=0 ");
					if (folderId != null)
						query.append(" and D.ld_folderid=" + Long.toString(folderId));
					query.append(") and ld_name in ");
					query.append(attrs.toString().replaceAll("\\[", "('").replaceAll("\\]", "')")
							.replaceAll(",", "','").replaceAll(" ", ""));

					final Locale l = LocaleUtil.toLocale(locale);
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
							} else if (type == ExtendedAttribute.TYPE_USER) {
								extValues.put(key, rs.getString(4));
							} else if (type == ExtendedAttribute.TYPE_BOOLEAN) {
								extValues.put(key,
										rs.getLong(5) == 1L ? I18N.message("true", l) : I18N.message("false", l));
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
								+ " A.signed, A.type, A.sourceDate, A.sourceAuthor, A.rating, A.fileVersion, A.comment, A.workflowStatus,"
								+ " A.startPublishing, A.stopPublishing, A.published, A.extResId, A.source, A.sourceId, A.recipient,"
								+ " A.object, A.coverage, B.name, A.docRefType, A.stamped "
								+ " from Document as A left outer join A.template as B ");
				query.append(" where A.deleted = 0 and not A.status=" + AbstractDocument.DOC_ARCHIVED);
				if (folderId != null)
					query.append(" and A.folder.id=" + folderId);
				if (StringUtils.isNotEmpty(request.getParameter("indexed")))
					query.append(" and A.indexed=" + request.getParameter("indexed"));

				if (filename != null)
					query.append(" and lower(A.fileName) like '%" + filename.toLowerCase() + "%' ");
				query.append(" order by A.lastModified desc");

				List<Object> records = (List<Object>) dao.findByQuery(query.toString(), null, null);
				List<Document> documents = new ArrayList<Document>();

				int begin = (page - 1) * max;
				int end = Math.min(begin + max - 1, records.size() - 1);

				/*
				 * Iterate over records composing the response XML document
				 */
				for (int i = begin; i <= end; i++) {
					Object[] cols = (Object[]) records.get(i);

					Document doc = new Document();
					doc.setId((Long) cols[0]);
					doc.setDocRef((Long) cols[2]);
					doc.setDocRefType((String) cols[35]);

					// Replace with the real document if this is an alias
					if (doc.getDocRef() != null && doc.getDocRef().longValue() != 0L) {
						long aliasId = doc.getId();
						long aliasDocRef = doc.getDocRef();
						String aliasDocRefType = doc.getDocRefType();
						doc = dao.findById(aliasDocRef);
						if(doc!=null){
							doc.setId(aliasId);
							doc.setDocRef(aliasDocRef);
							doc.setDocRefType(aliasDocRefType);
						}else
							continue;
					} else {
						doc.setStartPublishing((Date) cols[25]);
						doc.setStopPublishing((Date) cols[26]);
						doc.setPublished((Integer) cols[27]);

						if (doc.isPublishing() || user.isInGroup("admin") || user.isInGroup("publisher")) {
							doc.setCustomId((String) cols[1]);
							doc.setTitle((String) cols[4]);
							doc.setVersion((String) cols[5]);
							doc.setLastModified((Date) cols[6]);
							doc.setDate((Date) cols[7]);
							doc.setPublisher((String) cols[8]);
							doc.setCreation((Date) cols[9]);
							doc.setCreator((String) cols[10]);
							doc.setFileSize((Long) cols[11]);
							doc.setImmutable((Integer) cols[12]);
							doc.setIndexed((Integer) cols[13]);
							doc.setLockUserId((Long) cols[14]);
							doc.setFileName((String) cols[15]);
							doc.setStatus((Integer) cols[16]);
							doc.setSigned((Integer) cols[17]);
							doc.setType((String) cols[18]);
							doc.setSourceDate((Date) cols[19]);
							doc.setSourceAuthor((String) cols[20]);
							doc.setRating((Integer) cols[21]);
							doc.setFileVersion((String) cols[22]);
							doc.setComment((String) cols[23]);
							doc.setWorkflowStatus((String) cols[24]);
							doc.setExtResId((String) cols[28]);
							doc.setSource((String) cols[29]);
							doc.setSourceId((String) cols[30]);
							doc.setRecipient((String) cols[31]);
							doc.setObject((String) cols[32]);
							doc.setCoverage((String) cols[33]);
							doc.setTemplateName((String) cols[34]);
							doc.setStamped((Integer) cols[36]);
						}
					}

					if (doc.isPublishing() || user.isInGroup("admin") || user.isInGroup("publisher"))
						documents.add(doc);
				}

				for (Document doc : documents) {
					writer.print("<document>");
					writer.print("<id>" + doc.getId() + "</id>");
					writer.print("<customId><![CDATA[" + (doc.getCustomId() != null ? doc.getCustomId() : "")
							+ "]]></customId>");
					if (doc.getDocRef() != null) {
						writer.print("<docref>" + doc.getDocRef() + "</docref>");
						if (doc.getDocRefType() != null)
							writer.print("<docrefType>" + doc.getDocRefType() + "</docrefType>");
					}

					if ("pdf".equals(doc.getDocRefType()))
						writer.print("<icon>" + FilenameUtils.getBaseName(IconSelector.selectIcon("pdf")) + "</icon>");
					else
						writer.print("<icon>" + FilenameUtils.getBaseName(IconSelector.selectIcon(doc.getType()))
								+ "</icon>");
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
					writer.print("<type><![CDATA[" + doc.getType() + "]]></type>");
					writer.print("<status>" + doc.getStatus() + "</status>");
					if (doc.getSigned() == 0)
						writer.print("<signed>blank</signed>");
					else if (doc.getSigned() == 1)
						writer.print("<signed>rosette</signed>");
					if (doc.getStamped() == 0)
						writer.print("<stamped>blank</stamped>");
					else if (doc.getStamped() == 1)
						writer.print("<stamped>stamp</stamped>");
					
					writer.print("<sourceDate>" + (doc.getSourceDate() != null ? df.format(doc.getSourceDate()) : "")
							+ "</sourceDate>");
					writer.print("<rating>rating" + (doc.getRating() != null ? doc.getRating() : "0") + "</rating>");
					writer.print("<fileVersion><![CDATA[" + doc.getFileVersion() + "]]></fileVersion>");
					writer.print("<comment><![CDATA[" + (doc.getComment() != null ? doc.getComment() : "")
							+ "]]></comment>");
					writer.print("<workflowStatus><![CDATA["
							+ (doc.getWorkflowStatus() != null ? doc.getWorkflowStatus() : "") + "]]></workflowStatus>");
					writer.print("<startPublishing>" + df.format(doc.getStartPublishing()) + "</startPublishing>");
					if (doc.getStopPublishing() != null)
						writer.print("<stopPublishing>" + df.format(doc.getStopPublishing()) + "</stopPublishing>");
					else
						writer.print("<stopPublishing></stopPublishing>");
					writer.print("<publishedStatus>" + (doc.isPublishing() ? "yes" : "no") + "</publishedStatus>");

					if (doc.getExtResId() != null)
						writer.print("<extResId><![CDATA[" + doc.getExtResId() + "]]></extResId>");

					if (doc.getSource() != null)
						writer.print("<source><![CDATA[" + doc.getSource() + "]]></source>");

					if (doc.getSourceId() != null)
						writer.print("<sourceId><![CDATA[" + doc.getSourceId() + "]]></sourceId>");

					if (doc.getRecipient() != null)
						writer.print("<recipient><![CDATA[" + doc.getRecipient() + "]]></recipient>");

					if (doc.getObject() != null)
						writer.print("<object><![CDATA[" + doc.getObject() + "]]></object>");

					if (doc.getCoverage() != null)
						writer.print("<coverage><![CDATA[" + doc.getCoverage() + "]]></coverage>");

					if (doc.getTemplateName() != null)
						writer.print("<template><![CDATA[" + doc.getTemplateName() + "]]></template>");

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
}