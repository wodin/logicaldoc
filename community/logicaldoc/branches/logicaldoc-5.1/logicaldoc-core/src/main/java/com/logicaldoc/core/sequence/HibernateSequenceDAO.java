package com.logicaldoc.core.sequence;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.logicaldoc.core.generic.Generic;
import com.logicaldoc.core.generic.dao.GenericDAO;

/**
 * Hibernate implementation of <code>SequenceDAO</code>.
 * </p>
 * Sequences are implemented ad Generics whose type is 'sequence' and subtype is
 * the sequence name.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.0
 */
public class HibernateSequenceDAO extends HibernateDaoSupport implements SequenceDAO {
	public final String TYPE = "sequence";

	protected static Log log = LogFactory.getLog(HibernateSequenceDAO.class);

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
}