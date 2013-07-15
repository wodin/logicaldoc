package com.logicaldoc.core.document.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;

import com.logicaldoc.core.HibernatePersistentObjectDAO;
import com.logicaldoc.core.document.Rating;

/**
 * Hibernate implementation of <code>RatingDAO</code>
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.1
 */
@SuppressWarnings("unchecked")
public class HibernateRatingDAO extends HibernatePersistentObjectDAO<Rating> implements RatingDAO {

	public HibernateRatingDAO() {
		super(Rating.class);
		super.log = LoggerFactory.getLogger(HibernateRatingDAO.class);
	}

	
	@Override
	public Rating findVotesByDocId(long docId) {
		List<Rating> coll = new ArrayList<Rating>();
		try {

			/*
			 * Don't use AVG function to have more control on rounding policy
			 */
			String query = "select count(*), SUM(ld_vote) from ld_rating where ld_deleted=0 and ld_docid = " + docId;

			@SuppressWarnings("rawtypes")
			RowMapper ratingMapper = new BeanPropertyRowMapper() {
				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {

					Rating rating = new Rating();
					rating.setCount(rs.getInt(1));
					if (rs.getInt(1) > 0) {
						float div = (float) rs.getInt(2) / (float) rs.getInt(1);
						double avg = Math.round(div * 100.0) / 100.0;
						rating.setAverage(new Double(avg).floatValue());
					} else
						rating.setAverage(0F);

					return rating;
				}
			};

			coll = (List<Rating>) query(query, new Object[] {}, ratingMapper, null);
			if (!coll.isEmpty() && coll.get(0).getCount() != 0)
				return coll.get(0);

		} catch (Throwable e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public boolean findByDocIdAndUserId(long docId, long userId) {
		return findByWhere("_entity.docId =" + docId + " and _entity.userId =" + userId, null, null).size() > 0;
	}
}
