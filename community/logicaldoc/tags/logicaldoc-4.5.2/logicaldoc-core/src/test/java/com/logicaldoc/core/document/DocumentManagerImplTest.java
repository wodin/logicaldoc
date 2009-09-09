package com.logicaldoc.core.document;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Locale;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringEscapeUtils;

import com.logicaldoc.core.AbstractCoreTestCase;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.core.security.dao.UserDAO;

/**
 * Test case for <code>DocumentManagerImpl</code>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.5
 */
public class DocumentManagerImplTest extends AbstractCoreTestCase {

	/** A table of hex digits */
	private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
			'E', 'F' };

	private DocumentDAO docDao;

	private UserDAO userDao;

	private MenuDAO menuDao;

	// Instance under test
	private DocumentManager documentManager;

	public DocumentManagerImplTest(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		docDao = (DocumentDAO) context.getBean("DocumentDAO");
		userDao = (UserDAO) context.getBean("UserDAO");
		menuDao = (MenuDAO) context.getBean("MenuDAO");

		// Make sure that this is a DocumentManagerImpl instance
		documentManager = (DocumentManager) context.getBean("DocumentManager");
	}

	public void testMakeImmutable() throws Exception {
		User user = userDao.findByUserName("admin");
		Document doc = docDao.findById(1);
		String comment = "pippo_reason";
		assertNotNull(doc);
		documentManager.makeImmutable(doc.getId(), user, comment);
		doc = docDao.findById(1);
		assertEquals(1, doc.getImmutable());
		doc.setFileName("ciccio");
		docDao.initialize(doc);
		docDao.store(doc);
		assertEquals("pippo", doc.getFileName());
		doc.setImmutable(0);
		docDao.store(doc);
		docDao.findById(doc.getId());
		assertEquals(1, doc.getImmutable());
	}

	public void testLock() throws Exception {
		User user = userDao.findByUserName("admin");
		documentManager.unlock(1L, user, "");
		Document doc = docDao.findById(1);
		String comment = "pippo_reason";
		assertNotNull(doc);
		documentManager.lock(doc.getId(), 2, user, comment);
		doc = docDao.findById(1);
		assertEquals(2, doc.getStatus());
		assertEquals(1L, doc.getLockUserId().longValue());
	}

	public void testCreateTitleEquals() throws Exception {
		User user = userDao.findByUserName("admin");
		Menu folder = menuDao.findById(Menu.MENUID_HOME);

		File file = new File("C:/tmp/Anomalie Windows Vista.txt");
		String title = "Anomalie Windows Vista";

		Document doc = documentManager.create(file, folder, user, Locale.ITALIAN, title, null, "", "", "", "", "",
				null, null, null, null, null, null, null, false);

		// Find the created documents and check the title is equals
		long createdID = doc.getId();
		Document foundDoc = docDao.findById(createdID);
		assertEquals(title, foundDoc.getTitle());
	}

	public void testCreateGreekTitleEquals() throws Exception {
		User user = userDao.findByUserName("admin");
		Menu folder = menuDao.findById(Menu.MENUID_HOME);

		File file = new File(
				"C:/Users/alle/Desktop/LogicalDoc45RC3_Greek_problem/\u03B7 \u03B1\u03BD\u03B1\u03B6\u03AE\u03C4\u03B7\u03C3\u03B7 \u03B5\u03C1\u03B3\u03B1\u03C3\u03AF\u03B1\u03C2.pdf");

		String baseName = FilenameUtils.getBaseName(file.getName());
		System.out.println("baseName : " + baseName);

		// String urlenc1 = URLEncoder.encode(baseName, "UTF-8");
		// String htmlenc1 = StringEscapeUtils.escapeHtml(baseName);
		//String encoded = convertUnicodeToEncoded(baseName);
		
		// convert the srting to utf8
		String encoded = encodeUTF8(baseName);

		// String title = "\u03B7
		// \u03B1\u03BD\u03B1\u03B6\u03AE\u03C4\u03B7\u03C3\u03B7
		// \u03B5\u03C1\u03B3\u03B1\u03C3\u03AF\u03B1\u03C2.pdf";
		// String title = new String(baseName.getBytes(), "UTF-8");
		String title = encoded;

		Document doc = documentManager.create(file, folder, user, Locale.ITALIAN, title, null, "", "", "", "", "",
				null, null, null, null, null, null, null, false);

		// Find the created documents and check the title is equals
		long createdID = doc.getId();
		Document foundDoc = docDao.findById(createdID);
		System.out.println("encoded: " + encoded);
		System.out.println("fd.gTit: " + foundDoc.getTitle());
		assertEquals(title, foundDoc.getTitle());

		// At this point we verified that the title don't change in the DB

		// String urlenc2 = URLEncoder.encode(foundDoc.getTitle(), "UTF-8");
		// System.out.println("urlenc1: " + urlenc1);
		// System.out.println("urlenc2: " + urlenc2);
		// assertEquals(urlenc1, urlenc2);

		// create a string with characters escaped
		// String zzz = convertUnicodeToEncoded("fhjfhghèà");
		// System.out.println("zzz: " + zzz);

		// String htmlenc2 = StringEscapeUtils.escapeHtml(foundDoc.getTitle());
		// System.out.println("htmlenc1: " + htmlenc1);
		// System.out.println("htmlenc2: " + htmlenc2);
		// assertEquals(htmlenc1, htmlenc2);
	}
	
	
	public void testZH() throws Exception {
       Locale locale = Locale.CHINA;
       System.out.println(locale);
       locale = Locale.CHINESE;
       System.out.println(locale);
	}

	/**
	 * Converts unicodes to encoded &#92;uxxxx.
	 * 
	 * @param str
	 *            string to convert
	 * @return converted string
	 * @see java.util.Properties
	 */
	public static String convertUnicodeToEncoded(String str) {
		int len = str.length();
		StringBuffer outBuffer = new StringBuffer(len * 2);

		for (int x = 0; x < len; x++) {
			char aChar = str.charAt(x);
			if ((aChar < 0x0020) || (aChar > 0x007e)) {
				outBuffer.append('\\');
				outBuffer.append('u');
				outBuffer.append(toHex((aChar >> 12) & 0xF));
				outBuffer.append(toHex((aChar >> 8) & 0xF));
				outBuffer.append(toHex((aChar >> 4) & 0xF));
				outBuffer.append(toHex(aChar & 0xF));
			} else {
				outBuffer.append(aChar);
			}
		}
		return outBuffer.toString();
	}

	/**
	 * Converts a nibble to a hex character
	 * 
	 * @param nibble
	 *            the nibble to convert.
	 * @return a converted character
	 */
	private static char toHex(int nibble) {
		char hexChar = HEX_DIGITS[(nibble & 0xF)];
		return hexChar;
	}

	private String encodeUTF8(String mystr) {
		
		Charset charset = Charset.forName("UTF-8");
		CharsetEncoder encoder = charset.newEncoder();
		
        String x = null;
		try {
			// Convert a string to UTF-8 bytes in a ByteBuffer
			// The new ByteBuffer is ready to be read.
			ByteBuffer bbuf = encoder.encode(CharBuffer.wrap(mystr));
			
			x = new String(bbuf.array(), "UTF-8");
		} catch (CharacterCodingException e) {
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return x;
	}

}