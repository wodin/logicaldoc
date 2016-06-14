package com.logicaldoc.web;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.Version;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.document.dao.VersionDAO;
import com.logicaldoc.core.document.thumbnail.ThumbnailManager;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.Session;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.core.store.Storer;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.util.ServiceUtil;
import com.logicaldoc.web.util.ServletUtil;

/**
 * This servlet is responsible for document thumbnail. It searches for the
 * attribute docId in any scope and extracts the proper document's content. You
 * may specify the suffix to download the thumbnail or the tile.
 * 
 * @author Alessandro Gasparini - Logical Objects
 * @since 4.5
 */
public class ThumbnailServlet extends HttpServlet {

	/** Format can be tile.jpg, thumb.jpg or convert.pdf */
	protected static final String SUFFIX = "suffix";

	public static final String DOC_ID = "docId";

	private static final String FILE_VERSION = "fileVersion";

	private static final String VERSION = "version";

	private static final long serialVersionUID = -6956612970433309888L;

	protected static Logger log = LoggerFactory.getLogger(ThumbnailServlet.class);

	/**
	 * Constructor of the object.
	 */
	public ThumbnailServlet() {
		super();
	}

	/**
	 * The doGet method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String id = request.getParameter(DOC_ID);
		String fileVersion = request.getParameter(FILE_VERSION);
		String version = request.getParameter(VERSION);
		String suffix = request.getParameter(SUFFIX);

		try {
			Storer storer = (Storer) Context.get().getBean(Storer.class);

			// 1) check if the document exists
			long docId = Long.parseLong(id);
			if (StringUtils.isEmpty(suffix))
				suffix = "thumb.jpg";
			DocumentDAO docDao = (DocumentDAO) Context.get().getBean(DocumentDAO.class);
			Document doc = docDao.findById(docId);
			if (doc.getDocRef() != null) {
				doc = docDao.findById(doc.getDocRef());
				docId = doc.getId();
			}

			if (StringUtils.isEmpty(fileVersion))
				fileVersion = doc.getFileVersion();

			if (version != null) {
				VersionDAO vDao = (VersionDAO) Context.get().getBean(VersionDAO.class);
				Version ver = vDao.findByVersion(docId, version);
				if (ver != null)
					fileVersion = ver.getFileVersion();
			}

			Session session = ServiceUtil.validateSession(request);
			User user = session.getUser();

			if (doc != null && !user.isInGroup("admin") && !user.isInGroup("publisher") && !doc.isPublishing())
				throw new FileNotFoundException("Document not published");

			String resource = storer.getResourceName(docId, fileVersion, suffix);

			// 2) the thumbnail doesn't exist, create it
			if (!storer.exists(docId, resource)) {
				log.debug("Need for preview creation");
				createPreviewResource(session.getId(), doc, fileVersion, resource);
			}

			// 3) return the the thumbnail resource
			ServletUtil.downloadDocument(request, response, session.getId(), docId, fileVersion,
					storer.getResourceName(doc, fileVersion, suffix), suffix, user);
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			new IOException(t.getMessage());
		}
	}

	/**
	 * Creates the preview resource according to the specified format storing it
	 * in the repository for future access.
	 */
	protected void createPreviewResource(String sid, Document doc, String fileVersion, String resource) {
		Storer storer = (Storer) Context.get().getBean(Storer.class);
		ThumbnailManager thumbManager = (ThumbnailManager) Context.get().getBean(ThumbnailManager.class);

		// In any case try to produce the thumbnail
		String thumbResource = storer.getResourceName(doc, fileVersion, "thumb.jpg");
		if (!storer.exists(doc.getId(), thumbResource)) {
			try {
				thumbManager.createTumbnail(doc, fileVersion, sid);
				log.debug("Created thumbnail " + resource);
			} catch (Throwable t) {
				log.error(t.getMessage(), t);
			}
		}

		if (resource.endsWith("thumb.jpg"))
			return;

		if (resource.endsWith("tile.jpg")) {
			String tileResource = storer.getResourceName(doc, fileVersion, "tile.jpg");
			if (!storer.exists(doc.getId(), tileResource)) {
				try {
					thumbManager.createTile(doc, fileVersion, sid);
					log.debug("Created tile " + resource);
				} catch (Throwable t) {
					log.error(t.getMessage(), t);
				}
			}
		}

		if (resource.endsWith("tile.jpg"))
			return;
	}
}