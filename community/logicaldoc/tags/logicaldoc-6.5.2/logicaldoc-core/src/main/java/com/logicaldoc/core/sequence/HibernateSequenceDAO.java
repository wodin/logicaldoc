package com.logicaldoc.core.sequence;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.logicaldoc.core.generic.Generic;
import com.logicaldoc.core.generic.dao.GenericDAO;
import com.logicaldoc.util.sql.SqlUtil;

/**
 * Hibernate implementation of <code>SequenceDAO</code>. </p> Sequences are
 * implemented ad Generics whose type is 'sequence' and subtype is the sequence
 * name.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.0
 */
public class HibernateSequenceDAO extends HibernateDaoSupport implements SequenceDAO {
	public final String TYPE = "sequence";

	protected static Logger log = LoggerFactory.getLogger(HibernateSequenceDAO.class);

	private GenericDAO genericDao;

	public GenericDAO getGenericDao() {
		return genericDao;
	}

	public void setGenericDao(GenericDAO genericDao) {
		this.genericDao = genericDao;
	}

	@Override
	public synchronized void reset(String sequence, int value) {
		synchronized (SequenceDAO.class) {
			Generic generic = genericDao.findByAlternateKey(TYPE, sequence);
			if (generic == null) {
				generic = new Generic(TYPE, sequence);
				generic.setInteger1(value);
			}
			generic.setInteger1(value);
			genericDao.store(generic);
		}
	}

	@Override
	public synchronized int next(String sequence) {
		synchronized (SequenceDAO.class) {
			Generic generic = genericDao.findByAlternateKey(TYPE, sequence);
			if (generic == null) {
				generic = new Generic(TYPE, sequence);
				generic.setInteger1(0);
			}
			generic.setInteger1(generic.getInteger1().intValue() + 1);
			genericDao.store(generic);
			return generic.getInteger1();
		}
	}

	@Override
	public int getCurrentValue(String sequence) {
		Generic generic = genericDao.findByAlternateKey(TYPE, sequence);
		if (generic == null)
			return 0;
		else
			return generic.getInteger1();
	}

	@Override
	public List<Generic> findByName(String name) {
		String query = " 1=1 ";
		query += " and _entity.type like '" + SqlUtil.doubleQuotes(TYPE) + "' ";
		query += " and _entity.subtype like '" + SqlUtil.doubleQuotes(name) + "%' ";
		return genericDao.findByWhere(query, null, null);
	}
}