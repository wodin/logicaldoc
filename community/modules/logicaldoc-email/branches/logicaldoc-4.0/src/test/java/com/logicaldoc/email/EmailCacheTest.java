package com.logicaldoc.email;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import junit.framework.TestCase;

/**
 * Simple test case for the EmailCache
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 4.0
 * 
 */
public class EmailCacheTest extends TestCase {

	// Instance under test
	private EmailCache cache;

	protected void setUp() throws Exception {
		cache = new EmailCache(new File("target/emailCache.txt"));
		cache.put("mailId", new Date());
		cache.write();
	}

	public void testRead() throws IOException {
		cache.read();
		assertEquals(1, cache.size());
		assertNotNull(cache.get("mailId"));
	}

	public void testWrite() throws IOException {
		for (int i = 0; i < 20000; i++) {
			cache.put("mail" + i, new Date());
		}
		cache.write();
		cache.read();
		assertEquals(20001, cache.size());
		assertNotNull(cache.get("mail11123"));
	}

	public void testAppend() throws IOException {
		for (int i = 0; i < 20000; i++) {
			cache.put("mail" + i, new Date());
		}
		cache.write();
		cache.read();
		assertEquals(20001, cache.size());
		assertNotNull(cache.get("mail11124"));

		cache.put("mail21124", new Date());
		cache.append();
		cache.read();
		assertEquals(20002, cache.size());
		assertNotNull(cache.get("mail21124"));
	}
}
