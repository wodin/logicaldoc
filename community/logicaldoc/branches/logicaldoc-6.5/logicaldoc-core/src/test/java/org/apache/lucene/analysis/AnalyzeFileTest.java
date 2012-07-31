package org.apache.lucene.analysis;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import junit.framework.TestCase;

import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttributeImpl;
import org.apache.lucene.util.AttributeImpl;
import org.apache.lucene.util.Version;
import org.junit.Test;

import com.logicaldoc.core.i18n.Language;

public class AnalyzeFileTest extends TestCase {

	private Analyzer sbitAnal;

	private static final Version LUCENE_VERSION = Version.LUCENE_35;

	public AnalyzeFileTest() {
		Language language = new Language(new Locale("it"));
		sbitAnal = new SnowballAnalyzer(LUCENE_VERSION, "Italian", language.getStopWords());
	}

	/**
	 * A helper method that analizes a string
	 * 
	 * @param a the Analyzer to use
	 * @param input an input String to analyze
	 * @throws Exception in case an error occurs
	 */
	private String[] getAnalysisResult(org.apache.lucene.analysis.Analyzer a, String input) throws Exception {
		TokenStream ts = a.tokenStream("dummy", new StringReader(input));
		List<String> resultList = new ArrayList<String>();
		while (true) {
			boolean ret = ts.incrementToken();
			if (!ret) {
				ts.end();
				break;
			}
			java.util.Iterator<AttributeImpl> attributes = ts.getAttributeImplsIterator();
			while (attributes.hasNext()) {
				AttributeImpl att = attributes.next();
				if (att instanceof CharTermAttributeImpl)
					resultList.add(((CharTermAttributeImpl) att).toString());
			}
		}
		ts.close();
		return resultList.toArray(new String[0]);
	}

	@Test
	public void testSnowballAnalyzer() throws Exception {

		File file = new File(URLDecoder.decode(getClass().getClassLoader().getResource("AnalyzeFileTest_enc.txt")
				.getPath(), "UTF-8"));
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
		StringBuffer content = new StringBuffer();
		int ichar = 0;

		while ((ichar = bis.read()) > 0) {
			content.append((char) ichar);
		}

		long start = System.currentTimeMillis();
		String[] result2 = getAnalysisResult(sbitAnal, content.toString());
		long end = System.currentTimeMillis() - start;
		System.out.println("Elab time millis: " + end);

		for (String token : result2) {
			System.out.println(token);
		}

		assertTrue(result2.length > 0);
	}
}