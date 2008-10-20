package com.logicaldoc.core.document.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.logicaldoc.core.document.Term;

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
	 * @see com.logicaldoc.core.document.dao.TermDAO#delete(long)
	 */
	@SuppressWarnings("unchecked")
	public boolean delete(long docId) {
		boolean result = true;

		try {
			Collection<Term> coll = (Collection<Term>) getHibernateTemplate().find(
					"from Term _term where _term.docId = ?", new Object[] { new Long(docId) });
			getHibernateTemplate().deleteAll(coll);
		} catch (Exception e) {
			if (log.isErrorEnabled())
				logger.error(e.getMessage(), e);
			result = false;
		}

		return result;
	}

	/**
	 * @see com.logicaldoc.core.document.dao.TermDAO#findByDocId(long)
	 */
	@SuppressWarnings("unchecked")
	public Collection<Term> findByDocId(long docId) {
		Collection<Term> result = new ArrayList<Term>();

		try {
			result = (Collection<Term>) getHibernateTemplate().find("from Term _term where _term.docId = ?",
					new Object[] { new Long(docId) });
		} catch (Exception e) {
			if (log.isErrorEnabled())
				logger.error(e.getMessage(), e);
		}

		return result;
	}

	/**
	 * @see com.logicaldoc.core.document.dao.TermDAO#findById(long)
	 */
	public Term findById(long termId) {
		return (Term) getHibernateTemplate().get(Term.class, termId);
	}

	/**
	 * @see com.logicaldoc.core.document.dao.TermDAO#findByStem(long, int)
	 */
	@SuppressWarnings("unchecked")
	public List<Term> findByStem(long docId, int maxResults) {
		List<Term> result = new ArrayList<Term>();

		try {
			StringBuffer query = new StringBuffer("select _term.id from Term _term where _term.docId != ? ");
			Collection<Term> coll = findByDocId(docId);
			if (!coll.isEmpty()) {
				query.append("and _term.stem in (");
				boolean first = true;
				for (Term term : coll) {
					if (!first)
						query.append(",");
					query.append("'" + term.getStem() + "'");
					first = false;
				}
				query.append(") ");
			}

			query.append("order by id.docId, id.stem, value, wordCount, originWord");

			Collection<Long> tmp = (Collection<Long>) getHibernateTemplate().find(query.toString(),
					new Object[] { new Long(docId) });
			int i = 0;
			for (Long termId : tmp) {
				if (i >= maxResults)
					break;
				result.add(findById(termId.longValue()));
				i++;
			}
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