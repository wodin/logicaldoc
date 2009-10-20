package com.logicaldoc.core.document.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;

import com.logicaldoc.core.HibernatePersistentObjectDAO;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.History;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.MenuDAO;

/**
 * Hibernate implementation of <code>HistoryDAO</code>
 * 
 * @author Alessandro Gasparini - Logical Objects
 * @since 3.0
 */
public class HibernateHistoryDAO extends HibernatePersistentObjectDAO<History> implements HistoryDAO {

	private MenuDAO menuDAO;

	public void setMenuDAO(MenuDAO menuDAO) {
		this.menuDAO = menuDAO;
	}

	private HibernateHistoryDAO() {
		super(History.class);
		super.log = LogFactory.getLog(HibernateHistoryDAO.class);
	}

	/**
	 * @see com.logicaldoc.core.document.dao.HistoryDAO#findByDocId(int)
	 */
	@SuppressWarnings("unchecked")
	public List<History> findByDocId(long docId) {
		List<History> coll = new ArrayList<History>();

		try {
			DetachedCriteria dt = DetachedCriteria.forClass(History.class);
			dt.add(Property.forName("docId").eq(new Long(docId)));
			dt.addOrder(Order.asc("date"));
			coll = (List<History>) getHibernateTemplate().findByCriteria(dt);
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}

		return coll;
	}

	/**
	 * @see com.logicaldoc.core.document.dao.HistoryDAO#findByUsername(long)
	 */
	@SuppressWarnings("unchecked")
	public List<History> findByUserId(long userId) {
		List<History> coll = new ArrayList<History>();

		try {
			DetachedCriteria dt = DetachedCriteria.forClass(History.class);
			dt.add(Property.forName("userId").eq(userId));
			dt.addOrder(Order.asc("date"));
			coll = (List<History>) getHibernateTemplate().findByCriteria(dt);
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage());
		}

		return coll;
	}

	/**
	 * @see com.logicaldoc.core.document.dao.HistoryDAO#findByFolderId(long)
	 */
	@SuppressWarnings("unchecked")
	public List<History> findByFolderId(long folderId) {
		List<History> coll = new ArrayList<History>();

		try {
			DetachedCriteria dt = DetachedCriteria.forClass(History.class);
			dt.add(Property.forName("folderId").eq(new Long(folderId)));
			dt.addOrder(Order.asc("date"));
			coll = (List<History>) getHibernateTemplate().findByCriteria(dt);
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}

		return coll;
	}

	@Override
	public void createDocumentHistory(Document doc, User user, String eventType, String comment) {
		History history = new History();
		history.setDocId(doc.getId());
		history.setFolderId(doc.getFolder().getId());
		history.setTitle(doc.getTitle());
		history.setVersion(doc.getVersion());

		history.setPath(doc.getFolder().getPathExtended() + "/" + doc.getFolder().getText());
		history.setPath(history.getPath().replaceAll("//", "/"));
		history.setPath(history.getPath().replaceFirst("/menu.documents/", "/"));
		history.setPath(history.getPath().replaceFirst("/menu.documents", "/"));

		history.setDate(new Date());
		history.setUserId(user.getId());
		history.setUserName(user.getFullName());
		history.setEvent(eventType);
		history.setComment(comment);

		store(history);
	}

	@Override
	public void createFolderHistory(Menu folder, User user, String eventType, String comment) {
		History history = new History();
		history.setFolderId(folder.getId());
		history.setTitle(folder.getText());

		history.setPath(folder.getPathExtended() + "/" + folder.getText());
		history.setPath(history.getPath().replaceAll("//", "/"));
		history.setPath(history.getPath().replaceFirst("/menu.documents/", "/"));
		history.setPath(history.getPath().replaceFirst("/menu.documents", "/"));

		Date date = new Date();
		history.setDate(date);
		history.setUserId(user.getId());
		history.setUserName(user.getFullName());
		history.setEvent(eventType);
		history.setComment(comment);

		store(history);

		// Check if is necessary to add a new history entry for the parent
		// folder. This operation is not recursive, because we want to notify
		// only the parent folder.
		if (folder.getId() != folder.getParentId()) {
			Menu parent = menuDAO.findById(folder.getParentId());
			History parentHistory = new History();
			parentHistory.setFolderId(parent.getId());
			parentHistory.setTitle(parent.getText());

			parentHistory.setPath(parent.getPathExtended() + "/" + parent.getText() + "/" + folder.getText());
			parentHistory.setPath(parentHistory.getPath().replaceAll("//", "/"));
			parentHistory.setPath(parentHistory.getPath().replaceFirst("/menu.documents/", "/"));
			parentHistory.setPath(parentHistory.getPath().replaceFirst("/menu.documents", "/"));

			parentHistory.setDate(date);
			parentHistory.setUserId(user.getId());
			parentHistory.setUserName(user.getFullName());
			if (eventType.equals(History.EVENT_FOLDER_CREATED)) {
				parentHistory.setEvent(History.EVENT_FOLDER_SUBFOLDER_CREATED);
			} else if (eventType.equals(History.EVENT_FOLDER_RENAMED)) {
				parentHistory.setEvent(History.EVENT_FOLDER_SUBFOLDER_RENAMED);
			} else if (eventType.equals(History.EVENT_FOLDER_PERMISSION)) {
				parentHistory.setEvent(History.EVENT_FOLDER_SUBFOLDER_PERMISSION);
			}
			parentHistory.setComment("");

			store(parentHistory);
		}
	}
}