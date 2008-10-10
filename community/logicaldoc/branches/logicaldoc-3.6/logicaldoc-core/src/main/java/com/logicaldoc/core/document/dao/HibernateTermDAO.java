package com.logicaldoc.core.document.dao;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.logicaldoc.core.document.Term;
import com.logicaldoc.core.document.TermID;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * Hibernate implementation of <code>TermDAO</code>
 * 
 * @author Marco Meschieri
 * @version $Id: HibernateTermDAO.java,v 1.1 2007/06/29 06:28:28 marco Exp $
 * @since 3.0
 */
public class HibernateTermDAO extends HibernateDaoSupport implements TermDAO {

	protected static Log log = LogFactory.getLog(HibernateTermDAO.class);

	private HibernateTermDAO() {
	}

	/**
	 * @see com.logicaldoc.core.document.dao.TermDAO#delete(int)
	 */
	@SuppressWarnings("unchecked")
	public boolean delete(int menuId) {
		boolean result = true;

		try {
			Collection<Term> coll = (Collection<Term>) getHibernateTemplate().find(
					"from com.logicaldoc.core.document.Term _term where _term.id.menuId = ?",
					new Object[] { new Integer(menuId) });
			getHibernateTemplate().deleteAll(coll);
		} catch (Exception e) {
			if (log.isErrorEnabled())
				logger.error(e.getMessage(), e);
			result = false;
		}

		return result;
	}

	/**
	 * @see com.logicaldoc.core.document.dao.TermDAO#findByMenuId(int)
	 */
	@SuppressWarnings("unchecked")
	public Collection<Term> findByMenuId(int menuId) {
		Collection<Term> result = new ArrayList<Term>();

		try {
			result = (Collection<Term>) getHibernateTemplate().find(
					"from com.logicaldoc.core.document.Term _term where _term.id.menuId = ?",
					new Object[] { new Integer(menuId) });
		} catch (Exception e) {
			if (log.isErrorEnabled())
				logger.error(e.getMessage(), e);
		}

		return result;
	}

	/**
	 * @see com.logicaldoc.core.document.dao.TermDAO#findById(com.logicaldoc.core.document.TermID)
	 */
	public Term findById(TermID termId) {
		return (Term) getHibernateTemplate().get(Term.class, termId);
	}

	/**
	 * @see com.logicaldoc.core.document.dao.TermDAO#findByStem(int)
	 */
	@SuppressWarnings("unchecked")
	public Collection<Term> findByStem(int menuId, int maxResults) {
		Collection<Term> result = new ArrayList<Term>();

		try {
			StringBuffer query = new StringBuffer(
					"select _term.id.menuId, _term.id.stem from com.logicaldoc.core.document.Term _term where _term.id.menuId != ? ");
			Collection<Term> coll = findByMenuId(menuId);

			if (!coll.isEmpty()) {
				query.append("and _term.id.stem in (");
				boolean first = true;
				for (Term term : coll) {
					if (!first)
						query.append(",");
					query.append("'" + term.getStem() + "'");
					first = false;
				}
				query.append(") ");
			}

			query.append("order by id.menuId, id.stem, value, wordCount, originWord");

			Collection<Object[]> tmp = (Collection<Object[]>) getHibernateTemplate().find(query.toString(),
					new Object[] { new Integer(menuId) });
			int i = 0;
			for (Object[] entry : tmp) {
				if (i >= maxResults)
					break;
				TermID id = new TermID((Integer) entry[0], (String) entry[1]);
				result.add(findById(id));
				i++;
			}

			// result = (Collection<Term>)
			// getHibernateTemplate().find(query.toString(),
			// new Object[] { new Integer(menuId) });
		} catch (Exception e) {
			if (log.isErrorEnabled())
				logger.error(e.getMessage(), e);
		}

		return result;
	}

	/**
	 * @see com.logicaldoc.core.document.dao.TermDAO#store(com.logicaldoc.core.document.Term)
	 */
	public boolean store(Term term) {
		boolean result = true;

		if (term.getStem().length() > 50)
			term.setStem(term.getStem().substring(0, 50));

		if (term.getOriginWord().length() > 70)
			term.setOriginWord(term.getOriginWord().substring(0, 70));

		try {
			getHibernateTemplate().saveOrUpdate(term);
		} catch (Exception e) {
			if (log.isErrorEnabled())
				logger.error(e.getMessage(), e);
			result = false;
		}

		return result;
	}
}