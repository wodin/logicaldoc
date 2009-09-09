package com.logicaldoc.util.dbinit;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

/**
 * Test case for database initialisation utility
 * 
 * @author Marco Meschieri
 */
public class DBInitTest extends TestCase {

	// Instance under test
	private DBInit dbinit;

	public void setUp() throws Exception {
		List<String> sqlList = new ArrayList<String>();
		sqlList.add("sql1.sql");
		sqlList.add("sql2.sql");
		dbinit = new DBInit(sqlList);
		dbinit.setDriver("org.hsqldb.jdbcDriver");
		dbinit.setUrl("jdbc:hsqldb:mem:logicaldoc");
		dbinit.setUsername("sa");
		dbinit.setPassword("");
	}

	public void tearDown() throws Exception {
		

	}

	public void testExecute() throws Exception {
		dbinit.execute();
	}
}
