package com.logicaldoc.core.sequence;

import java.util.Date;
import java.util.List;

import org.slf4j.LoggerFactory;

import com.logicaldoc.core.HibernatePersistentObjectDAO;
import com.logicaldoc.util.sql.SqlUtil;

/**
 * Hibernate implementation of <code>SequenceDAO</code>. </p> Sequences are
 * implemented ad Generics whose type is 'sequence' and subtype is the sequence
 * name.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.0
 */
@SuppressWarnings("unchecked")
public class HibernateSequenceDAO extends HibernatePersistentObjectDAO<Sequence> implements SequenceDAO {

	private HibernateSequenceDAO() {
		super(Sequence.class);
		super.log = LoggerFactory.getLogger(HibernateSequenceDAO.class);
	}

	@Override
	public synchronized void reset(String sequence, Long objectId, long tenantId, long value) {
		synchronized (SequenceDAO.class) {
			flush();
			Sequence seq = findByAlternateKey(sequence, objectId, tenantId);
			if (seq == null) {
				seq = new Sequence();
				seq.setName(sequence);
				seq.setObjectId(objectId);
				seq.setTenantId(tenantId);
			}
			seq.setLastReset(new Date());
			seq.setValue(value);
			store(seq);
		}
	}

	@Override
	public synchronized long next(String sequence, Long objectId, long tenantId, long increment) {
		synchronized (SequenceDAO.class) {
			flush();
			Sequence seq = findByAlternateKey(sequence, objectId, tenantId);
			if (seq == null) {
				seq = new Sequence();
				seq.setName(sequence);
				seq.setObjectId(objectId);
				seq.setTenantId(tenantId);
			}
			seq.setValue(seq.getValue() + increment);
			store(seq);
			return seq.getValue();
		}
	}

	@Override
	public synchronized long next(String sequence, Long objectId, long tenantId) {
		return this.next(sequence, objectId, tenantId, 1L);
	}

	@Override
	public long getCurrentValue(String sequence, Long objectId, long tenantId) {
		flush();
		Sequence seq = findByAlternateKey(sequence, objectId, tenantId);
		if (seq == null)
			return 0L;
		else
			return seq.getValue();
	}

	@Override
	public List<Sequence> findByName(String name, long tenantId) {
		String query = " _entity.tenantId=" + tenantId;
		query += " and _entity.name like '" + SqlUtil.doubleQuotes(name) + "%' ";
		return findByWhere(query, null, null);
	}

	@Override
	public Sequence findByAlternateKey(String name, Long objectId, long tenantId) {
		String query = " _entity.tenantId=" + tenantId;
		query += " and _entity.name = '" + SqlUtil.doubleQuotes(name) + "' ";
		if (objectId != null)
			query += " and _entity.objectId = " + objectId;
		List<Sequence> sequences = findByWhere(query, null, null);
		if (sequences.isEmpty())
			return null;
		else
			return sequences.get(0);
	}
}